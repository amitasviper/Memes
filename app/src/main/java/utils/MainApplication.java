package utils;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


/**
 * Created by viper on 18/09/16.
 */
public class MainApplication extends Application {

    public static String FIREBASE_URL = "https://testapps-91a91.firebaseio.com/";
    public static String FIREBASE_STORAGE_URL = "gs://testapps-91a91.appspot.com";
    public static Firebase FIREBASE_REF;

    private static MainApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        FacebookSdk.sdkInitialize(getApplicationContext());
        Firebase.setAndroidContext(this);

        FIREBASE_REF = new Firebase(MainApplication.FIREBASE_URL);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        Picasso.setSingletonInstance(built);

    }

    public static synchronized MainApplication getInstance()
    {
        return mInstance;
    }
}
