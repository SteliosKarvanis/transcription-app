package com.example.android_client.api;

import com.example.android_client.models.TaskPromise;
import com.example.android_client.models.TaskResult;
import com.example.android_client.utils.ApiUtils;
import com.example.android_client.utils.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://168.231.89.123/";
    private static final String token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJob21lcm8ifQ.9caVOOhww0rhFf5jkmtILS-rIKT-NXyeP9g3qqpVQmg";
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
    private static final Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();

    public static void getTasks(ApiUtils.SuccessHandler<List<TaskPromise>> handler) {
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<List<TaskPromise>> call = apiService.getTasks(token);
        call.enqueue(ApiUtils.createCallback(handler));
    }

    public static void getTranscription(ApiUtils.SuccessHandler<TaskResult> handler, String taskId) {
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<TaskResult> call = apiService.getTaskTranscription(token, taskId);
        call.enqueue(ApiUtils.createCallback(handler));
    }
    public static void transcript(ApiUtils.SuccessHandler<TaskPromise> handler, File file) {
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        RequestBody requestBody = RequestBody.create(
                file,
                MediaType.parse("video/*")
        );
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                requestBody
        );
        Call<TaskPromise> call = apiService.transcript(token, filePart);
        call.enqueue(ApiUtils.createCallback(handler));
    }
}