package com.great.tudien;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linhtn9 on 5/5/2017.
 */

public class Data  {

    public static Data data = null;

    private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
    //destination path (location) of our database on device
    private static String DB_PATH = "";
    private static String SQL_NAME = "words.sql";

    private static String DB_NAME ="av.db";// Database name


    static final String MEAN_COLUMN_NAME = "mean";

    Context mContext;
    TextView mOutput;
    public Data( Context context){
        mContext = context;
        initDB2(null);
    }

    String outFileName;
    static boolean isReady = false;


    static Map<String, String> mapDic = new HashMap<String, String>();
    static Map<String, String> mapIndex = new HashMap<String, String>();
    public void initDB2(SQLiteDatabase db){

        String queries = "";
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        String line;

        Log.e(TAG, "STARTING IMPORT");



        //SQLiteDatabase db =  SQLiteDatabase.openDatabase(outFileName, null,SQLiteDatabase.OPEN_READWRITE);
        //SQLiteDatabase db = getWritableDatabase();
        try {

            InputStream inputStream = mContext.getAssets().open(SQL_NAME);

            br = new BufferedReader(new InputStreamReader(inputStream));
            Long tsLong = System.currentTimeMillis();
            int index = 0;
            while ((line = br.readLine()) != null) {
                String key = line.substring(0, line.indexOf('*'));
                mapDic.put(key, index + "_" + line);
                mapIndex.put(index + "", key);
                index++;
            }

            Long tsLong2 = System.currentTimeMillis();
            Log.i("TIME", (tsLong2 - tsLong) + "") ;


        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        Log.e(TAG, "FINISH IMPORT");

        isReady = true;

    /*    if(findword != null && mOutput != null){
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mOutput.setText(getMean(findword).toString());
                    Toast.makeText(mContext,"The app is ready!", Toast.LENGTH_LONG).show();
                }
            });

        }*/
        //db.close();

    }



    String findword = null;


    private String getSubget(String key, String line){

        if(line.indexOf('_') < 0 )
            return "";

        String indexMap = line.substring(0, line.indexOf('_'));
        int index = Integer.parseInt(indexMap);

        String key1 = mapIndex.get((index + 1)+"");
        String key2 = mapIndex.get((index + 2)+"");
        String key3 = mapIndex.get((index + 3)+"");

        String returnString = "";
        if(key1.contains(key))
            returnString= "-> " + returnString + key1;

        if(key2.contains(key)){
            if(returnString.length() > 0)
                returnString = returnString + ", ";
            returnString= returnString + key2;
        }


        if(key3.contains(key)){
            if(returnString.length() > 0)
                returnString = returnString + ", ";
            returnString= returnString + key3;
        }

        if(returnString.length() > 0)
            returnString = returnString + "?";

        return returnString;
    }
    public String[] getMean(String word) {

        if(!isReady) {
            findword = word;
            return null;
        }
        word = word.toLowerCase().trim();


        String line = mapDic.get(word);

        if(line ==null)
            line ="";

        String subgetion = getSubget(word, line);
        if( line.indexOf('_')> 0)
            line = line.substring(line.indexOf('_') + 1, line.length());

        int index = line.indexOf('*');
        //String word = line.substring(0, index);
        String mean = "";
        if(index > 0)
            mean = line.substring(index + 1, line.length());

        String[] rt = {
                subgetion,
                mean
        };

        return rt;

       /* String local = mContext.getAssets().getLocales().toString();

        Log.e(TAG, "Linh " + local);

        File dbFile = new File(outFileName);
        Log.e(TAG, "File info: " +  Integer.parseInt(String.valueOf(dbFile.length()/1024)));
        ArrayList<String> array_list = new ArrayList<String>();

        //SQLiteDatabase db =  SQLiteDatabase.openDatabase(outFileName, null,SQLiteDatabase.OPEN_READONLY);
        SQLiteDatabase db = getReadableDatabase();
        String DATABASE_FILE_PATH = "dir";
       //  db = SQLiteDatabase.openDatabase(DATABASE_FILE_PATH        + File.separator + "name", null,SQLiteDatabase.OPEN_READWRITE);

        Cursor res =  db.rawQuery( "select * from words where word='"+word +"'", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(MEAN_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;*/
    }



    class DatabaseContext extends ContextWrapper {

        private static final String DEBUG_CONTEXT = "DatabaseContext";

        public DatabaseContext(Context base) {
            super(base);
        }

        @Override
        public File getDatabasePath(String name)
        {
            /*File sdcard = Environment.getExternalStorageDirectory();
            String dbfile = sdcard.getAbsolutePath() + File.separator+ "databases" + File.separator + name;

            if (!dbfile.endsWith(".db"))
            {
                dbfile += ".db" ;
            }*/

            if(android.os.Build.VERSION.SDK_INT >= 17){
                DB_PATH = getApplicationInfo().dataDir + "/databases/";
            }
            else
            {
                DB_PATH = "/data/data/" + getPackageName() + "/databases/";
            }

            String dbfile = DB_PATH + name;

            if (!dbfile.endsWith(".db"))
            {
                dbfile += ".db" ;
            }


            File result = new File(dbfile);

            if (!result.getParentFile().exists())
            {
                result.getParentFile().mkdirs();
            }

            if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN))
            {
                Log.w(DEBUG_CONTEXT,
                        "getDatabasePath(" + name + ") = " + result.getAbsolutePath());
            }

            return result;
        }



        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory)
        {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            // SQLiteDatabase result = super.openOrCreateDatabase(name, mode, factory);
            if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN))
            {
                Log.w(DEBUG_CONTEXT,
                        "openOrCreateDatabase(" + name + ",,) = " + result.getPath());
            }
            return result;
        }

    }
}
