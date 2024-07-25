package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.tutupbuku

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeTutupBukuSettingsDialogBinding

class AnggaranSettingDialog : DialogFragment() {
    lateinit var frontend: FragmentHomeTutupBukuSettingsDialogBinding
    var amount: Double = 0.00
    private var listener: Event? = null
    private var isButtonEvent: Boolean = false

    interface Event {
        fun onDismiss() {}
        fun onCancel() {}
        fun onSelected(amount: Double) {}
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        frontend = FragmentHomeTutupBukuSettingsDialogBinding.inflate(inflater, container, false)
        return frontend.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.frontend.fragmentHomeTutupBukuSettingsEdittext.setText(amount.toString())

        this.frontend.fragmentHomeTutupBukuSettingsEdittext.addTextChangedListener(object:  TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                try {
                    amount = s.toString().toDouble()
                }
                catch (ignored: NumberFormatException) {}
            }
        })

        this.frontend.button4.setOnClickListener {
            isButtonEvent = true
            listener?.onCancel()
            this.dismiss()
        }
        this.frontend.button5.setOnClickListener {
            isButtonEvent= true
            listener?.onSelected(this.amount)
            this.dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (!isButtonEvent) {
            listener?.onDismiss()
            isButtonEvent = false
        }

        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    fun addEventListener(listener: Event) {
        this.listener = listener
    }
}