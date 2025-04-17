package com.example.android_client;

import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.android_client.databinding.FragmentTaskDetailBinding;
import com.example.android_client.models.Task;

public class TaskDetailFragment extends Fragment {

    private FragmentTaskDetailBinding binding;
    private static final String ARG_TASK = "task";
    public static TaskDetailFragment newInstance(Task task) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK, task);

        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        Bundle args = getArguments();
        if (args != null) {
            Task task = (Task) getArguments().getSerializable(ARG_TASK);
            Log.d("Task Detail", task.local_path);
            Uri videoUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.video);
            binding.videoView.setVideoURI(videoUri);
            binding.transcription.setText(task.transcription);
        }
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}