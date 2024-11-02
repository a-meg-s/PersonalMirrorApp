package com.example.database

import android.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.coroutineScope
import com.example.MyAdapter
import com.example.MyApplication
import com.example.database.databinding.ActivityMainBinding
import com.example.database.store.Music
import com.example.database.store.Person
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lifecycle.coroutineScope.launch {
            addPersonsIfNeeded()
            populateSpinner(getAllPersons())
        }

    }

    private fun populateSpinner(persons: List<Person>) {
        val adapter = MyAdapter(this, R.layout.simple_spinner_item, persons)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner.setAdapter(adapter)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.alarmClock.text = "Alarm: ".plus(persons[position].alarm)
            }

        }
    }

    private suspend fun addPersonsIfNeeded(){
        if (getAllPersons().isEmpty()) {
            MyApplication.database.personDao().insertAll(
                listOf(
                    Person(
                        name = "Maria",
                        imageIndex = 1,
                        alarm = listOf(),
                        musicTracks = listOf()
                    ),
                    Person(
                        name = "Nico",
                        imageIndex = 2,
                        alarm = listOf(),
                        musicTracks = listOf()
                    ),
                    Person(
                        name = "Alenka",
                        imageIndex = 3,
                        alarm = listOf(),
                        musicTracks = listOf(Music(2), Music(3))
                    ),
                    Person(
                        name = "Andrea",
                        imageIndex = 4,
                        alarm = listOf(),
                        musicTracks = listOf(Music(1), Music(2), Music(3))
                    )
                )
            )
        }
    }

    suspend fun getAllPersons(): List<Person> {
        val persons = MyApplication.database.personDao().getAllPersons()

        if (persons.isEmpty()) {
            Toast.makeText(this, "No persons found", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "${persons.size} Persons found", Toast.LENGTH_SHORT).show()
        }
        return persons
    }
}