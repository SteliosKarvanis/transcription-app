package com.example.android_client;

import android.net.Uri;
import android.os.Bundle;
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

    private static final String ARG_VIDEO = "video_path";
    private static final String ARG_DESC = "transcription";

    public static TaskDetailFragment newInstance(Task task) {
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO, task.task_id);
        args.putString(ARG_DESC, task.transcription);

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
            String videoPath = args.getString(ARG_VIDEO);
            String desc = args.getString(ARG_DESC);

            binding.videoView.setVideoURI(Uri.parse(videoPath));
            binding.transcription.setText(desc);
        }
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}