package final_project.pemrograman_mobile.kelompok_7.mymoney.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import final_project.pemrograman_mobile.kelompok_7.mymoney.R
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.CustomTypeConverter
import java.time.Instant
import java.util.Calendar
import java.util.Date

@Database(entities = [Asset::class, Transaction::class, Memo::class, Category::class, Anggaran::class, AppSettings::class], version = 1)
@TypeConverters(CustomTypeConverter::class)
abstract class MonefyDatabase : RoomDatabase() {
	companion object {
		private var database: MonefyDatabase? = null
		var INITIAL_ASSETS_SIZE: Int = 3
		var INITIAL_CATEGORIES_SIZE: Int = 9

		@JvmStatic fun get(): MonefyDatabase? {
			return database
		}

		@JvmStatic fun require(): MonefyDatabase {
			val a = get() ?: throw NullPointerException("Database is null")

			return a
		}

		@JvmStatic fun initialize(appContext: Context): MonefyDatabase {
			var a = get()
			if (a != null) return a;

			a = Room.databaseBuilder(appContext, MonefyDatabase::class.java, appContext.getString(R.string.app_name)).build()

			val probe = a.assets().selectById(1)
			if (probe == null) {
				prepare_base_data(a)
			}

			database = a

			return a
		}

		@JvmStatic private fun prepare_base_data(db: MonefyDatabase) {
			val base_time = Instant.now()

			// INFO: Kalau ngubah ini, pastikan update INITIAL_ASSETS_SIZE
			db.assets().insert(Asset(1, "Tunai", 0.00, base_time))
			db.assets().insert(Asset(2, "Bank", 0.00, base_time))
			db.assets().insert(Asset(3, "Kartu", 0.00, base_time))

			// INFO: Kalau ngubah ini, pastikan update INITIAL_CATEGORIES_SIZE
			db.categories().insert(Category(1, "Gaji", CategoryType.INCOME))
			db.categories().insert(Category(2, "Uang Jajan", CategoryType.INCOME))
			db.categories().insert(Category(3, "Tabungan", CategoryType.INCOME))
			db.categories().insert(Category(4, "Lainnya", CategoryType.INCOME))

			db.categories().insert(Category(5, "Makanan", CategoryType.OUTCOME))
			db.categories().insert(Category(6, "Minuman", CategoryType.OUTCOME))
			db.categories().insert(Category(7, "UKT", CategoryType.OUTCOME))
			db.categories().insert(Category(8, "Transportasi", CategoryType.OUTCOME))
			db.categories().insert(Category(10, "Belanja", CategoryType.OUTCOME))
			db.categories().insert(Category(11, "Lainnya", CategoryType.OUTCOME))

			db.appSettings().insert(AppSettings(1, ""))
		}
	}

	abstract fun assets(): AssetsTable
	abstract fun transactions(): TransactionsTable
	abstract fun memos(): MemosTable
	abstract fun categories(): CategoryTable
	abstract fun anggaran(): AnggaranTable
	abstract fun appSettings(): AppSettingsTable
}