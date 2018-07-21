package pro.plasius.planarr.data;

import java.util.UUID;

public class Task {
    private String taskId;
    private String title;
    private int priority;
    private int timestamp;


    public Task(String title, int priority, int timestamp){
        this.taskId = UUID.randomUUID().toString();
        this.title = title;
        this.priority = priority;
        this.timestamp = timestamp;
    }

    public Task(){}


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
