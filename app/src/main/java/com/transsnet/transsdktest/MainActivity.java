package com.transsnet.transsdktest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.transsnet.transsdk.constants.CommentStatusEnum;
import com.transsnet.transsdk.constants.LikeStatusEnum;
import com.transsnet.transsdk.constants.PageIDEnum;
import com.transsnet.transsdk.dto.VideoInfo;
import com.transsnet.transsdk.listener.VideoListener;
import com.transsnet.transsdk.manager.TransConfigManager;
import com.transsnet.transsdktest.databinding.ActivityMainBinding;
import com.transsnet.transsdktest.utils.Logger;
import com.transsnet.transsdktest.utils.MD5;
import com.transsnet.transsdktest.viewmodel.TestViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TestViewModel viewModel;

    private boolean isLike;
    private boolean isComment;
    private final List<VideoInfo> mVideoInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        VideoInfo info = new VideoInfo();
        info.setAuthorName("哈哈哈，我是视频标题");
        binding.setTestDto(info);

        initData();
    }

    private void initData() {
//        getLifecycle().addObserver(new TestHelper(this));
//        ViewModel viewModel= new ViewModelProvider(this, new ViewModelFactory(getApplication()))
//                .get(TestViewModel.class);
        viewModel = ViewModelProviders.of(this).get(TestViewModel.class);
//        viewModel.getLiveData().observe(this, videoInfoList -> {
//            Logger.d("observer receive " + videoInfoList.size());
//            mVideoInfoList.addAll(videoInfoList);
//        });

        findViewById(R.id.get_video_list).setOnClickListener(v -> {
            viewModel.loadVideoList();
        });

        findViewById(R.id.refresh).setOnClickListener(v -> {
            viewModel.refresh();
        });

        findViewById(R.id.loadmore).setOnClickListener(v -> {
            viewModel.loadMore();
        });
        findViewById(R.id.pv).setOnClickListener(v -> {
            viewModel.postPvEvent(PageIDEnum.VIDEO_lIST);
        });

        findViewById(R.id.play).setOnClickListener(v -> {
            if (mVideoInfoList.size() <=0 ) {
                Toast.makeText(getApplicationContext(), "请先获取列表数据", Toast.LENGTH_SHORT).show();
                return;
            }
            VideoInfo videoInfo = mVideoInfoList.get(mVideoInfoList.size() - 1);
            String raw = videoInfo.getVideoId() + System.currentTimeMillis();
            String extStamp = MD5.getMD5(raw);
            viewModel.postPlayEvent(videoInfo.getVideoId(), extStamp, 1000);
        });

        findViewById(R.id.start_play).setOnClickListener(v -> {
            if (mVideoInfoList.size() <=0 ) {
                Toast.makeText(getApplicationContext(), "请先获取列表数据", Toast.LENGTH_SHORT).show();
                return;
            }
            VideoInfo videoInfo = mVideoInfoList.get(mVideoInfoList.size() - 1);
            String raw = videoInfo.getVideoId() + System.currentTimeMillis();
            String extStamp = MD5.getMD5(raw);
            viewModel.postStartPlayEvent(videoInfo.getVideoId(), extStamp);
        });
        findViewById(R.id.like).setOnClickListener(v -> {
            if (mVideoInfoList.size() <=0 ) {
                Toast.makeText(getApplicationContext(), "请先获取列表数据", Toast.LENGTH_SHORT).show();
                return;
            }
            VideoInfo videoInfo = mVideoInfoList.get(mVideoInfoList.size() - 1);
            String raw = videoInfo.getVideoId() + System.currentTimeMillis();
            String extStamp = MD5.getMD5(raw);
            viewModel.postLikeEvent(videoInfo.getVideoId(), extStamp, isLike ? LikeStatusEnum.CANCEL_LIKE : LikeStatusEnum.LIKE);
            isLike = !isLike;
            if (isLike) {
                ((Button)v).setText("上报取消点赞数据");
            } else {
                ((Button)v).setText("上报点赞数据");
            }
        });
        findViewById(R.id.comment).setOnClickListener(v -> {
            if (mVideoInfoList.size() <=0 ) {
                Toast.makeText(getApplicationContext(), "请先获取列表数据", Toast.LENGTH_SHORT).show();
                return;
            }
            VideoInfo videoInfo = mVideoInfoList.get(mVideoInfoList.size() - 1);
            String raw = videoInfo.getVideoId() + System.currentTimeMillis();
            String extStamp = MD5.getMD5(raw);
            viewModel.postCommentEvent(videoInfo.getVideoId(), extStamp, isComment ? CommentStatusEnum.DELETE_COMMENT : CommentStatusEnum.COMMENT);
            isComment = !isComment;

            if (isComment) {
                ((Button)v).setText("上报删除评论数据");
            } else {
                ((Button)v).setText("上报评论数据");
            }
        });
        findViewById(R.id.cover_exposure).setOnClickListener(v -> {
            if (mVideoInfoList.size() <=0 ) {
                Toast.makeText(getApplicationContext(), "请先获取列表数据", Toast.LENGTH_SHORT).show();
                return;
            }
            VideoInfo videoInfo = mVideoInfoList.get(mVideoInfoList.size() - 1);
            String raw = videoInfo.getVideoId() + System.currentTimeMillis();
            String extStamp = MD5.getMD5(raw);
            viewModel.postCoverExposureEvent(videoInfo.getVideoId(), extStamp);
        });
        findViewById(R.id.cover_click).setOnClickListener(v -> {
            if (mVideoInfoList.size() <=0 ) {
                Toast.makeText(getApplicationContext(), "请先获取列表数据", Toast.LENGTH_SHORT).show();
                return;
            }
            VideoInfo videoInfo = mVideoInfoList.get(mVideoInfoList.size() - 1);
            String raw = videoInfo.getVideoId() + System.currentTimeMillis();
            String extStamp = MD5.getMD5(raw);
            viewModel.postCoverClickEvent(videoInfo.getVideoId(), extStamp);
        });

        findViewById(R.id.to_video_list).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, VideoListActivity.class));
        });

        viewModel.postPvEvent(PageIDEnum.OTHER_PAGE);

        viewModel.addVideoListener(mVideoListener);

        if (viewModel.isAddVideoListener(mVideoListener)) {
            Logger.d(TAG, "has added video Listener");
        }
    }

    private final VideoListener mVideoListener = new VideoListener() {
        @Override
        public void onLoadDataSuccess(List<VideoInfo> recommendVideoList) {
            Logger.d(TAG, "load success");
            mVideoInfoList.addAll(recommendVideoList);
        }

        @Override
        public void onLoadDataFailed(int code, String msg) {
            Logger.d(TAG, "load failed");
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        TransConfigManager.getTransConfig().destroy();
        viewModel.removeVideoListener(mVideoListener);
    }
}
