package com.example.uimirror

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.uimirror.databinding.ActivityAlarmEditorBinding
import java.util.*

class AlarmEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmEditorBinding
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Alarm Einstellen Button
        binding.setAlarmButton.setOnClickListener { setAlarm() }

        // Abbrechen Button
        binding.cancelButton.setOnClickListener { finish() }
    }

    private fun setAlarm() {
        // Hole die Uhrzeit vom TimePicker
       /* val hour = binding.timePicker.hour
        val minute = binding.timePicker.minute*/
        val hour: Int
        val minute: Int

        // Abwärtskompatibilität: Verwende die ältere Methode, wenn die API kleiner als 23 ist
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = binding.timePicker.hour
            minute = binding.timePicker.minute
        } else {
            hour = binding.timePicker.currentHour
            minute = binding.timePicker.currentMinute
        }

        // Stelle den Alarmzeitpunkt ein
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Wenn die Zeit bereits vergangen ist, den Alarm für den nächsten Tag einstellen
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Erstelle einen Intent für die Alarmbenachrichtigung
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Stelle den Alarm ein
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this, "Alarm für $hour:$minute gesetzt", Toast.LENGTH_SHORT).show()
    }
}
        /*
        // Erstelle einen Intent für die Alarmbenachrichtigung
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Stelle den Alarm ein
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this, "Alarm für $hour:$minute gesetzt", Toast.LENGTH_SHORT).show()
    }
}*/
