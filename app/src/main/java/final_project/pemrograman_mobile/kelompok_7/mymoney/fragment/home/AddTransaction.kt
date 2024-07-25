package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.MainActivity
import final_project.pemrograman_mobile.kelompok_7.mymoney.R
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Asset
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Category
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.MonefyDatabase
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Transaction
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.TransactionType
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeAddTransactionBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.SystemDatePicker
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.SystemTimePicker
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.MessageFormat
import java.time.ZonedDateTime

class AddTransaction : Fragment(), SystemDatePicker.Callback, SystemTimePicker.Callback {
    private lateinit var frontend: FragmentHomeAddTransactionBinding
    private lateinit var database: MonefyDatabase
    private lateinit var context: Context
    private lateinit var datePicker: SystemDatePicker
    private var datePickerBusy: Boolean = false
    private lateinit var timePicker: SystemTimePicker
    private var timePickerBusy: Boolean = false
    private lateinit var activity: MainActivity
    private lateinit var parentFragment: HomeFragment
    private lateinit var calendar: ZonedDateTime
    private lateinit var categoryListAdapter: ArrayAdapter<Category>
    private var selectedCategoryID = -1
    private lateinit var assetListAdapter: ArrayAdapter<Asset>
    private var selectedAssetID = -1
    private var addType: TransactionType = TransactionType.OUTCOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = requireContext()
        database = MonefyDatabase.require()
        activity = requireActivity() as MainActivity
        parentFragment = requireParentFragment() as HomeFragment
        calendar = parentFragment.getCurrentPagination()

        datePicker = SystemDatePicker()
        datePicker.setInitialDate(calendar)
        datePicker.setListener(this)

        timePicker = SystemTimePicker()
        timePicker.setInitialTime(calendar)
        timePicker.setListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        frontend = FragmentHomeAddTransactionBinding.inflate(inflater, container, false)
        return frontend.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainScope().launch(Dispatchers.IO) {
            initializeCategoryList(addType)
            initializeAssetList()
        }

        refreshDatePickerText()
        refreshTimePickerText()

        this.frontend.imageView.setOnClickListener {
            this.activity.onBackButtonPressed()
        }

        this.frontend.homeAddTransactionTypeIncomeButton.setOnClickListener {
            this.frontend.homeAddTransactionTypeIncomeButton.setBackgroundColor(this.context.getColor(R.color.red))
            this.frontend.homeAddTransactionTypeOutcomeButton.setBackgroundColor(Color.TRANSPARENT)
            this.frontend.homeAddTransactionTypeTransferButton.setBackgroundColor(Color.TRANSPARENT)
            addType = TransactionType.INCOME
            MainScope().launch(Dispatchers.IO) { initializeCategoryList(addType) }
        }

        this.frontend.homeAddTransactionTypeOutcomeButton.setOnClickListener {
            this.frontend.homeAddTransactionTypeIncomeButton.setBackgroundColor(Color.TRANSPARENT)
            this.frontend.homeAddTransactionTypeOutcomeButton.setBackgroundColor(this.context.getColor(R.color.red))
            this.frontend.homeAddTransactionTypeTransferButton.setBackgroundColor(Color.TRANSPARENT)
            addType = TransactionType.OUTCOME
            MainScope().launch(Dispatchers.IO) { initializeCategoryList(addType) }
        }

        this.frontend.homeAddTransactionTypeTransferButton.setOnClickListener {
            this.frontend.homeAddTransactionTypeIncomeButton.setBackgroundColor(Color.TRANSPARENT)
            this.frontend.homeAddTransactionTypeOutcomeButton.setBackgroundColor(Color.TRANSPARENT)
            this.frontend.homeAddTransactionTypeTransferButton.setBackgroundColor(this.context.getColor(R.color.red))
            addType = TransactionType.TRANSFER
            MainScope().launch(Dispatchers.IO) {
                initializeCategoryList(addType)
            }
        }

        this.frontend.homeAddTransactionDatePickerText.setOnClickListener {
            if (datePickerBusy) { return@setOnClickListener; }
            datePickerBusy = true

            this.datePicker.show(childFragmentManager, "datePicker")
        }

        this.frontend.homeAddTransactionTimePickerText.setOnClickListener {
            if (timePickerBusy) { return@setOnClickListener; }
            timePickerBusy = true

            this.timePicker.show(childFragmentManager, "timePicker")
        }

        this.frontend.button.setOnClickListener { submit() }
        this.frontend.fragmentHomeAddTransactionCategorySelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategoryID = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategoryID = -1
            }
        }
        this.frontend.fragmentHomeAddTransactionAssetsSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedAssetID = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedAssetID = -1
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

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

    private fun refreshDatePickerText() {
        this.frontend.homeAddTransactionDatePickerText.text = MessageFormat("{0}/{1}/{2}").format(arrayOf(
            calendar.dayOfMonth.toString().padStart(2, '0'),
            calendar.monthValue.toString().padStart(2, '0'),
            calendar.year.toString()
        ))
    }

    private fun refreshTimePickerText() {
        this.frontend.homeAddTransactionTimePickerText.text = MessageFormat("{0}:{1}").format(arrayOf(
            calendar.hour.toString().padStart(2, '0'),
            calendar.minute.toString().padStart(2, '0'),
        ))
    }

    @WorkerThread
    private fun initializeCategoryList(type: TransactionType) {
        val categories = this.database.categories().selectByType(type)
        val items = arrayOfNulls<Category>(categories.size)

        for (i in categories.indices) {
            items[i] = categories[i]
        }

        MainScope().launch(Dispatchers.Main) {
            categoryListAdapter = ArrayAdapter(context, R.layout.fragment_home_add_transaction_category_listitem, items)
            frontend.fragmentHomeAddTransactionCategorySelector.adapter = categoryListAdapter
        }
    }

    @WorkerThread
    private fun initializeAssetList() {
        val assets = this.database.assets().selectAll()
        val items = arrayOfNulls<Asset>(assets.size)

        for (i in assets.indices) {
            items[i] = assets[i]
        }

        MainScope().launch(Dispatchers.Main) {
            assetListAdapter = ArrayAdapter(context, R.layout.fragment_home_add_transaction_assets_listitem, items)
            frontend.fragmentHomeAddTransactionAssetsSelector.adapter = assetListAdapter
        }
    }

    private fun submit() {
        val name = this.frontend.homeAddTransactionNameEdittext.text.toString()
        val description = this.frontend.homeAddTransactionDescriptionEdittext.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(this.context, "Judul transaksi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val amount: Double

        try {
            amount = this.frontend.homeAddTransactionAmountEdittext.text.toString().toDouble()
        }
        catch (e: NumberFormatException) {
            Toast.makeText(this.context, "Nilai total transaksi tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategoryID == -1) {
            Toast.makeText(this.context, "Kategori transaksi perlu dipilih", Toast.LENGTH_SHORT).show()
            return
        }

        val category = categoryListAdapter.getItem(selectedCategoryID) ?: return
        val asset = assetListAdapter.getItem(selectedAssetID) ?: return

        MainScope().launch(Dispatchers.IO) {
            val selectedAsset = database.assets().selectById(asset.id)
            if (selectedAsset == null) {
                MainScope().launch(Dispatchers.Main) {
                    Toast.makeText(context, "Cannot get data for the selected asset", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            if (addType == TransactionType.INCOME) {
                selectedAsset.balance += amount
            }
            else if (addType == TransactionType.OUTCOME){
                selectedAsset.balance -= amount
            }

            val time = UtilityFunctions.fromZonedDateTime(calendar)

            val transaction = Transaction(
                0,
                selectedAsset.id,
                0,  // FIXME: Put actual asset ID here
                name,
                description,
                category.id,
                addType,
                amount,
                time,
                time
            )

            database.transactions().insert(transaction)
            database.assets().update(selectedAsset)

            MainScope().launch(Dispatchers.Main) { submitDone() }
        }
    }

    @MainThread
    private fun submitDone() {
        Toast.makeText(context, "Sukses menambahkan transaksi", Toast.LENGTH_SHORT).show()
        activity.onBackButtonPressed()
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

    override fun onSystemTimePickerSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        calendar = calendar.withHour(hourOfDay)
        calendar = calendar.withMinute(minute)
        refreshTimePickerText()
    }

    override fun onSystemTimePickerDismissed() {
        timePickerBusy = false
    }
}