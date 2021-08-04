package com.fawadiqbal.remotegsmmodem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;

public class Settings extends AppCompatActivity {

    EditText etLink, etNoOfMsgs, etNoOfMnts, etSecretKey;
    TextView tvAPIFetchNew, tvAPIUpdate, tvAPISecretKeyEx;
    Button btnSet;
    String link, numOfMsgs, numOfMinutes, secret_key_sp, secret_key;
    int timeGap_msgz, timeGap_mnts;
    private static final String TAG = "TAG";
    View ll_fetch_new, ll_update_api, ll_secret_key_0;

    String link_sp, timegap_sms_sp, timegap_mnts_sp;

    SharedPreferences sharedPreferences;
    Intent mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        etLink = findViewById(R.id.etLink);
        etNoOfMsgs = findViewById(R.id.etNoOfMsgs);
        etNoOfMnts = findViewById(R.id.etNoOfMnts);
        etSecretKey = findViewById(R.id.etSecretKey);
        btnSet = findViewById(R.id.btn_set);
        tvAPIFetchNew = findViewById(R.id.tvAPIFetchNew);
        tvAPIUpdate = findViewById(R.id.tvAPIUpdate);
        tvAPISecretKeyEx = findViewById(R.id.tvAPISecretKeyEx);
        ll_fetch_new = findViewById(R.id.ll_fetch_new);
        ll_update_api = findViewById(R.id.ll_update_api);
        ll_secret_key_0 = findViewById(R.id.ll_secret_key_0);


        // Storing data into SharedPreferences
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);

        link_sp = sharedPreferences.getString("link", "");
        timegap_sms_sp = sharedPreferences.getString("timeGap_msgz", "");
        timegap_mnts_sp = sharedPreferences.getString("timeGap_mnts", "");
        secret_key_sp = sharedPreferences.getString("secret_key", "");

        // Stop app from crashing because of null value
        if(link_sp!= null && !link_sp.equals(""))
        {
            Log.e(TAG, "TimeGap is NOT NULL" + timegap_sms_sp);
            // We can then use the data
            etLink.setText(link_sp);
            etNoOfMsgs.setText(timegap_sms_sp);
            etNoOfMnts.setText(timegap_mnts_sp);
            etSecretKey.setText(secret_key_sp);

            Log.d(TAG, "onClick link: " + link_sp);
            Log.d(TAG, "onClick: timegap_sms_sp: " + timegap_sms_sp);
            Log.d(TAG, "onClick: timegap_mnts_sp: " + timegap_mnts_sp);
            Log.d(TAG, "onClick: secret_key : " + secret_key_sp);


            tvAPIFetchNew.setVisibility(View.VISIBLE);
            tvAPIUpdate.setVisibility(View.VISIBLE);

            ll_fetch_new.setVisibility(View.VISIBLE);
            ll_update_api.setVisibility(View.VISIBLE);
            ll_secret_key_0.setVisibility(View.VISIBLE);

            tvAPIFetchNew.setText(link_sp + "test_api.php?action=fetch_new");
            tvAPIUpdate.setText(link_sp + "test_api.php?action=update&id=1&status=1");
            tvAPISecretKeyEx.setText(link_sp + "test_api.php?action=fetch_new&key=" + etSecretKey.getText().toString());
        }
        else{
            // There is no Link found for API
            Log.e(TAG, "link_sp is null");
            ll_fetch_new.setVisibility(View.GONE);
            ll_update_api.setVisibility(View.GONE);
            ll_secret_key_0.setVisibility(View.GONE);
        }


        // Listen to text Changes for URL Link and update URLS TextViews
        etLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ll_fetch_new.setVisibility(View.VISIBLE);
                ll_update_api.setVisibility(View.VISIBLE);
                ll_secret_key_0.setVisibility(View.VISIBLE);
                ll_secret_key_0.setVisibility(View.VISIBLE);
//
//                String aa;
//                double rande = Math.random();
//                aa = String.valueOf(rande) ;
//                Log.d(TAG, "onCreate: random: " + rande);
//                Log.d(TAG, "onCreate: random string: " + aa);
//                Log.d(TAG, "onCreate: aa.charAt(0) string: " + aa.charAt(0));

                ll_fetch_new.setAlpha(1f);
                ll_fetch_new.setTranslationY(0);
                ll_update_api.setAlpha(1f);
                ll_update_api.setTranslationY(0);
                ll_secret_key_0.setAlpha(1f);
                ll_secret_key_0.setTranslationY(0);

                tvAPIFetchNew.setVisibility(View.VISIBLE);
                tvAPIUpdate.setVisibility(View.VISIBLE);

                Log.d(TAG, "onKey: HEY ANY BUTTON IS CLICKED");

                etLink.setBackgroundResource(R.drawable.et_normal);

                Log.d(TAG, "onKey: " + etLink.getText() + "test_api.php?action=fetch_new");
                tvAPIFetchNew.setText(etLink.getText() + "test_api.php?action=fetch_new");
                tvAPIUpdate.setText(etLink.getText() + "test_api.php?action=update&id=1&status=1");
                tvAPISecretKeyEx.setText(etLink.getText().toString() + "test_api.php?action=fetch_new&key=" + etSecretKey.getText().toString());

                Log.d(TAG, "onKey: ET LINK IS: " + etLink.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {
                etLink.removeTextChangedListener(this);
                if (s.toString().equals("")){
                    Log.d(TAG, "onKey: BACK PRESSED NOW HIDE LAYOUTS");
                    etLink.setBackgroundResource(R.drawable.et_error);
                    etLink.setText("");

                    ll_fetch_new.setAlpha(1f);
                    ll_fetch_new.setTranslationY(0);
                    ll_fetch_new.animate().alpha(0f).translationYBy(50).setDuration(800);

                    ll_update_api.setAlpha(1f);
                    ll_update_api.setTranslationY(0);
                    ll_update_api.animate().alpha(0f).translationYBy(50).setDuration(1000);

                    ll_secret_key_0.setAlpha(1f);
                    ll_secret_key_0.setTranslationY(0);
                    ll_secret_key_0.animate().alpha(0f).translationYBy(50).setDuration(1000);
                }
                etLink.addTextChangedListener(this);
            }
        });

        // Listen to text Changes for Secret Key and update URLS TextViews
        etSecretKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: " + s);
                Log.d(TAG, "beforeTextChanged: start " + start);
                Log.d(TAG, "beforeTextChanged: count " + count);
                Log.d(TAG, "beforeTextChanged: after " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s);
                Log.d(TAG, "onTextChanged start: " + start);
                Log.d(TAG, "onTextChanged before: " + before);
                Log.d(TAG, "onTextChanged count: " + count);

                tvAPISecretKeyEx.setVisibility(View.VISIBLE);
                tvAPISecretKeyEx.setText(etLink.getText().toString() + "test_api.php?action=fetch_new&key=" + etSecretKey.getText().toString());

                ll_secret_key_0.setAlpha(1f);
                ll_secret_key_0.setTranslationY(0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                etSecretKey.removeTextChangedListener(this);
                //
                Log.d(TAG, "afterTextChanged: s " + s);
                Log.d(TAG, "afterTextChanged: s.lenght " + s.length());

                if(s.toString().equals("")){
                    Log.d(TAG, "afterTextChanged: s.lenght toString().equals ");

                    Log.d(TAG, "onKey: BACK LAYOUTS 333");
                    etSecretKey.setBackgroundResource(R.drawable.et_error);
                    etSecretKey.setText("");
                    tvAPISecretKeyEx.setText(etLink.getText().toString() + "test_api.php?action=fetch_new&key=");

                    ll_secret_key_0.setAlpha(1f);
                    ll_secret_key_0.setTranslationY(0);
                    ll_secret_key_0.animate().alpha(0f).translationYBy(50).setDuration(1000);

                }
                etSecretKey.addTextChangedListener(this);
            }
        });
    } // eof onCreate()


    // Button Click to Save Settings
    public void btnSetClick(View view){

        // timeGap = Integer.parseInt(numOfMsgs) / Integer.parseInt(numOfMinutes);
         /*
            Let suppose
            msgs = 60
            minute = 1 minute = 60 seconds
                     60 / 60 = 1 sec gap;           120 / 60 = 2sec gap
         */

        // If text boxes are empty
        if (etLink.getText().toString().trim().equals("")){
           Log.e(TAG, "etLink cannot be null");
           Log.d(TAG, "link log 1: " + link);
            etLink.setError("Can't be Empty!");
            etLink.setBackgroundResource(R.drawable.et_error);
           Toast.makeText(this, "Link cannot be empty", Toast.LENGTH_LONG).show();
           return;
       }else{
            etLink.setBackgroundResource(R.drawable.et_normal);
            link = etLink.getText().toString();
            numOfMsgs = etNoOfMsgs.getText().toString();
            numOfMinutes = etNoOfMnts.getText().toString();
            secret_key = etSecretKey.getText().toString();

            Log.d(TAG, "link log 2: " + link);

            tvAPIFetchNew.setVisibility(View.VISIBLE);
            tvAPIUpdate.setVisibility(View.VISIBLE);

            ll_fetch_new.setVisibility(View.VISIBLE);
            ll_update_api.setVisibility(View.VISIBLE);

            tvAPIFetchNew.setText(etLink + "test_api.php?action=fetch_new");
            tvAPIUpdate.setText(etLink + "test_api.php?action=update&id=1&status=1");

            // Creating an Editor object to edit(write to the file)
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

            // Storing the key and its value as the data fetched from edittext
            myEdit.putString("link", link);
            myEdit.putString("timeGap_msgz", numOfMsgs);
            myEdit.putString("timeGap_mnts", numOfMinutes);
            myEdit.putString("secret_key", secret_key);

            Log.d(TAG, "btnSetClick: numOfMsgs " + numOfMsgs);
            Log.d(TAG, "btnSetClick: numOfMinutes " + numOfMinutes);
            Log.d(TAG, "btnSetClick: secret_key is: " + secret_key);

            myEdit.apply();
            myEdit.commit();

            Log.d(TAG, "btnSetClick: timeGap_msgz " + timeGap_msgz);
            Log.d(TAG, "btnSetClick: timeGap_mnts " + timeGap_mnts);
            Log.d(TAG, "btnSetClick: Data saved to shared preferences ");

            Toast.makeText(this, "Link data saved", Toast.LENGTH_LONG).show();

           // TODO Put link and timeGap to Intent

           // TODO Set button function

        } // eof else

//          finish this activity
//          Settings.this.finish();
        finish();
        mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
    }
}