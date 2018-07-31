package pro.plasius.planarr.utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import pro.plasius.planarr.data.Task;
import pro.plasius.planarr.widget.WidgetRefresher;


public class ReferenceManager {
    private static final String TAG_REF_MANAGER = "Reference Manager";

    private static ReferenceManager instance;

    private static ReferenceManager getInstance(){
        if(instance == null){
            instance = new ReferenceManager();
        }
        return instance;
    }

    public interface TaskListener {
        void onDatabaseRead(ArrayList<Task> tasks);
    }

    private TaskListener listener;

    public static DatabaseReference getReference(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
            return null;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users/" + user.getUid()+"/tasks");
        reference.keepSynced(true);
        return reference;
    }

    public static void getTasksFromReference(final Object context, DatabaseReference reference){
        getInstance().listener = (TaskListener) context;

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            // your event listener logic here
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    ArrayList<Task> tasks = new ArrayList<Task>();
                    for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){
                        tasks.add(singleDataSnapshot.getValue(Task.class));
                    }

                    //sort by time and priority
                    Collections.sort(tasks, new Comparator<Task>() {
                        @Override
                        public int compare(Task task, Task t1) {
                            return task.compareTo(t1);
                        }
                    });

                    getInstance().listener.onDatabaseRead(tasks);

                    try{
                        WidgetRefresher.sendRefreshBroadcast((Context) context);
                    }catch(Exception e){
                        Log.d(TAG_REF_MANAGER, "Failed to cast to Context, probably a widget refresh");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG_REF_MANAGER, "Database error: " + databaseError.toString());
            }

        });
    }
}
