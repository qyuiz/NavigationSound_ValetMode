package com.example.navigationsound_valetmode;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.media.AudioManager;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class NavigationService extends Service {

    private Handler handler = new Handler();
    private Runnable runnable;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showToast("Service started");

        // AudioManagerのインスタンスを取得
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // AudioFocusRequestを作成
        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build())
                .setOnAudioFocusChangeListener(focusChangeListener, handler)
                .build();

        mediaPlayer = MediaPlayer.create(this, R.raw.will_500m_to_right);
        // MediaPlayerのループ再生を無効にする
        mediaPlayer.setLooping(false);

        // 定期的な処理を実行する
        runnable = new Runnable() {
            @Override
            public void run() {
                int ret = audioManager.requestAudioFocus(audioFocusRequest);
                if (ret== AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                    // ここでMP3を再生
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 放棄
                        audioManager.abandonAudioFocusRequest(audioFocusRequest);
                    }
                });
                handler.postDelayed(this, 10000); // 10秒ごとに実行
            }
        };
        handler.post(runnable);

        return START_STICKY;
    }

    // AudioFocusChangeListenerを実装
    private AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // MediaPlayerのリソースを解放
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        audioManager.abandonAudioFocusRequest(audioFocusRequest);
        handler.removeCallbacks(runnable);
        showToast("Service stopped");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
