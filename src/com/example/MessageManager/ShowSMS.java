package com.example.MessageManager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lkx on 2014/12/22.
 */
public class ShowSMS extends Activity {

    List<String> smsList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_sms);
        Intent intent = getIntent();
        String name = intent.getStringExtra("personName");
        ArrayList<String> number = intent.getStringArrayListExtra("personNumber");
        getSMS(name, number);
        //Log.d("ShowSMS", smsList.get(0));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowSMS.this, android.R.layout.simple_list_item_1, smsList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

    public void getSMS(String name, ArrayList<String> number){
        final String SMS_URI_ALL = "content://sms/";

        Uri uri = Uri.parse(SMS_URI_ALL);
        String[] projection = new String[] { "body", "date", "type", "read", "status" };
        String selection = "address = ?";
        String[] selectionArgs = new String[number.size()];
        selectionArgs[0] = number.get(0);
        Log.d("ShowSMS", selection);
        Log.d("ShowSMS", selectionArgs[0]);
        for(int i = 1; i< number.size(); i++){
            selection += " or address = ?";
            selectionArgs[i] = number.get(i);
        }
        //Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, "date desc");
        Cursor cursor = getContentResolver().query(uri, projection, null, null, "date desc");

        if(cursor.moveToFirst()){
            int indexBody = cursor.getColumnIndex("body");
            int indexDate = cursor.getColumnIndex("date");
            int indexType = cursor.getColumnIndex("type");
            int indexRead = cursor.getColumnIndex("read");
            int indexStatus = cursor.getColumnIndex("status");

            do{
                String strBody = cursor.getString(indexBody);

                long longDate = cursor.getLong(indexDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date d = new Date(longDate);
                String strDate = dateFormat.format(d);

                int type = cursor.getInt(indexType);
                int status = cursor.getInt(indexStatus);
                int read = cursor.getInt(indexRead);

                String newStr = "";
                switch(type){
                    case 1:
                        newStr += (name + ":\n");
                        break;
                    case 2:
                        newStr += "我:\n";
                        break;
                    default:
                        break;
                }
                newStr += (strBody + "\n" + strDate + " ");
                switch(status){
                    case -1:
                        newStr += "Received";
                        break;
                    case 0:
                        newStr += "Complete";
                        break;
                    case 64:
                        newStr += "Pending";
                        break;
                    case 128:
                        newStr += "Failed";
                        break;
                    default:
                        break;
                }
                newStr += " " + read;

                smsList.add(newStr);
                Log.d("SMS", newStr);
            }while(cursor.moveToNext());
        }

        cursor.close();
    }
}