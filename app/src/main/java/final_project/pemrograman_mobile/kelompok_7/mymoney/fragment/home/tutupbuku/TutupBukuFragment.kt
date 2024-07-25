package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.tutupbuku

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Anggaran
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.MonefyDatabase
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.TransactionType
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeMenuTutupBukuBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.HomeFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.HomeFragmentEvent
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZonedDateTime

class TutupBukuFragment : Fragment(), HomeFragmentEvent {
    private lateinit var frontend: FragmentHomeMenuTutupBukuBinding
    private lateinit var database: MonefyDatabase
    private lateinit var context: Context
    private lateinit var parentFragment: HomeFragment
    private lateinit var anggaranDialog: AnggaranSettingDialog
    private var anggaranObject: Anggaran? = null
    private var currentSpendingAmount: Double = 0.00

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = MonefyDatabase.require()
        parentFragment = getParentFragment() as HomeFragment
        parentFragment.addCustomEventListener(this)
        anggaranDialog = AnggaranSettingDialog()
        anggaranDialog.addEventListener(object: AnggaranSettingDialog.Event {
            override fun onSelected(amount: Double) {
                super.onSelected(amount)

                MainScope().launch(Dispatchers.IO) { updateLimitAnggaran(parentFragment.getCurrentPagination(), amount) }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        frontend = FragmentHomeMenuTutupBukuBinding.inflate(inflater, container, false)
        return frontend.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.frontend.textView5.setOnClickListener {
            val a = this.anggaranObject
            if (a != null) {
                this.anggaranDialog.amount = a.amount
            }

            this.anggaranDialog.show(childFragmentManager, AnggaranSettingDialog::class.simpleName)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        parentFragment.setSummaryAmountVisibility(true)
        parentFragment.setFabVisibility(true)

        MainScope().launch(Dispatchers.IO) {
            val a = parentFragment.getCurrentPagination()
            fetchTotalSummary(a)
            getLimitAnggaran(a)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        parentFragment.removeCustomEventListener(this)
        super.onDestroy()
    }

    override fun onDatePaginationChanged(newDate: ZonedDateTime) {
        super.onDatePaginationChanged(newDate)
        fetchTotalSummary(newDate)
        getLimitAnggaran(newDate)
    }

    @WorkerThread
    private fun fetchTotalSummary(pagination: ZonedDateTime) {
        var from = UtilityFunctions.deepCopyZonedDateTime(pagination)
        from = cleanUpForPagination(from, false)

        var to = UtilityFunctions.deepCopyZonedDateTime(pagination)
        to = cleanUpForPagination(to, true)

        val transactions = this.database.transactions().HomePagination(UtilityFunctions.fromZonedDateTime(from), UtilityFunctions.fromZonedDateTime(to))

        var incomeTotal = 0.0
        this.currentSpendingAmount = 0.0

        for (transaction in transactions) {
            if (transaction.type == TransactionType.INCOME) { incomeTotal += transaction.amount }
            else if (transaction.type == TransactionType.OUTCOME) { this.currentSpendingAmount += transaction.amount }
        }

        MainScope().launch(Dispatchers.Main) {
            frontend.textView10.text = UtilityFunctions.doubleToString(incomeTotal)
            parentFragment.updateSummaryAmount(incomeTotal, currentSpendingAmount)
        }
    }

    @MainThread
    fun hitungAnggaran() {
        val a = this.anggaranObject
        var anggaranAmount = 0.00
        if (a != null) {
            anggaranAmount = a.amount
        }

        var usedPercent = this.currentSpendingAmount / anggaranAmount
        if (usedPercent > 1) { usedPercent = 1.0 }
        else if (usedPercent.isNaN()) { usedPercent = 0.0 }

        val remainingPercent = 1.0 - usedPercent

        this.frontend.textView7.text = UtilityFunctions.doubleToString(anggaranAmount)
        this.frontend.textView10.text = UtilityFunctions.doubleToString(this.currentSpendingAmount)
        this.frontend.textView11.text = UtilityFunctions.doubleToString(anggaranAmount - this.currentSpendingAmount)

        val usedLayout = this.frontend.frontendHomeMenuTutupBukuUsedBar.layoutParams as LinearLayout.LayoutParams
        usedLayout.weight = usedPercent.toFloat()
        this.frontend.frontendHomeMenuTutupBukuUsedBar.layoutParams = usedLayout

        val remainingLayout = this.frontend.frontendHomeMenuTutupBukuRemainingBar.layoutParams as LinearLayout.LayoutParams
        remainingLayout.weight = remainingPercent.toFloat()
        this.frontend.frontendHomeMenuTutupBukuRemainingBar.layoutParams = remainingLayout
    }

    @WorkerThread
    private fun getLimitAnggaran(pagination: ZonedDateTime) {
        var from = UtilityFunctions.deepCopyZonedDateTime(pagination)
        from = cleanUpForPagination(from, false)

        var to = UtilityFunctions.deepCopyZonedDateTime(pagination)
        to = cleanUpForPagination(to, true)

        val results = this.database.anggaran().selectTimestampPagination(UtilityFunctions.fromZonedDateTime(from), UtilityFunctions.fromZonedDateTime(to))
        anggaranObject = null

        if (results.size == 1) {
            anggaranObject = results[0]
        }


        MainScope().launch(Dispatchers.Main) { hitungAnggaran() }
    }

    @WorkerThread
    private fun updateLimitAnggaran(pagination: ZonedDateTime, amount: Double) {
        var from = UtilityFunctions.deepCopyZonedDateTime(pagination)
        from = cleanUpForPagination(from, false)

        var to = UtilityFunctions.deepCopyZonedDateTime(pagination)
        to = cleanUpForPagination(to, true)

        val a = this.database.anggaran().selectTimestampPagination(UtilityFunctions.fromZonedDateTime(from), UtilityFunctions.fromZonedDateTime(to))
        if (a.size != 1) {
            val data = Anggaran(0, amount, Instant.now())
            this.database.anggaran().insert(data)

            getLimitAnggaran(pagination)
            return
        }

        val data = a[0]
        data.amount = amount

        this.database.anggaran().update(data)
        getLimitAnggaran(pagination)
    }

    private fun cleanUpForPagination(a: ZonedDateTime, isTo: Boolean): ZonedDateTime {
        var value = a

        if (isTo) {
            value = value.withDayOfMonth(1)
            value = value.plusMonths(1)
            value = value.minusDays(1)

            value = value.withHour(23)
            value = value.withMinute(59)
            value = value.withSecond(59)
        }
        else {
            value = value.withDayOfMonth(1)
            value = value.withHour(0)
            value = value.withMinute(0)
            value = value.withSecond(0)
        }

        return value
    }
}