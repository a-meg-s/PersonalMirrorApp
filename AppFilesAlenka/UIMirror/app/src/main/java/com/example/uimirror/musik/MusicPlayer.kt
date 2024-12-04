package com.example.uimirror.musik

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.os.Handler
import android.os.Looper
import androidx.room.Room
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Person
import com.example.uimirror.security.KeystoreManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


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
    private var primaryUser :Person? = null

    val database by lazy {
        val passphrase = SQLiteDatabase.getBytes(KeystoreManager.getPassphrase())
        val factory = SupportFactory(passphrase)
        Room.databaseBuilder(
            context,
            PersonDatabase::class.java,
            "encrypted_person_database"
        )
            .openHelperFactory(factory)
            .build()
    }

    val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        // Ensure the models are loaded at initialization
        primaryUser = loadPrimaryUser()
        /*coroutineScope.launch {
            primaryUser = database.personDao().getPrimaryUser(true)

        }*/
    }
    private fun loadPrimaryUser() : Person? {
        return runBlocking {
            val primaryUser = database.uiMirrorDao().getPrimaryUser(true)
            Log.d("CameraManager", "Cached ${primaryUser?.name} users from database.")
            primaryUser
        }
    }




    fun saveSelectedSongId(songId: Int) {
        coroutineScope.launch {
            // Hole den Primary User
            //val primaryUser = database.personDao().getPrimaryUser(true)
            primaryUser?.let {
                // Speichere die Song-ID und Position für den Primary User in der Datenbank
                database.uiMirrorDao().updateSongData(it.id, songId, 0) // 0 ist der Startpunkt der Position
            }
        }
    }


    fun loadSelectedSongId(): Int? {
        var songId: Int? = null
        //coroutineScope.launch {
        val primaryUser = loadPrimaryUser()
        songId = primaryUser?.selectedSongId
        Log.e("LoadSelected", "user ${primaryUser?.selectedSongId}")
        return songId
    }


    // Spielt den Hauptsong basierend auf der gespeicherten Song-ID
    fun playMainSong() {
        val newSongId = loadSelectedSongId() // Lädt den aktuell gespeicherten Song aus SharedPreferences
        Log.e("playMainSong", "songId ${newSongId} CurrentMainSongId ${currentMainSongId}")
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


    fun pauseMainSong() {
        Log.i("PauseMainsong", "mainPlayer")
        mainPlayer?.let {
            Log.i("PauseMainSong", "wird pausiert")
            currentPosition = it.currentPosition // Speichere die aktuelle Position
            coroutineScope.launch {
                val primaryUser = database.uiMirrorDao().getPrimaryUser(true)
                primaryUser?.let {
                    // Aktualisiere die Position des Songs in der Datenbank
                    database.uiMirrorDao().updateSongData(it.id, currentMainSongId, currentPosition)
                }
            }
            it.pause() // Pausiere den Song
            Log.i("PauseMainSong", "pausiert")
        }
        isMainPlaying = false
    }


    fun resumeMainSong() {
        coroutineScope.launch {
            val primaryUser = database.uiMirrorDao().getPrimaryUser(true)
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



    // Musik Enabled setzen
    fun isMusicEnabled(): Boolean {
        // Abrufen des primären Benutzers aus der Datenbank
        var musicEnabled = true
        val primaryUser = loadPrimaryUser()
            primaryUser?.let {
            musicEnabled = primaryUser?.isMusicEnabled ?: true
        }
        Log.d("Test", "Primary User isMusicEnabled: $musicEnabled, ${primaryUser?.isMusicEnabled} ${primaryUser?.name}")
        return musicEnabled
    }


    fun setMusicEnabled(enabled: Boolean) {
        coroutineScope.launch {
            val primaryUser = database.uiMirrorDao().getPrimaryUser(true)
            primaryUser?.let {
                // Setze den Musikstatus des Benutzers
                it.isMusicEnabled = enabled
                database.uiMirrorDao().updatePerson(it) // Stelle sicher, dass die DAO-Methode für das Update korrekt ist
                Log.d("setMusicEnabled", "Primary User isMusicEnabled:  ${primaryUser?.isMusicEnabled} ${primaryUser?.name}")
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
