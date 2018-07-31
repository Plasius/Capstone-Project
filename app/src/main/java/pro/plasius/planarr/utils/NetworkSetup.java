package pro.plasius.planarr.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.database.FirebaseDatabase;

import pro.plasius.planarr.sync.TaskJobService;

public class NetworkSetup extends android.app.Application{
    private static final String TAG_JOB = "PlannarSyncJobTag";


    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        scheduleJob(this);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void scheduleJob(Context context) {
        //creating new firebase job dispatcher
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        //creating new job and adding it with dispatcher
        Job job = createJob(dispatcher);
        dispatcher.mustSchedule(job);
    }

    public static Job createJob(FirebaseJobDispatcher dispatcher){

        Job job = dispatcher.newJobBuilder()
                //persist the task across boots
                .setLifetime(Lifetime.FOREVER)
                //.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                //call this service when the criteria are met.
                .setService(TaskJobService.class)
                //unique id of the task
                .setTag(TAG_JOB)
                //don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // We are mentioning that the job is periodic.
                .setRecurring(true)
                // Run between 1-2 hours from now.
                .setTrigger(Trigger.executionWindow(3600, 5400))
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                //Run this job only when the network is available.
                .setConstraints(Constraint.ON_ANY_NETWORK, Constraint.DEVICE_CHARGING)
                .build();
        Log.d(TAG_JOB, "starting job");
        return job;
    }


}