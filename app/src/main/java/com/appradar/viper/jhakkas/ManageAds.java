package com.appradar.viper.jhakkas;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by viper on 8/5/15.
 */
public class ManageAds extends AdListener {
    private static InterstitialAd interstitialAd;
    private static Context context;
    private static AdRequest.Builder builder;

    public static void prepareAds(Context con, String location, String id){
        ManageAds.context = con;
        if(interstitialAd == null)
            interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(id);
        refreshAd();
    }

    public static void refreshAd(){
        builder = new AdRequest.Builder();
        if(MainActivity.DEBUG)
            builder.addTestDevice("AD618B7A612CDAC611081C8F115FF919");
        interstitialAd.loadAd(builder.build());
    }

    public static InterstitialAd getInterstitialAd(){
        return interstitialAd;
    }
}
