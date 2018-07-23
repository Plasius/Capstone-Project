package pro.plasius.planarr;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
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
import java.util.List;

import pro.plasius.planarr.adapter.TaskAdapter;
import pro.plasius.planarr.data.Task;

import static pro.plasius.planarr.TaskActivity.EXTRA_TASK;

public class TaskListActivity extends AppCompatActivity implements TaskAdapter.OnItemClickListener {
    private static final String TAG_LISTER = "LISTER";
    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private RecyclerView mRecyclerView;
    ValueEventListener mValueEventListener;

    @Override
    public void onResume() {
        super.onResume();
        mReference.addListenerForSingleValueEvent(mValueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mReference.removeEventListener(mValueEventListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        mRecyclerView = findViewById(R.id.list_rv_tasks);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                mReference.child((String)viewHolder.itemView.getTag()).removeValue();

                //refresh
                mReference.removeEventListener(mValueEventListener);
                mReference.addListenerForSingleValueEvent(mValueEventListener);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        FloatingActionButton fab = findViewById(R.id.list_fab_add_task);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                makeTask(null);
            }
        });

        mUser = FirebaseAuth.getInstance().getCurrentUser();


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mReference = database.getReference("users/" + mUser.getUid()+"/tasks");

        mReference.keepSynced(true);

        mValueEventListener= new ValueEventListener() {
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

                    populateList(tasks);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG_LISTER, "Database error: " + database.toString());
            }

        };


    }

    private void populateList(ArrayList<Task> tasks){
        TaskAdapter adapter = new TaskAdapter(tasks, this);
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
                makeTask(null);
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

    private void makeTask(Task task){
        if(task == null){
            //new task
            Intent intent=  new Intent(this, TaskActivity.class);
            startActivity(intent);

        }else{
            //editing existing task
            Intent intent=  new Intent(this, TaskActivity.class);
            intent.putExtra(EXTRA_TASK, task);
            startActivity(intent);
        }
    }

    //recyclerview
    @Override
    public void onItemClick(Task task) {
        makeTask(task);
    }
}
