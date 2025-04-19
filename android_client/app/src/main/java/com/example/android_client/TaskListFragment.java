package com.example.android_client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android_client.api.ApiClient;
import com.example.android_client.databinding.FragmentTaskListBinding;
import com.example.android_client.models.TaskPromise;

public class TaskListFragment extends Fragment {
    private FragmentTaskListBinding binding;
    private TaskAdapter taskAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        taskAdapter = new TaskAdapter(this::selectTask);
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        binding.tasksList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.tasksList.setAdapter(taskAdapter);
        binding.refreshButton.setOnClickListener(this::updateTasks);
        binding.refreshButton.callOnClick();
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void selectTask(TaskPromise task){
        Bundle bundle = new Bundle();
        bundle.putSerializable(TaskDetailFragment.ARG_TASK, task);
        NavHostFragment.findNavController(TaskListFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
    }

    private void updateTasks(View view) {
        binding.refreshButton.setEnabled(false);
        ApiClient.getTasks(response -> taskAdapter.setTasks(response));
        binding.refreshButton.postDelayed(() -> {
            if (getView() != null){
                binding.refreshButton.setEnabled(true);
            }
        }, 5000);
        Toast.makeText(view.getContext(), "Tasks Atualizado", Toast.LENGTH_LONG).show();
    }
}