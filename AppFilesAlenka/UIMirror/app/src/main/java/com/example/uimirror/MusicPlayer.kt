package com.example.uimirror

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.util.Log
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.room.Room
import com.example.uimirror.database.PersonDatabase
import androidx.lifecycle.coroutineScope
import com.example.uimirror.database.models.Person
import com.example.uimirror.databinding.ActivityAlarmEditorBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class MusicPlayer(private val context: Context) {
    // SharedPreferences für das Speichern von Song-Einstellungen, wie z.B. die ausgewählte Song-ID und die Wiedergabeposition
    // private val sharedPref = context.getSharedPreferences("SongPreferences", Context.MODE_PRIVATE)
    // MediaPlayer für den Hauptsong und den Vorschau-Song
    private var mainPlayer: MediaPlayer? = null
    private var previewPlayer: MediaPlayer? = null

    // Variable zur Speicherung der aktuellen Song-ID, die gerade abgespielt wird
    private var currentMainSongId: Int? = null
    private var currentPosition: Int = 0
    // Status, ob der Hauptsong gerade abgespielt wird
    private var isMainPlaying: Boolean = false
    // Flag, um zu überprüfen, ob der Hauptsong vor der Vorschau abgespielt wurde
    private var wasMainPlayingBeforePreview: Boolean = false
    // Handler für das Ausführen von Aufgaben im Hauptthread (z.B. um den Vorschau-Song nach 10 Sekunden zu stoppen)
    private val handler = Handler(Looper.getMainLooper())

    private val database = Room.databaseBuilder(
        context,
        PersonDatabase::class.java,
        "person_database"
    )
        .fallbackToDestructiveMigration()  // Daten werden bei jeder Versionsänderung gelöscht
        .build()


    // Speichert die ID des ausgewählten Songs in SharedPreferences
    /*  fun saveSelectedSongId(songId: Int) {
          sharedPref.edit().putInt("selectedSongId", songId).apply() // Speichern der Song-ID
          sharedPref.edit().putInt("song_position", 0).apply()
      }*/


    // Verwende CoroutineScope manuell
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun saveSelectedSongId(songId: Int) {
        coroutineScope.launch {
            // Hole den Primary User
            val primaryUser = database.personDao().getPrimaryUser(true)
            primaryUser?.let {
                // Speichere die Song-ID und Position für den Primary User in der Datenbank
                database.personDao().updateSongData(it.id, songId, 0) // 0 ist der Startpunkt der Position
            }
        }
    }



    /* // Lädt die ID des zuletzt ausgewählten Songs aus SharedPreferences
    fun loadSelectedSongId(): Int? {
        val id = sharedPref.getInt("selectedSongId", -1) // Versuchen, die ID zu laden, Standardwert ist -1
        return if (id != -1) id else null // Gibt die ID zurück, wenn sie gültig ist, sonst null
    }*/
    fun loadSelectedSongId(): Int? {
        var songId: Int? = null
        coroutineScope.launch {
            val primaryUser = database.personDao().getPrimaryUser(true)
            primaryUser?.let {
                songId = primaryUser.selectedSongId
                Log.e("LoadSelected", "user ${primaryUser.selectedSongId}")
            }
        }
        return songId
    }


    // Spielt den Hauptsong basierend auf der gespeicherten Song-ID
    fun playMainSong() {
        val newSongId = loadSelectedSongId() // Lädt den aktuell gespeicherten Song aus SharedPreferences
        if (newSongId != null && newSongId != currentMainSongId) {
            mainPlayer?.release() // Wenn ein neuer Song geladen wird, vorherigen Player loslassen
            currentMainSongId = newSongId
            playSong(newSongId, isPreview = false) // Startet den neuen Hauptsong
        } else if (newSongId != null) {
            resumeMainSong() // Falls der Song derselbe ist, wird die Wiedergabe fortgesetzt
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
            Log.e("playSong","songId ${songId}")
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


    /*fun pauseMainSong() {
        mainPlayer?.let {
            currentPosition = it.currentPosition // Fortschritt speichern
            Log.d("MusicPlayer", "Aktuelle Position: $currentPosition") // Protokolliere die aktuelle Position
            sharedPref.edit().putInt("song_position", currentPosition).apply() // Fortschritt in SharedPreferences speichern
            it.pause()
        } ?: Log.d("MusicPlayer", "pauseMainSong: Main player is null or not playing")
      //  mainPlayer?.pause()
        isMainPlaying = false
    }*/

    fun pauseMainSong() {
        mainPlayer?.let {
            currentPosition = it.currentPosition // Speichere die aktuelle Position
            coroutineScope.launch {
                val primaryUser = database.personDao().getPrimaryUser(true)
                primaryUser?.let {
                    // Aktualisiere die Position des Songs in der Datenbank
                    database.personDao().updateSongData(it.id, currentMainSongId, currentPosition)
                }
            }
            it.pause() // Pausiere den Song
        }
        isMainPlaying = false
    }


    fun resumeMainSong() {
        coroutineScope.launch {
            val primaryUser = database.personDao().getPrimaryUser(true)
            primaryUser?.let { user ->
                // Hole die gespeicherten Song-Daten
                val songId = primaryUser.selectedSongId
                currentPosition = primaryUser.songPosition// Lade die gespeicherte Position


                if (songId != null) {
                    // Wenn der SongId nicht null ist, starte den Song
                    if (mainPlayer == null) {
                        playSong(songId, isPreview = false) // Starte den Song
                    } else {
                        mainPlayer?.seekTo(currentPosition) // Setze den Song an der gespeicherten Position fort
                        mainPlayer?.start() // Starte den Song
                    }
                    isMainPlaying = true // Setze den Status auf "Main-Song wird abgespielt"
                } else {
                    Log.e("MusicPlayer", "Selected song ID is null for user ${user.id}")
                }
            }
        }
    }





    /* private fun resumeMainSong() {
         currentPosition = sharedPref.getInt("song_position", 0) // Fortschritt aus SharedPreferences laden
         mainPlayer?.seekTo(currentPosition) // Setzt den Song an der gespeicherten Position fort
         mainPlayer?.start()
         isMainPlaying = true
     }*/

    // Speichert, ob die Musik aktiviert ist
    /*  fun setMusicEnabled(enabled: Boolean) {
          sharedPref.edit().putBoolean("music_enabled", enabled).apply()
      }

      // Lädt den Musikstatus (aktiviert oder deaktiviert) aus SharedPreferences
      fun isMusicEnabled(): Boolean {
          Log.d("MusicPlayer", "Music Enabled:")
          return sharedPref.getBoolean("music_enabled", true) // Standardmäßig aktiviert
      }*/
    // Musik Enabled setzen
    fun isMusicEnabled(): Boolean {
        // Abrufen des primären Benutzers aus der Datenbank
        var MusicEnabled = true
        coroutineScope.launch {
            val primaryUser = database.personDao().getPrimaryUser(true)
            MusicEnabled = primaryUser?.let { it.isMusicEnabled } ?: false
        }
        return MusicEnabled

    }
    fun setMusicEnabled(enabled: Boolean) {
        coroutineScope.launch {
            val primaryUser = database.personDao().getPrimaryUser(true)
            primaryUser?.let {
                // Setze den Musikstatus des Benutzers
                it.isMusicEnabled = enabled
                database.personDao().updatePerson(it) // Stelle sicher, dass die DAO-Methode für das Update korrekt ist
            }
        }
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
