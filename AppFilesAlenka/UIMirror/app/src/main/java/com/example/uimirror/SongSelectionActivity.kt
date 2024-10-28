package com.example.uimirror

//Importiert
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SongSelectionActivity : AppCompatActivity() {
    private  var mediaPlayer: MediaPlayer? = null // Nullable-Variable für den MediaPlayer, um Songs abzuspielen.
    private var currentPlayingSongId: Int? = null // Speichert die ID des aktuell abgespielten Songs.
    private var lastPlayedButton: ImageButton? = null // Track the last played button


    // Lebenszyklusmethode, die beim Erstellen der Aktivität aufgerufen wird
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_selection)  // Setzt das Layout für die Aktivität

        setupSongList() /// Initialisiert die Songliste
    }

    // Methode zur Einrichtung der RecyclerView und der Songliste
    private fun setupSongList() {
        // Liste der songs wird Vertikal angezeigt
        val songListView = findViewById<RecyclerView>(R.id.songListView) // Holt die RecyclerView
        songListView.layoutManager = LinearLayoutManager(this) // Setzt den LayoutManager für vertikale Liste

        // Liste der Song-Objekte mit Name, Künstler und Ressourcen-ID
        val songs = listOf(
            Song("Hey Brother", "Avicii", R.raw.hey_brother),
            Song("Rescue Me", "OneRepublic", R.raw.rescue_me),
            Song("Shivers", "EdSeeran", R.raw.shivers),
            Song("Bad Liars", "Imagine Dragons", R.raw.bad_liars)
        )

        // Erstellt den Adapter mit Callback-Funktionen
        val adapter = SongAdapter(songs,
            onPlayClick = { song, playButton -> playSongPreview(song, playButton) }, // Callback für Play-Button
            onSongSelect = { song -> onSongSelected(song) } // Callback für Songauswahl
        )
        songListView.adapter = adapter // Setzt den Adapter für die RecyclerView
    }

    // Methode zur Verwaltung der Songwiedergabe
    private fun playSongPreview(song: Song, playButton: ImageButton) {
        try {
            // Überprüft, ob der ausgewählte Song bereits abgespielt wird
            if (currentPlayingSongId == song.resourceId) {
                mediaPlayer?.let {
                    // Pausiert oder startet den Song je nach Zustand
                    if (it.isPlaying) {
                        it.pause() // Pausiert den Song
                        playButton.setImageResource(android.R.drawable.ic_media_play) // Setzt das Icon auf Play
                        lastPlayedButton = null // Reset last played button
                    } else {
                        it.start() // Startet den Song
                        playButton.setImageResource(android.R.drawable.ic_media_pause) // Setzt das Icon auf Pause
                        lastPlayedButton?.setImageResource(android.R.drawable.ic_media_play) // Reset previous button to play
                        lastPlayedButton = playButton // Update last played button
                    }
                }
            } else {
                // Gibt den vorherigen Song frei, falls ein neuer ausgewählt wird
                mediaPlayer?.release() // Gibt den MediaPlayer frei
                mediaPlayer = null // Setzt die MediaPlayer-Referenz auf null

                // Reset last played button to play if it's not null
                lastPlayedButton?.setImageResource(android.R.drawable.ic_media_play)

                // Erstellt eine neue MediaPlayer-Instanz für den neuen Song
                mediaPlayer = MediaPlayer.create(this, song.resourceId)
                mediaPlayer?.setOnCompletionListener {
                    it.release() // Gibt den MediaPlayer frei, wenn die Wiedergabe abgeschlossen ist
                    mediaPlayer = null // Setzt die MediaPlayer-Referenz auf null
                    currentPlayingSongId = null // Setzt die aktuelle Song-ID zurück
                    playButton.setImageResource(android.R.drawable.ic_media_play) // Setzt das Icon zurück auf Play
                    lastPlayedButton = null // Reset last played button
                }
                mediaPlayer?.start() // Startet die Wiedergabe des neuen Songs
                currentPlayingSongId = song.resourceId // Setzt ID des aktuell abgespielten Songs
                playButton.setImageResource(android.R.drawable.ic_media_pause) // Setzt das Icon auf Pause
                lastPlayedButton = playButton // Update last played button
            }
        } catch (e: Exception) {
            // Fehlerbehandlung bei Problemen mit der Songwiedergabe
            Log.e("SongSelectionActivity", "Error playing song: ${e.message}")
            Toast.makeText(this, "Fehler beim Abspielen des Songs", Toast.LENGTH_SHORT).show()
        }
    }

    // Methode zur Speicherung der ausgewählten Song-ID in den SharedPreferences
    private fun saveSongSelection(song: Song) {
        val sharedPref = getSharedPreferences("SongPreferences", Context.MODE_PRIVATE) // Holt die SharedPreferences
        with(sharedPref.edit()) {
            putInt("selectedSongId", song.resourceId) // Speichert die Song-ID
            apply() // Wendet die Änderungen an
        }
    }

    // Callback, wenn ein Song ausgewählt wird
    private fun onSongSelected(song: Song) {
        saveSongSelection(song) // Speichert die Auswahl des Songs
        Toast.makeText(this, "Song '${song.name}' ausgewählt", Toast.LENGTH_SHORT).show()
        //finish() // Kehrt zur MainActivity zurück
    }

    // Lebenszyklusmethode, die beim Pausieren der Aktivität aufgerufen wird
    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()// Pausiert die Wiedergabe, wenn die Aktivität pausiert wird
    }

    // Lebenszyklusmethode, die beim Zerstören der Aktivität aufgerufen wird
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Gibt den MediaPlayer frei
        mediaPlayer = null // Setzt die MediaPlayer-Referenz auf null
    }
}

// Datenklasse für die Song-Objekte
data class Song(val name: String, val artist: String, val resourceId: Int)

