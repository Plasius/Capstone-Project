package pro.plasius.planarr.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetRemoteViewsService extends RemoteViewsService{
    private RemoteViewsFactory mFactory;
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        mFactory = new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
        return mFactory;
    }

}
