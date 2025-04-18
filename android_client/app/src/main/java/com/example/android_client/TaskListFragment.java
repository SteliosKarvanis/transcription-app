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

import com.example.android_client.databinding.FragmentTaskListBinding;
import com.example.android_client.models.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TaskListFragment extends Fragment {

    private FragmentTaskListBinding binding;
    private TaskAdapter taskAdapter;
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiJ9.Xuae94z8TgirLrK_u2DbX-Qg_5y1Qzo29JLjpY47HU0";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        taskAdapter = new TaskAdapter(onTaskClicked());
        fetchTasks();
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        binding.tasksList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.tasksList.setAdapter(taskAdapter);
        binding.refreshButton.setOnClickListener(view -> updateTasks());
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

    private void fetchTasks() {
        Request request = new Request.Builder()
                .url("http://168.231.89.123/transcription/tasks")
                .addHeader("Authorization", "Bearer " + token)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Optionally show error in UI
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // Handle unsuccessful
                    return;
                }

                String json = response.body().string();
                getActivity().runOnUiThread(() -> taskAdapter.setTasks(parseTasks(json)));
            }
        });
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

    private void updateTasks() {
        binding.refreshButton.setEnabled(false);
        fetchTasks();
        binding.refreshButton.postDelayed(() -> binding.refreshButton.setEnabled(true), 5000);
        Toast.makeText(binding.getRoot().getContext(), "Tasks Atualizado", Toast.LENGTH_LONG).show();
    }

    private List<Task> parseTasks(String json) {
        List<Task> tasks = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                tasks.add(Task.fromJson(obj.toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tasks;
    }
}