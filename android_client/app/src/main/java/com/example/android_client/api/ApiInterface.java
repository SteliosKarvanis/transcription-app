package com.example.android_client.api;

import com.example.android_client.models.TaskPromise;
import com.example.android_client.models.TaskResult;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/auth")
    Call<Map<String, String>> authenticate(
            @Field("username") String username,
            @Field("password") String password
    );

    @GET("transcription/task/{taskId}")
    Call<TaskResult> getTaskTranscription(@Header("Authorization") String token, @Path("taskId") String taskId);

    @GET("transcription/tasks")
    Call<List<TaskPromise>> getTasks(@Header("Authorization") String token);

    @Multipart
    @POST("transcription")
    Call<Void> transcript(@Header("Authorization") String token, @Body MultipartBody file);
}
