package com.example.wenjun.client1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowVoteActivity extends AppCompatActivity {
    private TextView yesVote;
    private TextView noVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vote);
        yesVote = (TextView) findViewById(R.id.textYesVote);
        noVote = (TextView) findViewById(R.id.textNoVote);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        int yes = data.getInt("yes");
        int no = data.getInt("no");
        yesVote.setText(Integer.toString(yes) + " YES");
        noVote.setText(Integer.toString(no) + "NO");
    }
}
