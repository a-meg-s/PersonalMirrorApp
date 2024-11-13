package com.example.roomtest

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserFaceDao{
@Query("SELECT * FROM user_faces")
fun getAllFaces(): List<UserFaceEntity>

@Insert
fun insertFace(face: UserFaceEntity)

@Query("Delete FROM user_faces")
fun deleteAllFaces()
}