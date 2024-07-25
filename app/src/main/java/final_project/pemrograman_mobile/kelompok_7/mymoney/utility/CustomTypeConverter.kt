package final_project.pemrograman_mobile.kelompok_7.mymoney.utility

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date

class CustomTypeConverter {
    companion object {
        val instance: CustomTypeConverter = CustomTypeConverter()
    }

    @TypeConverter
    fun toDate(value: Long?): Date? {
        if (value == null) return value

        return Date(value)
    }

    @TypeConverter
    fun fromDate(value: Date?): Long? {
        if (value == null) return value

        return value.toInstant().toEpochMilli()
    }

    @TypeConverter
    fun toCalendar(value: Long?): Calendar? {
        if (value == null) return value

        return Calendar.getInstance().apply {
            time = Date(value)
        }
    }

    @TypeConverter
    fun fromCalendar(value: Calendar?): Long? {
        if (value == null) return value

        return value.toInstant().toEpochMilli()
    }

    @TypeConverter
    fun toZonedDateTime(value: Long): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault())
    }

    @TypeConverter
    fun fromZonedDateTime(value: ZonedDateTime): Long {
        return value.withZoneSameInstant(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    @TypeConverter
    fun fromInstant(value: Instant): ZonedDateTime {
        return UtilityFunctions.fromInstant(value)
    }

    @TypeConverter
    fun toInstant(value: ZonedDateTime): Instant {
        return UtilityFunctions.fromZonedDateTime(value)
    }
}