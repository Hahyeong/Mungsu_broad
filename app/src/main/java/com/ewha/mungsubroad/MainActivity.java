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
import android.widget.Toast;

import com.amazonaws.ivs.broadcast.BroadcastException;
import com.amazonaws.ivs.broadcast.BroadcastSession;
import com.amazonaws.ivs.broadcast.Presets;

public class MainActivity extends AppCompatActivity {
    Button broadbutton;

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

    Context ctx = getApplicationContext();
    BroadcastSession = new BroadcastSession(ctx, broadcastListener, Presets.Configuration.STANDARD_PORTRAIT, Presets.Devices.FRONT_CAMERA(ctx));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        broadbutton = (Button)findViewById(R.id.startbroad);

        broadbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                if(permissionCheck == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},0);
                } else {
                    // 권한 승인받았을 때
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,1);
                }
            }
        });

    }
}
