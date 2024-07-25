package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.bulanan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.MonefyDatabase
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Transaction
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.TransactionType
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeMenuBulananBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeMenuHarianBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.HomeFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.HomeFragmentEvent
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.TransactionHeaderListAdapter
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.launch
import java.sql.Time
import java.text.MessageFormat
import java.time.ZonedDateTime
import java.util.Calendar

class BulananFragment : Fragment(), HomeFragmentEvent {
    private lateinit var frontend: FragmentHomeMenuBulananBinding
    private lateinit var database: MonefyDatabase
    private lateinit var listAdapter: BulananItemListAdapter
    private lateinit var context: Context
    private lateinit var parentFragment: HomeFragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = MonefyDatabase.require()
        listAdapter = BulananItemListAdapter()
        parentFragment = getParentFragment() as HomeFragment
        parentFragment.addCustomEventListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        frontend = FragmentHomeMenuBulananBinding.inflate(inflater, container, false)
        return frontend.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.frontend.fragmentHomeMenuBulananRecyclerview.layoutManager = LinearLayoutManager(this.context)
        this.frontend.fragmentHomeMenuBulananRecyclerview.adapter = listAdapter
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        parentFragment.setSummaryAmountVisibility(true)
        parentFragment.setFabVisibility(true)

        MainScope().launch(Dispatchers.IO) { fetchTransactions(parentFragment.getCurrentPagination()) }
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

        frontend.fragmentHomeMenuBulananRecyclerview.visibility = View.VISIBLE
        frontend.loadingTextview.visibility = View.GONE

        fetchTransactions(newDate)
    }

    @WorkerThread
    private fun fetchTransactions(pagination: ZonedDateTime) {
        fun cleanUpForPagination(a: ZonedDateTime, b: Int, isTo: Boolean): ZonedDateTime {
            var value = a

            if (isTo) {
                value = value.withMonth(b)
                value = value.withDayOfMonth(1)
                value = value.plusMonths(1)
                value = value.minusDays(2)

                value = value.withHour(23)
                value = value.withMinute(59)
                value = value.withSecond(59)
            }
            else {
                value = value.withMonth(b)
                value = value.withDayOfMonth(1)
                value = value.withHour(0)
                value = value.withMinute(0)
                value = value.withSecond(0)
            }

            return value
        }

        val initFunction: (i: Int) -> BulananItemList = { BulananItemList() }
        val pair = Array(12, initFunction)
        var yearTotalIncome = 0.0
        var yearTotalOutcome = 0.0

        for(i in 0..11) {
            var from = UtilityFunctions.deepCopyZonedDateTime(pagination)
            from = cleanUpForPagination(from, i + 1, false)

            var to = UtilityFunctions.deepCopyZonedDateTime(pagination)
            to = cleanUpForPagination(to, i + 1, true)

            val transactions = this.database.transactions().HomePagination(UtilityFunctions.fromZonedDateTime(from), UtilityFunctions.fromZonedDateTime(to))

            val data = BulananItemList()

            for (transaction in transactions) {
                if (transaction.type == TransactionType.INCOME) {
                    data.incomeTotal += transaction.amount
                }
                else if (transaction.type == TransactionType.OUTCOME) {
                    data.outcomeTotal += transaction.amount
                }
            }

            yearTotalIncome += data.incomeTotal
            yearTotalOutcome += data.outcomeTotal
            pair[i] = data
        }

        MainScope().launch(Dispatchers.Main) {
            listAdapter.setDataset(pair)
            listAdapter.notifyDataSetChanged()

            parentFragment.updateSummaryAmount(yearTotalIncome, yearTotalOutcome)
            frontend.fragmentHomeMenuBulananRecyclerview.visibility = View.VISIBLE
            frontend.loadingTextview.visibility = View.GONE
        }
    }
}