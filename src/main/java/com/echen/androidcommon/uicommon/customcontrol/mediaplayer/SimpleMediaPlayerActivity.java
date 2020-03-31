package com.echen.androidcommon.uicommon.customcontrol.mediaplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;

import com.echen.androidcommon.R;
import com.echen.androidcommon.uicommon.BaseActivity;

import java.io.IOException;

/**
 * Created by echen on 3/2/2020
 */
public class SimpleMediaPlayerActivity extends BaseActivity implements
        MediaController.MediaPlayerControl,
        MediaPlayer.OnBufferingUpdateListener,
        SurfaceHolder.Callback {

    public static final String EXTRA_INITIAL_FILE_URI = "EXTRA_INITIAL_FILE_URI";

    private MediaPlayer mediaPlayer;
    private MediaController controller;
    private int bufferPercentage = 0;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_simple_media_player);
        mediaPlayer = new MediaPlayer();
        controller = new MediaController(this);
        controller.setAnchorView(findViewById(R.id.root_ll));
        initSurfaceView();
        Bundle extras = getIntent().getExtras();
        if (null != extras){
            String strUri = extras.getString(EXTRA_INITIAL_FILE_URI);
            mUri = TextUtils.isEmpty(strUri)?null:Uri.parse(strUri);
        }
    }
    private void initSurfaceView() {
        SurfaceView videoSuf = (SurfaceView) findViewById(R.id.controll_surfaceView);
        videoSuf.setZOrderOnTop(false);
        videoSuf.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        videoSuf.getHolder().addCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //String path = Environment.getExternalStorageDirectory().getPath() + "/20180730.mp4";
            mediaPlayer.setDataSource(this, mUri);
            mediaPlayer.setOnBufferingUpdateListener(this);
            //mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    start();
                }
            });

            controller.setMediaPlayer(this);
            controller.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mediaPlayer){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return super.onTouchEvent(event);
    }

    @Override
    public void start() {
        if (null != mediaPlayer){
            mediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (null != mediaPlayer){
            mediaPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer.isPlaying()){
            return true;
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return bufferPercentage;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        bufferPercentage = i;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
