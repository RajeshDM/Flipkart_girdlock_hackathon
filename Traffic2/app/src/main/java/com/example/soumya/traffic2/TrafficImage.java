package com.example.soumya.traffic2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.soumya.traffic2.app.CustomVolleyRequestQueue;

public class TrafficImage extends AppCompatActivity {

    private NetworkImageView mNetworkImageView;
    private ImageLoader mImageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_image);
        mNetworkImageView = (NetworkImageView) findViewById(R.id
                .networkImageView);
    }

    protected void onStart() {
        super.onStart();
        // Instantiate the RequestQueue.
        mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getImageLoader();
        //Image URL - This can point to any image file supported by Android
        Intent intent = getIntent();
        final String url = intent.getStringExtra("URL");
        System.out.println(url+ "traffficccccccc");
        mImageLoader.get(url, ImageLoader.getImageListener(mNetworkImageView,
                R.mipmap.ic_launcher, android.R.drawable
                        .ic_dialog_alert));
        mNetworkImageView.setImageUrl(url, mImageLoader);
    }


}
