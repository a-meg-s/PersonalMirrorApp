package com.example.uimirror.events

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.uimirror.R
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Event
import com.example.uimirror.database.models.Person
import com.example.uimirror.databinding.BottomsheetEventsBinding
import com.example.uimirror.events.EventsAdapter.EventsClickInterface
import com.example.uimirror.security.KeystoreManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class EventsListBottomSheet : BottomSheetDialogFragment(), EventsClickInterface {

    private lateinit var binding: BottomsheetEventsBinding
    private lateinit var primaryUser: Person
    private lateinit var database: PersonDatabase
    private lateinit var adapter: EventsAdapter
    private var eventsList: MutableList<Event> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetEventsBinding.inflate(inflater, container, false)

        //Option 2:
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        activity?.let { activity ->
            val passphrase = SQLiteDatabase.getBytes(KeystoreManager.getPassphrase())
            val factory = SupportFactory(passphrase)

            database = Room.databaseBuilder(
                activity.applicationContext,
                PersonDatabase::class.java,
                "encrypted_person_database"
            )
                .openHelperFactory(factory)
                .build()
        }

        // this creates a vertical layout Manager
        binding.rvEvents.layoutManager = LinearLayoutManager(this.activity)

        // This will pass the ArrayList to our Adapter
        adapter = EventsAdapter(eventsList, this@EventsListBottomSheet)

        // Setting the Adapter with the recyclerview
        binding.rvEvents.adapter = adapter

        toggleEmptyListText(eventsList)

        binding.fabAddEvent.setOnClickListener {
            /*val intent = Intent(this.activity, AddEventActivity::class.java)
            startActivity(intent)*/
            activity?.let {
                val addEventBottomSheet = AddEventBottomSheet()
                addEventBottomSheet.show(it.supportFragmentManager, "AddEventBottomSheet")
            }
        }



        return binding.root
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
        this.activity?.let {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure you want to delete this event?")
            builder.setPositiveButton("Yes") { _, _ ->
                lifecycle.coroutineScope.launch {
                    database.uiMirrorDao().deleteEvent(event.id)
                    primaryUser.events.remove(event)
                    database.uiMirrorDao().insertPerson(primaryUser)
                    eventsList.remove(event)
                    adapter.updateList(eventsList)
                    toggleEmptyListText(eventsList)
                }

                Toast.makeText(
                    it.applicationContext,
                    "Event deleted successfully!", Toast.LENGTH_SHORT
                ).show()
            }

            builder.setNegativeButton("No") { _, _ ->

            }

            builder.show()
        }
    }
}