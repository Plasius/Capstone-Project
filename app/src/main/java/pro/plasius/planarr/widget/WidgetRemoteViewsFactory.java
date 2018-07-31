package pro.plasius.planarr.widget;

import android.content.Context;
import android.content.Intent;

import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import pro.plasius.planarr.R;
import pro.plasius.planarr.utils.ReferenceManager;
import pro.plasius.planarr.data.Task;
import pro.plasius.planarr.utils.DateUtil;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, ReferenceManager.TaskListener {
    private Context mContext;
    private List<Task> mTasks;
    private CountDownLatch mCountDownLatch;

    WidgetRemoteViewsFactory(Context context, Intent intent){
        mContext = context;

    }

    private void refresh(){
        DatabaseReference reference = ReferenceManager.getReference();
        if(reference != null)
            ReferenceManager.getTasksFromReference(this, reference);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        mCountDownLatch = new CountDownLatch(1);
        refresh();
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(mTasks != null)
            return mTasks.size();

        return 0;
    }

    @Override
    public RemoteViews getViewAt(int pos) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_widget);
        rv.setTextViewText(R.id.item_tv_title, mTasks.get(pos).getTitle());
        rv.setTextViewText(R.id.item_tv_date, DateUtil.formatTimestamp(mTasks.get(pos).getTimestamp()));

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return mTasks.get(i).getTimestamp();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDatabaseRead(ArrayList<Task> tasks) {
        this.mTasks = tasks;
        mCountDownLatch.countDown();
    }
}
