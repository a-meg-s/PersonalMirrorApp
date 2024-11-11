package com.example.uimirror

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var settingsAdapter: SettingsAdapter
    private lateinit var musicPlayer: MusicPlayer
    private lateinit var permissionHandler: PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        musicPlayer = MusicPlayer(this)
        permissionHandler = PermissionHandler(this)

        recyclerView = findViewById(R.id.settingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val settingsList = listOf(
            SettingItem("Kamera", "Erlaubt der App, auf die Kamera zuzugreifen", R.drawable.logo_white, "camera_permission"),
            SettingItem("Benachrichtigungen", "Erlaubt der App, Benachrichtigungen zu senden", R.drawable.logo_white, "notification_permission"),
            SettingItem("Persönlicher Song", "Erlaubt das automatische Abspielen des ausgewählten Songs", R.drawable.logo_white, "song_permission")
        )

        settingsAdapter = SettingsAdapter(settingsList) { setting, isChecked ->
            when (setting.key) {
                "camera_permission" -> handleCameraPermission(isChecked)
                "notification_permission" -> handleNotificationPermission(isChecked)
                "song_permission" -> handleSongPermission(isChecked)
            }
        }

        recyclerView.adapter = settingsAdapter

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun handleCameraPermission(isChecked: Boolean) {
        if (isChecked) {
            if (!permissionHandler.isCameraPermissionGranted()) {
                permissionHandler.requestCameraPermissions()
                updateSettingsState()
            }
        } else {
            permissionHandler.showPermissionCameraDeniedDialog()
            updateSettingsState()
        }
    }

    private fun handleNotificationPermission(isChecked: Boolean) {
        if (isChecked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!permissionHandler.isNotificationPermissionGranted()) {
                    permissionHandler.requestNotificationPermission()
                    updateSettingsState()
                }
            } else {
                Toast.makeText(this, "Benachrichtigungen sind standardmässig aktiviert", Toast.LENGTH_SHORT).show()
            }
        } else {
            permissionHandler.showPermissionNotificationDeniedDialog()
            updateSettingsState()
        }

    }

    private fun handleSongPermission(isChecked: Boolean) {
        if (isChecked) {
            musicPlayer.setMusicEnabled(true)
            Toast.makeText(this, "Hauptmusik aktiviert", Toast.LENGTH_SHORT).show()
        } else {
            musicPlayer.setMusicEnabled(false)
            musicPlayer.pauseMainSong()
            Toast.makeText(this, "Hauptmusik deaktiviert", Toast.LENGTH_SHORT).show()
        }
        updateSettingsState()
    }

    private fun showPermissionDeniedDialog(permissionName: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("$permissionName-Berechtigung deaktivieren")
            .setMessage("Um die $permissionName-Berechtigung zu deaktivieren, müssen Sie die App-Einstellungen öffnen. Möchten Sie das jetzt tun?")
            .setPositiveButton("Ja") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Nein") { dialog, _ ->
                dialog.dismiss()
                updateSettingsState()
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHandler.handlePermissionsResult(requestCode, grantResults)
        updateSettingsState()
    }

    private fun updateSettingsState() {
        val updatedList = settingsAdapter.settings.map { setting ->
            when (setting.key) {
                "camera_permission" -> setting.copy(isEnabled = permissionHandler.isCameraPermissionGranted())
                "notification_permission" -> setting.copy(isEnabled = permissionHandler.isNotificationPermissionGranted())
                "song_permission" -> setting.copy(isEnabled = musicPlayer.isMusicEnabled())
                else -> setting
            }
        }
        settingsAdapter.updateSettings(updatedList)
    }

    override fun onResume() {
        super.onResume()
        updateSettingsState()
    }
}

data class SettingItem(
    val title: String,
    val description: String,
    val iconResId: Int,
    val key: String,
    var isEnabled: Boolean = false
)

class SettingsAdapter(
    var settings: List<SettingItem>,
    private val onSettingChanged: (SettingItem, Boolean) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.SettingViewHolder>() {

    class SettingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.settingIcon)
        val title: TextView = view.findViewById(R.id.settingTitle)
        val description: TextView = view.findViewById(R.id.settingDescription)
        val switch: Switch = view.findViewById(R.id.settingSwitch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_settings, parent, false)
        return SettingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        val setting = settings[position]
        holder.icon.setImageResource(setting.iconResId)
        holder.title.text = setting.title
        holder.description.text = setting.description
        holder.switch.isChecked = setting.isEnabled
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            onSettingChanged(setting, isChecked)
        }
    }

    override fun getItemCount() = settings.size

    fun updateSettings(newSettings: List<SettingItem>) {
        settings = newSettings
        notifyDataSetChanged()
    }
}


