package com.example.uimirror.events

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.uimirror.R
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Event
import com.example.uimirror.database.models.Person
import com.example.uimirror.databinding.ActivityEventsBinding
import kotlinx.coroutines.launch

class EventsListingActivity : AppCompatActivity(), EventsAdapter.EventsClickInterface {

    private lateinit var binding: ActivityEventsBinding
    private lateinit var adapter: EventsAdapter
    private var eventsList: MutableList<Event> = mutableListOf()
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "person_database"
        ).build()
    }
    private lateinit var primaryUser: Person

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerview = findViewById<RecyclerView>(R.id.rvEvents)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // This will pass the ArrayList to our Adapter
        adapter = EventsAdapter(eventsList, this@EventsListingActivity)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        toggleEmptyListText(eventsList)

        binding.fabAddEvent.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycle.coroutineScope.launch {
            val allPersons = getAllPersons()
            primaryUser = database.uiMirrorDao().getPrimaryUser(true) ?: allPersons.first()
            eventsList = primaryUser.events
            adapter.updateList(eventsList)
            toggleEmptyListText(eventsList)
        }
    }

    suspend fun getAllPersons(): List<Person> {
        val persons = database.uiMirrorDao().getAllPersons()
        return persons
    }

    private fun toggleEmptyListText(events: List<Event>) {
        if (events.isNotEmpty()) {
            binding.tvNoEvents.visibility = View.GONE
        } else {
            binding.tvNoEvents.visibility = View.VISIBLE
        }
    }

    override fun onDeleteEventClicked(event: Event) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this event?")
        builder.setPositiveButton("Yes") { _, _ ->
            lifecycle.coroutineScope.launch {
                database.uiMirrorDao().deleteEvent(event.id)
                eventsList.remove(event)
                adapter.updateList(eventsList)
                toggleEmptyListText(eventsList)
            }

            Toast.makeText(applicationContext,
                "Event deleted successfully!", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("No") { _, _ ->

        }

        builder.show()
    }
}