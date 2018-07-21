package pro.plasius.planarr.utils;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseSetup extends android.app.Application{

        @Override
        public void onCreate() {
            super.onCreate();
            /* Enable disk persistence  */
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
}