package com.example.uimirror.musik

import android.app.Application

class MyApp : Application() {
    // Globale Instanz von MusicPlayer
    lateinit var musicPlayer: MusicPlayer
        private set

    override fun onCreate() {
        super.onCreate()
        // Initialisiere die Instanz von MusicPlayer
        musicPlayer = MusicPlayer(this)
    }
}
