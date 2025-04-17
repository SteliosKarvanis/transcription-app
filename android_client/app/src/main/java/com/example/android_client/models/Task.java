package com.example.android_client.models;

import java.io.Serializable;

public class Task implements Serializable {
    public String task_id;
    public String local_path;
    public String user;
    public String status;
    public String transcription;

    public Task(String task_id, String local_path, String user, String status, String transcription) {
        this.task_id = task_id;
        this.local_path = local_path;
        this.user = user;
        this.status = status;
        this.transcription = transcription;
    }
}
