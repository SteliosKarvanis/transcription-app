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

import java.io.File;

public class TaskDetailFragment extends Fragment {

    private FragmentTaskDetailBinding binding;
    public static final String ARG_TASK = "task";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        Bundle args = getArguments();
        if (args != null) {
            Task task = (Task) args.getSerializable(ARG_TASK);
            Log.d("Task Detail", task.local_path);
            String videoPath = "/sdcard/Download/video.mp4";
            File file = new File(videoPath);
            Log.d("Video Exists", String.valueOf(file.exists()));
            Uri videoUri = Uri.parse(videoPath);
//            Uri videoUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.video);
            binding.videoView.setVideoPath(videoPath);
            binding.videoView.start();
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