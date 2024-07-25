package final_project.pemrograman_mobile.kelompok_7.mymoney.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import java.time.Instant

@Dao
interface AppSettingsTable {
	@Query("SELECT * FROM `settings` WHERE id = 1")
	fun select(): AppSettings

	@Insert
	fun insert(asset: AppSettings)

	@Update
	fun update(asset: AppSettings)
}

@Entity(tableName = "settings")
data class AppSettings(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER) val id: Int,
	@ColumnInfo(name = "pin", defaultValue = "") var pin: String,
)