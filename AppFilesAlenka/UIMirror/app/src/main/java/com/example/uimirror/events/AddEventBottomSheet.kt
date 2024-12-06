package com.example.uimirror.events

import android.R
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CalendarView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.coroutineScope
import androidx.room.Room
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Event
import com.example.uimirror.database.models.Person
import com.example.uimirror.databinding.BottomsheetAddEventBinding
import com.example.uimirror.security.KeystoreManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.text.SimpleDateFormat
import java.util.Calendar


class AddEventBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomsheetAddEventBinding
    private lateinit var database: PersonDatabase
    var calendar: Calendar = Calendar.getInstance()
    var calendarView: CalendarView? = null
    private lateinit var primaryUser: Person

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
        binding = BottomsheetAddEventBinding.inflate(inflater, container, false)

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

        calendarView = binding.calendarView
        calendarView?.minDate = Calendar.getInstance().timeInMillis
        lifecycle.coroutineScope.launch {
            val allPersons = getAllPersons()
            primaryUser = database.uiMirrorDao().getPrimaryUser(true) ?: allPersons.first()
        }
        binding.tvTimeSet.visibility = View.VISIBLE
        binding.tvTimeSet.text = "Event set for ${readDate(calendar.timeInMillis)}"

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            binding.tvTimeSet.visibility = View.VISIBLE
            binding.tvTimeSet.text = "Event set for ${readDate(calendar.timeInMillis)}"
        }

        val timePickerDialog = TimePickerDialog(
            this.activity,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        calendarView?.setOnDateChangeListener { _, year, month, day ->
            calendar.set(year, month, day)
            timePickerDialog.show()
        }


        binding.btnAddEvent.setOnClickListener {
            binding.eventName.text?.let {
                if (it.isEmpty()) {
                    Toast.makeText(this.activity, "Please enter Event Name", Toast.LENGTH_SHORT).show();
                } else {
                    lifecycle.coroutineScope.launch {
                        val event = Event(dateTime = calendar.timeInMillis, eventName = it.toString())
                        primaryUser.events.add(event)
                        database.uiMirrorDao().insertPerson(primaryUser)
                        Toast.makeText(this@AddEventBottomSheet.activity, "Event Created!", Toast.LENGTH_SHORT).show();
                        dismiss()
                    }
                }
            }
        }

        return binding.root
    }


    suspend fun getAllPersons(): List<Person> {
        val persons = database.uiMirrorDao().getAllPersons()
        return persons
    }

    private fun readDate(time: Long): String? {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a")
        return formatter.format(time)
    }
}