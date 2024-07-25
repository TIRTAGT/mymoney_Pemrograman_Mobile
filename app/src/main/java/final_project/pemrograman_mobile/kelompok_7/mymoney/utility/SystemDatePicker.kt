package final_project.pemrograman_mobile.kelompok_7.mymoney.utility

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.ZonedDateTime

class SystemDatePicker : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var listener: Callback? = null
    private lateinit var calendar: ZonedDateTime

    interface Callback {
        fun onSystemDatePickerSet(view: DatePicker, year: Int, month: Int, day: Int)
        fun onSystemDatePickerDismissed()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(requireContext(), this, calendar.year, calendar.month.ordinal, calendar.dayOfMonth)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val a = listener ?: return
        a.onSystemDatePickerDismissed()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val a = listener ?: return

        calendar = calendar.withYear(year)
        calendar = calendar.withMonth(month + 1)
        calendar = calendar.withDayOfMonth(day)

        a.onSystemDatePickerSet(view, year, month, day)
    }

    fun setListener(newListener: Callback) {
        listener = newListener
    }

    fun setInitialDate(a: ZonedDateTime) {
        this.calendar = a
    }
}