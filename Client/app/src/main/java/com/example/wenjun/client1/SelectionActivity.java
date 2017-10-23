package com.example.wenjun.client1;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SelectionActivity extends AppCompatActivity {

    private ListView events;
    private ListAdapter eventAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        events = (ListView)findViewById(R.id.event);
        eventAdapter = new MyCustomAdapter(this.getBaseContext());  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
        events.setAdapter(eventAdapter);


    }
}

class MyCustomAdapter extends BaseAdapter{
    private String[] type = new String[]{"vote","addition","where to go"};
    private String[] description = new String[]{"Go for a moive or not","sum of salary", "find a coffee shop"};
    private String[] party= new String[]{"Lindsay Jiang, Justin Shen, Jeremy Xu","Lindsay Jiang, Justin Shen, Jeremy Xu","Lindsay Jiang, Justin Shen, Jeremy Xu"};

    Context context;

    public MyCustomAdapter(Context aContext) {

        context = aContext;  //saving the context we'll need it again.

//initializing our data in the constructor.
//        episodes = (ArrayList<String>) Arrays.asList(aContext.getResources().getStringArray(R.array.episodes));  //retrieving list of episodes predefined in strings-array "episodes" in strings.xml
//        episodeDescriptions = (ArrayList<String>) Arrays.asList(aContext.getResources().getStringArray(R.array.episode_descriptions));  //Also casting to a friendly ArrayList.





    }

    @Override
    public int getCount() {
        return type.length;
    }

    @Override
    public Object getItem(int i) {
        return type[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //CRASH
            row = inflater.inflate(R.layout.listview_row, parent, false);
        }
        else
        {
            row = convertView;
        }
        TextView typeView = (TextView)row.findViewById(R.id.textViewType);
        TextView descriptionView = (TextView)row.findViewById(R.id.textViewDescription);
        TextView partyView = (TextView)row.findViewById(R.id.textViewParty);
        final Button start = (Button)row.findViewById(R.id.button);

        typeView.setText(type[position]);
        descriptionView.setText("description: " + description[position]);
        partyView.setText("participants: " + party[position]);
        start.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(position==0) {
                    Intent intent = new Intent(context, VoteActivity.class);
                    context.startActivity(intent);
                }else if(position == 1){
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, WhereToGoActivity.class);
                    context.startActivity(intent);
                }

            }
        });


        return row;

    }
}
