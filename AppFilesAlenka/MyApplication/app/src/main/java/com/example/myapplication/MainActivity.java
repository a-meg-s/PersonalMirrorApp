package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.myapplication.databinding.ActivityMainBinding;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ExecutorService cameraExecutor;

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //vollbildschirm

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialisiere den Kamera-Executor
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Überprüfen, ob die Berechtigungen erteilt sind
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    // Erstellen der Kamera-Preview (Vorschau in der App)
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
                    // Kamera-Reset: Falls bereits eine Kamera aktiv ist, wird sie gestoppt (kann nur 1x verwendet werden)
                    cameraProvider.unbindAll();

                    // Kamera-Selector (Front Kamera)!!!!!!! HIER SCHAUEN AM GERAET
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                    // Binde die Kamera und die Vorschau an den Lebenszyklus der Activity
                    cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, preview);

                } catch (Exception exc) {
                    Log.e("CameraBind", "Kamera-Bindung fehlgeschlagen: " + exc.getMessage());
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Kamerazugriff benötigt");
        builder.setMessage("Um diese Funktion zu nutzen, benötigt die App Zugriff auf die Kamera. Bitte aktivieren Sie die Berechtigung in den Einstellungen.");
        builder.setPositiveButton("Zu den Einstellungen", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(intent);
        });
        builder.setNegativeButton("Abbrechen", (dialog, which) -> {
            dialog.dismiss();
            Toast.makeText(this, "Kamerazugriff verweigert. Die Funktion ist eingeschränkt.", Toast.LENGTH_SHORT).show();
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            showPermissionDeniedDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
