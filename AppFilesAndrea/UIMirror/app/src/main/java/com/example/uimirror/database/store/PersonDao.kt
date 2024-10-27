package com.example.database.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(person: List<Person>)

    @Query("SELECT * FROM Person WHERE id = :id")
    suspend fun getPerson(id: Long): Person?

    @Query("SELECT * FROM Person")
    suspend fun getAllPersons(): List<Person>

}