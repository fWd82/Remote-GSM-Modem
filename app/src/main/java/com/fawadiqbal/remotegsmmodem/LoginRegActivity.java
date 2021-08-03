package com.fawadiqbal.remotegsmmodem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginRegActivity extends AppCompatActivity {

    Button btn_enter, btn_set_pin;
    EditText et_enterpin, et_setpin;
    Intent settings_intent, sendsms_intent, main_intent;
    String pin;

    SharedPreferences sh;
    private static final String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginreg);

        settings_intent = new Intent(this, Settings.class);
        sendsms_intent = new Intent(this, SendSMS.class);
        main_intent = new Intent(this, MainActivity.class);

        btn_enter = findViewById(R.id.btn_enter);
        btn_set_pin = findViewById(R.id.btn_set_pin);
        et_enterpin = findViewById(R.id.et_pin);
        et_setpin = findViewById(R.id.et_set_pin);

        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String pin = sh.getString("pin", "");
        // Log.d(TAG, "onCreate: pin value is: " + pin);

        if (pin.equals("")){
            btn_enter.setVisibility(View.INVISIBLE);
            et_enterpin.setVisibility(View.INVISIBLE);
            Log.d(TAG, "onCreate: equal empty");
        }else{
            btn_set_pin.setVisibility(View.INVISIBLE);
            et_setpin.setVisibility(View.INVISIBLE);
            Log.d(TAG, "onCreate: not empty");
        }

    } // eof onCreate();


    // Is internet connected
    public boolean isConnected()
    {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
        // retrun true | false
    }


    // Create PIN for app
    public void btnSetPin(View view) {

        pin = et_setpin.getText().toString();
        Log.d(TAG, "btnSetPin: " + pin);

        if (pin.equals("")){
            Toast.makeText(this, "Please Enter value for PIN", Toast.LENGTH_LONG).show();
            et_setpin.setError("Can't be Empty!");
            et_setpin.setBackgroundResource(R.drawable.et_error);
            return;
        }
        et_setpin.setBackgroundResource(R.drawable.et_normal);

        SharedPreferences.Editor myEdit = sh.edit();
        myEdit.putString("pin", pin);
        myEdit.apply();
        myEdit.commit();
        Log.d(TAG, "btnSetPin: PIN SET: " + pin);

        Toast.makeText(this, "PIN Create Successfully!", Toast.LENGTH_LONG).show();

        btn_enter.setVisibility(View.VISIBLE);
        et_enterpin.setVisibility(View.VISIBLE);
        Log.d(TAG, "onCreate: equal empty");
        btn_set_pin.setVisibility(View.GONE);
        et_setpin.setVisibility(View.GONE);

    }

    // Enter App by providing PIN
    public void btnClickEnter(View view) {

        pin = et_enterpin.getText().toString();
        Log.d(TAG, "btnClickEnter: " + pin);

        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String pin_sh = sh.getString("pin", "");

        // Check if PIN is entered Empty
        if (!pin.equals("")){
            // Check if the PIN enter is what set in SharedPreferences
            if (pin_sh.equals(pin)){
                Log.d(TAG, "btnClickEnter: YES PIN = PIN_SH: " + pin_sh +" : "+ pin);
                et_enterpin.setBackgroundResource(R.drawable.et_normal);
                et_enterpin.setText("");
                startActivity(main_intent);
            }else{
                Log.d(TAG, "btnClickEnter: NO PIN != PIN_SH: " + pin_sh +" : "+ pin);
                et_enterpin.setText("");
                et_enterpin.setError("Wrong PIN");
                et_enterpin.setBackgroundResource(R.drawable.et_error);
                Toast.makeText(this, "Please Enter Correct PIN", Toast.LENGTH_LONG).show();

            }
        }else{
            et_enterpin.setBackgroundResource(R.drawable.et_error);
            et_enterpin.setError("Enter PIN");
            Toast.makeText(this, "Enter PIN", Toast.LENGTH_LONG).show();
        }

    } // eof btnClickEnter();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settings:
                settings_intent = new Intent(this, Settings.class);
                startActivity(settings_intent);
                Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.sendsms:
                sendsms_intent = new Intent(this, SendSMS.class);
                startActivity(sendsms_intent);
                Toast.makeText(getApplicationContext(), "Send SMS Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.home:
                LoginRegActivity.this.finish();
//                main_intent = new Intent(this, MainActivity.class);
//                startActivity(main_intent);
                Toast.makeText(getApplicationContext(), "Home Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.tutorial:
                Toast.makeText(getApplicationContext(), "Tutorial Selected", Toast.LENGTH_LONG).show();
                return true;
            case R.id.about:
                Toast.makeText(getApplicationContext(), "About Selected", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}