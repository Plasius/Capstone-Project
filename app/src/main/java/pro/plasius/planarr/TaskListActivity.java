package pro.plasius.planarr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import pro.plasius.planarr.data.Task;

public class TaskListActivity extends AppCompatActivity {

    private static final String TAG_LISTER = "LISTER";
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    //mReference.child(UUID.randomUUID().toString()).setValue(new Task("Clean room", 1, 0));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        mUser = FirebaseAuth.getInstance().getCurrentUser();


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mReference = database.getReference("users/" + mUser.getUid()+"/tasks");

        //DEBUG
        mReference.child(UUID.randomUUID().toString()).setValue(new Task("Clean room", 1, 0));

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){
                        Task t = singleDataSnapshot.getValue(Task.class);
                        Log.d(TAG_LISTER, "Getting task: "+t.getTitle());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG_LISTER, "Database error: " + database.toString());
            }
        });
    }


}
