package com.example.wenjun.client1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    /*
    This activity is for secretAddition.
     */

    private Socket socket;

    private static final int SERVERPORT = 8000;
    private static final String SERVER_IP = "128.197.11.36";
    // this should be the same as number of parties.
    private int numParties = 2;

    private EditText textToBeEncrypted;


    private Button buttonShow;






    private int combined;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textToBeEncrypted = (EditText)findViewById(R.id.EditText01);

        buttonShow = (Button) findViewById(R.id.buttonShow);


        buttonShow.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                String input = textToBeEncrypted.getText().toString();
                MPCSecretAddition mpc = new MPCSecretAddition(SERVERPORT,SERVER_IP,numParties,Integer.parseInt(input));
                int result = mpc.sendMessage();
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("result", result);
                startActivity(intent);
            }
        });





    }


}
