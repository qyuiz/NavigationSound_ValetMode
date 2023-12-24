package com.example.navigationsound_valetmode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView serviceStatusTextView;
    private Button valetModeButton;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private Handler handler = new Handler();
    private boolean isValetMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navigation音声関連
        Button startServiceButton = findViewById(R.id.startServiceButton);
        Button stopServiceButton = findViewById(R.id.stopServiceButton);
        serviceStatusTextView = findViewById(R.id.serviceStatusTextView);

        // ValetMode関連
        valetModeButton = findViewById(R.id.valetModeButton);

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMyService();
            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMyService();
            }
        });

        valetModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // サービスは停止してフォーカス放棄
                stopMyService();

                // VALET MODE ON/OFF
                audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//                audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
//                        .setAudioAttributes(new AudioAttributes.Builder()
//                                .setUsage(AudioAttributes.USAGE_VALET_MODE)
//                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                                .build())
//                        .setOnAudioFocusChangeListener(focusChangeListener, handler)
//                        .build();
                if (!isValetMode){
//                    int ret = audioManager.requestAudioFocus(audioFocusRequest);
//                    if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
//                        valetModeButton.setText("VALET MODE OFF");
//                        isValetMode = true;
//                    }
//                    else if (ret == AudioManager.AUDIOFOCUS_LOSS){
//                        valetModeButton.setText("VALET MODE ON");
//                    }
                    isValetMode = true;
                    valetModeButton.setText("VALET MODE OFF");
                }
                else {
                    isValetMode = false;
//                    audioManager.abandonAudioFocusRequest(audioFocusRequest);
                    valetModeButton.setText("VALET MODE ON");
                }

            }
        });
    }

    private AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
                valetModeButton.setText("VALET MODE ON");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                valetModeButton.setText("VALET MODE OFF");
            }
        }
    };

    private void startMyService() {
        Intent serviceIntent = new Intent(this, NavigationService.class);
        startService(serviceIntent);
        serviceStatusTextView.setText("Navigation Status: Running");
    }

    private void stopMyService() {
        Intent serviceIntent = new Intent(this, NavigationService.class);
        stopService(serviceIntent);
        serviceStatusTextView.setText("Navigation Status: Stopped");
    }
}