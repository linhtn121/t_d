package com.great.tudien;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class  MainActivity extends AppCompatActivity {


    Data mSQLiteOpenHelper;

    EditText input;
    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        boolean iscopy = sharedPref.getBoolean("copy",false);
        if(!iscopy){

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("copy",true);
            editor.commit();
        }

        initView();
        mSQLiteOpenHelper = new Data(MainActivity.this, output);


        mSQLiteOpenHelper.initDB2(null);



        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);


    }



    private void initView(){
        input = (EditText) findViewById(R.id.editText);
        output = (TextView) findViewById(R.id.textView);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            int count = 0;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*String ok = mSQLiteOpenHelper.getMean(s.toString()).toString().replace("[","").replace("]","");

                if(ok.length() > 0 && ok.charAt(0) == '@' && count == 0){
                    count ++;
                    ok = mSQLiteOpenHelper.getMean(ok).toString().replace("[","").replace("]","");
                }

                if(ok.length() > 1)
                    ok = String.valueOf(ok.charAt(0)).toUpperCase() + ok.subSequence(1, ok.length());

                count = 0;*/
               // if(Data.isReady)
                    output.setText(mSQLiteOpenHelper.getMean(s.toString()));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

       // String ok = mSQLiteOpenHelper.getMean("kiss").toString();
        //Toast.makeText(this, ok, Toast.LENGTH_LONG).show();
    }

    private void initAds(){

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }




}
