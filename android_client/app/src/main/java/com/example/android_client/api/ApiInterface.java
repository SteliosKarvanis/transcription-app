package com.example.android_client.api;

import com.example.android_client.models.TaskPromise;
import com.example.android_client.models.TaskResult;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("transcription/task/{taskId}")
    Call<TaskResult> getTaskTranscription(@Header("Authorization") String token, @Path("taskId") String taskId);

    @GET("transcription/tasks")
    Call<List<TaskPromise>> getTasks(@Header("Authorization") String token);

    @Multipart
    @POST("transcription")
    Call<TaskPromise> transcript(@Header("Authorization") String token, @Part MultipartBody.Part file);
}
