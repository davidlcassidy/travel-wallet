/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:29 PM
 */

/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/24/18 2:17 PM
 */

package com.davidlcassidy.travelwallet.Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

/*
MainDatabaseHelper is used to manage the Main Database. It is made up of two tables,
one for loyalty programs and one for credit cards. These two tables hold the
user provided data.
 */

public class MainDatabaseHelper extends SQLiteOpenHelper {

	// Database version should be incremented for structural changes to the database.
	// This will have users re-create their local database on the next app launch.
	//
	// WARNING : Updating database version or date format will end backwards compatibility.
    // Changing versions will wiping all local user data and changing the data format will make
    // date values in local database unreadable and make the app unstable. If new fields are
    // required, consider using futureText and futureInteger fields rather than making structural
    // changes to the database.
    public static final int DATABASE_VERSION = 2;
    public static final SimpleDateFormat DATABASE_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String DATABASE_NAME = "DatabaseMain.db";

    public static final String TABLE_LP = "loyaltyprograms";
    public static final String COLUMN_LP_ID = "_id";
    public static final String COLUMN_LP_REFID = "refid";
    public static final String COLUMN_LP_OWNERID = "ownerId";
    public static final String COLUMN_LP_ACCOUNTNUMBER = "accountNumber";
    public static final String COLUMN_LP_POINTS = "points";
    public static final String COLUMN_LP_LASTACTIVITY = "lastActivity";
    public static final String COLUMN_LP_NOTIFICATIONSTATUS = "notificationStatus";
    public static final String COLUMN_LP_NOTES = "notes";
    public static final String COLUMN_LP_FUTURETEXT1 = "futureText1";
    public static final String COLUMN_LP_FUTURETEXT2 = "futureText2";
    public static final String COLUMN_LP_FUTURETEXT3 = "futureText3";
    public static final String COLUMN_LP_FUTURETEXT4 = "futureText4";
    public static final String COLUMN_LP_FUTURETEXT5 = "futureText5";
    public static final String COLUMN_LP_FUTUREINTEGER1 = "futureInteger1";
    public static final String COLUMN_LP_FUTUREINTEGER2 = "futureInteger2";
    public static final String COLUMN_LP_FUTUREINTEGER3 = "futureInteger3";
    public static final String COLUMN_LP_FUTUREINTEGER4 = "futureInteger4";
    public static final String COLUMN_LP_FUTUREINTEGER5 = "futureInteger5";

    public static final String TABLE_CC = "creditcards";
    public static final String COLUMN_CC_ID = "_id";
    public static final String COLUMN_CC_REFID = "refid";
    public static final String COLUMN_CC_OWNERID = "ownerId";
    public static final String COLUMN_CC_STATUS = "status";
    public static final String COLUMN_CC_NUMBER = "number";
    public static final String COLUMN_CC_OPENDATE = "openDate";
    public static final String COLUMN_CC_AFDATE = "annualFeeDate";
    public static final String COLUMN_CC_CLOSEDATE = "closeDate";
    public static final String COLUMN_CC_NOTIFICATIONSTATUS = "notificationStatus";
    public static final String COLUMN_CC_NOTES = "notes";
    public static final String COLUMN_CC_CREDITLIMIT = "futureText1";
    public static final String COLUMN_CC_FUTURETEXT2 = "futureText2";
    public static final String COLUMN_CC_FUTURETEXT3 = "futureText3";
    public static final String COLUMN_CC_FUTURETEXT4 = "futureText4";
    public static final String COLUMN_CC_FUTURETEXT5 = "futureText5";
    public static final String COLUMN_CC_FUTUREINTEGER1 = "futureInteger1";
    public static final String COLUMN_CC_FUTUREINTEGER2 = "futureInteger2";
    public static final String COLUMN_CC_FUTUREINTEGER3 = "futureInteger3";
    public static final String COLUMN_CC_FUTUREINTEGER4 = "futureInteger4";
    public static final String COLUMN_CC_FUTUREINTEGER5 = "futureInteger5";

    public static final String TABLE_O = "users";
    public static final String COLUMN_O_ID = "_id";
    public static final String COLUMN_O_NAME = "name";
    public static final String COLUMN_O_NOTES = "notes";
    public static final String COLUMN_O_FUTURETEXT1 = "futureText1";
    public static final String COLUMN_O_FUTURETEXT2 = "futureText2";
    public static final String COLUMN_O_FUTURETEXT3 = "futureText3";
    public static final String COLUMN_O_FUTURETEXT4 = "futureText4";
    public static final String COLUMN_O_FUTURETEXT5 = "futureText5";
    public static final String COLUMN_O_FUTUREINTEGER1 = "futureInteger1";
    public static final String COLUMN_O_FUTUREINTEGER2 = "futureInteger2";
    public static final String COLUMN_O_FUTUREINTEGER3 = "futureInteger3";
    public static final String COLUMN_O_FUTUREINTEGER4 = "futureInteger4";
    public static final String COLUMN_O_FUTUREINTEGER5 = "futureInteger5";

    private static final String TABLE_LOYALTYPROGRAMS_CREATE = "create table IF NOT EXISTS "
            + TABLE_LP + "( "
            + COLUMN_LP_ID + " integer primary key autoincrement, "
            + COLUMN_LP_REFID + " integer, "
            + COLUMN_LP_OWNERID + " integer, "
            + COLUMN_LP_ACCOUNTNUMBER + " text, "
            + COLUMN_LP_POINTS + " integer, "
            + COLUMN_LP_LASTACTIVITY + " text, "
            + COLUMN_LP_NOTIFICATIONSTATUS + " integer, "
            + COLUMN_LP_NOTES + " text, "
            + COLUMN_LP_FUTURETEXT1 + " text, "
            + COLUMN_LP_FUTURETEXT2 + " text, "
            + COLUMN_LP_FUTURETEXT3 + " text, "
            + COLUMN_LP_FUTURETEXT4 + " text, "
            + COLUMN_LP_FUTURETEXT5 + " text, "
            + COLUMN_LP_FUTUREINTEGER1 + " integer, "
            + COLUMN_LP_FUTUREINTEGER2 + " integer, "
            + COLUMN_LP_FUTUREINTEGER3 + " integer, "
            + COLUMN_LP_FUTUREINTEGER4 + " integer, "
            + COLUMN_LP_FUTUREINTEGER5 + " integer "
            +");";

    private static final String TABLE_CREDITCARDS_CREATE = "create table IF NOT EXISTS "
            + TABLE_CC + "( "
            + COLUMN_CC_ID + " integer primary key autoincrement, "
            + COLUMN_CC_REFID + " integer, "
            + COLUMN_CC_OWNERID + " integer, "
            + COLUMN_CC_STATUS + " integer, "
            + COLUMN_CC_NUMBER + " integer, "
            + COLUMN_CC_OPENDATE + " text, "
            + COLUMN_CC_AFDATE + " text, "
            + COLUMN_CC_CLOSEDATE + " text, "
            + COLUMN_CC_NOTIFICATIONSTATUS + " integer, "
            + COLUMN_CC_NOTES + " text, "
            + COLUMN_CC_CREDITLIMIT + " text, "
            + COLUMN_CC_FUTURETEXT2 + " text, "
            + COLUMN_CC_FUTURETEXT3 + " text, "
            + COLUMN_CC_FUTURETEXT4 + " text, "
            + COLUMN_CC_FUTURETEXT5 + " text, "
            + COLUMN_CC_FUTUREINTEGER1 + " integer, "
            + COLUMN_CC_FUTUREINTEGER2 + " integer, "
            + COLUMN_CC_FUTUREINTEGER3 + " integer, "
            + COLUMN_CC_FUTUREINTEGER4 + " integer, "
            + COLUMN_CC_FUTUREINTEGER5 + " integer "
            +");";

    private static final String TABLE_OWNERS_CREATE = "create table IF NOT EXISTS "
            + TABLE_O + "( "
            + COLUMN_O_ID + " integer primary key autoincrement, "
            + COLUMN_O_NAME + " text, "
            + COLUMN_O_NOTES + " text, "
            + COLUMN_O_FUTURETEXT1 + " text, "
            + COLUMN_O_FUTURETEXT2 + " text, "
            + COLUMN_O_FUTURETEXT3 + " text, "
            + COLUMN_O_FUTURETEXT4 + " text, "
            + COLUMN_O_FUTURETEXT5 + " text, "
            + COLUMN_O_FUTUREINTEGER1 + " integer, "
            + COLUMN_O_FUTUREINTEGER2 + " integer, "
            + COLUMN_O_FUTUREINTEGER3 + " integer, "
            + COLUMN_O_FUTUREINTEGER4 + " integer, "
            + COLUMN_O_FUTUREINTEGER5 + " integer "
            +");";

    public MainDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDatabase getDB() throws SQLException {
        return this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_LOYALTYPROGRAMS_CREATE);
        database.execSQL(TABLE_CREDITCARDS_CREATE);
        database.execSQL(TABLE_OWNERS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2){
            db.execSQL(TABLE_OWNERS_CREATE);
        } else {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LP);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CC);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_O);
            onCreate(db);
        }
    }

}