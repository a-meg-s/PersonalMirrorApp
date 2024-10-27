package com.example.uimirror

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uimirror.databinding.ActivityGreetingBinding

class GreetingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGreetingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Data Binding-Instanz für das Layout "login_view" erstellen
        binding = ActivityGreetingBinding.inflate(layoutInflater)
        // Setze den Inhalt der Activity auf die root-View des Bindings
        setContentView(binding.root)

        // Setze einen OnTouchListener auf das gesamte Layout
        binding.root.setOnClickListener {
            // Wechsle zur MainActivity
              val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Optional: Schließe die LoginActivity, wenn du nicht mehr zurückkehren möchtest
            }

        }
    }
