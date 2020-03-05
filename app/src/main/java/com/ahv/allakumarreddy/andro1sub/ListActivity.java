package com.ahv.allakumarreddy.andro1sub;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        lv = findViewById(R.id.lv);
        Intent intent = getIntent();
        final ArrayList<String[]> list = (ArrayList<String[]>) intent.getExtras().get("list");
        final ArrayList<String> name = (ArrayList<String>) intent.getExtras().get("name");
        // contacts 1
        // sms 2
        // call log 3
        // calender 4
        String actionBarText = "";
        switch (intent.getExtras().getInt("type")) {
            case 1:
                actionBarText = "Contacts";
                break;
            case 2:
                actionBarText = "Sms";
                break;
            case 3:
                actionBarText = "Call Log";
                break;
            case 4:
                actionBarText = "Calender";
                break;
            default:
                break;
        }

        getSupportActionBar().setTitle(actionBarText + " ( " + list.size() + " )");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, name);
        lv.setAdapter(adapter);
        lv.deferNotifyDataSetChanged();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] strArr = list.get(position);
                String data = "";
                for (String s : strArr)
                    data += s + "\n";
                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                builder.setMessage(data);
                AlertDialog alert = builder.create();
                alert.setTitle(name.get(position));
                alert.show();
            }
        });
    }
}
