package com.example.uimirror.events

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.room.Room
import com.example.uimirror.R
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Event
import com.example.uimirror.databinding.ActivityAddEventBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar


class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding
    var calendar: Calendar = Calendar.getInstance()
    var calendarView: CalendarView? = null
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "person_database"
        ).build()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        calendarView = findViewById<CalendarView>(R.id.calendarView)
        val tvTimeSet = findViewById<TextView>(R.id.tvTimeSet)
        calendarView?.minDate = Calendar.getInstance().timeInMillis

        tvTimeSet.visibility = View.VISIBLE
        tvTimeSet.text = "Event set for ${readDate(calendar.timeInMillis)}"

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            tvTimeSet.visibility = View.VISIBLE
            tvTimeSet.text = "Event set for ${readDate(calendar.timeInMillis)}"
        }

        val timePickerDialog = TimePickerDialog(
            this,
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
                    Toast.makeText(this, "Please enter Event Name", Toast.LENGTH_SHORT).show();
                } else {
                    lifecycle.coroutineScope.launch {
                        val event = Event(dateTime = calendar.timeInMillis, eventName = it.toString())
                        database.uiMirrorDao().insertEvent(event)
                        Toast.makeText(this@AddEventActivity, "Event Created!", Toast.LENGTH_SHORT).show();
                        finish()
                    }
                }
            }
        }
    }


    private fun readDate(time: Long): String? {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a")
        return formatter.format(time)
    }
}