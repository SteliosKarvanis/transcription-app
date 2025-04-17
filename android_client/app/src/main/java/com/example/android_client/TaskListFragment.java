package com.example.android_client;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android_client.databinding.FragmentTaskListBinding;
import com.example.android_client.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private FragmentTaskListBinding binding;

    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task);
    }
    private OnTaskSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskSelectedListener) {
            listener = (OnTaskSelectedListener) context;
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        binding.tasksList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.tasksList.setAdapter(new TaskAdapter(getTasks(), listener));

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.buttonFirst.setOnClickListener(v ->
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
//        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("task 1", "Stelios", "FINISHED", ""));
        tasks.add(new Task("task 2", "Stelios", "FINISHED", "aaaa"));
        return tasks;
    }
}