package com.example.MessageManager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.*;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private List<ContectView> smsList = new ArrayList<ContectView>();
    private HashMap<String, String> addressToPerson = new HashMap<String, String>();
    private ListView listView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        readContacts();
        getSms();
        ContectViewAdapter adapter = new ContectViewAdapter(
                MyActivity.this, R.layout.contect_view, smsList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ContectView contectView = smsList.get(position);
                String personName = contectView.getName();
                ArrayList<String> personNumber = new ArrayList<String>();
                Iterator<String> iterator = addressToPerson.keySet().iterator();
                while(iterator.hasNext()){
                    String number = iterator.next();
                    Log.d("SMS", number);
                    if(addressToPerson.get(number) == personName){
                        personNumber.add(number);
                    }
                }
                Intent intent = new Intent(MyActivity.this, ShowSMS.class);
                intent.putExtra("personName", personName);
                intent.putExtra("personNumber", personNumber);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        refreshList();
    }

    public void refreshList(){
        getSms();
        ContectViewAdapter adapter = new ContectViewAdapter(
                MyActivity.this, R.layout.contect_view, smsList);
        listView.setAdapter(adapter);
    }

    public void getSms(){
        smsList.clear();
        final String SMS_URI_ALL = "content://sms/";

        Uri uri = Uri.parse(SMS_URI_ALL);
        HashMap<String, String> Contects = new HashMap<String, String>();
        String[] projection = new String[] { "_id", "address", "person", "body", "date", "read" };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, "date desc");

        if(cursor.moveToFirst()){
            int indexAddress = cursor.getColumnIndex("address");
            int indexBody = cursor.getColumnIndex("body");
            int indexDate = cursor.getColumnIndex("date");
            int indexRead = cursor.getColumnIndex("read");
            do{
                String strAddress = cursor.getString(indexAddress);
                if(strAddress.charAt(0) == '+'){
                    strAddress = strAddress.substring(3);
                }
                String strPerson = addressToPerson.get(strAddress);
                if(strPerson == null){
                    strPerson = strAddress;
                    addressToPerson.put(strAddress, strAddress);
                }

                if(Contects.get(strPerson) == null) {
                    String strBody = cursor.getString(indexBody);
                    long longDate = cursor.getLong(indexDate);
                    //int intType = cursor.getInt(indexType);

                    Contects.put(strPerson,strBody);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);

                    int read = cursor.getInt(indexRead);

                    smsList.add(new ContectView(strPerson, strDate, strBody, read));
                }
            }while(cursor.moveToNext());
        }

        cursor.close();
    }

    private void readContacts(){
        Cursor cursor = null;
        try{
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while(cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(number.charAt(0) == '+'){
                    number = number.substring(3);
                }
                addressToPerson.put(number, name);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null)
                cursor.close();
        }
    }

    //Receive sms
    class MessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            //When received message, the sms database is already changed, so we refresh it to load the newly database.
            refreshList();
        }
    }
}
