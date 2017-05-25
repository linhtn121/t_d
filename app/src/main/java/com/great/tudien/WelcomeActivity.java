package com.great.tudien;



import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {


    Data mSQLiteOpenHelper;

    EditText input;
    TextView output;
    TextView historyTxt;

    ArrayList history = new ArrayList<String>();
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        mSQLiteOpenHelper = Data.data;

        sharedPref = getPreferences(Context.MODE_PRIVATE);


        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);


        TextToSpeech tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.i("TextToSpeech", "On init tts: " + status);
            }
        });
        tts.setLanguage(Locale.US);
        tts.speak("Text to say aloud", TextToSpeech.QUEUE_ADD, null);

    }

    String oldTxt = "";

    private void initView(){
        input = (EditText) findViewById(R.id.editText);
        output = (TextView) findViewById(R.id.textView);
        historyTxt = (TextView) findViewById(R.id.textHistory);
        Display display = getWindowManager().getDefaultDisplay();
        final int width = display.getWidth();



        historyTxt.clearAnimation();
        TranslateAnimation animate = new TranslateAnimation(width+20,- width - 20, 0, 0);
        animate.setDuration(9000);
        animate.setAnimationListener(new Animation.AnimationListener() {
            int index = 0;
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {



            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if(history.size() > 0){
                    if(index > history.size() - 1)
                        index = 0;
                    historyTxt.setText(history.get(index).toString());
                    index ++;
                }
            }
        });
        animate.setRepeatCount(1000);
        animate.setRepeatMode(Animation.INFINITE);
        historyTxt.startAnimation(animate);



        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            int count = 0;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String[] ok = mSQLiteOpenHelper.getMean(s.toString());
                    //checking @
                    if(ok !=null && ok[1].indexOf('@')>=0){
                        ok[1] = ok[1].replace("@","");
                        ok = mSQLiteOpenHelper.getMean(ok[1]);
                    }

                    if(ok == null)
                        return;

                    if(ok[0].length()> 0)
                        output.setText(ok[0] + "\n" + ok[1]);
                    else
                        output.setText(ok[1]);

                    //store history
                    if(ok[1].length() > 0 && s.toString().length() >3){
                        String data = s.toString() + ": " + ok[1];
                        history.add(data);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if(history.size() > 0)
            return;
        String txtHis = sharedPref.getString("history","");
        if(txtHis.indexOf("__")>= 0 ){
            String[] ok = txtHis.split("__");
            for(int i = 0; i <ok.length; i++){
                String tmp = ok[i];
                if(tmp != null && tmp.length() > 0)
                    history.add(tmp);
            }

        }



    }

    @Override
    protected void onPause() {
        super.onPause();


        SharedPreferences.Editor editor = sharedPref.edit();
        String tmp = "";
        int j = 0;
        for(int i = history.size() -1 ; i >=0 ; i--){
            tmp = tmp + "__" + history.get(i).toString();
            j++;
            if(j> 9)
                break;
        }

        editor.putString("history",tmp);
        editor.commit();
    }



    @Override
    protected void onResume() {
        super.onResume();

       // String ok = mSQLiteOpenHelper.getMean("kiss").toString();
        //Toast.makeText(this, ok, Toast.LENGTH_LONG).show();
    }

    private void initAds(){

  /*      AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
*/
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
