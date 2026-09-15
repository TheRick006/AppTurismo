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

    @Query("DELETE FROM favorites WHERE targetId = :id AND type = :type")
    suspend fun delete(id: Int, type: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE targetId = :id AND type = :type)")
    fun isFavorite(id: Int, type: String): Flow<Boolean>
}