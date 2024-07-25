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
import java.time.ZonedDateTime
import java.util.Calendar

@Dao
interface TransactionsTable {
	@Query("SELECT * FROM `transactions`")
	fun selectAll(): List<Transaction>

	@Query("SELECT * FROM `transactions` ORDER BY `created_at_timestamp` DESC")
	fun selectAllOrderByCreatedAscending(): List<Transaction>

	@Query("SELECT * FROM `transactions` WHERE (`created_at_timestamp` >= :from AND `created_at_timestamp` <= :to) ORDER BY `created_at_timestamp` DESC")
	fun HomePagination(from: Instant, to: Instant): List<Transaction>

	@Query("SELECT * FROM `transactions` WHERE id = :id")
	fun selectById(id: Int): Transaction

	@Query("SELECT * FROM `transactions` WHERE id IN (:ids)")
	fun selectByIds(ids: IntArray): List<Transaction>

	@Insert()
	fun insert(asset: Transaction)

	@Update
	fun update(asset: Transaction)

	@Delete
	fun delete(asset: Transaction)

	@Query("DELETE FROM `transactions` WHERE id = :id")
	fun deleteById(id: Int)
}

@Entity(tableName = "transactions")
data class Transaction(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER) val id: Int,
	@ColumnInfo(name = "source_asset_id") val sourceAssetId: Int,
	@ColumnInfo(name = "destination_asset_id") val destinationAssetId: Int,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "description") val description: String,
	@ColumnInfo(name = "category_id") val categoryId: Int,
	@ColumnInfo(name = "type") val type: TransactionType,
	@ColumnInfo(name = "amount") val amount: Double,
	@ColumnInfo(name = "created_at_timestamp") val createdAtTimestamp: Instant,
	@ColumnInfo(name = "updated_at_timestamp") val updatedAtTimestamp: Instant
)

enum class TransactionType {
	INCOME, OUTCOME, TRANSFER
}