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
import com.example.uimirror.CameraManager
import com.example.uimirror.PermissionHandler
import com.example.uimirror.R
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Event
import com.example.uimirror.database.models.Person
import com.example.uimirror.databinding.ActivityEventsBinding
import com.example.uimirror.security.KeystoreManager
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class EventsListingActivity : AppCompatActivity(), EventsAdapter.EventsClickInterface {

    private lateinit var binding: ActivityEventsBinding
    private lateinit var adapter: EventsAdapter
    private var eventsList: MutableList<Event> = mutableListOf()
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var cameraManager: CameraManager

    val database by lazy {
        val passphrase = SQLiteDatabase.getBytes(KeystoreManager.getPassphrase())
        val factory = SupportFactory(passphrase)
        Room.databaseBuilder(
            this.applicationContext,
            PersonDatabase::class.java,
            "encrypted_person_database"
        )
            .openHelperFactory(factory)
            .build()
    }

    private lateinit var primaryUser: Person

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisiere Kamera und Permissionhandler (damit Preview funktioniert)
        cameraManager = CameraManager(this, findViewById(R.id.previewView), database, false)
        permissionHandler = PermissionHandler(this)

        // Kamera starten, wenn Berechtigung gewährt ist
        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            permissionHandler.showPermissionCameraDeniedDialog()
        }

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

        // Initialisiere Kamera und Permissionhandler (damit Preview funktioniert)
        cameraManager = CameraManager(this, findViewById(R.id.previewView), database, false)
        permissionHandler = PermissionHandler(this)

        // Kamera starten, wenn Berechtigung gewährt ist
        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            permissionHandler.showPermissionCameraDeniedDialog()
        }

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