package com.example.uimirror


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uimirror.databinding.ActivityLoginBinding

import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Data Binding-Instanz für das Layout "login_view" erstellen
        binding = ActivityLoginBinding.inflate(layoutInflater)
        // Setze den Inhalt der Activity auf die root-View des Bindings
        setContentView(binding.root)

        // Setze einen OnTouchListener auf das gesamte Layout
        binding.root.setOnClickListener {
            // Wechsle zur MainActivity
            val intent = Intent(this, GreetingActivity::class.java)
            startActivity(intent)
            finish() // Optional: Schließe die LoginActivity, wenn du nicht mehr zurückkehren möchtest
        }

    }
}