package com.transsnet.transsdktest.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.transsnet.transsdk.constants.PageIDEnum;
import com.transsnet.transsdk.dto.CoverExposureEvent;
import com.transsnet.transsdk.dto.VideoInfo;
import com.transsnet.transsdk.listener.VideoListener;
import com.transsnet.transsdktest.R;
import com.transsnet.transsdktest.player.PlayerActivity;
import com.transsnet.transsdktest.ui.adapter.VideoListAdapter;
import com.transsnet.transsdktest.utils.Logger;
import com.transsnet.transsdktest.utils.MD5;
import com.transsnet.transsdktest.viewmodel.TestViewModel;

import java.util.ArrayList;
import java.util.List;

public class VideoListFragment extends Fragment {
    private static final String TAG = "VideoListFragment";

//    private VideoViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private VideoListAdapter mAdapter;
    private List<VideoInfo> mDatas = new ArrayList<>();
    private SmartRefreshLayout refreshLayout;

    private TestViewModel viewModel;
    private boolean isRefresh;
    private boolean isAddExposureEvent = true;      // 默认第一页需自动添加曝光事件
    private ProgressBar mProgressBar;
    private final List<CoverExposureEvent> exposureEvents = new ArrayList<>();

    public static VideoListFragment newInstance() {
        return new VideoListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mProgressBar = view.findViewById(R.id.progress);
        mRecyclerView = view.findViewById(R.id.rv_video_list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mAdapter = new VideoListAdapter(mDatas);
        mRecyclerView.setAdapter(mAdapter);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(1000);
            isRefresh = true;
            // 每次刷新也需要增加曝光事件
            isAddExposureEvent = true;
            viewModel.refresh();
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            refreshLayout.finishLoadMore(1000);
            viewModel.loadMore();
        });
        mAdapter.setOnItemClickListener(position -> {
            VideoInfo videoInfo = mDatas.get(position);
            String expStamp = getExpStamp(videoInfo.getVideoId());
            long curTime = System.currentTimeMillis();
            if (TextUtils.isEmpty(expStamp)) {
                expStamp = MD5.getMD5(videoInfo.getVideoId() + curTime);
            }

            Intent intent = new Intent(getContext(), PlayerActivity.class);
            intent.putExtra(PlayerActivity.KEY_INDEX, position);
            intent.putParcelableArrayListExtra(PlayerActivity.DATA, (ArrayList<? extends Parcelable>) mDatas);
            intent.putExtra(PlayerActivity.EXP_STAMP, expStamp);
            getContext().startActivity(intent);

            viewModel.postCoverClickEvent(videoInfo.getVideoId(), expStamp);
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Logger.d(TAG, "newState: " + newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof GridLayoutManager) {
                        addExposureEvents((GridLayoutManager) layoutManager);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        initData();

        viewModel.addVideoListener(mVideoListener);
    }

    /**
     * 添加封面曝光事件
     */
    private void addExposureEvents(GridLayoutManager layoutManager) {
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        int lastPosition = layoutManager.findLastVisibleItemPosition();
        if (firstPosition < 0) return;
        List<CoverExposureEvent> events = new ArrayList<>();
        for (int i = firstPosition; i <= lastPosition; i++) {
            CoverExposureEvent event = new CoverExposureEvent();
            VideoInfo videoInfo = mDatas.get(i);
            event.setVideoId(videoInfo.getVideoId());
            long curTime = System.currentTimeMillis();
            String expStamp = MD5.getMD5(videoInfo.getVideoId() + curTime);
            event.setExpStamp(expStamp);
            events.add(event);
        }

        viewModel.postCoverExposureEvents(events);
        exposureEvents.addAll(events);
    }

    private String getExpStamp(String videoId) {
        if (videoId == null || exposureEvents.size() <= 0) return null;
        for (int i = exposureEvents.size(); i > 0; i--) {
            // 取最近添加的一个封面曝光事件作为点击事件曝光戳
            CoverExposureEvent exposureEvent = exposureEvents.get(i-1);
            if (videoId.equals(exposureEvent.getVideoId())) {
                return exposureEvent.getExpStamp();
            }
        }
        return null;
    }

    private void initData() {
        viewModel = new ViewModelProvider(this).get(TestViewModel.class);
//        viewModel.getLiveData().observe(this, videoInfos -> {
//            if (isRefresh) {
//                mDatas.clear();
//                isRefresh = false;
//            }
//            mDatas.addAll(videoInfos);
//            mAdapter.notifyDataSetChanged();
//            if (mDatas.size() >= 0) {
//                mProgressBar.setVisibility(View.GONE);
//                refreshLayout.setVisibility(View.VISIBLE);
//            }
//        });
        viewModel.loadVideoList();

        viewModel.postPvEvent(PageIDEnum.VIDEO_lIST);
    }

    private final VideoListener mVideoListener = new VideoListener() {
        @Override
        public void onLoadDataSuccess(List<VideoInfo> recommendVideoList) {
            Logger.d(TAG, "load success");
            if (isRefresh) {
                mDatas.clear();
                isRefresh = false;
            }
            mDatas.addAll(recommendVideoList);
            mAdapter.notifyDataSetChanged();
            if (mDatas.size() >= 0) {
                mProgressBar.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
            }

            if (isAddExposureEvent) {
                mRecyclerView.postDelayed(()->{
                    if (isAdded()) {
                        addExposureEvents((GridLayoutManager) mRecyclerView.getLayoutManager());
                    }
                }, 1000);
                isAddExposureEvent = false;
            }
        }

        @Override
        public void onLoadDataFailed(int code, String msg) {
            Logger.d(TAG, "load failed");
            Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.removeVideoListener(mVideoListener);
    }
}