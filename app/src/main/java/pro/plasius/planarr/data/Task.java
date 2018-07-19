package pro.plasius.planarr.data;

public class Task {
    private String title;
    private int priority;
    private int timestamp;

    public Task(String title, int priority, int timestamp){
        this.title = title;
        this.priority = priority;
        this.timestamp = timestamp;
    }

    public Task(){}

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
