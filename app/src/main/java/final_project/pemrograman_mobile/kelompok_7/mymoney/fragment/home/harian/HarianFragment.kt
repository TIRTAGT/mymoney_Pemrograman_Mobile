package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.harian

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
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeMenuHarianBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.HomeFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.HomeFragmentEvent
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.TransactionHeaderListAdapter
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.MessageFormat
import java.time.ZonedDateTime

class HarianFragment : Fragment(), HomeFragmentEvent {
    private lateinit var frontend: FragmentHomeMenuHarianBinding
    private lateinit var database: MonefyDatabase
    private lateinit var listAdapter: TransactionHeaderListAdapter
    private lateinit var context: Context
    private lateinit var parentFragment: HomeFragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = MonefyDatabase.require()
        listAdapter = TransactionHeaderListAdapter(this.context)
        parentFragment = getParentFragment() as HomeFragment
        parentFragment.addCustomEventListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        frontend = FragmentHomeMenuHarianBinding.inflate(inflater, container, false)
        return frontend.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.frontend.fragmentHomeMenuHarianRecyclerview.layoutManager = LinearLayoutManager(this.context)
        this.frontend.fragmentHomeMenuHarianRecyclerview.adapter = listAdapter
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

        fetchTransactions(newDate)
    }

    @WorkerThread
    private fun fetchTransactions(pagination: ZonedDateTime) {
        fun cleanUpForPagination(a: ZonedDateTime, isTo: Boolean): ZonedDateTime {
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
        var from = UtilityFunctions.deepCopyZonedDateTime(pagination)
        from = cleanUpForPagination(from, false)

        var to = UtilityFunctions.deepCopyZonedDateTime(pagination)
        to = cleanUpForPagination(to, true)

        val transactions = this.database.transactions().HomePagination(UtilityFunctions.fromZonedDateTime(from), UtilityFunctions.fromZonedDateTime(to))

        // FIXME: Why is this a HashMap, instead of a 2D Array?
        val pair = HashMap<String, ArrayList<Transaction>>()
        var incomeTotal = 0.0
        var outcomeTotal = 0.0

        for (transaction in transactions) {
            val calendar = UtilityFunctions.fromInstant(transaction.createdAtTimestamp)

            val mapKey = MessageFormat.format("{0}-{1}-{2}", *arrayOf(calendar.dayOfMonth, ( calendar.monthValue), calendar.year.toString()))

            var b = pair[mapKey]
            if (b == null) { b = ArrayList() }
            b.add(transaction)

            pair[mapKey] = b

            if (transaction.type == TransactionType.INCOME) { incomeTotal += transaction.amount }
            else if (transaction.type == TransactionType.OUTCOME) { outcomeTotal += transaction.amount }
        }

        MainScope().launch(Dispatchers.Main) {
            listAdapter.setDataset(pair)
            listAdapter.notifyDataSetChanged()

            parentFragment.updateSummaryAmount(incomeTotal, outcomeTotal)
        }
    }
}