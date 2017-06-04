package com.appradar.viper.jhakkas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import CustomFragments.FirstFragment;
import CustomFragments.FriendsFragment;
import CustomFragments.Pager;
import Temp.Person;
import customadapters.TextPostsAdapter;
import models.User;
import utils.CircleTransform;

public class MainActivity extends AppCompatActivity
        implements TabLayout.OnTabSelectedListener,
        FirstFragment.StoreInDatabaseListener,
        FriendsFragment.OnFriendClickListener,
        TextPostsAdapter.TextPostClickListner {

    TabLayout tabLayout;
    ViewPager viewPager;
    DrawerLayout leftDrawerLayout;
    ListView leftDrawerList;
    public static Firebase ref;

    boolean isExitPressed;
    InterstitialAd interstitialAd;

    static boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ref = MainApplication.FIREBASE_REF;

        leftDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawerList = (ListView) findViewById(R.id.drawer_list);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CacheLargeAd();
        //Initializing the tablayout
        tabLayout.addTab(tabLayout.newTab().setText("ARTICLES"));
        tabLayout.addTab(tabLayout.newTab().setText("FRIENDS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != "FRIENDS")
                    displayInterstitialAd();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.e("onPageScrolled", position + "  " +positionOffset );
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("onPageSelected", "" + position);
                TabLayout.Tab tab = tabLayout.getTabAt(position);
                tab.select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //handle if needed
            }
        });

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);

        // Creating an ArrayAdapter to add items to the listview leftDrawerList
        ArrayAdapter<String> adapterL = new ArrayAdapter<String>(
                getBaseContext(),
                R.layout.drawer_list_item,
                getResources().getStringArray(R.array.drawerOptions)
        );

        // Setting the adapter on leftDrawerList
        leftDrawerList.setAdapter(adapterL);

        // Enabling Home button
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Disable Up navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Setting item click listener for the listview leftDrawerList NOT_WORKING
        leftDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                leftDrawerLayout.closeDrawers();
                String option_text = ((TextView) view).getText().toString();
                if (option_text.equalsIgnoreCase(getString(R.string.submitArticle))) {
                    Intent intent = new Intent(MainActivity.this, SubmitJoke.class);
                    startActivity(intent);
                    return;
                }
                if (option_text.equalsIgnoreCase(getString(R.string.submitImage))) {
                    Intent intent = new Intent(MainActivity.this, SubmitImage.class);
                    startActivity(intent);
                    return;
                }
                if (option_text.equalsIgnoreCase("Rate Us")) {
                    String appPackage = MainActivity.this.getPackageName();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
                    MainActivity.this.startActivity(intent);
                }
                if (getResources().getString(R.string.logout).equalsIgnoreCase(option_text)) {
                    Logout();
                }
                leftDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, leftDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        leftDrawerLayout.setDrawerListener(drawerToggle);

        drawerToggle.syncState();

        SetupProfileInfo();
        leftDrawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        Log.e("onTabSelected", "Position : " + tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
        }

        if (menuItem.getItemId() == R.id.logout) {
            Logout();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onStoreRequestGenerated(Person person) {
        ref.child("images").child(person.getName()).setValue(person);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.layout_menu, menu);
        return true;
    }

    private void StartLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        StartLoginActivity();
    }

    private void SetupProfileInfo() {
        ImageView iv_profile_image = (ImageView) findViewById(R.id.iv_profile_image);
        TextView iv_profile_name = (TextView) findViewById(R.id.tv_profile_name);
        TextView iv_profile_email = (TextView) findViewById(R.id.tv_profile_email);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        Picasso.with(MainActivity.this)
                .load(user.getPhotoUrl())
                .resize(80, 80)
                .placeholder(R.drawable.profile_empty)
                .transform(new CircleTransform())
                .into(iv_profile_image);

        iv_profile_name.setText(user.getDisplayName());
        iv_profile_email.setText(user.getEmail());
    }

    @Override
    public void onFriendClick(View view, int position) {
        LinearLayout friend_row_ll = (LinearLayout) view;
        TextView friend_name = (TextView) friend_row_ll.findViewById(R.id.tv_friend_name);
        Log.e("onFriendClick", friend_name.getText().toString());
        User friend = (User) friend_name.getTag();
        StartChatActivity(friend);
    }

    private void StartChatActivity(User friend) {
        Intent intent = new Intent(this, NewChatActivity.class);
        intent.putExtra("friend", friend);
        startActivity(intent);
    }

    @Override
    public void onItemClick(RecyclerView.ViewHolder holder, View view, int HolderType) {

        String application = "";
        String shareText = "";
        if (HolderType == TextPostsAdapter.TEXT_POST)
            shareText = ((TextPostsAdapter.TextPostHolder) holder).GetViewContents();
        else
            shareText = ((TextPostsAdapter.ImagePostHolder) holder).GetViewContents();

        shareText += "\n Sent Via : https://goo.gl/Xg51Ha";

        switch (view.getId()) {
            case R.id.iv_whatsapp:
                application = "com.whatsapp";
                break;

            case R.id.iv_facebook:
                application = "com.facebook.katana";
                shareText = "https://play.google.com/store/apps/details?id=com.appradar.lateststatusnquotes";
                break;

            case R.id.iv_twitter:
                application = "com.twitter.android";
                break;

            case R.id.iv_share:
                application = "";
                break;

        }

        Intent intent = this.getPackageManager().getLaunchIntentForPackage(application);
        if (intent != null) {
            // The application exists
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.setPackage(application);
            // Start the specific social application
            this.startActivity(shareIntent);
        } else {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            Intent chooser = Intent.createChooser(shareIntent, "Share Via");
            this.startActivity(chooser);
            Log.e("onItemClick", "Application not found : " + application);
        }
    }

    @Override
    public void onBackPressed() {
        if (isExitPressed) {
            super.onBackPressed();
            return;
        }

        this.isExitPressed = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                isExitPressed = false;
            }
        }, 2000);
        isExitPressed = true;
    }

    private void CacheLargeAd() {
        interstitialAd = MainApplication.ManageAdsObject.getInterstitialAd();
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                MainApplication.ManageAdsObject.refreshAd();
            }

            @Override
            public void onAdLoaded() {
                Log.e("INTERSTETIAL", "INTERSTETIAL AD loaded");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.e("INTERSTETIAL", "INTERSTETIAL AD FAILED FAILED");
            }
        });
    }

    private void displayInterstitialAd() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }
}
