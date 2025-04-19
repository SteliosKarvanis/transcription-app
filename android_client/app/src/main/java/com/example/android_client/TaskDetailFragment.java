package com.example.android_client;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android_client.api.ApiClient;
import com.example.android_client.databinding.FragmentTaskDetailBinding;
import com.example.android_client.models.TaskPromise;
import com.example.android_client.utils.PrintHandler;

public class TaskDetailFragment extends Fragment {
    WebView webView;
    private FragmentTaskDetailBinding binding;
    public static final String ARG_TASK = "task";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        // Parse Arguments
        Bundle args = getArguments();
        TaskPromise task = (TaskPromise) args.getSerializable(ARG_TASK);
        // Fetch Transcription
        Uri videoUri = Uri.parse("content://media" + task.sender_file_path);
        ApiClient.getTranscription(response -> binding.transcription.setText(response.transcription), task.task_id);
        // Bind Actions
        binding.videoView.setOnClickListener( v -> openVideo(v, videoUri));
        binding.printButton.setOnClickListener(this::createWebViewAndPrint);
        // Set Web Client
        webView = new WebView(binding.getRoot().getContext());
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createWebViewAndPrint(View v) {
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                PrintHandler.createPrintJob(v, view);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                PrintHandler.createPrintJob(v, view);
            }
        });
        String htmlContent = "<html><body><p style='font-size: 32px;'>" + binding.transcription.getText() + "</p></body></html>";
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null);
    }

    private void openVideo(View view, Uri videoUri){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(videoUri, "video/*");
        if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "No app found to open video", Toast.LENGTH_SHORT).show();
        }
    }
}