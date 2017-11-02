package com.project.onur.playerx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.project.onur.playerx.model.User;


/**
 * Created by onur on 5.8.2017 at 20:23.
 */

public class SQLiteUser extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "User.db";
    private static final String TABLE_NAME = "User";
    private static final int VERSION =1 ;
    private static final String TAG = "DATABASE";

    private static final String USERID = "USERID";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";
    private static final String USERNAME = "USERNAME";
    private static final String PROFILURL = "PROFILEURL";
    private static final String RANGE = "RANGE";
    private static final String FCMTOKEN = "FCMTOKEN";


    public SQLiteUser(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL("CREATE TABLE "
                +TABLE_NAME+"("
                +USERID+" TEXT,"
                +EMAIL+" TEXT,"
                +PASSWORD+" TEXT, "
                +USERNAME+" TEXT,"
                +PROFILURL+" TEXT,"
                +RANGE+" INTEGER,"
                +FCMTOKEN+" TEXT)");

        Log.i(TAG,"Table created");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE İF EXİST"+TABLE_NAME);
        onCreate(db);

    }

    public void addUserToSQLite(User user){

        resetTables();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USERID,user.getUserID());
        values.put(EMAIL,user.getEmail());
        values.put(PASSWORD,user.getPassword());
        values.put(USERNAME,user.getUsername());
        values.put(PROFILURL,user.getProfilURL());
        values.put(RANGE,user.getRange());
        values.put(FCMTOKEN,user.getFcmToken());

        db.insert(TABLE_NAME,null,values);
        Log.i(TAG,"User added");
        db.close();

    }
    public User getUserFromSQLite(Cursor cursor){

        cursor.moveToFirst();
        User user = new User();
        user.setUserID(cursor.getString(cursor.getColumnIndex(USERID)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
        user.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));
        user.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
        user.setProfilURL(cursor.getString(cursor.getColumnIndex(PROFILURL)));
        user.setRange(cursor.getInt(cursor.getColumnIndex(RANGE)));
        user.setFcmToken(cursor.getString(cursor.getColumnIndex(FCMTOKEN)));

        return user;
    }

    private void resetTables(){

        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public Cursor query(){

        SQLiteDatabase db = this.getReadableDatabase();
        String[] COLUMNS = {USERID,EMAIL,PASSWORD,USERNAME,PROFILURL,RANGE,FCMTOKEN};
        Cursor cursor = db.query(TABLE_NAME,COLUMNS,null,null,null,null,null,null);


        return cursor;
    }

    public void setEmail(String userId,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME+" SET "+EMAIL+"='"+email+"' WHERE "+USERID+"='"+userId+"'");
        Log.e("EMAİL","veritabanındaki email bilgisi değişti");
        db.close();
    }

    public void setPassword(String userId,String password){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME+" SET "+PASSWORD+"='"+password+"' WHERE "+USERID+"='"+userId+"'");
        Log.e("PASSWORD","veritabanındaki şifre bilgisi değişti");
        db.close();
    }

    public void setFcmtoken(String userId,String fcmToken){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_NAME+" SET "+FCMTOKEN+"='"+fcmToken+"' WHERE "+USERID+"='"+userId+"'");
        Log.e("FCMTOKEN","veritabanındaki fcmToken bilgisi değişti");
        db.close();
    }



}
