package data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import data.local.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE dateKey = :dateKey ORDER BY createdAt DESC")
    fun getNotesForDate(dateKey: String): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY dateKey DESC, createdAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE noteId = :noteId")
    suspend fun getNoteById(noteId: Long): Note?

    @Query("SELECT DISTINCT dateKey FROM notes")
    fun getDatesWithNotes(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM notes WHERE dateKey = :dateKey")
    suspend fun countNotesForDate(dateKey: String): Int
}