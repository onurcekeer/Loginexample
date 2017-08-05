package com.project.onur.playerx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


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
    private static final String USERNAME = "USERNAME";
    private static final String PROFILURL = "PROFILEURL";
    private static final String RANGE = "RANGE";
    private static final String LASTLOCATION = "LASTLOCATION";

    public SQLiteUser(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL("CREATE TABLE "
                +TABLE_NAME+"("
                +USERID+" TEXT,"
                +EMAIL+" TEXT,"
                +USERNAME+" TEXT,"
                +LASTLOCATION+" TEXT,"
                +PROFILURL+" TEXT,"
                +RANGE+" INTEGER)");

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
        values.put(USERNAME,user.getUsername());
        values.put(LASTLOCATION,user.getLastLocation());
        values.put(PROFILURL,user.getProfilURL());
        values.put(RANGE,user.getRange());

        db.insert(TABLE_NAME,null,values);
        Log.i(TAG,"User added");
        db.close();

    }
    public User getUserFromSQLite(Cursor cursor){

        cursor.moveToFirst();
        User user = new User("1","1","1","1","1",1);
        user.setUserID(cursor.getString(cursor.getColumnIndex(USERID)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
        user.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
        user.setLastLocation(cursor.getString(cursor.getColumnIndex(LASTLOCATION)));
        user.setProfilURL(cursor.getString(cursor.getColumnIndex(PROFILURL)));
        user.setRange(cursor.getInt(cursor.getColumnIndex(RANGE)));

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
        String[] COLUMNS = {USERID,EMAIL,USERNAME,LASTLOCATION,PROFILURL,RANGE};
        Cursor cursor = db.query(TABLE_NAME,COLUMNS,null,null,null,null,null,null);

        return cursor;
    }
}
