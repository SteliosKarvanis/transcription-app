package com.example.android_client.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TaskPromise implements Serializable {

    public String task_id;
    public String status;
    public String sender_file_path;
    public LocalDateTime last_requested_at;

    public TaskPromise(String task_id, String status, String sender_file_path, LocalDateTime last_requested_at) {
        this.task_id = task_id;
        this.status = status;
        this.sender_file_path = sender_file_path;
        this.last_requested_at = last_requested_at;
    }
}

