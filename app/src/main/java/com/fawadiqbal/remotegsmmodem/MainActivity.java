package com.fawadiqbal.remotegsmmodem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fawadiqbal.remotegsmmodem.JSONResponse;
import com.fawadiqbal.remotegsmmodem.LoginRegActivity;
import com.fawadiqbal.remotegsmmodem.Movie;
import com.fawadiqbal.remotegsmmodem.MovieApi;
import com.fawadiqbal.remotegsmmodem.R;
import com.fawadiqbal.remotegsmmodem.SendSMS;
import com.fawadiqbal.remotegsmmodem.Settings;
import com.fawadiqbal.remotegsmmodem.Tutorial;
import com.fawadiqbal.remotegsmmodem.update.Results;
import com.fawadiqbal.remotegsmmodem.update.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import java.lang.reflect.Type;
//import java.util.Arrays;
//import java.util.Collection;
//import com.google.gson.reflect.TypeToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity {
    TextView tvRetroResponse_status;
    TextView tvMessagesFound, tvMessagesFoundtxt;
    TextView tvMsg_srno, tvMsg_name, tvMsg_mobile, tvMsg_msg, tvMsg_status;

    List<Movie> movieList;
    ImageButton ibButtonStart;

    View card_view_settings, card_view_messages, card_view_on_off, card_view_statistics, card_view_messages_2;

    Handler handler = new Handler();

    StringBuilder allStr;
    int strMsg_srno;
    String strMsg_name;
    String strMsg_mobile;
    String strMsg_msg;
    String strMsg_status;


    Intent settings_intent, sendsms_intent, loginreg_intent, tutorial_intent;

    com.fawadiqbal.remotegsmmodem.MainActivity.MainThread mainThread;

    ListView superListView;
    private static final String TAG = "TAG";
    private volatile boolean exit = false;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    String time;
    boolean is_thread_running = false;

    // for sending sms
    private static final int REQUEST_CODE = 1;
    private int mMessageSentParts;
    private int mMessageSentTotalParts;
    private int mMessageSentCount;

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    private Object[] array;

    String message = "", link_sp, update_link, timegap_sms_sp, timegap_mnts_sp, app_pin;

    int numberOfLoops = 0;
    // Boolean if_busy = false;
    int mobileSmscWaitTime  = 10;    // 10 seconds
    int threadWaitTime      = 10 * 1000; // 10 seconds = 10,000 milliseconds
    SharedPreferences sp;

    public static String linkUrl = "";

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //superListView           = findViewById(R.id.superListView);

        tvMessagesFound         = findViewById(R.id.tvMessagesFound);
        tvMessagesFoundtxt      = findViewById(R.id.tvMessagesFoundtxt);

        card_view_settings      = findViewById(R.id.card_view_settings);
        card_view_messages      = findViewById(R.id.card_view_messages);
        card_view_on_off      = findViewById(R.id.card_view_on_off);
        card_view_statistics      = findViewById(R.id.card_view_statistics);
        card_view_messages_2      = findViewById(R.id.card_view_messages_2);

        tvRetroResponse_status  = findViewById(R.id.tvRetroResponse_status);
        ibButtonStart           = findViewById(R.id.ibStartThread);


        tvMsg_srno        = findViewById(R.id.tvMsg_srno);
        tvMsg_name        = findViewById(R.id.tvMsg_name);
        tvMsg_mobile      = findViewById(R.id.tvMsg_mobile);
        tvMsg_msg         = findViewById(R.id.tvMsg_msg);
        tvMsg_status      = findViewById(R.id.tvMsg_status);



        movieList = new ArrayList<>();
        allStr    = new StringBuilder();
        handler   = new Handler();
        time      = dateFormat.format(new Date());

        // Shared Preferences
        sp       = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        link_sp  = sp.getString("link", "");
        // update_link = link_sp + "gsm_api.php?action=update";
        update_link = link_sp + "test_api.php?action=update";
        timegap_sms_sp  = sp.getString("timeGap_msgz", "");
        timegap_mnts_sp = sp.getString("timeGap_mnts", "");
        app_pin         = sp.getString("pin", "");
        Log.d(TAG, "update_link: " + update_link);

        // Intents
        settings_intent = new Intent(this, Settings.class);
        loginreg_intent = new Intent(this, LoginRegActivity.class);
        sendsms_intent  = new Intent(this, SendSMS.class);
        tutorial_intent = new Intent(this, Tutorial.class);
        //intent = new Intent(this, MainActivity.class);

        // updateSendSmsValue(1, 1);

        // If no API Link found
        if (link_sp == null || link_sp.equals("")) {
            Log.e(TAG, "API Link is null");
            tvMessagesFound.setText("API Link not set");
            tvMessagesFoundtxt.setText("Please go to Settings and enter API Link");
            com.fawadiqbal.remotegsmmodem.MainActivity.this.finish();
            startActivity(settings_intent);
        }
        Log.e(TAG, "Link is: " + link_sp);

        // If the app has no pin
        if(app_pin== null || app_pin.equals("")){
            com.fawadiqbal.remotegsmmodem.MainActivity.this.finish();
            startActivity(loginreg_intent);
        }

        // If not connected to WiFi or Data
        if (isConnected()) {
            Log.d(TAG, "onCreate: Connected to internet - WiFi Opened");

            Log.d(TAG, "onCreate: link_sp: " +  link_sp);
            Log.d(TAG, "onCreate: update_link: " + update_link);
            // Yes, connected, do animations
            // Ref: https://androiddvlpr.com/android-button-animation/
            ibButtonStart.setAlpha(0f);
            ibButtonStart.setTranslationY(50);
            ibButtonStart.animate().alpha(1f).translationYBy(-50).setDuration(1500);

            Log.d(TAG, "Method Loaded: Checking for flag value: " + is_thread_running);

            if (is_thread_running) {
                ibButtonStart.setBackgroundResource(R.drawable.icons8_on);

            } else {
                is_thread_running = true;
                ibButtonStart.setBackgroundResource(R.drawable.icons8_off);
            }
        } else {
            Log.e(TAG, "No internet connection: ");
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
        } // eof isConnected

    } // eof onCreate();

    // Is internet connected
    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
        // retrun true | false
    }

// ***************************************************************** //

    public void start_thread(){
        Log.d(TAG, "start_thread: Now");
        Log.d(TAG, "send_new_message: TIME: " + time);

        link_sp = sp.getString("link", "");
        if(link_sp== null || link_sp.equals(""))
        {
            Log.e(TAG, "ALI Link is null");
            tvMessagesFound.setText("API Link not set");
            tvMessagesFoundtxt.setText("Please go to Settings and enter API Link");
            startActivity(settings_intent);
        }else{
            mainThread = new MainActivity.MainThread(mobileSmscWaitTime);
            mainThread.start();
        }

    } // eof start_thread()

    public void stop_thread(){
        Log.d(TAG, "stop_thread: NOW: " + Thread.currentThread());
        exit = true;

        if (mainThread.isInterrupted()){
            Log.d(TAG, "Oh its isInterrupted: NOW");
            exit = true;
        }
        if (Thread.currentThread().isInterrupted()){
            Log.d(TAG, "Oh its isInterrupted: NOW 2");
            exit = true;
        }
    } // eof stop_thread()

    // ***************************************************************** //

    public void fetch_data() throws InterruptedException {
        Log.d(TAG, "Inside fetch_data() Method ");
        Log.d(TAG, "TIME: " + time);

        numberOfLoops++;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMessagesFoundtxt.setText("No of Loops: " + numberOfLoops);
                Log.d(TAG, "run: Fetching now. Loops: " + numberOfLoops);
            }
        });

        Log.d(TAG, "numberOfLoops 1X: " + numberOfLoops);

        // Calling Online API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(link_sp)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieApi movieApi = retrofit.create(MovieApi.class);
        Call<JSONResponse> call = movieApi.getMovies();
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {

                Log.d(TAG, "onCreate: link_sp xx1: " +  link_sp);
                Log.d(TAG, "onCreate: update_link xx2: " + update_link);

                if (response.code()!=200){
                    Log.d(TAG, "Not working, Status Code: " + response.code());
                    Log.d(TAG, "onResponse: jsonResponse 3: " + response);
                }else{
                    Log.d(TAG, "Status 200 OK");
                }

                JSONResponse jsonResponse = response.body();

                Log.d(TAG, "onResponse: jsonResponse 1: " + jsonResponse);
                Log.d(TAG, "onResponse: jsonResponse 2: " + response);

                if (jsonResponse == null){
                    return;
                }

                // assert jsonResponse != null;
                // movieList = new ArrayList<>(Arrays.asList(jsonResponse.getMoviesArray()));

                movieList = new ArrayList<>(jsonResponse.getMoviesArray());

                System.out.println("_______ Advanced For Loop _______ ");
                for (Movie num : movieList) {
                    Log.d(TAG, "ID: " + num.getId()+"");
                    Log.d(TAG, "Name: " + num.getName());
                    Log.d(TAG, "Message: " + num.getMessage());
                    Log.d(TAG, "Mobile: " + num.getMobile());
                    Log.d(TAG, "Status: " + num.getStatus());
                    Log.d(TAG, "_____________");
                    Log.d(TAG, "onResponse: sent NOW IN SLEEP MODE AFTER SENDING SMS");

//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), "YES now print list: " + num.getName(), Toast.LENGTH_LONG).show();
//                            Log.d(TAG, "YES now print list " + num.getName());
//                            superListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, Collections.singletonList(num.getName())));
//                        }
//                    });

                    allStr
                            .append(num.getName()).append(" : \t ")
                            .append(num.getMessage()).append(" : \t")
                            .append(" Status: ").append(num.getStatus()).append(" \n");

                    strMsg_srno = num.getId();
                    strMsg_name = num.getName();
                    strMsg_mobile = num.getMobile();
                    strMsg_msg = num.getMessage();
                    strMsg_status = num.getStatus();

                    // If  found new message , we will send right away.
                    if (num.getStatus().equals("0")){

                        Log.e(TAG, "Found new messages whoes value is ZERO");
                        Log.d(TAG, "Found new message for ID: " + num.getStatus() + " : Name : " + num.getName() + "\n");
                        // if_busy = true;
                        // Log.e(TAG, "Found new messages flag changed to true");

                        // TODO Check again for permission if granted
                        // TODO Send unsent messages
                        sendSMS(num.getMobile(), num.getMessage());
                        // TODO See if message is sent or not (return type) - then update SMS Status
                        updateSendSmsValue(num.getId(), 1); // int id, int status

                        try {
                            Log.d(TAG, "onResponse: NOW IN SLEEP MODE AFTER SENDING SMS");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } // eof try{}

                    }else{
                        Log.e(TAG, "ELSE statement calling No New Messages");
                    } // if (value == 0)
                }

                Log.d(TAG, "Now outside foreach loop");
                // new ArrayList<>(jsonResponse.getMoviesArray()
                // ArrayAdapter<Movie> myAdapter = new ArrayAdapter<Movie>(this, android.R.layout.simple_expandable_list_item_1, movieList);
                // superListView.setAdapter(myAdapter);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        ArrayAdapter<Movie> myAdapter = new ArrayAdapter<Movie>(this, android.R.layout.simple_expandable_list_item_1, movieList);
//                        superListView.setAdapter(myAdapter);
//                        superListView.setAdapter(new ArrayAdapter<Movie>(getApplicationContext(), android.R.layout.simple_list_item_1, jsonResponse.getMoviesArray()));

                        tvMessagesFoundtxt.setText("Fetching Finished..Loops: " + numberOfLoops);
                        // tvRetroResponse.setText("Fetched: \n" + allStr);

                        tvMsg_srno.setText(""+strMsg_srno);
                        tvMsg_name.setText(strMsg_name);
                        tvMsg_mobile.setText(strMsg_mobile);
                        tvMsg_msg.setText(strMsg_msg);
                        tvMsg_status.setText(strMsg_msg);

                    }
                });

            } // eof onResponse

            // onFailure
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable throwable) {
                // Error Messages:

                tvMessagesFoundtxt.setText(throwable.toString());
                Log.e(TAG, throwable.toString());
                Toast.makeText(com.fawadiqbal.remotegsmmodem.MainActivity.this,throwable.toString(),Toast.LENGTH_SHORT).show();
            }
        });

    } // eof FetchFromServer();

    // Update SMS Status after successfully sending message
    private void updateSendSmsValue(int id, int status) {

//        Call<List<Results>> call = RetrofitClient.getInstance().getMyApi().getsuperHeroes(id, status);
        Call<List<Results>> call = RetrofitClient.getInstance().getMyApi().getsuperHeroes(update_link, id, status);
        call.enqueue(new Callback<List<Results>>() {

            // On Success
            @Override
            public void onResponse(Call<List<Results>> call, Response<List<Results>> response) {
                List<Results> myheroList = response.body();
                String[] oneHeroes = new String[myheroList.size()];

                for (int i = 0; i < myheroList.size(); i++) {
                    oneHeroes[i] = myheroList.get(i).getName();
                    Log.d(TAG, oneHeroes[i]);
                }

                Log.d(TAG, "YES Updated Status Value: " + oneHeroes[0]);
                Toast.makeText(getApplicationContext(), "YES Updated: " + oneHeroes[0], Toast.LENGTH_LONG).show();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "YES: " + oneHeroes[0], Toast.LENGTH_LONG).show();
                        Log.d(TAG, oneHeroes[0]);
                        // superListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, oneHeroes));
                    }
                });

            }
            // On Failure
            @Override
            public void onFailure(Call<List<Results>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: "+t, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void btnClickSettings(View view) {
        startActivity(settings_intent);
    }

    public void btnClickMessages(View view) {
        startActivity(sendsms_intent);
    }


    // Thread
    public class MainThread extends Thread{
        int seconds;
        // Constructor
        public MainThread(int seconds){
            this.seconds = seconds;
        }

        @Override
        public void run() {
            int i = 0;
            Log.d(TAG, "TIME: " + time);

            while (!exit){
                Log.d(TAG, "start_thread: jkl" + i);
                try {
                    Log.d(TAG, "run: Thread.sleep now called -----");
                    Log.d(TAG, "TIME: " + time);

                    // FETCH DATA EVERYTIME
                    fetch_data();

                    Thread.sleep(threadWaitTime);
                    Log.d(TAG, "TIME: " + time);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                if (i == 5){
//                    mainHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            tvRetroResponse.setText("50% NOW");
//                        }
//                    });
//                }
//                if (i == 7){
//                    mainHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            tvRetroResponse.setText("70% Now | Stopping");
//                            exampleThread.interrupt();
//                            stop_thread();
//
//                            if(Thread.currentThread().isInterrupted()){
//                                Log.d(TAG, "run: Ah now interrupter");
//                            }
//                        }
//
//                    });
//                }
                i++;
            }

        } // eof run()
        @Override
        public void interrupt() {
            super.interrupt();
        }
    } // eof MainThread

    // --------------- FOR SENDING MESSAGES --------------------

    private void startSendMessages(){
        registerBroadCastReceivers();
        mMessageSentCount = 0;
        sendSMS(array[mMessageSentCount].toString(), message);
    } // eof startSendMessages()

    private void sendNextMessage(){
        if(thereAreSmsToSend()){
            sendSMS(array[mMessageSentCount].toString(), message);
        }else{
            Log.d(TAG, "sendNextMessage: All Pending SMS have been sent");
            // Toast.makeText(getBaseContext(), "All SMS have been sent",Toast.LENGTH_SHORT).show();
        }
    } // eof sendNextMessage()

    private boolean thereAreSmsToSend(){
        return mMessageSentCount < array.length;
    }

    public void sendSMS(final String phoneNumber, String message) {
        Log.d(TAG, "sendSMS: inside sendSMS method.");

        boolean sent = false;
        String SENT      = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        mMessageSentTotalParts = parts.size();

        Log.i(TAG, "Message Count: " + mMessageSentTotalParts);

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

        PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);

        for (int j = 0; j < mMessageSentTotalParts; j++) {
            sentIntents.add(sentPI);
            deliveryIntents.add(deliveredPI);
        }
        Log.d(TAG, "sendSMS: inside sendSMS now here .");
        mMessageSentParts = 0;
        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);

        Log.d(TAG, "sendSMS: SMS SUCCESSFULLY SENT NOW 2");
//        if () == true){
//
//        }



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

    // ######################################################

    public void btnClickStartThread(View view) {
        Log.d(TAG, "btnClickStartThread: Button clicked 111: " + is_thread_running);

        if (isConnected()){
            if (is_thread_running){
                start_thread();
                ibButtonStart.animate().rotation(360).setDuration(1000);
                ibButtonStart.setBackgroundResource(R.drawable.icons8_on);
                is_thread_running = false;


                // Cards Animations Slide | Hide -- START
                card_view_settings.setAlpha(1f);
                card_view_settings.setTranslationX(500);
                card_view_settings.animate().alpha(0f).translationXBy(500).setDuration(1500);

                card_view_messages.setAlpha(1f);
                card_view_messages.setTranslationX(-500);
                card_view_messages.animate().alpha(0f).translationXBy(-500).setDuration(1500);

                // On Off Button Card
                card_view_on_off.setAlpha(1f);
                card_view_on_off.setTranslationY(0);
                card_view_on_off.animate().alpha(1f).translationYBy(-340).setDuration(1500);

                // Statistics Card
                card_view_statistics.setAlpha(0f);
                card_view_statistics.setTranslationY(0);
                card_view_statistics.animate().alpha(1f).translationYBy(-340).setDuration(1500);

                // Messages Card
                card_view_messages_2.setAlpha(0f);
                card_view_messages_2.setTranslationY(0);
                card_view_messages_2.animate().alpha(1f).translationYBy(-340).setDuration(1500);

//
//                card_view_on_off.animate().withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
////                      card_view_settings.setVisibility(View.GONE);
////                      card_view_messages.setVisibility(View.GONE);
//                        Toast.makeText(getApplicationContext(), "Animation Finished ", Toast.LENGTH_SHORT).show();
//
//                    }
//                });

                // eof Cards Animations Slide | Hide

            }else{
                stop_thread();
                ibButtonStart.animate().rotation(0).setDuration(500);
                ibButtonStart.setBackgroundResource(R.drawable.icons8_off);
                is_thread_running = true;


                // Cards Animations Slide | Hide -- STOP
                card_view_settings.setAlpha(0f);
                card_view_settings.setTranslationX(0);
                card_view_settings.animate().alpha(1f).translationXBy(450).setDuration(1500);

                card_view_messages.setAlpha(0f);
                card_view_messages.setTranslationX(0);
                card_view_messages.animate().alpha(1f).translationXBy(-450).setDuration(1500);

                //
                // On Off Button Card
                card_view_on_off.setAlpha(1f);
                // card_view_on_off.setTranslationY(35);
                card_view_on_off.animate().alpha(1f).translationYBy(340).setDuration(1500);

                // Statistics Card
                card_view_statistics.setAlpha(0.5f);
                //card_view_statistics.setTranslationY(0);
                card_view_statistics.animate().alpha(1f).translationYBy(340).setDuration(1500);

                // Messages Card
                card_view_messages_2.setAlpha(0.5f);
                //card_view_messages_2.setTranslationY(0);
                card_view_messages_2.animate().alpha(1f).translationYBy(340).setDuration(1500);


//                card_view_on_off.animate().withEndAction(new Runnable() {
//                    @Override
//                    public void run() {
////                      card_view_settings.setVisibility(View.GONE);
////                      card_view_messages.setVisibility(View.GONE);
//                        Toast.makeText(getApplicationContext(), "Animation Finished ", Toast.LENGTH_SHORT).show();
//
//                    }
//                });

                // eof Cards Animations Slide | Hide


            }
        } else {
            Toast.makeText(getApplicationContext(), "No internet Connection!", Toast.LENGTH_SHORT).show();
        }

    } // eof btnClickStartThread

    // For Menu Options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                startActivity(settings_intent);
                // Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.sendsms:
                startActivity(sendsms_intent);
                // Toast.makeText(getApplicationContext(), "Send SMS Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.home:
                // Toast.makeText(getApplicationContext(), "Home Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.login_register:
                startActivity(loginreg_intent);
                //Toast.makeText(getApplicationContext(), "GSM Modem Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.tutorial:
                startActivity(tutorial_intent);
                // Toast.makeText(getApplicationContext(), "Tutorial Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about:
                Toast.makeText(getApplicationContext(), "No page associated!", Toast.LENGTH_SHORT).show();
                // startActivity(about_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        Toast.makeText(getApplicationContext(), "Back Pressed", Toast.LENGTH_SHORT).show();
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else { Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show(); }
        mBackPressed = System.currentTimeMillis();
    }
} // eof MainActivity{}