package com.slastanna.questory.tables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import static android.content.Context.MODE_PRIVATE;
import static com.slastanna.questory.EmailPasswordActivity.userCurrentKey;

public class Statistics {
    String DBNAME;
    String TABLENAME;
    String USER_ID="USER_ID";
    String NICKNAME="USER_NICKNAME";
    String NAME="USER_NAME";
    String SURNAME="USER_SURNAME";
    //String DATE_START="DATE_START";
    //String DATE_END="DATE_END";
    String DAYS_USED="DAYS_USED";
    String AGE="USER_AGE";
    String POINTS="USER_POINTS";
    SQLiteDatabase db;

    Context context;


    public void createDB(String TABLENAME){
        this.TABLENAME=TABLENAME;
        DBNAME="DBQuestory"+userCurrentKey;
        db = context.openOrCreateDatabase(DBNAME, MODE_PRIVATE, null);
        db.execSQL("CREATE IF NOT EXISTS "+TABLENAME+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+USER_ID+" TEXT, "+NICKNAME+" TEXT, "+
                NAME+" TEXT, "+SURNAME+" TEXT, "+DAYS_USED+" INTEGER, "+AGE+" INTEGER, "+POINTS+" INTEGER);");
    }

    public void getDaysUsed(String start, String end){}

    public void clearTable(String TABLENAME){db.execSQL("delete * from " + TABLENAME);}


}
