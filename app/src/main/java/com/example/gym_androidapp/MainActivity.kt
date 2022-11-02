package com.example.gym_androidapp

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    var video : VideoView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        video = findViewById(R.id.video);
        val videoPath = "android.resource://" + packageName + "/" + R.raw.video;
        val uri = Uri.parse(videoPath);
        video!!.setVideoURI(uri);

        val mediaController = MediaController(this);
        video!!.setMediaController(mediaController);
        mediaController.setAnchorView(video);
        video!!.start();
        video!!.setMediaController(null);
        video!!.setOnPreparedListener { mp -> mp.isLooping = true }
    }

    override fun onResume() {
        super.onResume()
        video!!.start()
    }

}