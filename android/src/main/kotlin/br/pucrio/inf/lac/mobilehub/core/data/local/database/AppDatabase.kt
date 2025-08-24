package br.pucrio.inf.lac.mobilehub.core.data.local.database

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.pucrio.inf.lac.mobilehub.core.data.local.dao.CepQueryDao
import br.pucrio.inf.lac.mobilehub.core.data.local.dao.MobileObjectDriverDao
import br.pucrio.inf.lac.mobilehub.core.data.local.models.CepQueryModel
import br.pucrio.inf.lac.mobilehub.core.data.local.models.MobileObjectDriverModel

@Database(entities = [
    CepQueryModel::class,
    MobileObjectDriverModel::class
], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {
    companion object {
        @VisibleForTesting
        private const val DATABASE_NAME = "MobileHub-db"

        private val LOCK = Any()

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(LOCK) {
                    if (INSTANCE == null) {
                        INSTANCE = getDatabase(context)
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private fun getDatabase(context: Context): Builder<AppDatabase> =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
    }

    abstract fun cepQueryDao(): CepQueryDao
    abstract fun mobileObjectDriverDao(): MobileObjectDriverDao
}