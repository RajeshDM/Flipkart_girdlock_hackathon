package com.example.soumya.traffic2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CongestionInfo extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congestion_info);
        Intent intent = getIntent();
        final String s = intent.getStringExtra("Marker");
        final String url = intent.getStringExtra("urlimage");
        System.out.println("URL" + url + "heyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy url");


        TextView t = (TextView) findViewById(R.id.textView);
        t.setText(s);
        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CongestionInfo.this, TrafficImage.class);
                intent.putExtra("URL", url);
                startActivity(intent);
            }
        });


    }


}
