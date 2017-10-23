package com.example.wenjun.client1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    /*
    This activity is just for showing result.
     */
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        result = (TextView)findViewById(R.id.textView);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        int returned = data.getInt("result");
        result.setText(Integer.toString(returned));
    }

}
