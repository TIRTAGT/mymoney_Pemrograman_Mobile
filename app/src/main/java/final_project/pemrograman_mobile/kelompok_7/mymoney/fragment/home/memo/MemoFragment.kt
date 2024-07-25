package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.memo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import final_project.pemrograman_mobile.kelompok_7.mymoney.MainActivity
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Memo
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.MonefyDatabase
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Transaction
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.TransactionType
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeMenuMemoBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.HomeFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.HomeFragmentEvent
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.MessageFormat
import java.time.ZonedDateTime

class MemoFragment : Fragment(), HomeFragmentEvent, MemoItemClicked {
    private lateinit var frontend: FragmentHomeMenuMemoBinding
    private lateinit var database: MonefyDatabase
    private lateinit var listAdapter: MemoHeaderItemListAdapter
    private lateinit var context: Context
    private lateinit var parentFragment: HomeFragment
    private lateinit var parentActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = MonefyDatabase.require()
        listAdapter = MemoHeaderItemListAdapter()
        listAdapter.setOnClickListener(this)
        parentFragment = getParentFragment() as HomeFragment
        parentFragment.addCustomEventListener(this)
        parentActivity = requireActivity() as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        frontend = FragmentHomeMenuMemoBinding.inflate(inflater, container, false)
        return frontend.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.frontend.fragmentHomeMenuMemoRecyclerview.layoutManager = LinearLayoutManager(this.context)
        this.frontend.fragmentHomeMenuMemoRecyclerview.adapter = listAdapter
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        parentFragment.setSummaryAmountVisibility(false)
        parentFragment.setFabVisibility(true)

        MainScope().launch(Dispatchers.IO) { fetchMemo(parentFragment.getCurrentPagination()) }
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

        fetchMemo(newDate)
    }

    @WorkerThread
    private fun fetchMemo(pagination: ZonedDateTime) {
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

        val memos = this.database.memos().selectTimestampPagination(UtilityFunctions.fromZonedDateTime(from), UtilityFunctions.fromZonedDateTime(to))

        // FIXME: Why is this a HashMap, instead of a 2D Array?
        val pair = ArrayList<ArrayList<Memo>>()
        var latestPairKey = ""

        for (memo in memos) {
            val calendar = UtilityFunctions.fromInstant(memo.createdAtTimestamp)
            val pairKey = MessageFormat.format("{0}-{1}-{2}", *arrayOf(calendar.dayOfMonth, ( calendar.monthValue), calendar.year.toString()))

            if (pairKey == latestPairKey) {
                pair.last().add(memo)
                continue
            }
            latestPairKey = pairKey

            val newPair = ArrayList<Memo>()
            newPair.add(memo)
            pair.add(newPair)
        }

        MainScope().launch(Dispatchers.Main) {
            listAdapter.setDataset(pair)
            listAdapter.notifyDataSetChanged()
        }
    }

    override fun onMemoItemClicked(memo: Memo) {
        val data = Bundle()
        data.putInt("memo_id", memo.id)

        parentActivity.switchFragment(AddMemo::class, 1, data)
    }
}