package final_project.pemrograman_mobile.kelompok_7.mymoney.utility

import java.text.MessageFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar

class UtilityFunctions {
    companion object {
        public fun doubleToString(value: Double): String {
            return MessageFormat.format("Rp. {0}", *arrayOf(value))
        }

        public fun deepCopyCalendar(value: Calendar?): Calendar? {
            return CustomTypeConverter.instance.toCalendar(CustomTypeConverter.instance.fromCalendar(value))
        }

        /** From Instant (UTC) to system-default timezone */
        fun fromInstant(a: Instant): ZonedDateTime {
            return a.atZone(ZoneId.systemDefault())
        }

        /** From system-default timezone to Instant (UTC) */
        fun fromZonedDateTime(a: ZonedDateTime): Instant {
            return a.toInstant()
        }

        /** Utility function to make a deep copy of a ZonedDateTime */
        fun deepCopyZonedDateTime(a: ZonedDateTime): ZonedDateTime {
            return this.fromInstant(this.fromZonedDateTime(a))
        }
    }
}