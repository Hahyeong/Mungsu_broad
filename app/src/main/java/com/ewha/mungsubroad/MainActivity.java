package com.ewha.mungsubroad;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.se.omapi.Session;

import com.amazonaws.ivs.broadcast.BroadcastException;
import com.amazonaws.ivs.broadcast.BroadcastSession;
import com.amazonaws.ivs.broadcast.Device;
import com.amazonaws.ivs.broadcast.ImageDevice;
import com.amazonaws.ivs.broadcast.ImagePreviewView;
import com.amazonaws.ivs.broadcast.Presets;

public class MainActivity extends AppCompatActivity {
    Button startbroad;
    Button stopbroad;
    LinearLayout previewHolder;

    // Event Listener
    BroadcastSession.Listener broadcastListener =
            new BroadcastSession.Listener() {
                @Override
                public void onStateChanged(@NonNull BroadcastSession.State state) {
                    Log.d(TAG, "State=" + state);
                }

                @Override
                public void onError(@NonNull BroadcastException exception) {
                    Log.e(TAG, "Exception: " + exception);
                }
            };

    // getApplicationContext() <-- null error 고치기
    Context ctx = getApplicationContext();
    BroadcastSession broadcastSession = new BroadcastSession(ctx,
            broadcastListener,
            Presets.Configuration.STANDARD_PORTRAIT,
            Presets.Devices.FRONT_CAMERA(ctx));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startbroad = (Button)findViewById(R.id.startbroad);
        stopbroad = (Button)findViewById(R.id.stopbroad);

        // 오디오 및 카메라 권한 설정
        final String[] requiredPermissions =
                { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };

        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // If any permissions are missing we want to just request them all.
                ActivityCompat.requestPermissions(this, requiredPermissions, 0x100);
                break;
            }
        }

        // 녹화 시작
        startbroad.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               broadcastSession.start("rtmps://3c90cab980bd.global-contribute.live-video.net:443/app/", "sk_ap-northeast-2_yFcoiZCkIOLg_vKCoVZPK8UBYWahI8dUXOVUmQlwASj");
           }
        });

        broadcastSession.awaitDeviceChanges(() -> {
            for(Device device: broadcastSession.listAttachedDevices()) {
                // Find the camera we attached earlier
                if(device.getDescriptor().type == Device.Descriptor.DeviceType.CAMERA) {
                    LinearLayout previewHolder = findViewById(R.id.previewHolder);
                    ImagePreviewView preview = ((ImageDevice)device).getPreviewView();
                    preview.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    previewHolder.addView(preview);
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions, grantResults);
        if (requestCode == 0x100) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    return;
                }
            }
        }
    }
}