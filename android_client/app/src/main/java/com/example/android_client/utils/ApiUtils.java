package com.example.android_client.utils;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiUtils {
        public interface SuccessHandler<T> {
            void onSuccess(T response);
        }
        public static <T> Callback<T> createCallback(SuccessHandler<T> success) {
            return new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        success.onSuccess(response.body());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                }
            };
        }
}
