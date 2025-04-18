package com.example.android_client;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android_client.databinding.FragmentTaskListBinding;
import com.example.android_client.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private FragmentTaskListBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        binding.tasksList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.tasksList.setAdapter(new TaskAdapter(getTasks(), onTaskClicked()));

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("task 1", "/document/raw:/storage/emulated/0/Download/video.mp4", "Stelios", "FINISHED", ""));
        tasks.add(new Task("task 2", "/document/raw:/storage/emulated/0/Download/video.mp4", "Stelios", "FINISHED", "aaaa"));
        return tasks;
    }

    private TaskAdapter.OnTaskSelectedListener onTaskClicked() {
        return new TaskAdapter.OnTaskSelectedListener(){
            @Override
            public void onTaskSelected(Task task) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(TaskDetailFragment.ARG_TASK, task);
                NavHostFragment.findNavController(TaskListFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
            }
        };
    }
}