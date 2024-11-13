package com.example.roomtest

import android.util.Log

class UserFaceRepository(private val userFaceDao: UserFaceDao) {
    fun getAllFaces(): List<UserFaceEntity> {
        Log.d("UserFaceRepository", "getAllFaces() called")
        return userFaceDao.getAllFaces()
    }

    fun insertFace(face: UserFaceEntity) {
        Log.d("UserFaceRepository", "insertFace() called with ${face.name}")
        userFaceDao.insertFace(face)
    }

    fun deleteAllFaces() {
        Log.d("UserFaceRepository", "deleteAllFaces() called")
        userFaceDao.deleteAllFaces()
    }
}