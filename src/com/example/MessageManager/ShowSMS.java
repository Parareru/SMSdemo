package com.example.MessageManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Lkx on 2014/12/22.
 */
public class ShowSMS extends Activity {

    private List<String> smsList = new ArrayList<String>();
    private ArrayList<String> number = new ArrayList<String>();
    private Button send = null;
    private EditText msg = null;

    private IntentFilter sendFilter;
    private SendStatusReceiver sendStatusReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_sms);
        Intent intent = getIntent();
        String name = intent.getStringExtra("personName");
        //ArrayList<String> number = intent.getStringArrayListExtra("personNumber");
        readNumbers(name);
        getSMS(name, number);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowSMS.this, android.R.layout.simple_list_item_1, smsList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        //Register the filter and broad cast to check sms sending status
        sendFilter = new IntentFilter();
        sendFilter.addAction("SENT_SMS_ACTION");
        sendStatusReceiver = new SendStatusReceiver();
        registerReceiver(sendStatusReceiver, sendFilter);

        final String[] numbers = new String[number.size()];
        for(int i = 0; i < number.size(); i++){
            numbers[i] = number.get(i);
        }
        //Listed method will cause a crash. I can't understand why.
        //final String[] numbers = (String[]) number.toArray();

        send = (Button) findViewById(R.id.send);
        msg = (EditText) findViewById(R.id.msg);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ShowSMS.this);
                dialog.setTitle("Select the number send to");
                //dialog.setMessage("ss");
                dialog.setCancelable(true);
                dialog.setItems(numbers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SmsManager smsManager = SmsManager.getDefault();
                        Intent sentIntent = new Intent("SENT_SMS_ACTION");
                        PendingIntent pi = PendingIntent.getBroadcast(ShowSMS.this, 0, sentIntent, 0);
                        smsManager.sendTextMessage(numbers[which], null, msg.getText().toString(), pi, null);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(sendStatusReceiver);
    }

    public void getSMS(String name, ArrayList<String> number){
        final String SMS_URI_ALL = "content://sms/";

        Uri uri = Uri.parse(SMS_URI_ALL);
        String[] projection = new String[] { "body", "date", "type", "read", "status" };
        String selection = "address = ?";
        String[] selectionArgs = new String[number.size()];
        selectionArgs[0] = number.get(0);

        for(int i = 1; i< number.size(); i++){
            selection += " or address = ?";
            selectionArgs[i] = number.get(i);
        }
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, "date desc");
        //Cursor cursor = getContentResolver().query(uri, projection, null, null, "date desc");

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

    private void readNumbers(String personName){
        Cursor cursor = null;
        try{
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?", new String[]{ personName }, null);
            while(cursor.moveToNext()){
                //String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String currentNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                number.add(currentNumber);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null)
                cursor.close();
        }
    }

    class SendStatusReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            if(getResultCode() == RESULT_OK){
                Toast.makeText(context, "Send succeeded", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(context, "Send failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}