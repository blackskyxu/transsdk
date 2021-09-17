package com.transsnet.transsdktest.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.transsnet.transsdk.dto.VideoInfo;
import com.transsnet.transsdktest.R;
import com.transsnet.transsdktest.player.cache.PreloadManager;
import com.transsnet.transsdktest.view.CircleImageView;
import com.transsnet.transsdktest.view.TikTokView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayAdapter extends PagerAdapter {

    /**
     * View缓存池，从ViewPager中移除的item将会存到这里面，用来复用
     */
    private List<View> mViewPool = new ArrayList<>();

    /**
     * 数据源
     */
    private List<VideoInfo> mVideoBeans;

    public PlayAdapter(List<VideoInfo> videoBeans) {
        this.mVideoBeans = videoBeans;
    }

    @Override
    public int getItemPosition(@NonNull @NotNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mVideoBeans == null ? 0 : mVideoBeans.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Context context = container.getContext();
        View view = null;
        if (mViewPool.size() > 0) {//取第一个进行复用
            view = mViewPool.get(0);
            mViewPool.remove(0);
        }

        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_adapter, container, false);
            viewHolder = new ViewHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        VideoInfo item = mVideoBeans.get(position);
        //开始预加载
        PreloadManager.getInstance(context).addPreloadTask(item.getVideoUrl(), position);
        Glide.with(context)
                .load(item.getVideoImage())
                .placeholder(android.R.color.white)
                .into(viewHolder.mThumb);
        viewHolder.mTitle.setText(item.getTitle());
        viewHolder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "点击了标题", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.mAuthorName.setText(item.getAuthorName());
        Glide.with(context).load(item.getAvatarUrl()).into(viewHolder.header);
        viewHolder.mPosition = position;
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View itemView = (View) object;
        container.removeView(itemView);
        if (mVideoBeans.size() > position) {
            VideoInfo item = mVideoBeans.get(position);
            //取消预加载
            PreloadManager.getInstance(container.getContext()).removePreloadTask(item.getVideoUrl());
            //保存起来用来复用
            mViewPool.add(itemView);
        }
    }

    /**
     * 借鉴ListView item复用方法
     */
    public static class ViewHolder {

        public int mPosition;
        public TextView mTitle;//标题
        public ImageView mThumb;//封面图
        public TikTokView mTikTokView;
        public FrameLayout mPlayerContainer;
        private AppCompatTextView mAuthorName;
        private CircleImageView header;

        ViewHolder(View itemView) {
            mTikTokView = itemView.findViewById(R.id.tiktok_View);
            mTitle = mTikTokView.findViewById(R.id.tv_title);
            mThumb = mTikTokView.findViewById(R.id.iv_thumb);
            mAuthorName = mTikTokView.findViewById(R.id.author_name);
            header = mTikTokView.findViewById(R.id.header);
            mPlayerContainer = itemView.findViewById(R.id.container);
            itemView.setTag(this);
        }
    }
}