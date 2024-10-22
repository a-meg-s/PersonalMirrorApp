package com.example.uimirror

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.uimirror.MainActivity

// Diese Klasse verwaltet die Berechtigungen, die für den Zugriff auf die Kamera erforderlich sind.
class PermissionHandler (private val activity: MainActivity)  {
    // Überprüft, ob alle erforderlichen Berechtigungen gewährt wurden.
    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(activity.baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /*fun isNotificationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    fun requestNotificationPermission() {
        activity.requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }*/

    // Dialog anzeigen, wenn die Berechtigung verweigert wurde
    fun showPermissionDeniedDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Kamerazugriff benötigt")
        builder.setMessage("Um diese Funktion zu nutzen, benötigt die App Zugriff auf die Kamera. Bitte aktivieren Sie die Berechtigung in den Einstellungen.")
        builder.setPositiveButton("Zu den Einstellungen") { dialog, _ ->
            dialog.dismiss()
            // Öffnet die App-Einstellungen, um die Berechtigungen manuell zu ändern.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.fromParts("package", activity.packageName, null)
            }
            activity.startActivity(intent)
        }
        builder.setNegativeButton("Abbrechen") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(activity, "Kamerazugriff verweigert. Die Funktion ist eingeschränkt.", Toast.LENGTH_SHORT).show()
        }
        builder.setCancelable(false) // Verhindert das Schließen des Dialogs, wenn außerhalb geklickt wird
        builder.create().show() // Zeigt den Dialog an
    }

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10 // Code für die Berechtigungsanforderung
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA) // Erforderliche Berechtigungen
    }
}

