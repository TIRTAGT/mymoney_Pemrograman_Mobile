package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.memo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Memo
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeMenuMemoListItemContentBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeMenuMemoListItemHeaderBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.Constants
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions
import java.text.MessageFormat

class MemoHeaderItemListAdapter : RecyclerView.Adapter<MemoHeaderItemListAdapter.ViewHolder>(), MemoItemClicked {
    private var localDataset: ArrayList<ArrayList<Memo>> = ArrayList()
    private var onCLickListener: MemoItemClicked? = null

    class ViewHolder(a: MemoItemClicked, b: FragmentHomeMenuMemoListItemHeaderBinding) : RecyclerView.ViewHolder(b.root) {
        var frontend: FragmentHomeMenuMemoListItemHeaderBinding = b
        private var listAdapter: MemoContentItemListAdapter = MemoContentItemListAdapter(a)

        init {
            this.frontend.fragmentHomeMenuMemoListItemHeaderRecyclerview.layoutManager = LinearLayoutManager(b.root.context)
            this.frontend.fragmentHomeMenuMemoListItemHeaderRecyclerview.adapter = listAdapter
        }

        fun setDataset(data: ArrayList<Memo>) {
            listAdapter.setDataset(data)
            listAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val a = FragmentHomeMenuMemoListItemHeaderBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(this, a)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val a = this.localDataset[position]

        if (a.isEmpty()) { return }
        val sampleData = a[0]

        val time = UtilityFunctions.fromInstant(sampleData.createdAtTimestamp)

        holder.frontend.fragmentHomeMenuMemoListItemHeaderDate.text = MessageFormat.format("{0}.{1} ({2})", *arrayOf(time.dayOfMonth, time.month.value, Constants.stringHariPendek(time.dayOfWeek.ordinal)))
        holder.setDataset(a)
    }

    override fun getItemCount(): Int { return this.localDataset.size }

    fun setDataset(dataset: ArrayList<ArrayList<Memo>>) {
        this.localDataset = dataset
    }

    fun setOnClickListener(listener: MemoItemClicked) {
        this.onCLickListener = listener
    }

    override fun onMemoItemClicked(memo: Memo) {
        val a = this.onCLickListener ?: return

        a.onMemoItemClicked(memo)
    }
}

class MemoContentItemListAdapter(a: MemoItemClicked) : RecyclerView.Adapter<MemoContentItemListAdapter.ViewHolder>() {
    private val listener = a
    private var localDataset: ArrayList<Memo> = ArrayList()

    class ViewHolder(a: FragmentHomeMenuMemoListItemContentBinding) : RecyclerView.ViewHolder(a.root) {
        var frontend: FragmentHomeMenuMemoListItemContentBinding = a
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val a = FragmentHomeMenuMemoListItemContentBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(a)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val a = this.localDataset[position]

        holder.frontend.linearLayout1.setOnClickListener {
            listener.onMemoItemClicked(this.localDataset[position])
        }
        holder.frontend.textView.text = a.title
        holder.frontend.textView3.text = a.content
    }

    override fun getItemCount(): Int { return this.localDataset.size }

    fun setDataset(dataset: ArrayList<Memo>) {
        this.localDataset = dataset
    }
}

interface MemoItemClicked {
    fun onMemoItemClicked(memo: Memo)
}