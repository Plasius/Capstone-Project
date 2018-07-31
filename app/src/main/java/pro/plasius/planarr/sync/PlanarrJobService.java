package pro.plasius.planarr.sync;

import com.firebase.jobdispatcher.JobService;

import pro.plasius.planarr.utils.ReferenceManager;

public class PlanarrJobService extends JobService{
    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReferenceManager.getReference();
                //at this point the sync has happened
            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }
}
