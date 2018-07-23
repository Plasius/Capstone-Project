package pro.plasius.planarr;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import pro.plasius.planarr.data.Task;

public class TaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public static final String EXTRA_TASK = "extraTask";
    public static final String EXTRA_DATE = "extraTaskDate";

    private Task editTask;
    DatePickerDialog mDatePickerDialog;
    private long millis = 0;



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        editTask = getIntent().getParcelableExtra(EXTRA_TASK);


        Calendar calendar = Calendar.getInstance();

        if(editTask != null){
            ((EditText)findViewById(R.id.task_ed_title)).setText(editTask.getTitle());
            ((SeekBar)findViewById(R.id.task_sb_priority)).setProgress(editTask.getPriority());

            calendar.setTimeInMillis(editTask.getTimestamp());
        }

        ((Button)findViewById(R.id.task_bt_date)).setText(Integer.toString(calendar.get(Calendar.YEAR))+"-"
                +calendar.get(Calendar.MONTH)+"-"
                +calendar.get(Calendar.DAY_OF_MONTH));

        mDatePickerDialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        millis = calendar.getTimeInMillis();

    }

    public void datePickClicked(View v){
        mDatePickerDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                if(publishTask())
                    finish();
                return true;
            case R.id.menu_add_new:
                if(publishTask()){
                    Intent intent = new Intent(this, TaskActivity.class);
                    startActivity(intent);
                    finish();

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Toast.makeText(this, "Picked date!", Toast.LENGTH_SHORT).show();
        ((Button)findViewById(R.id.task_bt_date)).setText(Integer.toString(year)+"-"+month+"-"+day);

        Calendar pickedCalendar = Calendar.getInstance();
        pickedCalendar.set(year, month, day);
        millis = pickedCalendar.getTimeInMillis();
    }

    private boolean publishTask(){
        String title = ((EditText)findViewById(R.id.task_ed_title)).getText().toString();
        int priority = ((SeekBar)findViewById(R.id.task_sb_priority)).getProgress();

        if(title.equals("")){
            Toast.makeText(this, "Please name the task.", Toast.LENGTH_SHORT).show();
            return false;
        }

        FirebaseUser user;
        DatabaseReference reference;
        user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("users/" + user.getUid()+"/tasks");

        if(editTask != null){
            reference.child(editTask.getTaskId()).setValue(new Task(editTask.getTaskId(), title, priority, millis));
        }else{
            Task t = new Task(title, priority, millis);
            reference.child(t.getTaskId()).setValue(t);
        }

        return true;

    }
}
