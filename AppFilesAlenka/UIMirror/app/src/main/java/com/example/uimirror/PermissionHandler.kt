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
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Diese Klasse verwaltet die Berechtigungen, die für den Zugriff auf die Kamera erforderlich sind.

class PermissionHandler (private val activity: MainActivity)  { // Mainactivity wird an PermissionHandler übergeben
    // Überprüft, ob alle erforderlichen Berechtigungen gewährt wurden. (true wenn ja , false wenn nicht)
   /* fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(activity.baseContext, it) == PackageManager.PERMISSION_GRANTED
    }*/
    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun isNotificationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    // Anfrage für Kamera-Berechtigung
    fun requestCameraPermissions() {
        if (isCameraPermissionGranted()) {
            // Wenn Berechtigungen bereits erteilt wurden, Kamera starten
            activity.getCameraManager().startCamera()
        } else {
            // Wenn Berechtigungen fehlen, den Benutzer darüber informieren
            Toast.makeText(activity, "Bitte erlauben Sie den Kamerazugriff", Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CODE_CAMERA_PERMISSION)
        }
    }

    fun requestNotificationPermission() {
        if (!isNotificationPermissionGranted()) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION_PERMISSION)
        }
    }


    // Zeige eine Erklärung, warum die Berechtigung benötigt wird
    fun explainPermissionRationale() {
        MaterialAlertDialogBuilder(activity)
            .setTitle("Berechtigung erforderlich")
            .setMessage("Die Anwendung benötigt die Kameraberechtigung, um die Mirror- und Gesichtserkennung-Funktionalität zu ermöglichen. Ohne diese Berechtigung funktioniert die App nicht vollständig.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                requestCameraPermissions() // Fordere Berechtigung an, wenn der Dialog geschlossen wird
            }
            .setCancelable(false)
            .show()
    }

    // Dialog anzeigen, wenn die Berechtigung verweigert wurde
    fun showPermissionCameraDeniedDialog() {

        MaterialAlertDialogBuilder(activity)
            .setTitle("Kamerazugriff benötigt")
            .setMessage("Um diese Funktion zu nutzen, benötigt die App Zugriff auf die Kamera. Bitte aktivieren Sie die Berechtigung in den Einstellungen.")
            .setPositiveButton("Zu den Einstellungen") { dialog, _ ->
                dialog.dismiss()
                openAppSettings()
            }
            .setNegativeButton("Abbrechen") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(activity, "Kamerazugriff verweigert. Die Funktion ist eingeschränkt.", Toast.LENGTH_SHORT).show()
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
                activity.getCameraManager().startCamera()
            } else if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                activity.showNotification("Benachrichtigungen sind aktiviert!")
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
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA) // Erforderliche Berechtigungen

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