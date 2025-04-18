package com.example.android_client;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android_client.models.Task;
import com.example.android_client.databinding.TaskItemBinding;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final List<Task> tasks;
    private OnTaskSelectedListener listener;

    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskSelectedListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(TaskItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Task task = tasks.get(position);
        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(task.task_id, MediaStore.Images.Thumbnails.MINI_KIND);
        holder.imageView.setImageBitmap(thumbnail);
        holder.taskIdText.setText(task.task_id);
        holder.statusText.setText(task.status);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;
        public final TextView taskIdText;
        public final TextView statusText;

        public ViewHolder(TaskItemBinding binding) {
            super(binding.getRoot());
            imageView = binding.imageView;
            statusText = binding.taskStatus;
            taskIdText = binding.taskId;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAbsoluteAdapterPosition();
                    listener.onTaskSelected(tasks.get(pos));
                }
            });
        }
    }
}