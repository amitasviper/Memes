package com.appradar.viper.jhakkas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DisplayImage extends AppCompatActivity {

    ImageView iv_fullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        String url = getIntent().getStringExtra("url");

        iv_fullscreen = (ImageView) findViewById(R.id.iv_fullscreen);

        Picasso.with(this).load(url).into(iv_fullscreen);

    }
}
