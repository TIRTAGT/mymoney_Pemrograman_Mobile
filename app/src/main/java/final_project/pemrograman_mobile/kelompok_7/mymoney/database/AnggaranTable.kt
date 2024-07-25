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
interface AnggaranTable {
    @Query("SELECT * FROM `anggaran`")
    fun selectAll(): List<Anggaran>

    @Query("SELECT * FROM `anggaran` WHERE id = :id")
    fun selectById(id: Int): Anggaran

    @Query("SELECT * FROM `anggaran` WHERE id IN (:ids)")
    fun selectByIds(ids: IntArray): List<Anggaran>

    @Query("SELECT * FROM `anggaran` WHERE (`timestamp` >= :from AND `timestamp` <= :to) ORDER BY `timestamp` DESC")
    fun selectTimestampPagination(from: Instant, to: Instant): List<Anggaran>

    @Insert
    fun insert(asset: Anggaran)

    @Update
    fun update(asset: Anggaran)

    @Delete
    fun delete(asset: Anggaran)

    @Query("DELETE FROM `anggaran` WHERE id = :id")
    fun deleteById(id: Int)
}

@Entity(tableName = "anggaran")
data class Anggaran(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER) val id: Int,
    @ColumnInfo(name = "amount", defaultValue = "0.00") var amount: Double,
    @ColumnInfo(name = "timestamp") val createdAtTimestamp: Instant
)