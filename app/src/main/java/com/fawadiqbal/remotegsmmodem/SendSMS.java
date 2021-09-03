package com.fawadiqbal.remotegsmmodem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SendSMS extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private int mMessageSentParts;
    private int mMessageSentTotalParts;
    private int mMessageSentCount;
    
    String message = "";

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    private Object[] array;
    private static final String TAG = "SendSMS";
    Button btnSend;
    ProgressBar progressBar;
    String number, messagetext, time;
    EditText etNumber, etMessage;
    TextView tvTxtLength, tvTxtLengthTotal;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        btnSend = findViewById(R.id.btn_send_x);
        etNumber = findViewById(R.id.etNumber);
        etMessage = findViewById(R.id.etMessage);
        tvTxtLength = findViewById(R.id.tvTxtLength);
        tvTxtLengthTotal = findViewById(R.id.tvTxtLengthTotal);
        progressBar = findViewById(R.id.progressBar);


        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS))
                != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,"Manifest.permission.READ_SMS") ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,"Manifest.permission.READ_SMS")) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{"Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS"},
                        REQUEST_CODE);

                // REQUEST_CODE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                //sendSMS("+923001234567", "Hi msg16");
                //sendSMS("+923001234568", "Hi msg17");
            }
        }

        else {
            // Permission has already been granted
            //sendSMS("+923001234567", "Hi m14");
            // sendSMS("+923001234568", "Hi m15");
        }



        if (ContextCompat.checkSelfPermission(SendSMS.this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Ask for permision
            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.SEND_SMS}, 1);
            Toast.makeText(SendSMS.this, "SMS Granted", Toast.LENGTH_LONG).show();
            //
        }

        // Send Message Button
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time = dateFormat.format(new Date());

                number = etNumber.getText().toString();
                messagetext = etMessage.getText().toString();

                // Toast.makeText(SendSMS.this, time + " Send Button Clicked.." + number + " : " + messagetext, Toast.LENGTH_LONG).show();

                // Check For Number TextEdit if it empty
                if (number.equals("")){
                    Toast.makeText(getApplicationContext(), "Please Enter mobile number", Toast.LENGTH_LONG).show();
                    etNumber.setError("Can't be Empty!");
                    etNumber.setBackgroundResource(R.drawable.et_error);
                    return;
                }else{
                    etNumber.setBackgroundResource(R.drawable.et_normal);
                }

                // Check For Message TextEdit if it is empty
                if (messagetext.equals("")){
                    Toast.makeText(getApplicationContext(), "Please Enter your message", Toast.LENGTH_LONG).show();
                    etMessage.setError("Can't be Empty!");
                    etMessage.setBackgroundResource(R.drawable.et_error);
                    return;
                }else{
                    etMessage.setBackgroundResource(R.drawable.et_normal);
                }

                // If both Edit Texts are having values, Yes, send Demo message
                if (!number.equals("") && !messagetext.equals("")){
                    if (isConnected()) {
                        sendSMS(number, messagetext + "\n -- at " + time);
                    }else{
                        Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
                    } // eof if connected
                } // eof if equals ""

            } // eof onClick
        }); // eof setOnClickListener

        // Message Characters Count
        etMessage.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.d(TAG, "beforeTextChanged: s: " + s.length());
//                Log.d(TAG, "beforeTextChanged: start: " + start);
//                Log.d(TAG, "beforeTextChanged: count: " + count);
//                tvTxtLength.setText("beforeTextChanged ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Log.d(TAG, "onTextChanged: s: " + s.length());
//                Log.d(TAG, "onTextChanged: start: " + start);
//                Log.d(TAG, "onTextChanged: before: " + before);
//                Log.d(TAG, "onTextChanged: count: " + count);
//                tvTxtLength.setText("onTextChanged count: " + count);

                progressBar.setProgress(160 - s.length());

                if (s.length() > 159){
                    tvTxtLength.setTextColor(Color.RED);
                    tvTxtLengthTotal.setTextColor(Color.RED);
                }else{
                    tvTxtLength.setTextColor(Color.DKGRAY);
                    tvTxtLengthTotal.setTextColor(Color.DKGRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Log.d(TAG, "afterTextChanged: s:" + s.length());
                tvTxtLength.setText("" + s.length());
            }
        });

    } // eof OnCreate

    // Is internet connected
    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
        // retrun true | false
    } // eof isConnected()

    private void startSendMessages(){
        registerBroadCastReceivers();
        mMessageSentCount = 0;
        sendSMS(array[mMessageSentCount].toString(), message);
    } // eof startSendMessages()

    private void sendNextMessage(){
        if(thereAreSmsToSend()){
            sendSMS(array[mMessageSentCount].toString(), message);
        }else{
            Toast.makeText(getBaseContext(), "All SMS have been sent",
                    Toast.LENGTH_SHORT).show();
        }
    } // eof sendNextMessage()

    private boolean thereAreSmsToSend(){
        return mMessageSentCount < array.length;
    }

    public void sendSMS(final String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        mMessageSentTotalParts = parts.size();

        Log.i("Message Count", "Message Count: " + mMessageSentTotalParts);

        Toast.makeText(this, "Message Count: " + mMessageSentTotalParts, Toast.LENGTH_SHORT).show();

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);

        for (int j = 0; j < mMessageSentTotalParts; j++) {
            sentIntents.add(sentPI);
            deliveryIntents.add(deliveredPI);
        }

        mMessageSentParts = 0;
        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
    } // eof sendSMS()

    private void registerBroadCastReceivers(){

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:

                        mMessageSentParts++;
                        if ( mMessageSentParts == mMessageSentTotalParts ) {
                            mMessageSentCount++;
                            sendNextMessage();
                        }

                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {

                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

    } // eof registerBroadCastReceivers()



} // eof SendSMS2