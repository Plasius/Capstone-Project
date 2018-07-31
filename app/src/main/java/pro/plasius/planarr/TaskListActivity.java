package pro.plasius.planarr;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.plasius.planarr.adapter.TaskAdapter;
import pro.plasius.planarr.utils.ReferenceManager;
import pro.plasius.planarr.data.Task;

import static pro.plasius.planarr.TaskActivity.EXTRA_TASK;

public class TaskListActivity extends AppCompatActivity implements TaskAdapter.OnItemClickListener, ReferenceManager.TaskListener {
    private static final String TAG_LISTER = "LISTER";
    private DatabaseReference mReference;
    private FirebaseAnalytics mFirebaseAnalytics;

    @BindView(R.id.list_fab_add_task) FloatingActionButton mFabAddTask;
    @BindView(R.id.list_rv_tasks) RecyclerView mRvTasks;

    @Override
    public void onResume() {
        super.onResume();
        refreshTasks();
    }

    @Override
    public void onPause() {
        super.onPause();
        refreshTasks();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        ButterKnife.bind(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mRvTasks.setHasFixedSize(true);
        mRvTasks.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                mReference.child((String)viewHolder.itemView.getTag()).removeValue();

                //log event
                mFirebaseAnalytics.logEvent("event_task_complete", null);


                //refresh
                refreshTasks();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRvTasks);


        mFabAddTask.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                makeTask(null);
            }
        });


        refreshTasks();
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


    private void populateList(ArrayList<Task> tasks){
        TaskAdapter adapter = new TaskAdapter(tasks, this);
        mRvTasks.setAdapter(adapter);

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

    private void refreshTasks(){
        mReference = ReferenceManager.getReference();
        if(mReference != null)
            ReferenceManager.getTasksFromReference(this, mReference);

    }

    //RecyclerView - Click
    @Override
    public void onItemClick(Task task) {
        makeTask(task);
    }

    //ReferenceManager
    @Override
    public void onDatabaseRead(ArrayList<Task> tasks) {
        populateList(tasks);
    }
}
