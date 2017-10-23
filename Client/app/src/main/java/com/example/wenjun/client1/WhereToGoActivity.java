package com.example.wenjun.client1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;

public class WhereToGoActivity extends AppCompatActivity {


    private String[] places = new String[]{"Regal Fenway", "amc", "Landmark Kendall Square"};
    private int[] distance = new int[]{5,7,3};
    private int[] sortedDistance = new int[distance.length];
    private int[] preference = new int[distance.length];
    private static final int SERVERPORT = 8000;
    private static final String SERVER_IP = "128.197.11.36";
    private int numParties = 2;
    private TextView textTogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_to_go);


        textTogo = (TextView) findViewById(R.id.textTogo);
        // just a copy
        for(int i = 0; i < distance.length; i++){
            sortedDistance[i] = distance[i];
        }
        // sort the distance vector.
        Arrays.sort(sortedDistance);

        // now construct the preference vector - this is the vector to be summed.
        for(int i = 0; i < distance.length; i++){
            preference[i] = Arrays.binarySearch(sortedDistance,distance[i]);
            Log.i("Prefenrence", Integer.toString(preference[i]));
        }

        MPCVectorAddition mpc = new MPCVectorAddition(SERVERPORT,SERVER_IP,numParties,distance);
        int[] result = mpc.sendMessage();
        int[] sortedResult = new int[result.length];

        // just a copy
        for(int i = 0; i < result.length; i++){
            sortedResult[i] = result[i];
            Log.i("After Result", Integer.toString(result[i]));
        }

        Arrays.sort(sortedResult);

        int lowest = sortedResult[0];
        int index = 0;
        for(int i = 0; i < result.length; i++){
            if(result[i] == lowest){
                index = i;
                break;
            }
        }
        //int togo = ArrayUtils.indexOf(result,sortedResult[0]);
        Log.i("SortedResult", Integer.toString(index));

        textTogo.setText(places[index]);
    }
}
