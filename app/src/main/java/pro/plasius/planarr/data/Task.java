package pro.plasius.planarr.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.UUID;

public class Task implements Parcelable, Comparable<Task>{
    private String taskId;
    private String title;
    private int priority;
    private long timestamp;


    public Task(String title, int priority, long timestamp){
        this.taskId = UUID.randomUUID().toString();
        this.title = title;
        this.priority = priority;
        this.timestamp = timestamp;
    }


    public Task(String taskId, String title, int priority, long timestamp){
        this.taskId = taskId;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    //Comparing
    @Override
    public int compareTo(@NonNull Task task) {
        Calendar c1 = Calendar.getInstance();
        c1.setTimeInMillis(timestamp);

        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(task.getTimestamp());

        boolean sameDay = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);

        if(sameDay){
            return priority - task.getPriority();
        }else if(c1.before(c2)){
            return -1;
        }else{
            return 1;
        }
    }


    //Parcel
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(taskId);
        parcel.writeString(title);
        parcel.writeInt(priority);
        parcel.writeLong(timestamp);
    }

    private Task(Parcel in) {
        taskId = in.readString();
        title = in.readString();
        priority = in.readInt();
        timestamp = in.readLong();
    }

    public static final Parcelable.Creator<Task> CREATOR
            = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

}
