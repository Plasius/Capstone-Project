package pro.plasius.planarr;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.plasius.planarr.utils.ReferenceManager;
import pro.plasius.planarr.data.Task;
import pro.plasius.planarr.utils.DateUtil;

public class TaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public static final String EXTRA_TASK = "extraTask";

    private Task editTask;
    DatePickerDialog mDatePickerDialog;
    private long millis = 0;

    @BindView(R.id.task_ed_title) EditText mEdTitle;
    @BindView(R.id.task_sb_priority) SeekBar mSbPriority;
    @BindView(R.id.task_bt_date) Button mBtDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);

        editTask = getIntent().getParcelableExtra(EXTRA_TASK);


        Calendar calendar = Calendar.getInstance();

        if(editTask != null){
            mEdTitle.setText(editTask.getTitle());
            mSbPriority.setProgress(editTask.getPriority());

            calendar.setTimeInMillis(editTask.getTimestamp());
        }

        mBtDate.setText(DateUtil.getMonthForInt(calendar.get(Calendar.MONTH))+" "
                +calendar.get(Calendar.DAY_OF_MONTH));

        mDatePickerDialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        millis = calendar.getTimeInMillis();

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
        String sb = DateUtil.getMonthForInt(month) +
                " " +
                day;
        mBtDate.setText(sb);

        Calendar pickedCalendar = Calendar.getInstance();
        pickedCalendar.set(year, month, day);
        millis = pickedCalendar.getTimeInMillis();
    }

    private boolean publishTask(){
        String title = mEdTitle.getText().toString();
        int priority = 4 - mSbPriority.getProgress();

        if(title.equals("")){
            Toast.makeText(this, R.string.task_no_name, Toast.LENGTH_SHORT).show();
            return false;
        }

        Bundle bundle = new Bundle();
        bundle.putString("task_title", title);
        bundle.putLong("task_timestamp", millis);
        bundle.putInt("task_priority", priority);
        FirebaseAnalytics.getInstance(this).logEvent("event_task_added", bundle);

        DatabaseReference reference = ReferenceManager.getReference();

        if(editTask != null){
            reference.child(editTask.getTaskId()).setValue(new Task(editTask.getTaskId(), title, priority, millis));
        }else{
            Task t = new Task(title, priority, millis);
            reference.child(t.getTaskId()).setValue(t);
        }

        return true;


    }


    //Button - Pick Date
    public void datePickClicked(View v){
        mDatePickerDialog.show();
    }


}
