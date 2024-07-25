package final_project.pemrograman_mobile.kelompok_7.mymoney.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import java.sql.Date
import java.time.Instant
import java.util.Calendar

@Dao
interface AssetsTable {
	@Query("SELECT * FROM `assets`")
	fun selectAll(): List<Asset>

	@Query("SELECT * FROM `assets` WHERE id = :id")
	fun selectById(id: Int): Asset?

	@Query("SELECT * FROM `assets` WHERE id IN (:ids)")
	fun selectByIds(ids: IntArray): List<Asset>

	@Insert
	fun insert(asset: Asset)

	@Update
	fun update(asset: Asset)

	@Delete
	fun delete(asset: Asset)

	@Query("DELETE FROM `assets` WHERE id = :id")
	fun deleteById(id: Int)
}

@Entity(tableName = "assets")
data class Asset(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER) val id: Int,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "balance", defaultValue = "0.00") var balance: Double,
	@ColumnInfo(name = "created_at_timestamp") val createdAtTimestamp: Instant
) {
	override fun toString(): String { return this.name }
}