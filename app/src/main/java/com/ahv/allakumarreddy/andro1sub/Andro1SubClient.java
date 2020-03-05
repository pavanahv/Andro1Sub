package com.ahv.allakumarreddy.andro1sub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Andro1SubClient extends AppCompatActivity {

    private static final String TAG = "pavan";
    private String fileContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_andro1_sub_client);

        final Button btn[] = new Button[]{
                findViewById(R.id.contacts),
                findViewById(R.id.sms),
                findViewById(R.id.call),
                findViewById(R.id.cal)
        };
        for (int i = 0; i < btn.length; i++)
            btn[i].setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                fileContent = readLocalFile();
                Log.d(TAG, "read file content done");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < btn.length; i++)
                            btn[i].setEnabled(true);
                    }
                });
            }
        }).start();
    }

    public void contacts(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "read file content done");
                try {
                    JSONObject mainObj = new JSONObject(fileContent);
                    JSONArray jsonArr = mainObj.getJSONArray("contacts");
                    final int len = jsonArr.length();
                    final ArrayList<String[]> list = new ArrayList<>();
                    final ArrayList<String> name = new ArrayList<>();

                    for (int i = 0; i < len; i++) {
                        String data[] = new String[6];
                        for (int j = 0; j < data.length; j++)
                            data[j] = "";
                        try {
                            JSONObject obj = jsonArr.getJSONObject(i);
                            data[0] = "ID -> " + obj.getString("id");
                            data[1] = obj.getString("name");
                            data[2] = "Phone -> " + obj.getString("phone");
                            data[3] = "Email -> " + obj.getString("email");
                            data[4] = "EType -> " + obj.getString("etype");
                            data[5] = "";
                        } catch (Exception ex) {
                            Log.d(TAG, ex.getMessage());
                        }
                        list.add(data);
                        name.add(data[1]);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Andro1SubClient.this, ListActivity.class);
                            intent.putExtra("type", 1);
                            intent.putExtra("list", list);
                            intent.putExtra("name", name);
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }).start();

    }

    public void calLog(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "read file content done");
                try {
                    JSONObject mainObj = new JSONObject(fileContent);
                    JSONArray jsonArr = mainObj.getJSONArray("call");
                    final int len = jsonArr.length();
                    final ArrayList<String[]> list = new ArrayList<>();
                    final ArrayList<String> name = new ArrayList<>();

                    for (int i = 0; i < len; i++) {
                        String data[] = new String[4];
                        for (int j = 0; j < data.length; j++)
                            data[j] = "";
                        try {
                            JSONObject obj = jsonArr.getJSONObject(i);
                            data[0] = obj.getString("pno");
                            data[1] = "Type -> " + obj.getString("ctype");
                            data[2] = "Call Time -> " + obj.getString("ctime");
                            data[3] = "Call Duration -> " + obj.getString("cdur");
                        } catch (Exception ex) {
                            Log.d(TAG, ex.getMessage());
                        }
                        list.add(data);
                        name.add(data[0]);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Andro1SubClient.this, ListActivity.class);
                            intent.putExtra("type", 3);
                            intent.putExtra("list", list);
                            intent.putExtra("name", name);
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }).start();
    }

    public void sms(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "read file content done");
                try {
                    JSONObject mainObj = new JSONObject(fileContent);
                    JSONArray jsonArr = mainObj.getJSONArray("sms");
                    final int len = jsonArr.length();
                    final ArrayList<String[]> list = new ArrayList<>();
                    final ArrayList<String> name = new ArrayList<>();

                    for (int i = 0; i < len; i++) {
                        String data[] = new String[6];
                        for (int j = 0; j < data.length; j++)
                            data[j] = "";
                        try {
                            JSONObject obj = jsonArr.getJSONObject(i);
                            data[0] = "ID -> " + obj.getString("id");
                            data[1] = "Address -> " + obj.getString("address");
                            data[2] = "Message -> ";
                            data[3] = "State -> " + obj.getString("state");
                            data[4] = "Time -> " + obj.getString("time");
                            data[5] = obj.getString("fname");
                        } catch (Exception ex) {
                            Log.d(TAG, ex.getMessage());
                        }
                        list.add(data);
                        name.add(data[5]);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Andro1SubClient.this, ListActivity.class);
                            intent.putExtra("type", 3);
                            intent.putExtra("list", list);
                            intent.putExtra("name", name);
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }).start();
    }

    public void calenderEvents(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "read file content done");
                try {
                    JSONObject mainObj = new JSONObject(fileContent);
                    JSONArray jsonArr = mainObj.getJSONArray("calender");
                    final int len = jsonArr.length();
                    final ArrayList<String[]> list = new ArrayList<>();
                    final ArrayList<String> name = new ArrayList<>();

                    for (int i = 0; i < len; i++) {
                        String data[] = new String[4];
                        for (int j = 0; j < data.length; j++)
                            data[j] = "";
                        try {
                            JSONObject obj = jsonArr.getJSONObject(i);
                            data[0] = obj.getString("name");
                            data[1] = "Start Date -> " + obj.getString("startdate");
                            data[2] = "End Date -> " + obj.getString("enddate");
                            data[3] = "Description -> " + obj.getString("des");
                        } catch (Exception ex) {
                            Log.d(TAG, ex.getMessage());
                        }
                        list.add(data);
                        name.add(data[0]);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Andro1SubClient.this, ListActivity.class);
                            intent.putExtra("type", 3);
                            intent.putExtra("list", list);
                            intent.putExtra("name", name);
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }).start();
    }

    public String readLocalFile() {
        StringBuilder outs = new StringBuilder("");
        try {
            RandomAccessFile rs = new RandomAccessFile(MainActivity.getMediaFile(), "rw");
            for (String s = ""; s != null; s = rs.readLine()) {
                outs.append(s);
            }
            rs.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return outs.toString();
    }
}
