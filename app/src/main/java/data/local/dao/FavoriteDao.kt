package data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.local.entity.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE touristPointId = :pointId")
    suspend fun delete(pointId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE touristPointId = :pointId)")
    fun isFavorite(pointId: Int): Flow<Boolean>
}