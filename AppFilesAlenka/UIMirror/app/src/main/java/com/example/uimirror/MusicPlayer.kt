package com.example.uimirror

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.util.Log
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton

class MusicPlayer(private val context: Context) {
    private val sharedPref = context.getSharedPreferences("SongPreferences", Context.MODE_PRIVATE)
    private var mainPlayer: MediaPlayer? = null
    private var previewPlayer: MediaPlayer? = null
    private var currentMainSongId: Int? = null
    private var isMainPlaying = false
    private var wasMainPlayingBeforePreview = false
    private val handler = Handler(Looper.getMainLooper())

    fun saveSelectedSongId(songId: Int) {
        sharedPref.edit().putInt("selectedSongId", songId).apply()
    }

    fun loadSelectedSongId(): Int? {
        val id = sharedPref.getInt("selectedSongId", -1)
        return if (id != -1) id else null
    }

    fun playMainSong() {
        loadSelectedSongId()?.let { playSong(it, isPreview = false) }
    }

    fun toggleMainPlayback(songId: Int) {
        when {
            currentMainSongId == songId && isMainPlaying -> pauseMainSong()
            currentMainSongId == songId && !isMainPlaying -> resumeMainSong()
            else -> playSong(songId, isPreview = false)
        }
    }

    fun playPreview(songId: Int) {
        if (isMainPlaying) {
            wasMainPlayingBeforePreview = true
            pauseMainSong()
        }
        stopPreview() // Stop any currently playing preview
        playSong(songId, isPreview = true)

        // Schedule preview to stop after 10 seconds
        handler.postDelayed({ stopPreview() }, 10000)
    }

    fun stopPreview() {
        handler.removeCallbacksAndMessages(null) // Remove any pending stop callbacks
        previewPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        previewPlayer = null
        if (wasMainPlayingBeforePreview) {
            resumeMainSong()
            wasMainPlayingBeforePreview = false
        }
    }

    private fun playSong(songId: Int, isPreview: Boolean) {
        try {
            val player = MediaPlayer.create(context, songId)
            player?.setOnCompletionListener {
                if (isPreview) {
                    stopPreview()
                } else {
                    isMainPlaying = false
                    currentMainSongId = null
                }
            }
            player?.start()

            if (isPreview) {
                previewPlayer?.release()
                previewPlayer = player
            } else {
                mainPlayer?.release()
                mainPlayer = player
                isMainPlaying = true
                currentMainSongId = songId
            }
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Error playing song: ${e.message}")
        }
    }

    private fun pauseMainSong() {
        mainPlayer?.pause()
        isMainPlaying = false
    }

    private fun resumeMainSong() {
        mainPlayer?.start()
        isMainPlaying = true
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        mainPlayer?.release()
        mainPlayer = null
        previewPlayer?.release()
        previewPlayer = null
        currentMainSongId = null
        isMainPlaying = false
    }

    fun isMainPlaying() = isMainPlaying
}
  /*  private val sharedPref: SharedPreferences = context.getSharedPreferences("SongPreferences", Context.MODE_PRIVATE)
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
}*/
