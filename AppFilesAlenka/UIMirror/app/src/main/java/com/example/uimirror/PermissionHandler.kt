package com.example.uimirror

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Diese Klasse verwaltet die Berechtigungen, die für den Zugriff auf die Kamera erforderlich sind.

class PermissionHandler(private val activity: Activity)  { // Mainactivity wird an PermissionHandler übergeben
    // Überprüft, ob alle erforderlichen Berechtigungen gewährt wurden. (true wenn ja , false wenn nicht)
   /* fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(activity.baseContext, it) == PackageManager.PERMISSION_GRANTED
    }*/

    // Überprüft, ob die Kamera-Berechtigung in den App-Einstellungen gewährt ist
    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    // Überprüft, ob die Benachrichtigungs-Berechtigung gewährt ist
    fun isNotificationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }
    fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    // Fordert die Kamera-Berechtigung an oder startet die Kamera, wenn die Berechtigung bereits vorhanden ist
    fun requestCameraPermissions() {
        if (isCameraPermissionGranted()) {
            // Wenn Berechtigungen bereits erteilt wurden, Kamera starten
           // activity.getCameraManager().startCamera()
            (activity as MainActivity).getCameraManager().startCamera()

        } else {
            // Wenn Berechtigungen fehlen -> Zeigt eine Meldung an und fordert die Berechtigung an
            Toast.makeText(activity, "Bitte erlauben Sie den Kamerazugriff", Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CODE_CAMERA_PERMISSION)
        }
    }
    fun requestStoragePermissions() {
        if (!isStoragePermissionGranted()) {
            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUEST_CODE_STORAGE_PERMISSION)
        }
    }

    // Fordert die Benachrichtigungs-Berechtigung an
    fun requestNotificationPermission() {
        if (!isNotificationPermissionGranted()) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION_PERMISSION)
        }
    }

    // Handle permission rationale for storage
    fun explainStoragePermissionRationale() {
        MaterialAlertDialogBuilder(activity)
            .setTitle("Berechtigung für Speicherzugriff erforderlich")
            .setMessage("Die App benötigt Zugriff auf den Speicher, um Dateien zu speichern und zu lesen.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                requestStoragePermissions()
            }
            .setCancelable(false)
            .show()
    }

    // Zeigt einen Dialog an, wenn die Kamera-Berechtigung verweigert wurde und öffnet die Einstellungen
    fun showPermissionCameraDeniedDialog() {

        MaterialAlertDialogBuilder(activity)
            .setTitle("Kamerazugriff benötigt")
            .setMessage("Um diese Funktion zu nutzen, benötigt die App Zugriff auf die Kamera. Bitte aktivieren Sie die Berechtigung in den Einstellungen.")
            .setPositiveButton("Zu den Einstellungen") { dialog, _ ->
                dialog.dismiss()
                openAppSettings() // Öffnet die App-Einstellungen für manuelle Berechtigungserteilung
            }
            .setNegativeButton("Abbrechen") { dialog, _ ->
                dialog.dismiss()
                // Zeigt Meldung an wenn beim Abbruch Kamerazugriff nicht gewährt ist...
                if(!isCameraPermissionGranted()) {
                    Toast.makeText(
                        activity,
                        "Kamerazugriff verweigert. Die Funktion ist eingeschränkt.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setCancelable(false)
            .show()
    }

    // Dialog anzeigen, wenn die Berechtigung verweigert wurde
    fun showPermissionNotificationDeniedDialog() {

        MaterialAlertDialogBuilder(activity)
            .setTitle("Benachrichtigung benötigt")
            .setMessage("Um die Alarmfunktion zu nutzen, benötigt die App Zugriff auf die Benachrichtigungen. Bitte aktivieren Sie die Berechtigung in den Einstellungen.")
            .setPositiveButton("Zu den Einstellungen") { dialog, _ ->
                dialog.dismiss()
                openAppSettings()
            }
            .setNegativeButton("Abbrechen") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(activity, "Benachrichtigungen verweigert. Die Funktion ist eingeschränkt.", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }
    // Öffnet die App-Einstellungen für die manuelle Erteilung der Berechtigungen
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }

    // Diese Methode wird aufgerufen, wenn das Ergebnis der Berechtigungsanfrage empfangen wird
    fun handlePermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Kamera starten, wenn die Berechtigung erteilt wurde
                //activity.getCameraManager().startCamera()
                (activity as MainActivity).getCameraManager().startCamera()
            } else if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               // activity.showNotification("Benachrichtigungen sind aktiviert!")
                (activity as MainActivity).showNotification("Benachrichtigungen sind aktiviert!")
                }
            else if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, "Speicherberechtigungen gewährt", Toast.LENGTH_SHORT).show()
                } else {
                    explainStoragePermissionRationale()
                }
            }
            else {
                // Zeige einen Dialog, wenn die Berechtigung verweigert wurde
                showPermissionCameraDeniedDialog()
            }
        }
    }

    companion object {
        const val REQUEST_CODE_NOTIFICATION_PERMISSION = 11
        const val REQUEST_CODE_CAMERA_PERMISSION = 10 // Code für die Berechtigungsanforderung
        const val REQUEST_CODE_STORAGE_PERMISSION = 12
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) // Erforderliche Berechtigungen

    }
}

/*
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
    } // schliesst Dialog und gibt Meldung Kamerazugriff sei verweigert.
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
}*/