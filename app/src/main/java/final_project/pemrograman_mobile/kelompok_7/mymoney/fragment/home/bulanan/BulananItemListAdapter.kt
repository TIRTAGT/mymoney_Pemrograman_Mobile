package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.bulanan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeMenuBulananListitemBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.Constants
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions

class BulananItemList {
	var incomeTotal: Double = 0.0
	var outcomeTotal: Double = 0.0
}

class BulananItemListAdapter : RecyclerView.Adapter<BulananItemListAdapter.ViewHolder>() {
	private var localDataset: Array<BulananItemList> = arrayOf()

	class ViewHolder(a: FragmentHomeMenuBulananListitemBinding) : RecyclerView.ViewHolder(a.root) {
		var frontend: FragmentHomeMenuBulananListitemBinding = a
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val layoutInflater = LayoutInflater.from(parent.context)
		val a = FragmentHomeMenuBulananListitemBinding.inflate(layoutInflater, parent, false)

		return ViewHolder(a)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val a = this.localDataset[position]

		holder.frontend.fragmentHomeMenuBulananListitemMonthName.text = Constants.stringBulan[position]
		holder.frontend.fragmentHomeMenuBulananListitemIncomeTotal.text = UtilityFunctions.doubleToString(a.incomeTotal)
		holder.frontend.fragmentHomeMenuBulananListitemOutcomeTotal.text = UtilityFunctions.doubleToString(a.outcomeTotal)
	}

	override fun getItemCount(): Int { return this.localDataset.size }

	fun setDataset(dataset: Array<BulananItemList>) {
		this.localDataset = dataset
	}
}