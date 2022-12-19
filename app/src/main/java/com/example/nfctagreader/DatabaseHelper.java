package com.example.nfctagreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;




public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="User.db";
    public static final String TABLE_NAME="users_table";
    public static final String COL_1="ID";
    public static final String COL_2="TAGID";
    public static final String COL_3="NAME";
    public static final String COL_4="BALANCE";
    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    SQLiteDatabase db=this.getWritableDatabase();

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,TAGID INTEGER ,NAME TEXT,BALANCE FLOAT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String tagid,String name,String balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,tagid);
        contentValues.put(COL_3,name);
        contentValues.put(COL_4,balance);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
    public boolean updateData(String id,String tagid,String name,String balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1,id);
        contentValues.put(COL_2,tagid);
        contentValues.put(COL_3,name);
        contentValues.put(COL_4,balance);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }
    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }


}
