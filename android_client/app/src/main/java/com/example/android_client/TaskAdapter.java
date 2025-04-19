package com.example.android_client;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android_client.databinding.TaskItemBinding;
import com.example.android_client.models.TaskPromise;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<TaskPromise> tasks;
    private OnTaskSelectedListener listener;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");

    public interface OnTaskSelectedListener {
        void onTaskSelected(TaskPromise task);
    }
    public void setTasks(List<TaskPromise> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        notifyDataSetChanged();
    }
    public TaskAdapter(OnTaskSelectedListener listener) {
        this.tasks = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(TaskItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TaskPromise task = tasks.get(position);
        holder.dateStr.setText(task.last_requested_at.format(formatter));
        holder.taskIdText.setText(task.task_id);
        setStatusImage(holder, task.status);
    }

    private void setStatusImage(ViewHolder holder, String status){
        if(status.equals("COMPLETED")){
            holder.statusText.setImageResource(R.drawable.check);
        }else if(status.equals("IN_PROGRESS")){
            holder.statusText.setImageResource(R.drawable.load);
        } else {
            holder.statusText.setImageResource(R.drawable.cancelled);
        }
    }
    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dateStr;
        public final TextView taskIdText;
        public final ImageView statusText;

        public ViewHolder(TaskItemBinding binding) {
            super(binding.getRoot());
            dateStr = binding.dateStr;
            statusText = binding.taskStatus;
            taskIdText = binding.taskId;
            binding.getRoot().setOnClickListener(v -> {
                int pos = getAbsoluteAdapterPosition();
                listener.onTaskSelected(tasks.get(pos));
            });
        }
    }
}