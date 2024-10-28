package com.example.uimirror

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.util.Log

class MusicPlayer(private val context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("SongPreferences", Context.MODE_PRIVATE)
    private var mediaPlayer: MediaPlayer? = null // MediaPlayer-Instanz
    private var isPlaying: Boolean = false // Status, ob der Hauptsong spielt

    // Speichert die ausgewählte Song-ID in den SharedPreferences
    fun saveSelectedSongId(songId: Int) {
        with(sharedPref.edit()) {
            putInt("selectedSongId", songId)
            apply()
        }
    }

    // Lädt die ausgewählte Song-ID aus den SharedPreferences
    fun loadSelectedSongId(): Int? {
        return sharedPref.getInt("selectedSongId", -1).takeIf { it != -1 }
    }

    // Überprüft, ob ein Song ausgewählt wurde und spielt ihn ab
    fun checkAndPlaySelectedSong() {
        val selectedSongId = loadSelectedSongId()
        if (selectedSongId != null) {
            playSong(selectedSongId)
        } else {
            Log.i("MusicPlayer", "Kein Song ausgewählt.")
        }
    }

    // Spielt den Song mit der gegebenen ID ab
    private fun playSong(songId: Int) {
        try {
            // Gibt den vorherigen MediaPlayer frei, falls er existiert
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, songId) // Neuen MediaPlayer erstellen
            mediaPlayer?.start() // Song abspielen

            isPlaying = true // Setze den Status auf "spielend"

            // Setze einen Listener, um zu wissen, wenn der Song beendet ist
            mediaPlayer?.setOnCompletionListener {
                isPlaying = false // Setze den Status auf "nicht spielend", wenn der Song endet
            }
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Fehler beim Abspielen des ausgewählten Songs: ${e.message}")
        }
    }

    // Pausiert den aktuell spielenden Song
    fun pauseSong() {
        if (isPlaying) {
            mediaPlayer?.pause() // Pause den Song
            isPlaying = false // Setze den Status auf "nicht spielend"
        }
    }

    // Setzt den aktuell pausierten Song fort
    fun resumeSong() {
        if (!isPlaying) {
            mediaPlayer?.start() // Setze den Song fort
            isPlaying = true // Setze den Status auf "spielend"
        }
    }

    // Gibt die MediaPlayer-Ressourcen frei
    fun release() {
        mediaPlayer?.release() // Bestehenden MediaPlayer freigeben
        mediaPlayer = null // Setze mediaPlayer auf null
    }
}
