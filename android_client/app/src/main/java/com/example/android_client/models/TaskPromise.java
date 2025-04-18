package com.example.android_client.models;

import java.io.Serializable;
import java.sql.Timestamp;

import com.google.gson.Gson;

public class TaskPromise implements Serializable {
    private static final Gson gson = new Gson();

    public String task_id;
    public String status;
    public String sender_file_path;
    public Timestamp last_requested_at;

    public TaskPromise(String task_id, String status, String sender_file_path, Timestamp last_requested_at) {
        this.task_id = task_id;
        this.status = status;
        this.sender_file_path = sender_file_path;
        this.last_requested_at = last_requested_at;
    }

    public static TaskPromise fromJson(String json) {
        return gson.fromJson(json, TaskPromise.class);
    }
}

