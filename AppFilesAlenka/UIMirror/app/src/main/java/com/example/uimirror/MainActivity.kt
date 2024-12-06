package com.example.uimirror

// importierte Packete für die Funktion der App
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.uimirror.alarm.AlarmEditorActivity
import com.example.uimirror.musik.MusicPlayer
import com.example.uimirror.musik.MyApp
import com.example.uimirror.musik.SongSelectionActivity
import com.example.uimirror.database.PersonDatabase
import com.example.uimirror.database.models.Person
import com.example.uimirror.databinding.ActivityMainBinding
import com.example.uimirror.database.DatabaseUtils
import com.example.uimirror.events.EventsListBottomSheet
import com.example.uimirror.security.KeystoreManager
import com.example.uimirror.events.EventsListingActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


// MainActivity ist die Hauptklasse der App, die von AppCompatActivity erbt
class MainActivity : AppCompatActivity() {

    // Spätere Initialisierung der UI-Binding und Kamera-Executor Variablen
    private lateinit var binding: ActivityMainBinding // UI-Bindings für die Aktivität
    private lateinit var cameraManager: CameraManager //Instanz von CameraManager
    private lateinit var permissionHandler: PermissionHandler // Instanz von PermissionHandler
    private lateinit var musicPlayer: MusicPlayer // Instanz von MusicPlayer

/*
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "person_database"
        ) .fallbackToDestructiveMigration()  // Daten werden bei jeder Versionsänderung gelöscht
            .build()
    }
*/
/*
    private val encryptedDatabase by lazy {
        val passphrase = SQLiteDatabase.getBytes(KeystoreManager.getPassphrase())
        val factory = SupportFactory(passphrase)
        Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "encrypted_person_database" // New encrypted database name
        )
            .openHelperFactory(factory)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Step 1: Backup unencrypted database
        val isBackupSuccessful = DatabaseUtils.backupDatabase(this)
        if (!isBackupSuccessful) {
            Toast.makeText(this, "Database backup failed. Aborting migration.", Toast.LENGTH_LONG).show()
            return
        }

        // Step 2: Migrate to encrypted database
        migrateToEncryptedDatabase()

        // Initialize OpenCV and other components
        initializeOpenCV()
        initializeComponents()
        setupUIListeners()

        CoroutineScope(Dispatchers.Main).launch {
            primaryUser = encryptedDatabase.uiMirrorDao().getPrimaryUser(true)!!
            ckeckforAGB()
        }
    }

    private fun migrateToEncryptedDatabase() {
        val passphrase = SQLiteDatabase.getBytes(KeystoreManager.getPassphrase())
        val factory = SupportFactory(passphrase)

        // Open the unencrypted database
        val unencryptedDatabase = Room.databaseBuilder(
            applicationContext,
            PersonDatabase::class.java,
            "person_database" // Current unencrypted database name
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Retrieve all data from the unencrypted database
                val allPersons = unencryptedDatabase.uiMirrorDao().getAllPersons()

                // Open the encrypted database
                val encryptedDatabase = Room.databaseBuilder(
                    applicationContext,
                    PersonDatabase::class.java,
                    "encrypted_person_database" // New encrypted database name
                )
                    .openHelperFactory(factory)
                    .build()

                // Insert all data into the encrypted database
                encryptedDatabase.uiMirrorDao().insertAll(allPersons)

                // Close and delete the old database
                unencryptedDatabase.close()
                applicationContext.deleteDatabase("person_database")
                Log.i("MainActivity", "Migration to encrypted database completed successfully!")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "Migration failed: ${e.message}")
            }
        }
    }
*/

    //NEW CODE AFTER MIGRATION IS COMPLETE


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

    // Getter-Methoden für den Zugriff im PermissionHandler
    fun getCameraManager(): CameraManager = cameraManager
    fun getBinding(): ActivityMainBinding = binding
    private lateinit var primaryUser: Person

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initializeOpenCV()
        initializeComponents()
        setupUIListeners()
        CoroutineScope(Dispatchers.Main).launch {
            primaryUser = database.uiMirrorDao().getPrimaryUser(true)!!
            ckeckforAGB()
        }


        // Berechtigungen beim Start überprüfen
        //requestCameraPermissions()

        if(!permissionHandler.isNotificationPermissionGranted()){
            permissionHandler.showPermissionNotificationDeniedDialog()
        }
        // Request storage permissions if not already granted
        if (!permissionHandler.isStoragePermissionGranted()) {
            permissionHandler.requestStoragePermissions()
        }

    }

    fun initializeOpenCV() {
        //Initialize OpenCVLibrary
        if (OpenCVLoader.initDebug()) {
            Log.i("MainActivity", "OpenCV loaded successfully");
        } else {
            Log.e("MainActivity", "OpenCV initialization failed!");
            Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private fun initializeComponents() {
        // Initialisiere die Klassen für Kamera und Berechtigungen
        cameraManager = CameraManager(this, binding.previewView, database, false)
        permissionHandler = PermissionHandler(this)
        musicPlayer = (applicationContext as MyApp).musicPlayer // Musikplayer initialisieren

        // Berechtigungen beim Start überprüfen

        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            // Erklären warum Berechtigung benötigt
            permissionHandler.showPermissionCameraDeniedDialog()
        }

        //musicPlayer.playMainSong()
        if (musicPlayer.isMusicEnabled()) {
            musicPlayer.playMainSong()
        }
    }


    private fun setupUIListeners() {
        // Setze den OnClickListener für das Kalender-Icon
        binding.playIcon?.setOnClickListener {
            musicPlayer.pauseMainSong()
            val intent = Intent(this, SongSelectionActivity::class.java)
            startActivity(intent)
        }

        // Setze den OnClickListener für das Alarm-Icon
        binding.alarmIcon?.setOnClickListener {
            val intent = Intent(this, AlarmEditorActivity::class.java)
            startActivity(intent)
        }

        // Setze den OnClickListener für das zurück zum Login (mail Icon)
        binding.calendarIcon?.setOnClickListener {
            val eventsBottomSheetDialogFragment = EventsListBottomSheet()
            eventsBottomSheetDialogFragment.show(supportFragmentManager, "EventListScreen")
        }

        // Setze den OnClickListener für das zurück zum Login (mail Icon)
        binding.somethingIcon?.setOnClickListener {
            Toast.makeText(this, "Noch keine Funktion", Toast.LENGTH_LONG).show()
        }

        //logout
        binding.buttonlogout?.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Abmelden")
                .setMessage("Möchten Sie sich wirklich abmelden?")
                .setPositiveButton("Logout") { dialog, _ ->
                    dialog.dismiss()
                    CoroutineScope(Dispatchers.Main).launch {
                        Log.e("logout", "isPrimaryUser: ${primaryUser.isPrimaryUser}")
                        val primaryUser = database.uiMirrorDao().getPrimaryUser(true)
                        primaryUser?.isPrimaryUser = false
                        Log.e("logout", "isPrimaryUser: ${primaryUser?.isPrimaryUser}")
                    }
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)

                }
                .setNegativeButton("Abbrechen") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }

        // Setze den OnClickListener für das Settings-Icon
        binding.settingsIcon?.setOnClickListener {
            // Zeigt den Dialog an, um zur App-Einstellungsseite weiterzuleiten
            //permissionHandler.showPermissionCameraDeniedDialog()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        // Überprüfen, ob die Berechtigungen erteilt wurden, um die Kamera zu starten

        if (permissionHandler.isCameraPermissionGranted()) {
            cameraManager.startCamera()
        } else {
            // Berechtigungen fehlen, Dialog anzeigen
            permissionHandler.showPermissionCameraDeniedDialog()
        }


        // Überprüfen und abspielen des ausgewählten Songs bei rückkehr zur Activity
        if(!musicPlayer.isMainPlaying()) {
            Log.i("MainActivity", "Music is playing");
            if(musicPlayer.isMusicEnabled()){
                Log.i("MainActivity", "Music is enabled");
                musicPlayer.playMainSong()
            }
        } else{
            Log.i("MainActivity", "Music not playing");
        }
    }

    override fun onPause() {
        super.onPause()
        //  musicPlayer.pauseMainSong()
        //  musicPlayer.release()
    }

    // wird von Android autom. aufgerufen, enn Berechtigungsanfrage bearbeitet wurde.

    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.handlePermissionsResult(requestCode, grantResults)
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraManager.shutdown() // Stoppt die Kamera, wenn die Aktivität zerstört wird
        musicPlayer.release()
    }

    // Neue Methode zum Anzeigen einer Benachrichtigung
    fun showNotification(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun ckeckforAGB(){
        CoroutineScope(Dispatchers.Main).launch {
            Log.e("logout", "isPrimaryUser: ${primaryUser.isPrimaryUser}")
            val primaryUser = database.uiMirrorDao().getPrimaryUser(true)
            if (primaryUser?.isAGBread != null && primaryUser.isAGBread != true) {
                Log.i("CheckAGB", "AGB checked: ${primaryUser.isAGBread}")
                 MaterialAlertDialogBuilder(this@MainActivity)
                .setTitle("Datenschutz & Nutzungsbedingungen")
                .setMessage(
                    "Danke, dass du unsere App nutzt! Hier sind einige wichtige Informationen zum Datenschutz und zur Nutzung:\n\n" +
                            "1. **Datenspeicherung:** Alle von dir eingegebenen Daten, wie z. B. persönliche Einstellungen und Präferenzen, werden sicher auf deinem Gerät in einer lokalen Room-Datenbank gespeichert. Diese Daten bleiben nur auf deinem Gerät und werden nicht an externe Server weitergegeben.\n\n" +
                            "2. **Datensicherung:** Deine Daten werden automatisch gesichert und verschlüsselt, um ihre Sicherheit zu gewährleisten. So stellen wir sicher, dass deine Informationen geschützt sind und keine unbefugten Zugriffe möglich sind.\n\n" +
                            "3. **Berechtigungen:** Für die Nutzung der App benötigen wir bestimmte Berechtigungen, z. B. den Zugriff auf Kamera und Speicher. Diese Berechtigungen werden ausschließlich für die Funktionen der App genutzt und nicht für andere Zwecke verwendet.\n\n" +
                            "4. **Datenlöschung:** Du kannst jederzeit auf deine gespeicherten Daten zugreifen und diese in den App-Einstellungen löschen. Wir speichern deine Daten nur so lange, wie es für den Betrieb der App erforderlich ist.\n\n" +
                            "5. **Verantwortung:** Obwohl wir alle notwendigen Sicherheitsvorkehrungen getroffen haben, bist du für die Sicherheit deiner Daten verantwortlich. Wir übernehmen keine Haftung für unbefugten Zugriff oder Datenverlust, der außerhalb unserer Kontrolle liegt.\n\n" +
                            "Indem du auf 'OK' klickst, bestätigst du, dass du diese Informationen zur Kenntnis genommen hast."
                )
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    CoroutineScope(Dispatchers.Main).launch {
                    primaryUser.isAGBread = true
                    database.uiMirrorDao().updatePerson(primaryUser)
                }
                }
                .setCancelable(false)
                .show()
            }
        }
    }

}

