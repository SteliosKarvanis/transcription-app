package com.example.android_client;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android_client.databinding.TaskItemBinding;
import com.example.android_client.models.TaskPromise;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<TaskPromise> tasks;
    private OnTaskSelectedListener listener;

    public interface OnTaskSelectedListener {
        void onTaskSelected(TaskPromise task);
    }
    public void setTasks(List<TaskPromise> tasks) {
        this.tasks.clear();
        this.tasks.addAll(tasks);
        notifyDataSetChanged();
    }
    public TaskAdapter(OnTaskSelectedListener listener) {
        this.tasks = new ArrayList<TaskPromise>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(TaskItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TaskPromise task = tasks.get(position);
        holder.dateStr.setText(task.last_requested_at.toLocalDate().toString());
        holder.taskIdText.setText(task.task_id);
        holder.statusText.setText(task.status);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dateStr;
        public final TextView taskIdText;
        public final TextView statusText;

        public ViewHolder(TaskItemBinding binding) {
            super(binding.getRoot());
            dateStr = binding.dateStr;
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