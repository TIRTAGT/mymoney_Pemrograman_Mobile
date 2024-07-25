package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentAssetsBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentStatsBinding

class StatsFragment() : Fragment() {
	private lateinit var frontend: FragmentStatsBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		frontend = FragmentStatsBinding.inflate(inflater, container, false)
		return frontend.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
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