package final_project.pemrograman_mobile.kelompok_7.mymoney.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryTable {
    @Query("SELECT * FROM `categories`")
    fun selectAll(): List<Category>

    @Query("SELECT * FROM `categories` WHERE id = :id")
    fun selectById(id: Int): Category

    @Query("SELECT * FROM `categories` WHERE id IN (:ids)")
    fun selectByIds(ids: IntArray): List<Category>

    @Query("SELECT * FROM `categories` WHERE type = :type")
    fun selectByType(type: TransactionType): List<Category>

    @Insert
    fun insert(asset: Category)

    @Update
    fun update(asset: Category)

    @Delete
    fun delete(asset: Category)

    @Query("DELETE FROM `categories` WHERE id = :id")
    fun deleteById(id: Int)
}

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: CategoryType
) {
    override fun toString(): String { return this.name }
}

enum class CategoryType {
    INCOME, OUTCOME
}