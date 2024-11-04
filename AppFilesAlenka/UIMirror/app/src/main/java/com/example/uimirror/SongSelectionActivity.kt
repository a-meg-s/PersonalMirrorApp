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
import android.widget.Button

class SongSelectionActivity : AppCompatActivity() {
    private lateinit var musicPlayer: MusicPlayer
    private lateinit var adapter: SongAdapter
    private var currentPreviewButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_selection)

        musicPlayer = MusicPlayer(this)
        setupSongList()

        returnToMainActivity()
        deactivateMainSong()
        

    }

    private fun setupSongList() {
        val songListView = findViewById<RecyclerView>(R.id.songListView)
        songListView.layoutManager = LinearLayoutManager(this)

        val songs = listOf(
            Song("Hey Brother", "Avicii", R.raw.hey_brother),
            Song("Rescue Me", "OneRepublic", R.raw.rescue_me),
            Song("Shivers", "Ed Sheeran", R.raw.shivers),
            Song("Bad Liars", "Imagine Dragons", R.raw.bad_liars)
        )

        adapter = SongAdapter(
            songs = songs,
            onPlayClick = { song, button -> onPlayClick(song, button) },
            onSongSelect = { song -> onSongSelected(song) }
        )
        songListView.adapter = adapter
    }

    private fun returnToMainActivity() {
        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish() // Beendet die SongSelectionActivity und kehrt zur MainActivity zurück
        }
    }

    // In SongSelectionActivity
    private fun deactivateMainSong() {
       val deactivateButton =  findViewById<Button>(R.id.deactivateMainSongButton)
       if(musicPlayer.isMusicEnabled()){
           deactivateButton.text = "Song deaktivieren" // Button-Text ändern
       } else{
           deactivateButton.text = "Song aktivieren" // Button-Text ändern
       }

           deactivateButton.setOnClickListener {
            // Überprüfen, ob Musik gerade läuft
            if (musicPlayer.isMusicEnabled()) {
                musicPlayer.pauseMainSong() // Musik pausieren
                musicPlayer.setMusicEnabled(false) // Musik als deaktiviert markieren
                deactivateButton.text = "Song aktivieren" // Button-Text ändern
                Toast.makeText(this, "Hauptmusik deaktiviert", Toast.LENGTH_SHORT).show()
                Log.d("SongSelectionActivity", "deactivateMainSong: Deactivate Main Song button clicked")
            } else {
                musicPlayer.setMusicEnabled(true) // Musik als aktiviert markieren
                //musicPlayer.playMainSong() // Musik abspielen
                deactivateButton.text = "Song deaktivieren" // Button-Text ändern
                Toast.makeText(this, "Hauptmusik aktiviert", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPlayClick(song: Song, button: ImageButton) {
        if (button == currentPreviewButton) {
            musicPlayer.stopPreview()
            updatePlayButton(button, false)
            currentPreviewButton = null
        } else {
            currentPreviewButton?.let { updatePlayButton(it, false) }
            musicPlayer.playPreview(song.resourceId)
            updatePlayButton(button, true)
            currentPreviewButton = button
        }
    }

    private fun updatePlayButton(button: ImageButton, isPlaying: Boolean) {
        button.setImageResource(
            if (isPlaying) android.R.drawable.ic_media_pause
            else android.R.drawable.ic_media_play
        )
    }

    private fun onSongSelected(song: Song) {
        musicPlayer.saveSelectedSongId(song.resourceId)
        Toast.makeText(this, "Song '${song.name}' ausgewählt", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onPause() {
        super.onPause()
        musicPlayer.stopPreview()
        currentPreviewButton?.let { updatePlayButton(it, false) }
        currentPreviewButton = null
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer.release()
    }
}

data class Song(val name: String, val artist: String, val resourceId: Int)

    /*private  var mediaPlayer: MediaPlayer? = null // Nullable-Variable für den MediaPlayer, um Songs abzuspielen.
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
    // Methode zur Verwaltung der Songwiedergabe
    private fun playSongPreview(song: Song, playButton: ImageButton) {
        try {
            // Wenn ein anderer Song aktuell abgespielt wird, wird dieser pausiert
            if (currentPlayingSongId != null) {
                // Den aktuellen Song pausieren, wenn er nicht der neue Song ist
                mediaPlayer?.pause() // Pausiert den aktuell abgespielten Song, falls vorhanden
                lastPlayedButton?.setImageResource(android.R.drawable.ic_media_play) // Setzt das Icon des vorherigen Buttons auf Play
            }

            // Überprüft, ob der ausgewählte Song bereits abgespielt wird
            if (currentPlayingSongId == song.resourceId) {
                mediaPlayer?.let {
                    // Wenn der aktuelle Song bereits abgespielt wird, dann pausieren oder starten
                    if (it.isPlaying) {
                        it.pause() // Pausiert den Song
                        playButton.setImageResource(android.R.drawable.ic_media_play) // Setzt das Icon auf Play
                        lastPlayedButton = null // Setzt den zuletzt abgespielten Button zurück
                    } else {
                        it.start() // Startet den Song
                        playButton.setImageResource(android.R.drawable.ic_media_pause) // Setzt das Icon auf Pause
                        lastPlayedButton?.setImageResource(android.R.drawable.ic_media_play) // Setzt das vorherige Icon auf Play
                        lastPlayedButton = playButton // Aktualisiert den zuletzt abgespielten Button
                    }
                }
            } else {
                // Gibt den vorherigen MediaPlayer frei, falls ein neuer Song abgespielt wird
                mediaPlayer?.release() // Gibt den MediaPlayer frei
                mediaPlayer = null // Setzt die MediaPlayer-Referenz auf null

                // Erstellt eine neue MediaPlayer-Instanz für den neuen Song
                mediaPlayer = MediaPlayer.create(this, song.resourceId)
                mediaPlayer?.setOnCompletionListener {
                    it.release() // Gibt den MediaPlayer frei, wenn die Wiedergabe abgeschlossen ist
                    mediaPlayer = null // Setzt die MediaPlayer-Referenz auf null
                    currentPlayingSongId = null // Setzt die aktuelle Song-ID zurück
                    playButton.setImageResource(android.R.drawable.ic_media_play) // Setzt das Icon zurück auf Play
                    lastPlayedButton = null // Setzt den zuletzt abgespielten Button zurück
                }
                mediaPlayer?.start() // Startet die Wiedergabe des neuen Songs
                currentPlayingSongId = song.resourceId // Setzt die ID des aktuell abgespielten Songs
                playButton.setImageResource(android.R.drawable.ic_media_pause) // Setzt das Icon auf Pause
                lastPlayedButton = playButton // Aktualisiert den zuletzt abgespielten Button
            }
        } catch (e: Exception) {
            // Fehlerbehandlung bei Problemen mit der Songwiedergabe
            Log.e("SongSelectionActivity", "Error playing song: ${e.message}")
            Toast.makeText(this, "Fehler beim Abspielen des Songs", Toast.LENGTH_SHORT).show()
        }
    }

    /* private fun playSongPreview(song: Song, playButton: ImageButton) {
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
     }*/

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
        finish() // Kehrt zur MainActivity zurück
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

*/