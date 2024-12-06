package com.example.uimirror.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uimirror.database.models.Alarm
import com.example.uimirror.database.models.Event
import com.example.uimirror.database.models.Person

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(person: List<Person>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person)

    @Query("SELECT * FROM Person WHERE id = :id")
    suspend fun getPerson(id: Long): Person?

    @Query("SELECT * FROM Person WHERE name = :name")
    suspend fun getPersonByName(name: String): Person?

    @Query("SELECT * FROM Person WHERE isPrimaryUser = :isPrimaryUser")
    suspend fun getPrimaryUser(isPrimaryUser: Boolean): Person?
    //@Query("SELECT * FROM person WHERE isPrimaryUser = 1 LIMIT 1")
    //suspend fun getPrimaryUser(isPrimary: Boolean): Person?

    @Query("SELECT * FROM Person WHERE faceData = :openCvFaceData")
    suspend fun getFaceDetectedPerson(openCvFaceData: List<Byte>): Person?

    @Query("SELECT * FROM Person")
    suspend fun getAllPersons(): List<Person>

    @Query("DELETE FROM Person")
    suspend fun deleteAllPersons()

    @Query("DELETE FROM Person WHERE isPrimaryUser = 1")
    suspend fun deletePrimaryUser()

    @Query("DELETE FROM PERSON WHERE name = :name")
    suspend fun deletePersonsByName(name: String)

    // @Query("UPDATE Person SET alarm = null, selectedSongId = null, songPosition = 0, isMusicEnabled = 1, isAGBread = 0 WHERE id = :personId")
    //suspend fun resetPrimaryUser(personId: Int)

    // Neue Methoden für Musikdaten hinzufügen
    @Query("UPDATE Person SET selectedSongId = :songId, songPosition = :position WHERE id = :personId")
    suspend fun updateSongData(personId: Int, songId: Int?, position: Int)

    /* @Query("SELECT selectedSongId, songPosition FROM Person WHERE id = :personId")
     suspend fun getSongData(personId: Int):  Person?*/

    @Update
    suspend fun updatePerson(person: Person)

   /* @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)*/

    /*@Query("SELECT * FROM Event")
    suspend fun getAllEvents(): List<Event>*/

    @Query("DELETE FROM Event WHERE id = :id")
    suspend fun deleteEvent(id: Int)
}

data class SongData(
    val selectedSongId: Int?,
    val songPosition: Int
)