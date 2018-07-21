package pro.plasius.planarr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pro.plasius.planarr.adapter.TaskAdapter;
import pro.plasius.planarr.data.Task;

public class TaskListActivity extends AppCompatActivity {

    private static final String TAG_LISTER = "LISTER";
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private RecyclerView mRecyclerView;


    //mReference.child(UUID.randomUUID().toString()).setValue(new Task("Clean room", 1, 0));



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        mRecyclerView = findViewById(R.id.list_rv_tasks);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUser = FirebaseAuth.getInstance().getCurrentUser();


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mReference = database.getReference("users/" + mUser.getUid()+"/tasks");

        mReference.keepSynced(true);

        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    ArrayList<Task> tasks = new ArrayList<Task>();
                    for(DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()){
                        tasks.add(singleDataSnapshot.getValue(Task.class));
                    }
                    populateList(tasks);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG_LISTER, "Database error: " + database.toString());
            }
        });

    }

    private void populateList(ArrayList<Task> tasks){
        TaskAdapter adapter = new TaskAdapter(tasks);
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_add_new_task:
                return true;
            case R.id.menu_sign_out:
                FirebaseAuth.getInstance().signOut();
                AuthUI.getInstance().signOut(TaskListActivity.this);

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
