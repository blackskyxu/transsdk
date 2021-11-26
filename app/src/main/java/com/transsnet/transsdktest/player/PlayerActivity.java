package com.transsnet.transsdktest.player;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.transsnet.transsdk.constants.CommentStatusEnum;
import com.transsnet.transsdk.constants.LikeStatusEnum;
import com.transsnet.transsdk.constants.PageIDEnum;
import com.transsnet.transsdk.dto.VideoInfo;
import com.transsnet.transsdk.listener.VideoListener;
import com.transsnet.transsdktest.R;
import com.transsnet.transsdktest.controller.TikTokController;
import com.transsnet.transsdktest.player.cache.PreloadManager;
import com.transsnet.transsdktest.player.cache.ProxyVideoCacheManager;
import com.transsnet.transsdktest.render.TikTokRenderViewFactory;
import com.transsnet.transsdktest.ui.adapter.PlayAdapter;
import com.transsnet.transsdktest.utils.Logger;
import com.transsnet.transsdktest.utils.MD5;
import com.transsnet.transsdktest.utils.Utils;
import com.transsnet.transsdktest.view.SwipeRefreshLoadLayout;
import com.transsnet.transsdktest.view.VerticalViewPager;
import com.transsnet.transsdktest.viewmodel.TestViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity implements SwipeRefreshLoadLayout.OnRefreshLoadListener, VideoListener {

    private static final String TAG = "PlayerActivity";

    public static final String KEY_INDEX = "key_index";
    public static final String DATA = "data";
    public static final String EXP_STAMP = "exp_stamp";

    private  int mCurPos;
    private VerticalViewPager mViewPager;
    private PreloadManager mPreloadManager;
    private PlayAdapter mPlayAdapter;
    private List<VideoInfo> mVideoList = new ArrayList<>();
    private VideoView  mVideoView;
    private TikTokController mController;
    private TestViewModel viewModel;
    private long startPlayTime;
    private String expStamp;        // 曝光时间戳唯一的
    private SwipeRefreshLoadLayout refreshLayout;

    private boolean isLike;
    private boolean isComment;
    private boolean isRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
    }

    private void initView() {
        initRefreshLayout();
        viewModel = new ViewModelProvider(this).get(TestViewModel.class);

        initViewPager();
        initVideoView();
        initClickEvent();
        initListener();

        mPreloadManager = PreloadManager.getInstance(this);

        Intent extras = getIntent();
        List<VideoInfo> datas = extras.getParcelableArrayListExtra(DATA);
        if (datas != null) {
            mVideoList.addAll(datas);
            mPlayAdapter.notifyDataSetChanged();
        }
        int index = extras.getIntExtra(KEY_INDEX, 0);
        expStamp = extras.getStringExtra(EXP_STAMP);
        mViewPager.setCurrentItem(index);

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                startPlay(index);
            }
        });

        viewModel.postPvEvent(PageIDEnum.VIDEO_PLAY);
    }

    private void initRefreshLayout() {
        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshLoadListener(this);
    }

    private void initViewPager() {
        mViewPager = findViewById(R.id.vvp);
        mViewPager.setOffscreenPageLimit(4);
        mPlayAdapter = new PlayAdapter(mVideoList);
        mViewPager.setAdapter(mPlayAdapter);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            private int mCurItem;

            /**
             * VerticalViewPager是否反向滑动
             */
            private boolean mIsReverseScroll;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == mCurItem) {
                    return;
                }
                mIsReverseScroll = position < mCurItem;
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                refreshLayout.setEnabled(position == 0);
                if (position == mCurPos) return;
                startPlay(position);
                requestNextPage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == VerticalViewPager.SCROLL_STATE_DRAGGING) {
                    mCurItem = mViewPager.getCurrentItem();
                }

                if (state == VerticalViewPager.SCROLL_STATE_IDLE) {
                    mPreloadManager.resumePreload(mCurPos, mIsReverseScroll);
                } else {
                    mPreloadManager.pausePreload(mCurPos, mIsReverseScroll);
                }
            }
        });
    }

    private VideoInfo lastVideoInfo;
    private void initVideoView() {
        mVideoView = new VideoView(this);
        mVideoView.setLooping(true);

        //以下只能二选一，看你的需求
        mVideoView.setRenderViewFactory(TikTokRenderViewFactory.create());
//        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_CENTER_CROP);

        mController = new TikTokController(this);
        mVideoView.setVideoController(mController);
        mVideoView.addOnStateChangeListener(new VideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {
                Logger.d(TAG, "playerState: " + playerState);
            }

            @Override
            public void onPlayStateChanged(int playState) {
                Logger.d(TAG, "playState: " + playState);
                if (playState == VideoView.STATE_PREPARING) {
                    VideoInfo videoInfo = mVideoList.get(mCurPos);
                    startPlayTime = System.currentTimeMillis();
                    if (TextUtils.isEmpty(expStamp)) {
                        expStamp = MD5.getMD5(videoInfo.getVideoId() + startPlayTime);
                    }
                    viewModel.postStartPlayEvent(videoInfo.getVideoId(), expStamp);
                } else if (playState == VideoView.STATE_IDLE) {
                    if (lastVideoInfo == null) return;
                    long curTime = System.currentTimeMillis();
                    if (TextUtils.isEmpty(expStamp)) {
                        expStamp = MD5.getMD5(lastVideoInfo.getVideoId() + curTime);
                    }
                    long duration = curTime - startPlayTime;
                    viewModel.postPlayEvent(lastVideoInfo.getVideoId(), expStamp, duration);
                }

            }
        });
    }

    private void initClickEvent() {
        findViewById(R.id.like).setOnClickListener(v -> {
            VideoInfo videoInfo = mVideoList.get(mCurPos);
            if (TextUtils.isEmpty(expStamp)) {
                expStamp = MD5.getMD5(videoInfo.getVideoId() + System.currentTimeMillis());
            }
            viewModel.postLikeEvent(videoInfo.getVideoId(), expStamp, isLike ? LikeStatusEnum.CANCEL_LIKE : LikeStatusEnum.LIKE);
            isLike = !isLike;
            if (isLike) {
                ((Button)v).setText("上报取消点赞数据");
            } else {
                ((Button)v).setText("上报点赞数据");
            }
        });
        findViewById(R.id.comment).setOnClickListener(v -> {
            VideoInfo videoInfo = mVideoList.get(mCurPos);
            if (TextUtils.isEmpty(expStamp)) {
                expStamp = MD5.getMD5(videoInfo.getVideoId() + System.currentTimeMillis());
            }
            viewModel.postCommentEvent(videoInfo.getVideoId(), expStamp, isComment ? CommentStatusEnum.DELETE_COMMENT : CommentStatusEnum.COMMENT);
            isComment = !isComment;

            if (isComment) {
                ((Button)v).setText("上报删除评论数据");
            } else {
                ((Button)v).setText("上报评论数据");
            }
        });
    }

    private void initListener() {
        viewModel.addVideoListener(this);
    }

    private void startPlay(int position) {
        int count = mViewPager.getChildCount();
        for (int i = 0; i < count; i ++) {
            View itemView = mViewPager.getChildAt(i);
            PlayAdapter.ViewHolder viewHolder = (PlayAdapter.ViewHolder) itemView.getTag();
            if (viewHolder.mPosition == position) {
                mVideoView.release();
                Utils.removeViewFormParent(mVideoView);

                VideoInfo tiktokBean = mVideoList.get(position);
                String playUrl = mPreloadManager.getPlayUrl(tiktokBean.getVideoUrl());
                L.i("startPlay: " + "position: " + position + "  url: " + playUrl);
                mVideoView.setUrl(playUrl);
                mController.addControlComponent(viewHolder.mTikTokView, true);
                viewHolder.mPlayerContainer.addView(mVideoView, 0);
                mVideoView.start();
                lastVideoInfo = mVideoList.get(mCurPos);
                mCurPos = position;
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.release();
        }
        mPreloadManager.removeAllPreloadTask();
        //清除缓存，实际使用可以不需要清除，这里为了方便测试
        ProxyVideoCacheManager.clearAllCache(this);
        viewModel.removeVideoListener(this);
    }

    @Override
    public void onload() {
        Logger.d(TAG, "onLoad");
        viewModel.loadVideoList();
    }

    private void requestNextPage() {
        if (mVideoList.size() - mCurPos <= 3) {
            viewModel.loadVideoList();
        }
    }

    @Override
    public void onRefresh() {
        viewModel.refresh();
        isRefresh = true;
    }

    @Override
    public void onLoadDataSuccess(List<VideoInfo> recommendVideoList) {
        if (isRefresh) {
            isRefresh = false;
            refreshLayout.setRefreshing(false);
            mVideoList.clear();
            mPlayAdapter.notifyDataSetChanged();
            mVideoList.addAll(recommendVideoList);
            mPlayAdapter.notifyDataSetChanged();
            mCurPos = 0;
            startPlay(mCurPos);
        } else {
            refreshLayout.completeLoadMore();
            mVideoList.addAll(recommendVideoList);
            mPlayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadDataFailed(int code, String msg) {
        if (isRefresh) {
            isRefresh = false;
            refreshLayout.setRefreshing(false);
        } else {
            refreshLayout.completeLoadMore();
        }
        Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
    }
}