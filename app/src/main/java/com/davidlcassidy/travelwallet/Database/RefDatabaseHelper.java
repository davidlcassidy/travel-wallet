/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:40 PM
 */

package com.davidlcassidy.travelwallet.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
RefDatabaseHelper is used to manage the Reference Database. It is made up of two
tables, one for loyalty programs and one for credit cards. These two tables hold the
static (non user provided) reference data. To maintain data integrity, this database
will never have any insert or update queries run within the application and will only
be updated with new app versions.
 */

public class RefDatabaseHelper extends SQLiteOpenHelper{

	// Database version should be incremented for structural changes to the
    // database. This will have users re-create their local database on the next app
    // launch. Unlike the Main Database, updating database version will not result
    // in loss of user data as long as the unique ID remains unchanged
    public static int DATABASE_VERSION = 2;

    private String DB_DIRECTORY;
    private static String DB_NAME;
    private String DB_PATH;
    private SQLiteDatabase db;
    private final Context context;

    public static final String TABLE_LP_REF = "loyaltyprograms_ref";
    public static final String COLUMN_LP_ID = "_id";
    public static final String COLUMN_LP_TYPEID = "typeId";
    public static final String COLUMN_LP_NAME = "name";
    public static final String COLUMN_LP_POINTVALUE = "pointValue";
    public static final String COLUMN_LP_INACTIVITYEXPIRATION = "inactivityExpiration";
    public static final String COLUMN_LP_EXPIRATIONOVERRIDE = "expirationOverride";
    public static final String COLUMN_LP_DEPRECIATED = "depreciated";

    public static final String TABLE_CC_REF = "creditcards_ref";
    public static final String COLUMN_CC_ID = "_id";
    public static final String COLUMN_CC_BANKID = "bankId";
    public static final String COLUMN_CC_NAME = "name";
    public static final String COLUMN_CC_TYPE = "type";
    public static final String COLUMN_CC_AF = "annualFee";
    public static final String COLUMN_CC_FTF = "foreignTransactionFee";
    public static final String COLUMN_CC_DEPRECIATED = "depreciated";

    public RefDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        DB_DIRECTORY = "/data/data/" + context.getPackageName() + "/databases/";
        DB_NAME = "RefDatabase.db";
        DB_PATH = DB_DIRECTORY + DB_NAME;

    }

    public SQLiteDatabase getDB()  {
        if(db == null){
            createDataBaseFromAppAssetsDir();
            db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);
            }
        return db;
    }

    public final void createDataBaseFromAppAssetsDir() {

        // Creates local copy of Ref Database
        try {
            InputStream myInput = null;
            myInput = context.getAssets().open(DB_NAME);
            OutputStream myOutput = new FileOutputStream(DB_PATH);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void close() {
        if(db != null)
            db.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2) {
        createDataBaseFromAppAssetsDir();
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
        return db.query("EMP_TABLE", null, null, null, null, null, null);
    }
}