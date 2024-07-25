package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import final_project.pemrograman_mobile.kelompok_7.mymoney.R
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Transaction
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.TransactionType
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeMenuHarianTransactionEntryItemBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeMenuHarianTransactionHeaderItemBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.Constants
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions
import java.text.MessageFormat
import java.util.Calendar

class TransactionHeaderListAdapter(parentContext: Context) : RecyclerView.Adapter<TransactionHeaderListAdapter.ViewHolder>() {
    private var localDataset: HashMap<String, ArrayList<Transaction>> = HashMap()
    private var localDatasetKeys: Array<String> = arrayOf()
    private val context = parentContext

    class ViewHolder(parentContext: Context, a: FragmentHomeMenuHarianTransactionHeaderItemBinding) : RecyclerView.ViewHolder(a.root) {
        var frontend: FragmentHomeMenuHarianTransactionHeaderItemBinding = a
        private var listAdapter: TransactionEntryListAdapter = TransactionEntryListAdapter(parentContext)

        init {
            frontend.fragmentHomeTransactionItemTransactionsRecyclerview.layoutManager = LinearLayoutManager(a.root.context)
            frontend.fragmentHomeTransactionItemTransactionsRecyclerview.adapter = listAdapter
        }

        fun setDataset(data: ArrayList<Transaction>) {
            listAdapter.setDataset(data)
            listAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val a = FragmentHomeMenuHarianTransactionHeaderItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(context, a)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = this.localDatasetKeys[position]
        val a = this.localDataset[key]

        if (a.isNullOrEmpty()) return

        val calendar = UtilityFunctions.fromInstant(a[0].createdAtTimestamp)

        var totalIncome = 0.0
        var totalOutcome = 0.0

        for (transaction in a) {
            if (transaction.type == TransactionType.INCOME) {
                totalIncome += transaction.amount
            }
            else {
                totalOutcome += transaction.amount
            }
        }

        holder.frontend.fragmentHomeTransactionItemHeaderDate.text = calendar.dayOfMonth.toString().padStart(2, '0')
        holder.frontend.fragmentHomeTransactionItemHeaderDayOfWeekName.text = Constants.stringHariPendek(calendar.dayOfWeek.ordinal)
        holder.frontend.fragmentHomeTransactionItemHeaderMonthYearText.text = MessageFormat.format("{0}.{1}", *arrayOf((calendar.monthValue).toString().padStart(2, '0'), calendar.year.toString()))
        holder.frontend.fragmentHomeTransactionItemHeaderIncomeTotal.text = UtilityFunctions.doubleToString(totalIncome)
        holder.frontend.fragmentHomeTransactionItemHeaderOutcomeTotal.text = UtilityFunctions.doubleToString(totalOutcome)

        holder.setDataset(a)
    }

    override fun getItemCount(): Int { return this.localDataset.size }

    public fun setDataset(dataset: HashMap<String, ArrayList<Transaction>>) {
        this.localDataset = dataset
        this.localDatasetKeys = this.localDataset.keys.toTypedArray<String>()
    }
}

class TransactionEntryListAdapter(context: Context)  : RecyclerView.Adapter<TransactionEntryListAdapter.ViewHolder>() {
    private var localDataset: ArrayList<Transaction> = ArrayList()
    @ColorInt private var incomeColor = context.getColor(R.color.light_blue)
    @ColorInt private var outcomeColor = context.getColor(R.color.red)

    class ViewHolder(a: FragmentHomeMenuHarianTransactionEntryItemBinding) : RecyclerView.ViewHolder(a.root) {
        var frontend: FragmentHomeMenuHarianTransactionEntryItemBinding = a
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val a = FragmentHomeMenuHarianTransactionEntryItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(a)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val a = this.localDataset[position]

        holder.frontend.fragmentHomeTransactionEntryTitle.text = a.name
        holder.frontend.fragmentHomeTransactionEntryAmount.text = UtilityFunctions.doubleToString(a.amount)
        holder.frontend.fragmentHomeTransactionEntryCategoryName.text = "" // TODO: Fix this lol

        if (a.type == TransactionType.INCOME) {
            holder.frontend.fragmentHomeTransactionEntryAmount.setTextColor(incomeColor)
        }
        else if (a.type == TransactionType.OUTCOME) {
            holder.frontend.fragmentHomeTransactionEntryAmount.setTextColor(outcomeColor)
        }
    }

    override fun getItemCount(): Int { return this.localDataset.size }

    fun setDataset(dataset: ArrayList<Transaction>) {
        this.localDataset = dataset
    }
}