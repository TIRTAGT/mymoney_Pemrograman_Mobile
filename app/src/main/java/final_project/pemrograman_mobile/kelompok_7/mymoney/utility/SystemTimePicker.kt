package final_project.pemrograman_mobile.kelompok_7.mymoney.utility

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.time.ZonedDateTime

class SystemTimePicker : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private var listener: Callback? = null
    private lateinit var calendar: ZonedDateTime

    interface Callback {
        fun onSystemTimePickerSet(view: TimePicker, hourOfDay: Int, minute: Int)
        fun onSystemTimePickerDismissed()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TimePickerDialog(activity, this, calendar.hour, calendar.minute, DateFormat.is24HourFormat(activity))
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val a = listener ?: return
        a.onSystemTimePickerDismissed()
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val a = listener ?: return

        a.onSystemTimePickerSet(view, hourOfDay, minute)
    }

    fun setListener(newListener: Callback) {
        listener = newListener
    }
    fun setInitialTime(a: ZonedDateTime) {
        this.calendar = a
    }
}