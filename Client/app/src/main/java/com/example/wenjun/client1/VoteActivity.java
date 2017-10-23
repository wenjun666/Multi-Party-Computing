package com.example.wenjun.client1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VoteActivity extends AppCompatActivity {

    private Button voteYes;
    private Button voteNo;
    private Button submit;
    private TextView voted;
    // default vote is no.
    private int vote = 0;
    private int numParties = 2;
    private static final int SERVERPORT = 8000;
    private static final String SERVER_IP = "128.197.11.36";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        voteYes = (Button)findViewById(R.id.buttonYes);
        voteNo = (Button) findViewById(R.id.buttonNo);
        submit = (Button) findViewById(R.id.buttonSubmitVote);
        voted = (TextView) findViewById(R.id.textViewVoted);

        voteYes.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                vote = 1;
                voted.setText("You voted Yes");
            }
        });

        voteNo.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                vote = 0;
                voted.setText("You voted No");
            }
        });

        submit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                MPCSecretAddition mpc = new MPCSecretAddition(SERVERPORT,SERVER_IP,numParties,vote);
                int result = mpc.sendMessage();
                Intent intent = new Intent(VoteActivity.this, ShowVoteActivity.class);
                intent.putExtra("yes", result);
                intent.putExtra("no", numParties - result);
                startActivity(intent);
            }
        });

    }
}
