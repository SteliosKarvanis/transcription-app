package com.example.android_client.models;

import java.io.Serializable;

public class TaskResult implements Serializable {
    public String task_id;
    public String sender_file_path;
    public String transcription;

    public TaskResult(String task_id, String sender_file_path, String transcription) {
        this.task_id = task_id;
        this.sender_file_path = sender_file_path;
        this.transcription = transcription;
    }
}
