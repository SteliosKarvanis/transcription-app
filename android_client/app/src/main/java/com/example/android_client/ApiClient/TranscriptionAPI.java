package com.example.android_client.ApiClient;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android_client.MainActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TranscriptionAPI {
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiJ9.Xuae94z8TgirLrK_u2DbX-Qg_5y1Qzo29JLjpY47HU0";

    private void uploadVideoToServer(Uri fileUri) {
        try {
            OkHttpClient client = new OkHttpClient();
            String filePath = fileUri.getPath();
            File videoFile = new File(filePath); // helper below
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] videoBytes = getBytes(inputStream);

            RequestBody videoBody = RequestBody.create(videoBytes, MediaType.parse("video/mp4"));
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", filePath, videoBody)
                    .build();

            Request request = new Request.Builder()
                    .url("http://191.101.235.101/transcription")
                    .addHeader("Authorization", "Bearer " + token)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.d("Request", "Upload Failed");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload failed for " + filePath, Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Upload failed: " + response.message(), Toast.LENGTH_LONG).show();
                            Log.d("Request Error", response.message().toString());
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to get video path", Toast.LENGTH_SHORT).show();
        }
    }
}
