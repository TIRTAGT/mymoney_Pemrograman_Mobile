package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.MainActivity
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentMoreBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.asset.AssetFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.StatsFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.more.category.CategoryFragment

class MoreFragment : Fragment() {
	private lateinit var frontend: FragmentMoreBinding
	private lateinit var activity: MainActivity

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		activity = requireActivity() as MainActivity
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		frontend = FragmentMoreBinding.inflate(inflater, container, false)
		return frontend.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		this.frontend.fragmentMoreCategoryMenu.setOnClickListener {
			activity.switchFragment(CategoryFragment::class, 1)
		}

		this.frontend.fragmentMoreAssetMenu.setOnClickListener { activity.switchFragment(
            AssetFragment::class, 0) }
		this.frontend.fragmentMoreStatsMenu.setOnClickListener { activity.switchFragment(StatsFragment::class, 0) }

		this.frontend.fragmentMorePinMenu.setOnClickListener {
			val data = Bundle()
			data.putBoolean("write_enabled", true)
			activity.switchFragment(PINFragment::class, 1, data)
		}
	}

	override fun onStart() {
		super.onStart()
	}

	override fun onResume() {
		super.onResume()
	}

	override fun onPause() {
		super.onPause()
	}

	override fun onStop() {
		super.onStop()
	}

	override fun onDestroy() {
		super.onDestroy()
	}
}