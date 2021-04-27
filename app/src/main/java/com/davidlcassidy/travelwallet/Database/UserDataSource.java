/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.davidlcassidy.travelwallet.Classes.AppPreferences;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Enums.ItemField;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
UserDataSource is used to manage and access all local User data, via access
to the User table in MainDatabase. There is no reference database for Users.
 */

public class UserDataSource {

    private static UserDataSource instance;
    private static AppPreferences appPreferences;
    private static Context context;
    private static SQLiteDatabase dbMain;
    private static MainDatabaseHelper dbHelperMain;
    private static String tableNameMain;
    private static String[] tableColumnsMain;

    private UserDataSource(Context c) {
        appPreferences = AppPreferences.getInstance(c);
        context = c;
        dbHelperMain = new MainDatabaseHelper(context);
        dbMain = dbHelperMain.getDB();

        tableNameMain = MainDatabaseHelper.TABLE_U;
        Cursor dbCursor1 = dbMain.query(tableNameMain, null, null, null, null, null, null);
        tableColumnsMain = dbCursor1.getColumnNames();
        dbCursor1.close();
    }

    public static UserDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new UserDataSource(context);
        }
        return instance;
    }

    // Creates new user and inserts into main database
    public User create(String name, String notes) {
        ContentValues values = new ContentValues();
        values.put(MainDatabaseHelper.COLUMN_U_NAME, name);
        values.put(MainDatabaseHelper.COLUMN_U_NOTES, notes);

        long insertId = dbMain.insert(tableNameMain, null, values);
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, MainDatabaseHelper.COLUMN_CC_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        User newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    // Deletes all users
    public void deleteAll() {
        dbMain.delete(tableNameMain, null, null);
    }

    // Deletes specific user
    public void delete(User user) {
        int id = user.getId();
        delete(id);
    }

    // Deletes specific user, based on user ID
    public void delete(int userID) {
        dbMain.delete(tableNameMain, MainDatabaseHelper.COLUMN_U_ID + " = " + userID, null);
    }

    // Returns a list of all users in database, sorted by sortField parameter
    public ArrayList<User> getAll(ItemField sortField, ProgramDataSource programDS, CardDataSource cardDS) {
        ArrayList<User> userList = new ArrayList<User>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = cursorToUser(cursor);
            if (user != null) {

                if (programDS != null && cardDS != null) {
                    ArrayList<LoyaltyProgram> userPrograms = programDS.getAll(user, null, false);
                    ArrayList<CreditCard> userCards = cardDS.getAll(user, null, false, false);
                    ArrayList<CreditCard> userChase524Cards = cardDS.getChase524StatusCards(user);
                    SimpleDateFormat dateFormat = appPreferences.getSetting_DatePattern().getDateFormat();
                    user.setValues(userPrograms, userCards, userChase524Cards, dateFormat);
                }

                userList.add(user);
            }
            cursor.moveToNext();
        }
        cursor.close();

        //Defines default sort order
        if (sortField == null || programDS == null || cardDS == null) {
            sortField = ItemField.USER_NAME;
        }

        // Sorts users by selected sort field
        final ItemField finalSortField = sortField;
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                Integer c = null;
                switch (finalSortField) {
                    case USER_NAME:
                        c = o1.getName().compareTo(o2.getName());
                        break;
                    case PROGRAMS_VALUE:
                        BigDecimal o1value = o1.getTotalProgramValue();
                        BigDecimal o2value = o2.getTotalProgramValue();
                        c = o2value.compareTo(o1value);
                        if (c == 0) {
                            c = o1.getName().compareTo(o2.getName());
                        }
                        break;
                    case CREDIT_LIMIT:
                        c = o2.getCreditLimit().compareTo(o1.getCreditLimit());
                        if (c == 0) {
                            c = o1.getName().compareTo(o2.getName());
                        }
                        break;
                }
                return c;
            }
        });
        return userList;
    }

    // Returns a list of all user names in database
    public ArrayList<String> getAllNames() {
        ArrayList<String> nameList = new ArrayList<String>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = cursorToUser(cursor);
            if (user != null) {
                nameList.add(user.getName());
            }
            cursor.moveToNext();
        }
        cursor.close();

        // Sorts user names
        Collections.sort(nameList, new Comparator<String>() {
            @Override
            public int compare(String u1, String u2) {
                Integer c = null;
                c = u1.compareTo(u2);
                return c;
            }
        });
        return nameList;
    }

    // Returns a single user by user ID
    public User getSingle(int id, ProgramDataSource programDS, CardDataSource cardDS) {
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, MainDatabaseHelper.COLUMN_U_ID + " = " + id, null, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }
        cursor.close();
        if (programDS != null && cardDS != null) {
            ArrayList<LoyaltyProgram> userPrograms = programDS.getAll(user, null, false);
            ArrayList<CreditCard> userCards = cardDS.getAll(user, null, false, false);
            ArrayList<CreditCard> userChase524Cards = cardDS.getChase524StatusCards(user);
            SimpleDateFormat dateFormat = appPreferences.getSetting_DatePattern().getDateFormat();
            user.setValues(userPrograms, userCards, userChase524Cards, dateFormat);
        }
        return user;
    }

    // Look up single user by user name
    public User getSingle(String userName, ProgramDataSource programDS, CardDataSource cardDS) {
        Integer userId = null;
        Cursor cursor = dbMain.query(tableNameMain, new String[]
                        {MainDatabaseHelper.COLUMN_U_ID, MainDatabaseHelper.COLUMN_U_NAME},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int mainIndex_id = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_U_ID);
            int mainIndex_name = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_U_NAME);
            while (!cursor.isAfterLast()) {
                Integer id = cursor.getInt(mainIndex_id);
                String name = cursor.getString(mainIndex_name);
                if (name.equals(userName)) {
                    userId = id;
                    break;
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return userId == null ? null : getSingle(userId, programDS, cardDS);
    }


    // Update all fields for an individual user in the main database
    public int update(User user) {
        Integer ID = user.getId();
        String name = user.getName();
        String notes = user.getNotes();

        ContentValues values = new ContentValues();
        values.put(MainDatabaseHelper.COLUMN_U_NAME, name);
        values.put(MainDatabaseHelper.COLUMN_CC_NOTES, notes);

        int numOfRows = dbMain.update(tableNameMain, values, MainDatabaseHelper.COLUMN_CC_ID + "=" + ID, null);
        return numOfRows;
    }

    // Converts database cursor to user
    private User cursorToUser(Cursor cursor) {
        int mainIndex_id = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_U_ID);
        int mainIndex_name = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_U_NAME);
        int mainIndex_notes = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_U_NOTES);

        Integer id = cursor.getInt(mainIndex_id);
        String name = cursor.getString(mainIndex_name);
        String notes = cursor.getString(mainIndex_notes);

        return new User(id, name, notes);
    }

}