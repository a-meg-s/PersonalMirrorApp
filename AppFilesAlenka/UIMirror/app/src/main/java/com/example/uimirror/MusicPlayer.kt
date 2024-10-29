package com.example.uimirror

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.util.Log
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton

class MusicPlayer(private val context: Context) {
    // SharedPreferences für das Speichern von Song-Einstellungen, wie z.B. die ausgewählte Song-ID und die Wiedergabeposition
    private val sharedPref = context.getSharedPreferences("SongPreferences", Context.MODE_PRIVATE)
    // MediaPlayer für den Hauptsong und den Vorschau-Song
    private var mainPlayer: MediaPlayer? = null
    private var previewPlayer: MediaPlayer? = null

    // Variable zur Speicherung der aktuellen Song-ID, die gerade abgespielt wird
    private var currentMainSongId: Int? = null
    private var currentPosition: Int = 0
    // Status, ob der Hauptsong gerade abgespielt wird
    private var isMainPlaying = false
    // Flag, um zu überprüfen, ob der Hauptsong vor der Vorschau abgespielt wurde
    private var wasMainPlayingBeforePreview = false
    // Handler für das Ausführen von Aufgaben im Hauptthread (z.B. um den Vorschau-Song nach 10 Sekunden zu stoppen)
    private val handler = Handler(Looper.getMainLooper())

    // Speichert die ID des ausgewählten Songs in SharedPreferences
    fun saveSelectedSongId(songId: Int) {
        sharedPref.edit().putInt("selectedSongId", songId).apply() // Speichern der Song-ID
    }

    // Lädt die ID des zuletzt ausgewählten Songs aus SharedPreferences
    fun loadSelectedSongId(): Int? {
        val id = sharedPref.getInt("selectedSongId", -1) // Versuchen, die ID zu laden, Standardwert ist -1
        return if (id != -1) id else null // Gibt die ID zurück, wenn sie gültig ist, sonst null
    }

    // Spielt den Hauptsong basierend auf der gespeicherten Song-ID
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

    // Spielt eine Vorschau eines Songs für 10 Sekunden ab
    fun playPreview(songId: Int) {
        if (isMainPlaying) {
            wasMainPlayingBeforePreview = true // Merke, dass der Hauptsong vor der Vorschau abgespielt wurde
            pauseMainSong() // Pausiere den Hauptsong
        }
        stopPreview() // Stoppe eine eventuell laufende Vorschau
        playSong(songId, isPreview = true) // Spiele den Vorschau-Song

        // Plane das Stoppen der Vorschau nach 20 Sekunden
        handler.postDelayed({ stopPreview() }, 20000)
    }

    // Stoppt die Vorschau und resume den Hauptsong, wenn er vorher gespielt wurde
    fun stopPreview() {
        handler.removeCallbacksAndMessages(null) // Remove any pending stop callbacks
        previewPlayer?.apply { // Wenn der Vorschau-Player existiert
            if (isPlaying) { // und gerade spielt,
                stop() // stoppe die Wiedergabe
            }
            release() // Gib die Ressourcen des Players frei
        }
        previewPlayer = null // Setze den Vorschau-Player auf null
        if (wasMainPlayingBeforePreview) {
            resumeMainSong() // resume den Hauptsong wenn er vorher abgespielt wurde
            wasMainPlayingBeforePreview = false // Setze das Flag zurück
        }
    }

    // Spielt einen Song ab (entweder Haupt- oder Vorschau-Song)
    private fun playSong(songId: Int, isPreview: Boolean) {
        try {
            val player = MediaPlayer.create(context, songId) // Erstelle einen neuen MediaPlayer für den angegebenen Song
            player?.setOnCompletionListener {
                if (isPreview) {
                    stopPreview()
                } else {
                    isMainPlaying = false // setze den Hauptspielstatus auf false
                    currentMainSongId = null // setze die aktuelle Song-ID auf null
                }
            }
            player?.start() // Starte die Wiedergabe des Songs

            if (isPreview) {
                previewPlayer?.release() // gib den vorherigen Vorschau-Player frei
                previewPlayer = player // setze den neuen Player als Vorschau-Player
            } else {
                mainPlayer?.release() // gib den vorherigen Haupt-Player frei
                mainPlayer = player
                isMainPlaying = true
                currentMainSongId = songId
            }
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Error playing song: ${e.message}")
        }
    }

    private fun pauseMainSong() {
        mainPlayer?.let {
            currentPosition = it.currentPosition // Fortschritt speichern
            sharedPref.edit().putInt("song_position", currentPosition).apply() // Fortschritt in SharedPreferences speichern
            it.pause()
        }
      //  mainPlayer?.pause()
        isMainPlaying = false
    }

    private fun resumeMainSong() {
        currentPosition = sharedPref.getInt("song_position", 0) // Fortschritt aus SharedPreferences laden
        mainPlayer?.seekTo(currentPosition) // Setzt den Song an der gespeicherten Position fort
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
