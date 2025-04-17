package com.example.android_client.models;

public class Task {
    public String task_id;
    public String user;
    public String status;
    public String transcription;

    public Task(String task_id, String user, String status, String transcription) {
        this.task_id = task_id;
        this.user = user;
        this.status = status;
        this.transcription = transcription;
    }
}
