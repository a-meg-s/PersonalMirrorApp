import android.os.Bundle;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.Context;

public class MainActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a RelativeLayout to contain the camera feed and the buttons
        RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        // Create a Camera instance
        mCamera = getCameraInstance();
        if (mCamera == null) {
            Toast.makeText(this, "Failed to access the camera", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a CameraPreview view to display the camera feed
        mPreview = new CameraPreview(this, mCamera);
        layout.addView(mPreview);

        // Create 4 floating buttons on the right-hand side of the screen
        for (int i = 1; i <= 4; i++) {
            Button button = new Button(this);
            button.setText("Button " + i);
            button.setId(i);

            // Set button layout parameters
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.setMargins(0, i * 150, 30, 0);  // Add spacing between buttons
            button.setLayoutParams(params);

            // Set OnClickListener for each button
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Button " + v.getId() + " clicked", Toast.LENGTH_SHORT).show();
                }
            });

            // Add button to the layout
            layout.addView(button);
        }

        // Set the layout as the content view
        setContentView(layout);
    }

    // A safe way to get an instance of the Camera object
    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT); // Open the front camera
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return camera;
    }

    // Release the camera when the application is closed
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // Release the camera for other applications
            mCamera = null;
        }
    }
}