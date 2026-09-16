package data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.itanes.appturismo.data.local.dao.TourDao
import data.local.entity.Favorite
import data.local.entity.Tour
import data.local.dao.FavoriteDao
import com.itanes.appturismo.data.local.dao.TouristPointDao
import com.itanes.appturismo.data.local.entity.TouristPoint
import data.local.dao.NoteDao
import data.local.entity.Note
import utils.Converters

@Database(
    entities = [
        Tour::class,
        TouristPoint::class,
        Favorite::class,
        Note::class,
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tourDao(): TourDao
    abstract fun touristPointDao(): TouristPointDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_turismo.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}