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

public class Data extends SQLiteOpenHelper {

    private static String TAG = "DataBaseHelper"; // Tag just for the LogCat window
    //destination path (location) of our database on device
    private static String DB_PATH = "";
    private static String SQL_NAME = "words.sql";

    private static String DB_NAME ="av.db";// Database name


    static final String MEAN_COLUMN_NAME = "mean";

    Context mContext;
    TextView mOutput;
    public Data(final Context context,TextView output){
        super(context,DB_NAME,null,1);
        mContext = context;
        mOutput = output;

        if(android.os.Build.VERSION.SDK_INT >= 17){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        }
        else
        {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }


       // DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        outFileName = DB_PATH + DB_NAME;
        Log.i(TAG, "file out: " + outFileName);

        this.getReadableDatabase();
        this.close();

    }




    @Override
    public void onCreate(SQLiteDatabase db) {


        boolean mDataBaseExist = checkDataBase();
        initDB2(db);
        if(!mDataBaseExist)
        {
            //initDB();

        }

    }

    private boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        Log.e(TAG, "File info: " +  Integer.parseInt(String.valueOf(dbFile.length()/1024)));
        return dbFile.exists();
    }


    String outFileName;
    static boolean isReady = false;


    static Map<String, String> mapDic = new HashMap<String, String>();
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
            while ((line = br.readLine()) != null) {
               // sb.append(line);
                  //  int index = line.indexOf('*');

                    //String word = line.substring(0, index);
                    //String mean = line.substring(index + 1, line.length());
                    mapDic.put(line.substring(0, line.indexOf('*')), line);

            }

            Long tsLong2 = System.currentTimeMillis();
            Log.i("Linh Linh", (tsLong2 - tsLong) + "") ;


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

    public void initDB()
    {
        Log.e(TAG, "init db");

        try
        {
            Toast.makeText(mContext,"Please wait!", Toast.LENGTH_LONG).show();
            InputStream mInput = mContext.getAssets().open(DB_NAME);


            OutputStream mOutput = new FileOutputStream(outFileName);
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = mInput.read(mBuffer))>0)
            {
                mOutput.write(mBuffer, 0, mLength);
            }


            mOutput.flush();
            mOutput.close();
            mInput.close();

            Toast.makeText(mContext,"The app is ready!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "createDatabase database created");


            SQLiteDatabase database = SQLiteDatabase.openDatabase(outFileName, null,SQLiteDatabase.OPEN_READWRITE);
            database.close();

    } catch (FileNotFoundException e) {
        e.printStackTrace();
        Log.i(TAG, "******* File not found. Did you" +
                " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
    }
        catch (Exception e)
        {
            Toast.makeText(mContext,"Something went wrong, " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    String findword = null;
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getMean(String word) {

        if(!isReady) {
            findword = word;
            return "Loading...";
        }
        word = word.toLowerCase();

        String line = mapDic.get(word);

        if(line ==null)
            line ="";
        int index = line.indexOf('*');
        //String word = line.substring(0, index);
        String mean = "";
        if(index > 0)
            mean = line.substring(index + 1, line.length());

        return mean;

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
