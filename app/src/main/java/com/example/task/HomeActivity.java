package com.example.task;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {
    AppCompatButton btnStart;
    public String fileextn = ".mp4";
    MediaRecorder mediaRecorder;
    final int REQUEST_PREMISSION_CODE=1000;
    private boolean isRecording = false;
    private static final String TAG = "VideoRecorderService";

    private Uri videoPath;
    private static int CAMERA_PERMISSION_CODE = 100;
    private static int Video_recorder_code = 101;
    private static final long MAX_DURATION_MS = 3 * 60 * 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
btnStart=findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPremissionFromDEvice()) {

                    if (!isRecording) {
//                                       Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                                       startActivityForResult(intent,1);


                        SetUpMediaRecorder();
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                            Toast.makeText(HomeActivity.this, "Video Done", Toast.LENGTH_SHORT).show();
                            btnStart.setText("Start Recording");
                            btnStart.setBackgroundColor(Color.RED);

                        } catch (IOException e) {
                            Log.e(TAG, "prepare() failed");
                            System.out.println(""+e);    //to display the error
                        }
                        Timer  mTimer = new Timer();
                                    mTimer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            stopRecording();
                                        }
                                    }, MAX_DURATION_MS);
                    }else{
                        Toast.makeText(HomeActivity.this, "Don't Do this", Toast.LENGTH_SHORT).show();
                        stopRecording();
                    }
                }else{
                    requestpermissions();
                }
            };
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK && requestCode == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            VideoView videoView = new VideoView(this);
            videoView.setVideoURI(data.getData());
            videoView.start();
            builder.setView(videoView);
        }
    }

    private void SetUpMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoSize(1280, 720);
        mediaRecorder.setVideoEncodingBitRate(1000000);
        mediaRecorder.setOutputFile(getFilePath());
    }
    public void stopRecording() {

        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            isRecording = false;
            btnStart.setText("Start Recording");
            Toast.makeText(this, "Stop", Toast.LENGTH_SHORT).show();
            btnStart.setBackgroundColor(Color.GREEN);
        }

    }




    private String getFilePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/video.mp4";
    }

    private void requestpermissions() {
        ActivityCompat.requestPermissions(this,new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.CAMERA
        },REQUEST_PREMISSION_CODE);
    }
    private boolean checkPremissionFromDEvice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission( this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        int Camera_Result=ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED&&
                Camera_Result == PackageManager.PERMISSION_GRANTED;
    }
}