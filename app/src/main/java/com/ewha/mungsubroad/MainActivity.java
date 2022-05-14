package com.ewha.mungsubroad;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.ivs.broadcast.BroadcastException;
import com.amazonaws.ivs.broadcast.BroadcastSession;
import com.amazonaws.ivs.broadcast.Device;
import com.amazonaws.ivs.broadcast.ImageDevice;
import com.amazonaws.ivs.broadcast.ImagePreviewView;
import com.amazonaws.ivs.broadcast.Presets;

public class MainActivity extends AppCompatActivity {
    Button broadbutton;
    Button stopbutton;

    // eventlistener -> 상태 업데이트, 오류 및 세션 변경
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        // 권한 확인
        final String[] requiredPermissions = { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                // If any permissions are missing we want to just request them all.
                ActivityCompat.requestPermissions(MainActivity.this, requiredPermissions, 0x100);
                break;
            }
        }

        broadbutton = (Button)findViewById(R.id.startbroad);
        stopbutton = (Button)findViewById(R.id.stopbroad);

        // Create broadcastSession
        Context ctx = getApplicationContext();
        BroadcastSession broadcastSession = new BroadcastSession(ctx, broadcastListener, Presets.Configuration.STANDARD_LANDSCAPE, Presets.Devices.BACK_CAMERA(ctx));

        broadcastSession.awaitDeviceChanges(() -> {
            for(Device device: broadcastSession.listAttachedDevices()) {
                // Find the camera we attached earlier
                if(device.getDescriptor().type == Device.Descriptor.DeviceType.CAMERA) {
                    LinearLayout previewHolder = findViewById(R.id.videoView);
                    ImagePreviewView preview = ((ImageDevice)device).getPreviewView();
                    preview.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    previewHolder.addView(preview);
                }
            }
        });

        // Broad 버튼 눌렀을 때
        broadbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                broadcastSession.start("rtmps://3c90cab980bd.global-contribute.live-video.net:443/app/", "sk_ap-northeast-2_yFcoiZCkIOLg_vKCoVZPK8UBYWahI8dUXOVUmQlwASj");

                Toast.makeText(ctx,"CCTV 촬영을 시작합니다", Toast.LENGTH_SHORT).show();
                Toast.makeText(ctx,"네트워크와 전원 연결을 유지해 주세요", Toast.LENGTH_SHORT).show();
            }
        });

        // Stop 버튼 눌렀을 때
        stopbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               broadcastSession.stop();

               Toast.makeText(ctx,"CCTV 촬영을 종료합니다", Toast.LENGTH_SHORT).show();
           }
        });


    }
}
