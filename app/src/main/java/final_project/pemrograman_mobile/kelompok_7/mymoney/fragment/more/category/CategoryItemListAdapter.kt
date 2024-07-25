package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.more.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Category
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentMoreMenuCategoryListitemBinding

class CategoryItemListAdapter : RecyclerView.Adapter<CategoryItemListAdapter.ViewHolder>() {
    private var localDataset: ArrayList<Category> = ArrayList()
    private var onCLickListener: EventListener? = null

    interface EventListener {
        fun onCategoryItemDeleteIconClicked(id: Int)
    }

    class ViewHolder(a: FragmentMoreMenuCategoryListitemBinding) : RecyclerView.ViewHolder(a.root) {
        var frontend: FragmentMoreMenuCategoryListitemBinding = a
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val a = FragmentMoreMenuCategoryListitemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(a)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val a = this.localDataset[position]

        holder.frontend.textView1.text = a.name
        holder.frontend.imageView1.setOnClickListener {
            val listener = this.onCLickListener ?: return@setOnClickListener

            listener.onCategoryItemDeleteIconClicked(a.id)
        }
    }

    override fun getItemCount(): Int { return this.localDataset.size }

    fun setDataset(dataset: ArrayList<Category>) {
        this.localDataset = dataset
    }

    fun setOnClickListener(listener: EventListener) {
        this.onCLickListener = listener
    }
}