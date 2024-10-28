package com.example.uimirror

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SongSelectionActivity : AppCompatActivity() {
    private  var mediaPlayer: MediaPlayer? = null
    private var currentPlayingSongId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_selection)

        setupSongList()
    }

    private fun setupSongList() {
        val songListView = findViewById<RecyclerView>(R.id.songListView)
        songListView.layoutManager = LinearLayoutManager(this)

        // Liste der Songs
        val songs = listOf(
            Song("Hey Brother", "Avicii", R.raw.hey_brother),
            Song("Rescue Me", "Imagin Dragons", R.raw.rescue_me),
            Song("Shivers", "EdSeeran", R.raw.shivers)
        )

        val adapter = SongAdapter(songs,
            onPlayClick = { song -> playSongPreview(song) },
            onSongSelect = { song -> onSongSelected(song) }
        )
        songListView.adapter = adapter
    }

    private fun playSongPreview(song: Song) {
        try {
            if (currentPlayingSongId == song.resourceId) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        it.pause()
                    } else {
                        it.start()
                    }
                }
            } else {
                mediaPlayer?.release()
                mediaPlayer = null
                mediaPlayer = MediaPlayer.create(this, song.resourceId)
                mediaPlayer?.setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                    currentPlayingSongId = null
                }
                mediaPlayer?.start()
                currentPlayingSongId = song.resourceId
            }
        } catch (e: Exception) {
            Log.e("SongSelectionActivity", "Error playing song: ${e.message}")
            Toast.makeText(this, "Fehler beim Abspielen des Songs", Toast.LENGTH_SHORT).show()
        }
    }
        /*val adapter = SongAdapter(songs) { song ->
            playSongPreview(song.resourceId)
        }
        songListView.adapter = adapter
    }*/
       /* private fun playSongPreview(resourceId: Int) {
            if (currentPlayingSongId == resourceId) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        it.pause()
                    } else {
                        it.start()
                    }
                }
            } else {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(this, resourceId)
                mediaPlayer?.start()
                currentPlayingSongId = resourceId
            }
        }*/

    /*private fun playSongPreview(resourceId: Int) {
        if (currentPlayingSongId == resourceId) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        } else {
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.release()
            }
            mediaPlayer = MediaPlayer.create(this, resourceId)
            mediaPlayer.start()
            currentPlayingSongId = resourceId
        }
    }*/

    private fun saveSongSelection(song: Song) {
        val sharedPref = getSharedPreferences("SongPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("selectedSongId", song.resourceId)
            apply()
        }
    }

    private fun onSongSelected(song: Song) {
        saveSongSelection(song)
        Toast.makeText(this, "Song '${song.name}' ausgewählt", Toast.LENGTH_SHORT).show()
        //finish() // Kehrt zur MainActivity zurück
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
       /* if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }*/
    }
}

data class Song(val name: String, val artist: String, val resourceId: Int)

   /* override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_song_selection)
        val songList = listOf(R.raw.hey_brother, R.raw.rescue_me, R.raw.shivers) // MP3-Dateien

        val recyclerView = findViewById<RecyclerView>(R.id.songRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = SongAdapter(this, songList) { selectedSongResId ->
            // Song auswählen und speichern
            getSharedPreferences("UserPreferences", MODE_PRIVATE).edit()
                .putInt("selectedSong", selectedSongResId)
                .apply()
            finish() // Schließe die Auswahl nach Speicherung
        }

    }
}*/