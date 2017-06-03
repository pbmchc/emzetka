package com.pbmchc.emzetka.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pbmchc.emzetka.models.Schedule;
import com.pbmchc.emzetka.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piotrek on 2017-01-04.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "emzetka";
    private static final String DB_TABLE = "schedule";
    public static final String KEY_ID = "scheduleId";
    public static final String KEY_NAME = "stopName";
    public static final String KEY_LINE = "lineNumber";
    public static final String KEY_DIRECTION = "direction";


    private static final String DB_CREATE_SCHEDULE_TABLE =
    "CREATE TABLE " + DB_TABLE + "(" + KEY_ID + " TEXT PRIMARY KEY, " +
            KEY_NAME + " TEXT NOT NULL," + KEY_LINE + " TEXT NOT NULL," +  KEY_DIRECTION + " TEXT NOT NULL);";

    private static final String DROP_SCHEDULE_TABLE =
            "DROP TABLE IF EXISTS schedule";

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(DB_CREATE_SCHEDULE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DROP_SCHEDULE_TABLE);
        onCreate(db);
    }

    public void insertSchedule(Schedule schedule){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ID, schedule.getScheduleId());
        contentValues.put(KEY_NAME, schedule.getStopName());
        contentValues.put(KEY_LINE, StringUtils.fixLineNameForQuerying(schedule.getLineNumber()));
        contentValues.put(KEY_DIRECTION, schedule.getDirection());
        db.insert(DB_TABLE, null, contentValues);
        db.close();
    }

    public void deleteSchedule(String scheduleId){
        SQLiteDatabase db = this.getWritableDatabase();
        String where = KEY_ID + "='" + scheduleId + "'";
        db.delete(DB_TABLE, where, null);
        db.close();
    }

    public List<Schedule> getSchedules(){
        List<Schedule> schedules = new ArrayList<>();
        String select = "SELECT * FROM " + DB_TABLE + " ORDER BY LINENUMBER";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            do{
                Schedule schedule = new Schedule();
                schedule.setScheduleId(cursor.getString(0));
                schedule.setStopName(cursor.getString(1));
                schedule.setLineNumber(StringUtils.fixLineNameForDisplayingFromDatabase(cursor.getString(2)));
                schedule.setDirection(cursor.getString(3));
                schedules.add(schedule);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return schedules;
    }

    public boolean getSchedule(String scheduleId){
        boolean isFav = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String where = KEY_ID + "='" + scheduleId + "'";
        String[] columns = {KEY_ID, KEY_NAME};
        Cursor cursor = db.query(DB_TABLE, columns, where, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            isFav = true;
            cursor.close();
        }
        db.close();
        return isFav;
    }

    public int getCount(){
        String select = "SELECT * FROM " + DB_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public void deleteAllLines(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE, null, null);
        db.close();
    }
}
