package com.example.uimirror

import android.widget.ImageButton
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.graphics.Color
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(
    private val songs: List<Song>, // Liste von Song-Objekten
    private val onPlayClick: (Song, ImageButton) -> Unit, // Callback für den Play-Button
    private val onSongSelect: (Song) -> Unit // Callback für die Songauswahl
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    // ViewHolder-Klasse, die UI-Elemente für jeden Song hält
    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Sucht UI-Element in Layout song_item anhand ID
        val songName: TextView = view.findViewById(R.id.songName) // TextView für den Songnamen
        val artistName: TextView = view.findViewById(R.id.artistName)  // TextView für den Künstlernamen
        val playButton: ImageButton = view.findViewById(R.id.playButton) // Button zum Abspielen des Songs
    }

    // Erstellt eine neue ViewHolder-Instanz, wenn benötigt
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        // wandelt XML-Layout in View-Objekte um mit LayoutInflater
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_item, parent, false)
        return SongViewHolder(view) // gibt SongViewHolder-Instanz zurück
    }

    // Bindet Daten an ViweHolder-Instanz
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[holder.adapterPosition] // Holt den Song an der aktuellen Adapter-Position
        holder.songName.text = song.name // Setzt Text des TextView für Songnamen
        holder.artistName.text = song.artist // Setzt Text des TextView für Künstlernamen

        // Setzt Hintergrund basierend auf der ausgewählten Position
       /* if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#c9bce7")) // Lila-Farbe
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT) // Standard-Hintergrund
        }*/
        holder.itemView.setBackgroundColor(
            if (position == selectedPosition) Color.parseColor("#c9bce7")
            else Color.TRANSPARENT
        )


        // Setzt den Click-Listener für den Play-Button
        holder.playButton.setOnClickListener {
            onPlayClick(song, holder.playButton)
        }
        // Setzt den Click-Listener für das gesamte Item
        holder.itemView.setOnClickListener {
            selectedPosition = holder.adapterPosition // Aktualisiert die ausgewählte Position
            onSongSelect(song) // Ruft onSongSelect auf für die Songauswahl
            //onPlayClick(song, holder.playButton) // spielt song auch ab wenn auf Name geklickt wird
            notifyDataSetChanged() // Aktualisiert die RecyclerView
        }
    }

    // Gibt Anzahl Songs in Liste zurück, die der Adapter verwalten soll.
    override fun getItemCount() = songs.size
}



