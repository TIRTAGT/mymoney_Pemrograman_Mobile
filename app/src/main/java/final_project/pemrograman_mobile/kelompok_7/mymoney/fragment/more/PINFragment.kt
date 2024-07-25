package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.MainActivity
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.MonefyDatabase
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentMoreBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentMorePinBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PINFragment : Fragment() {
	private lateinit var frontend: FragmentMorePinBinding
	private lateinit var pin_storage: StringBuilder
	private lateinit var temp_pin_storage: StringBuilder
	private var readOnlyStorage: Boolean = true
	private var confirmPinAgain: Boolean = false
	private lateinit var activity: MainActivity
	private  var listener: OnPINFragmentResponse? = null

	interface OnPINFragmentResponse {
		fun run(pin: String)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		this.activity = requireActivity() as MainActivity
		pin_storage = StringBuilder()

		val arg = arguments
		if (arg != null) {
			readOnlyStorage = !arg.getBoolean("write_enabled", false)
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		frontend = FragmentMorePinBinding.inflate(inflater, container, false)
		return frontend.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (readOnlyStorage) {
			frontend.textView8.text = "PIN diperlukan untuk masuk"
		}
		else {
			frontend.textView8.text = "Atur PIN"
		}

		frontend.fragmentPin0.setOnClickListener { addStorage('0') }
		frontend.fragmentPin1.setOnClickListener { addStorage('1') }
		frontend.fragmentPin2.setOnClickListener { addStorage('2') }
		frontend.fragmentPin3.setOnClickListener { addStorage('3') }
		frontend.fragmentPin4.setOnClickListener { addStorage('4') }
		frontend.fragmentPin5.setOnClickListener { addStorage('5') }
		frontend.fragmentPin6.setOnClickListener { addStorage('6') }
		frontend.fragmentPin7.setOnClickListener { addStorage('7') }
		frontend.fragmentPin8.setOnClickListener { addStorage('8') }
		frontend.fragmentPin9.setOnClickListener { addStorage('9') }
		frontend.fragmentPinOk.setOnClickListener { onOkPressed() }
		frontend.fragmentPinDel.setOnClickListener { onDeletePressed() }
	}

	override fun onStart() {
		super.onStart()
	}

	override fun onResume() {
		super.onResume()

		activity.setBottomNavVisibility(false)
	}

	override fun onPause() {
		super.onPause()
	}

	override fun onStop() {
		super.onStop()

		activity.setBottomNavVisibility(true)
	}

	override fun onDestroy() {
		super.onDestroy()
	}

	private fun addStorage(char: Char) {
		this.pin_storage.append(char)
		syncStorageToUI()
	}

	private fun syncStorageToUI() {
		this.frontend.editTextNumber.setText(this.pin_storage)
	}

	private fun onOkPressed() {
		// Jika dalam mode menyimpan
		if (!this.readOnlyStorage) {
			if (!this.confirmPinAgain) {
				this.temp_pin_storage = StringBuilder(this.pin_storage)
				this.pin_storage.clear()
				this.frontend.editTextNumber.setText("")
				this.frontend.textView8.setText("Konfirmasi Atur PIN")
				this.confirmPinAgain = true
				return
			}

			val konfirmasiBenar = this.pin_storage.toString() == this.temp_pin_storage.toString()

			if (!konfirmasiBenar) {
				this.temp_pin_storage.clear()
				frontend.textView8.setText("Masukan PIN")
				this.confirmPinAgain = false
				return
			}

			val targetPin = this.temp_pin_storage.toString()

			MainScope().launch(Dispatchers.IO) {
				val a = MonefyDatabase.require()
				val b = a.appSettings().select()
				b.pin = targetPin

				a.appSettings().update(b)

				MainScope().launch(Dispatchers.Main) { onOkPressedComplete() }
			}

			return
		}

		val targetListener = this.listener ?: return

		targetListener.run(this.pin_storage.toString())
	}

	@MainThread
	private fun onOkPressedComplete() {
		Toast.makeText(context, "PIN sukses diatur", Toast.LENGTH_LONG).show()
		activity.onBackButtonPressed()
	}

	private fun onDeletePressed() {
		val a = this.pin_storage.lastIndex

		if (a == -1) { return }

		this.pin_storage.deleteCharAt(this.pin_storage.lastIndex)
		this.syncStorageToUI()
	}

	fun setListener(a: OnPINFragmentResponse) {
		this.listener = a
	}

	fun triggerInvalidPINMessage() {
		this.pin_storage.clear()
		this.frontend.editTextNumber.setText("")
		Toast.makeText(context, "PIN salah, coba lagi", Toast.LENGTH_LONG).show()
	}
}