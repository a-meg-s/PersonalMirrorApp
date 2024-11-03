package com.example.uimirror

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.coroutineScope
import com.example.uimirror.database.models.Alarm
import com.example.uimirror.database.models.Person
import com.example.uimirror.databinding.ActivityAlarmEditorBinding
import kotlinx.coroutines.launch
import java.util.*

class AlarmEditorActivity : AppCompatActivity() {

    private var primaryUser: Person? = null
    private lateinit var binding: ActivityAlarmEditorBinding
    private lateinit var alarmManager: AlarmManager
    private val REQUEST_SCHEDULE_EXACT_ALARM = 123 // Unique request code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycle.coroutineScope.launch {
            primaryUser =
                UiMirrorApplication.database.personDao().getPrimaryUser(true)

        }

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        createNotificationChannel()

        // Set Alarm Button
        binding.setAlarmButton.setOnClickListener {
            if (checkExactAlarmPermission()) {
                setAlarm()
            } else {
                requestExactAlarmPermission()
            }
        }

        // Cancel Button
        binding.cancelButton.setOnClickListener { finish() }
    }

    private fun setAlarm() {
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        val title = binding.textView.text.toString()
        val message = "Your alarm is set!"

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val time = getTime()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For API level 23 and above, use setExactAndAllowWhileIdle
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        } else {
            // For API level 21 to 22, use setExact
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        }


        //Save the alarm to database
        primaryUser?.alarm = Alarm(dateTime = time)

        primaryUser?.let {
            lifecycle.coroutineScope.launch {
                UiMirrorApplication.database.personDao().insertPerson(it)
            }
        }
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage("Title: $title\nMessage: $message\nAt: ${timeFormat.format(Date(time))}")
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    private fun getTime(): Long {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel"
            val descriptionText = "Channel for alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkExactAlarmPermission(): Boolean {
        // Use AlarmManager.canScheduleExactAlarms() to check if permission is granted
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Redirect to settings if permission is not granted
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }
    }
}
