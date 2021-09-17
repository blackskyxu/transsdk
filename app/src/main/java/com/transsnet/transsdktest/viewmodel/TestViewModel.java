package com.transsnet.transsdktest.viewmodel;

import androidx.lifecycle.ViewModel;

import com.transsnet.transsdk.constants.CommentStatusEnum;
import com.transsnet.transsdk.constants.LikeStatusEnum;
import com.transsnet.transsdk.constants.PageIDEnum;
import com.transsnet.transsdk.dto.CoverExposureEvent;
import com.transsnet.transsdk.listener.VideoListener;
import com.transsnet.transsdk.manager.TransEventAgentManager;
import com.transsnet.transsdk.manager.VideoManager;

import java.util.List;


public class TestViewModel extends ViewModel {


    public TestViewModel() {
    }

    public void loadVideoList() {
        VideoManager.getVideoService().loadData();
    }

    public void refresh() {
        VideoManager.getVideoService().refresh();
    }

    public void loadMore() {
        VideoManager.getVideoService().loadMore();
    }

    public void addVideoListener(VideoListener videoListener) {
        VideoManager.getVideoService().addVideoListener(videoListener);
    }

    public boolean isAddVideoListener(VideoListener videoListener) {
        return VideoManager.getVideoService().isAddVideoListener(videoListener);
    }

    public void removeVideoListener(VideoListener videoListener) {
        VideoManager.getVideoService().removeVideoListener(videoListener);
    }

    public void postPvEvent(PageIDEnum pageID) {
        TransEventAgentManager.getTransEventAgent().pvEvent(pageID);
    }

    public void postPlayEvent(String videoId, String exposureStamp, long duration) {
        TransEventAgentManager.getTransEventAgent().playEvent(videoId, exposureStamp, duration);
    }

    public void postCoverExposureEvent(String videoId, String exposureStamp) {
        TransEventAgentManager.getTransEventAgent().coverExposureEvent(videoId, exposureStamp);
    }

    public void postCoverExposureEvents(List<CoverExposureEvent> events) {
        TransEventAgentManager.getTransEventAgent().coverExposureEvents(events);
    }

    public void postCoverClickEvent(String videoId, String exposureStamp) {
        TransEventAgentManager.getTransEventAgent().coverClickEvent(videoId, exposureStamp);
    }

    public void postStartPlayEvent(String videoId, String exposureStamp) {
        TransEventAgentManager.getTransEventAgent().startPlayEvent(videoId, exposureStamp);
    }

    public void postLikeEvent(String videoId, String exposureStamp, LikeStatusEnum likeStatus) {
        TransEventAgentManager.getTransEventAgent().likeEvent(videoId, exposureStamp, likeStatus);
    }

    public void postCommentEvent(String videoId, String exposureStamp, CommentStatusEnum commentStatus) {
        TransEventAgentManager.getTransEventAgent().commentEvent(videoId, exposureStamp, commentStatus);
    }
}
