package com.example.uimirror.alarm

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.room.Room
import com.example.uimirror.CameraManager
import com.example.uimirror.PermissionHandler
import com.example.uimirror.R
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Alarm
import com.example.uimirror.database.models.Person
import com.example.uimirror.databinding.ActivityAlarmEditorBinding
import com.example.uimirror.security.KeystoreManager
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.util.*

class AlarmEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmEditorBinding
    private lateinit var alarmManager: AlarmManager
    private val REQUEST_SCHEDULE_EXACT_ALARM = 123 // Unique request code

    private lateinit var cameraManager: CameraManager // Hinzufügen der Kamera-Manager Instanz
    private lateinit var permissionHandler: PermissionHandler // Instanz von PermissionHandler
    private lateinit var primaryUser: Person



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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        createNotificationChannel()

        // Cancel Button
        binding.setCancelButton.setOnClickListener {
            finish()
        }


        // Set Alarm Button
        binding.setAlarmButton.setOnClickListener {
            if (checkExactAlarmPermission()) {
                setAlarm()
            } else {
                requestExactAlarmPermission()
            }
        }


        // Inizialisiert Kamera und Permissionhandler (damit Preview funktioniert)
        cameraManager = CameraManager(this, findViewById(R.id.previewView), database, false) // Initialisiere CameraManager mit PreviewView
        permissionHandler = PermissionHandler(this) // Initialisiere den PermissionHandler hier

        // Starte die Kamera, wenn die Berechtigung gewährt ist
        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            permissionHandler.showPermissionCameraDeniedDialog()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm() {
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        val title = binding.titleET.text.toString()
        val message = "Your alarm is set!"
        intent.putExtra(titleExtra, title)

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

        lifecycle.coroutineScope.launch {
            val allPersons = getAllPersons()
            primaryUser = database.uiMirrorDao().getPrimaryUser(true) ?: allPersons.first()
            primaryUser.alarm = Alarm(dateTime = time)

            database.uiMirrorDao().insertPerson(primaryUser)
        }



        showAlert(time, title, message)
    }

    suspend fun getAllPersons(): List<Person> {
        val persons = database.uiMirrorDao().getAllPersons()
        if (persons.isEmpty()) {
            Toast.makeText(this, "Inserting Users", Toast.LENGTH_SHORT).show()
        }
        return persons
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage("Title: " + title + "\nMessage: " + message + "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getTime(): Long {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
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

