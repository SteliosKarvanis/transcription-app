package com.example.android_client;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.android_client.databinding.FragmentTaskDetailBinding;
import com.example.android_client.models.TaskPromise;
import com.example.android_client.models.TaskResult;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TaskDetailFragment extends Fragment {
    WebView webView;
    private FragmentTaskDetailBinding binding;
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiJ9.Xuae94z8TgirLrK_u2DbX-Qg_5y1Qzo29JLjpY47HU0";

    public static final String ARG_TASK = "task";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        Bundle args = getArguments();
        if (args != null) {
            TaskPromise task = (TaskPromise) args.getSerializable(ARG_TASK);
            Uri videoUri = Uri.parse("content://media" + task.sender_file_path);
            binding.videoView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(videoUri, "video/*");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Optional but often necessary

                            // Check if there's an app to handle this intent
                            if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                Toast.makeText(requireContext(), "No app found to open video", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
            fetchTranscription(task.task_id);
        }
        binding.printButton.setOnClickListener(v -> createWebViewAndPrint());
        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createWebViewAndPrint() {
        webView = new WebView(binding.getRoot().getContext());
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                createPrintJob(view);
            }
        });

        // The text you want to print
        String htmlContent = "<html><body><h3>" + binding.transcription.getText() + "</h3></body></html>";
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null);
    }
    private void createPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) requireContext().getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("MyDocument");

        String jobName = getString(R.string.app_name) + " Document";
        printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }
    private void fetchTranscription(String taskId) {
        Request request = new Request.Builder()
                .url("http://168.231.89.123/task/" + taskId)
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
                TaskResult taskResult = TaskResult.fromJson(json);
                getActivity().runOnUiThread(() -> setElements(taskResult));
            }
        });
    }
    private void setElements(TaskResult taskResponse) {
        binding.transcription.setText(taskResponse.transcription);
    }
}