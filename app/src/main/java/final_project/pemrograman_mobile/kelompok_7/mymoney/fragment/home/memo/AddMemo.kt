package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.memo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.MainActivity
import final_project.pemrograman_mobile.kelompok_7.mymoney.R
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Memo
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.MonefyDatabase
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeAddMemoBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.HomeFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.Constants
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.SystemDatePicker
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.MessageFormat
import java.time.ZonedDateTime

class AddMemo : Fragment(), SystemDatePicker.Callback {
    private lateinit var frontend: FragmentHomeAddMemoBinding
    private lateinit var context: Context
    private lateinit var datePicker: SystemDatePicker
    private var datePickerBusy: Boolean = false
    private lateinit var parentFragment: HomeFragment
    private lateinit var calendar: ZonedDateTime
    private var existingMemoId: Int? = null
    private var existingMemoPinned: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = requireContext()
        parentFragment = requireParentFragment() as HomeFragment
        calendar = parentFragment.getCurrentPagination()

        datePicker = SystemDatePicker()
        datePicker.setInitialDate(calendar)
        datePicker.setListener(this)

        val arg = arguments
        if (arg != null) {
            val id = arg.getInt("memo_id", -1)
            if (id > 0) { existingMemoId = id }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        frontend = FragmentHomeAddMemoBinding.inflate(inflater, container, false)
        return frontend.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.frontend.imageView.setOnClickListener {
            this.parentFragment.pressBackButton()
        }

        this.frontend.textView2.setOnClickListener {
            if (datePickerBusy) { return@setOnClickListener; }
            datePickerBusy = true

            this.datePicker.show(childFragmentManager, "datePicker")
        }

        this.frontend.imageView2.setOnClickListener {
            if (existingMemoPinned) {
                existingMemoPinned = false

                refreshPinnedStatus()
                return@setOnClickListener
            }

            existingMemoPinned = true
            refreshPinnedStatus()
        }

        this.frontend.imageView3.setOnClickListener {
            val id = existingMemoId ?: return@setOnClickListener

            MainScope().launch(Dispatchers.IO) { delete(id) }
        }

        this.frontend.button2.setOnClickListener { submit() }
        refreshDatePickerText()
    }

    override fun onStart() {
        super.onStart()

        val id = existingMemoId
        if (id != null) {
            MainScope().launch(Dispatchers.IO) { fetchExistingMemoData(id) }
        }
    }

    override fun onResume() {
        super.onResume()
        parentFragment.setBottomNavVisibility(false)
        parentFragment.setFabVisibility(false)
        parentFragment.setTopBarVisibility(false)
        parentFragment.setSummaryAmountVisibility(false)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        parentFragment.setBottomNavVisibility(true)
        parentFragment.setFabVisibility(true)
        parentFragment.setTopBarVisibility(true)
        parentFragment.setSummaryAmountVisibility(true)
        super.onDestroy()
    }

    @WorkerThread
    private fun fetchExistingMemoData(id: Int) {
        val database: MonefyDatabase = MonefyDatabase.require()

        val data = database.memos().selectById(id)
        calendar = UtilityFunctions.fromInstant(data.createdAtTimestamp)
        existingMemoPinned = data.isPinned

        MainScope().launch(Dispatchers.Main) {
            frontend.textView2.isEnabled = false
            refreshDatePickerText()
            refreshPinnedStatus()

            frontend.fragmentHomeAddMemoExistAction.visibility = View.VISIBLE
            frontend.editTextText.setText(data.title)
            frontend.editTextText2.setText(data.content)
            frontend.button2.text = "Update memo"
        }
    }

    @WorkerThread
    private fun delete(id: Int) {
        val database: MonefyDatabase = MonefyDatabase.require()

        database.memos().deleteById(id)

        MainScope().launch(Dispatchers.Main) {
            Toast.makeText(context, "Sukses menghapus memo", Toast.LENGTH_SHORT).show()
            parentFragment.pressBackButton()
        }
    }

    private fun refreshDatePickerText() {
        this.frontend.textView2.text = MessageFormat("{0}.{1} ({2})").format(arrayOf(
            calendar.dayOfMonth.toString().padStart(2, '0'),
            calendar.monthValue.toString().padStart(2, '0'),
            Constants.stringHariPendek(calendar.dayOfWeek.ordinal)
        ))
    }

    private fun refreshPinnedStatus() {
        if (existingMemoPinned) {
            this.frontend.imageView2.setImageDrawable(AppCompatResources.getDrawable(this.context, R.drawable.active_push_pin_24))
            return
        }

        this.frontend.imageView2.setImageDrawable(AppCompatResources.getDrawable(this.context, R.drawable.inactive_push_pin_24))
    }

    private fun submit() {
        val database: MonefyDatabase = MonefyDatabase.require()

        val name = this.frontend.editTextText.text.toString()
        val description = this.frontend.editTextText2.text.toString()

        if (name.isEmpty() && description.isEmpty()) {
            Toast.makeText(this.context, "Judul dan deskripsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val ins = UtilityFunctions.fromZonedDateTime(calendar)
        val existingId = existingMemoId
        var id = 0

        if (existingId != null) {
            id = existingId
        }

        val memoData = Memo(
            id,
            name,
            description,
            ins,
            existingMemoPinned
        )

        MainScope().launch(Dispatchers.IO) {
            if (existingId != null) {
                database.memos().update(memoData)
            }
            else {
                database.memos().insert(memoData)
            }

            MainScope().launch(Dispatchers.Main) { submitDone() }
        }
    }

    @MainThread
    private fun submitDone() {
        if (existingMemoId != null) {
            Toast.makeText(context, "Sukses mengupdate memo", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Sukses menambahkan memo", Toast.LENGTH_SHORT).show()
        }
        this.parentFragment.pressBackButton()
    }

    override fun onSystemDatePickerSet(view: DatePicker, year: Int, month: Int, day: Int) {
        calendar = calendar.withYear(year)
        calendar = calendar.withMonth(month + 1)
        calendar = calendar.withDayOfMonth(day)
        refreshDatePickerText()
    }

    override fun onSystemDatePickerDismissed() {
        datePickerBusy = false
    }
}