package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.asset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import final_project.pemrograman_mobile.kelompok_7.mymoney.MainActivity
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Asset
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.MonefyDatabase
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentAssetsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZonedDateTime

class AssetFragment : Fragment() {
	private lateinit var frontend: FragmentAssetsBinding
	private lateinit var listAdapter: AssetListAdapter
	private lateinit var parentActivity: MainActivity
	private lateinit var db: MonefyDatabase

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		this.listAdapter = AssetListAdapter()
		this.parentActivity = requireActivity() as MainActivity
		this.db = MonefyDatabase.require()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		frontend = FragmentAssetsBinding.inflate(inflater, container, false)
		return frontend.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		this.listAdapter.setOnClickListener(object : AssetListAdapter.EventListener {
			override fun onCategoryItemDeleteIconClicked(id: Int) {
				MainScope().launch(Dispatchers.IO) {
					deleteAsset(id)
				}
			}
		})
		this.frontend.recyclerView1.layoutManager = LinearLayoutManager(this.context)
		this.frontend.recyclerView1.adapter = this.listAdapter

		this.frontend.button1.setOnClickListener {
			val name: String = this.frontend.editText1.text.toString()

			if (name.isBlank()) {
				Toast.makeText(this.context, "Nama aset tidak boleh kosong", Toast.LENGTH_SHORT).show()
				return@setOnClickListener
			}

			MainScope().launch(Dispatchers.IO) {
				insertAsset(name)
			}
		}
	}

	override fun onStart() {
		super.onStart()
	}

	override fun onResume() {
		super.onResume()

		MainScope().launch(Dispatchers.IO) { fetchAssets() }
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

	@WorkerThread
	fun insertAsset(name: String) {
		val a = Asset(0, name, 0.00, Instant.now())

		db.assets().insert(a)

		MainScope().launch(Dispatchers.Main) {
			frontend.editText1.isEnabled = false
			frontend.editText1.setText("")
			frontend.editText1.isEnabled = true
		}

		fetchAssets()
	}

	@WorkerThread
	fun fetchAssets() {
		val results = db.assets().selectAll()
		val data = arrayOfNulls<Asset>(results.size)

		for (i in results.indices) {
			data[i] = results[i]
		}

		MainScope().launch(Dispatchers.Main) {
			listAdapter.setDataset(results.toTypedArray())
			listAdapter.notifyDataSetChanged()
		}
	}

	@WorkerThread
	fun deleteAsset(id: Int) {
		if (id <= MonefyDatabase.INITIAL_ASSETS_SIZE) { return }

		db.assets().deleteById(id)

		fetchAssets()
	}
}