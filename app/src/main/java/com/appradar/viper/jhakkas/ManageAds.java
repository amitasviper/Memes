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
    private InterstitialAd interstitialAd;
    private Context context;
    private AdRequest.Builder builder;

    public ManageAds(Context con){
        this.context = con;
        if(interstitialAd == null)
            interstitialAd = new InterstitialAd(context);
        String id = context.getResources().getString(R.string.InterstetialOne);
        interstitialAd.setAdUnitId(id);
        refreshAd();
    }

    public void refreshAd(){
        builder = new AdRequest.Builder();
        interstitialAd.loadAd(builder.build());
    }

    public InterstitialAd getInterstitialAd(){
        Log.d("ManageAds", "Request for an ad :getInterstitialAd:");
        return this.interstitialAd;
    }
}
