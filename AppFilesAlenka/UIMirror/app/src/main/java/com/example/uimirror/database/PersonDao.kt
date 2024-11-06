package com.example.uimirror.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uimirror.database.models.Person

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(person: List<Person>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person)

    @Query("SELECT * FROM Person WHERE id = :id")
    suspend fun getPerson(id: Long): Person?


    @Query("SELECT * FROM Person WHERE isPrimaryUser = :isPrimaryUser")
    suspend fun getPrimaryUser(isPrimaryUser: Boolean): Person?

    @Query("SELECT * FROM Person WHERE faceData = :openCvFaceData")
    suspend fun getFaceDetectedPerson(openCvFaceData: List<Byte>): Person?

    @Query("SELECT * FROM Person")
    suspend fun getAllPersons(): List<Person>

    @Query("DELETE FROM Person")
    suspend fun deleteAllPersons()
}