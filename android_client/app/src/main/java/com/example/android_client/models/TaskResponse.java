package com.example.android_client.models;

import org.json.JSONObject;

public class TaskResponse extends Task {
    public String transcription;

    public TaskResponse(String task_id, String sender_file_path, String user, String status, String transcription) {
        super(task_id, sender_file_path, user, status);
        this.transcription = transcription;
    }

    public TaskResponse from_json(JSONObject json_obj) {
        String task_id = json_obj.optString("task_id");
        String sender_file_path = json_obj.optString("sender_file_path");
        String user = json_obj.optString("user");
        String status = json_obj.optString("status");
        String transcription = json_obj.optString("transcription");
        return new TaskResponse(task_id, sender_file_path, user, status, transcription);
    }
}
