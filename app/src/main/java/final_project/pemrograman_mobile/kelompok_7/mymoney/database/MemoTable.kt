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
interface MemosTable {
    @Query("SELECT * FROM `memos`")
    fun selectAll(): List<Memo>

    @Query("SELECT * FROM `memos` WHERE id = :id")
    fun selectById(id: Int): Memo

    @Query("SELECT * FROM `memos` WHERE id IN (:ids)")
    fun selectByIds(ids: IntArray): List<Memo>

    @Query("SELECT * FROM `memos` WHERE (`created_at_timestamp` >= :from AND `created_at_timestamp` <= :to) ORDER BY `created_at_timestamp` DESC")
    fun selectTimestampPagination(from: Instant, to: Instant): List<Memo>

    @Insert
    fun insert(asset: Memo)

    @Update
    fun update(asset: Memo)

    @Delete
    fun delete(asset: Memo)

    @Query("DELETE FROM `memos` WHERE id = :id")
    fun deleteById(id: Int)
}

@Entity(tableName = "memos")
data class Memo(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER) val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "created_at_timestamp") val createdAtTimestamp: Instant,
    @ColumnInfo(name = "is_pinned") val isPinned: Boolean,
)