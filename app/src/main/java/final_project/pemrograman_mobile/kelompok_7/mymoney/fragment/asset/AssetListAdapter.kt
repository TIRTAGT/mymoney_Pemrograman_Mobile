package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.asset

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Asset
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentAssetsListviewBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions

class AssetListAdapter : RecyclerView.Adapter<AssetListAdapter.ViewHolder>() {
    private var localDataset: Array<Asset> = emptyArray()
    private var onCLickListener: EventListener? = null

    interface EventListener {
        fun onCategoryItemDeleteIconClicked(id: Int)
    }

    class ViewHolder(a: FragmentAssetsListviewBinding) : RecyclerView.ViewHolder(a.root) {
        var frontend: FragmentAssetsListviewBinding = a
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val a = FragmentAssetsListviewBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(a)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val a = this.localDataset[position]

        holder.frontend.fragmentAssetsListviewTitle.text = a.name
        holder.frontend.fragmentAssetsListviewBalance.text = UtilityFunctions.doubleToString(a.balance)
        holder.frontend.imageView4.setOnClickListener {
            onCLickListener?.onCategoryItemDeleteIconClicked(a.id)
        }
    }

    override fun getItemCount(): Int {
        return this.localDataset.size
    }

    fun setDataset(dataset: Array<Asset>) {
        this.localDataset = dataset
    }

    fun setOnClickListener(listener: EventListener) {
        this.onCLickListener = listener
    }
}