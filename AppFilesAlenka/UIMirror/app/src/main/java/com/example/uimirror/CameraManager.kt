package com.example.uimirror

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// Diese Klasse verwaltet die Kamera und ihre Vorschau
class CameraManager (private val context: Context, private val previewView: PreviewView?) {
    // Executor für die Kameraoperationen
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    // Startet die Kamera
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                // Setzt die Oberfläche für die Vorschau
                it.setSurfaceProvider(previewView?.surfaceProvider)
            }


            try {
                cameraProvider.unbindAll() // Trennt alle vorherigen Bindungen (wenn kamera schon verwendet wurde)
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // Wählt die Rückkamera aus
                // Bindet die Kamera an den Lebenszyklus der Aktivität
                cameraProvider.bindToLifecycle(context as androidx.lifecycle.LifecycleOwner, cameraSelector, preview)
            } catch (exc: Exception) {
                Log.e("CameraManager", "Kamera-Bindung fehlgeschlagen: ${exc.message}") // Fehlerprotokollierung
            }
        }, ContextCompat.getMainExecutor(context)) // Führt den Listener im Hauptthread aus
    }

    // Stoppt die Kamera
    fun shutdown() {
        cameraExecutor.shutdown() // Beendet den Executor
    }
}