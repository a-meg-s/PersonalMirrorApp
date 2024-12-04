package com.example.uimirror.database

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object DatabaseUtils {

    private const val DATABASE_NAME = "person_database" // current database name
    private const val BACKUP_NAME = "backup_person_database.db" // Backup file name

    /**
     * Back up the current database to a safe location (app's files directory).
     */
    fun backupDatabase(context: Context): Boolean {
        val databasePath = context.getDatabasePath(DATABASE_NAME)
        val backupPath = File(context.filesDir, BACKUP_NAME)

        return try {
            FileInputStream(databasePath).use { input ->
                FileOutputStream(backupPath).use { output ->
                    input.copyTo(output)
                }
            }
            println("Database backup created at: ${backupPath.absolutePath}")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            println("Failed to create database backup: ${e.message}")
            false
        }
    }

    /**
     * Restore the database from the backup file.
     */
    fun restoreDatabase(context: Context): Boolean {
        val databasePath = context.getDatabasePath(DATABASE_NAME)
        val backupPath = File(context.filesDir, BACKUP_NAME)

        return try {
            FileInputStream(backupPath).use { input ->
                FileOutputStream(databasePath).use { output ->
                    input.copyTo(output)
                }
            }
            println("Database restored from backup.")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            println("Failed to restore database: ${e.message}")
            false
        }
    }
}