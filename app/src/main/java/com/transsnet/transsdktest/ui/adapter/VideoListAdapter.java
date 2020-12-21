package com.transsnet.transsdktest.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.transsnet.transsdk.dto.VideoInfo;
import com.transsnet.transsdktest.R;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder>{

    private List<VideoInfo> mDatas;
    private OnItemClickListener onItemClickListener;

    public VideoListAdapter(List<VideoInfo> datas) {
        mDatas = datas;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_video_list, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mDatas.get(position));
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView imageView;
        private final OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            imageView = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(v -> {
                if (this.onItemClickListener != null) {
                    this.onItemClickListener.onItemClickListener(getAdapterPosition());
                }
            });
        }

        private void setData(VideoInfo videoInfo) {
            Glide.with(itemView.getContext()).load(videoInfo.getVideoImage()).into(imageView);
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }

}
