package com.transsnet.transsdktest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.transsnet.transsdktest.ui.fragment.VideoListFragment;


public class VideoListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_list_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, VideoListFragment.newInstance())
                    .commitNow();
        }
    }
}