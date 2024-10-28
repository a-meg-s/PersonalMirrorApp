package com.example.uimirror

import android.widget.ImageButton
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(
    private val songs: List<Song>,
    private val onPlayClick: (Song) -> Unit,
    private val onSongSelect: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val songName: TextView = view.findViewById(R.id.songName)
        val artistName: TextView = view.findViewById(R.id.artistName)
        val playButton: ImageButton = view.findViewById(R.id.playButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.songName.text = song.name
        holder.artistName.text = song.artist
        holder.playButton.setOnClickListener { onPlayClick(song) }
        holder.itemView.setOnClickListener { onPlayClick(song) }
    }

    override fun getItemCount() = songs.size
}



/*class SongAdapter(
    private val context: Context,
    private val songList: List<Int>, // List of song resource IDs
    private val onSongSelected: (Int) -> Unit // Callback for selected song
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_song_selection, parent, false)

        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val songResId = songList[position]
        holder.bind(songResId)
    }

    override fun getItemCount() = songList.size

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songItemLayout: LinearLayout = itemView.findViewById(R.id.songItemLayout)
        private val songNameTextView: TextView = itemView.findViewById(R.id.songName)
        private val playButton: ImageView = itemView.findViewById(R.id.playButton)

        fun bind(songResId: Int) {
            songNameTextView.text = context.resources.getResourceEntryName(songResId)

            playButton.setOnClickListener {
                MediaPlayer.create(context, songResId).apply {
                    start()
                    setOnCompletionListener { release() } // Release after playback
                }
            }

            songItemLayout.setOnClickListener {
                onSongSelected(songResId)
            }
        }
    }
}*/