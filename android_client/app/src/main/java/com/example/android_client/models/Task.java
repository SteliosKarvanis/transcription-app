package com.example.android_client.models;

import org.json.JSONObject;

import java.io.Serializable;
import com.google.gson.Gson;

public class Task implements Serializable {
    private static final Gson gson = new Gson();

    public String task_id;
    public String sender_file_path;
    public String user;
    public String status;

    public Task(String task_id, String sender_file_path, String user, String status) {
        this.task_id = task_id;
        this.sender_file_path = sender_file_path;
        this.user = user;
        this.status = status;
    }

//    public static Task from_json(JSONObject json_obj) {
//        String task_id = json_obj.optString("task_id");
//        String sender_file_path = json_obj.optString("sender_file_path");
//        String user = json_obj.optString("user");
//        String status = json_obj.optString("status");
//        return new Task(task_id, sender_file_path, user, status);
//    }

    public static TaskResponse fromJson(String json) {
        return gson.fromJson(json, TaskResponse.class);
    }
}

