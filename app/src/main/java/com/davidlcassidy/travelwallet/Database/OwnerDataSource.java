package com.davidlcassidy.travelwallet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static java.util.Calendar.MONTH;

/*
OwnerDataSource is used to manage and access all local Owner data, via access
to the Owner table in MainDatabase. There is no reference database for Owners.
 */

public class OwnerDataSource {

    private static OwnerDataSource instance;
    private static UserPreferences userPreferences;
    private static Context context;
    private static SQLiteDatabase dbMain;
    private static MainDatabaseHelper dbHelperMain;
    private static String tableNameMain;
    private static String[] tableColumnsMain;

    public static OwnerDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new OwnerDataSource(context);
        }
        return instance;
    }

    private OwnerDataSource(Context c) {
        userPreferences = UserPreferences.getInstance(c);
        context = c;
        dbHelperMain = new MainDatabaseHelper(context);
        dbMain = dbHelperMain.getDB();

        tableNameMain = dbHelperMain.TABLE_O;
        Cursor dbCursor1 = dbMain.query(tableNameMain, null, null, null, null, null, null);
        tableColumnsMain = dbCursor1.getColumnNames();
        dbCursor1.close();
    }

	// Creates new user created owner and inserts owner into main database
    public Owner create(String name, String notes) {
        ContentValues values = new ContentValues();
        values.put(dbHelperMain.COLUMN_O_NAME, name);
        values.put(dbHelperMain.COLUMN_O_NOTES, notes);

        long insertId = dbMain.insert(tableNameMain, null, values);
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, dbHelperMain.COLUMN_CC_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Owner newOwner = cursorToUser(cursor);
        cursor.close();
        return newOwner;
    }

	// Deletes all owners
    public void deleteAll(){
        dbMain.delete(tableNameMain, null, null);
    }

	// Deletes specific owner
    public void delete(Owner owner) {
        int id = owner.getId();
        delete(id);
    }

	// Deletes specific owner, based on woner ID
    public void delete(int userID) {
        dbMain.delete(tableNameMain, dbHelperMain.COLUMN_O_ID + " = " + userID, null);
    }

	// Returns a list of all owners in database, sorted by sortField parameter
    public ArrayList <Owner> getAll(ItemField sortField, ProgramDataSource programDS, CardDataSource cardDS){
        ArrayList<Owner> ownerList = new ArrayList<Owner>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Owner owner = cursorToUser(cursor);
            if (owner != null) {

                if (programDS != null && cardDS != null) {
                    ArrayList<LoyaltyProgram> userPrograms = programDS.getAll(owner, null, false);
                    ArrayList<CreditCard> userCards = cardDS.getAll(owner, null, false, false);
                    ArrayList<CreditCard> userChase524Cards = cardDS.getChase524StatusCards(owner);
                    SimpleDateFormat dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();
                    owner.setValues(userPrograms, userCards, userChase524Cards, dateFormat);
                }

                ownerList.add(owner);
            }
            cursor.moveToNext();
        }
        cursor.close();

        //Defines default sort order
        if (sortField == null || programDS == null || cardDS == null){
            sortField = ItemField.OWNERNAME;
        }

        // Sorts owners by selected sort field
        final String sortBy = sortField.getName();
        Collections.sort(ownerList, new Comparator<Owner>() {
            @Override
            public int compare(Owner o1, Owner o2) {
                Integer c = null;
                switch (sortBy) {
                    case "Owner Name":
                        c = o1.getName().compareTo(o2.getName());
                        break;
                    case "Programs Value":
                        BigDecimal o1value = o1.getTotalProgramValue();
                        BigDecimal o2value = o2.getTotalProgramValue();
                        c = o2value.compareTo(o1value);
                        if (c == 0) {
                            c = o1.getName().compareTo(o2.getName());
                        }
                        break;
                    case "Credit Limit":
                        c = o2.getCreditLimit().compareTo(o1.getCreditLimit());
                        if (c == 0) {
                            c = o1.getName().compareTo(o2.getName());
                        }
                        break;
                }
                return c;
            }
        });
        return ownerList;
    }

    // Returns a list of all owner names in database
    public ArrayList <String> getAllNames(){
        ArrayList<String> nameList = new ArrayList<String>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Owner owner = cursorToUser(cursor);
            if (owner != null) {
                nameList.add(owner.getName());
            }
            cursor.moveToNext();
        }
        cursor.close();

        // Sorts owner names
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

	// Returns a single owner by owner ID
    public Owner getSingle(int id, ProgramDataSource programDS, CardDataSource cardDS) {
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, dbHelperMain.COLUMN_O_ID + " = " + id, null, null, null, null);
        Owner owner = null;
        if (cursor.moveToFirst()) {
            owner = cursorToUser(cursor);
        }
        cursor.close();
        if (programDS != null && cardDS != null) {
            ArrayList<LoyaltyProgram> userPrograms = programDS.getAll(owner, null, false);
            ArrayList<CreditCard> userCards = cardDS.getAll(owner, null, false, false);
            ArrayList<CreditCard> userChase524Cards = cardDS.getChase524StatusCards(owner);
            SimpleDateFormat dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();
            owner.setValues(userPrograms, userCards, userChase524Cards, dateFormat);
        }
        return owner;
    }

    // Look up single owner by owner name
    public Owner getSingle(String userName, ProgramDataSource programDS, CardDataSource cardDS) {
        Integer userId = null;
        Cursor cursor = dbMain.query(tableNameMain, new String[] {"_id", "name"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Integer id = cursor.getInt(0);
                String name = cursor.getString(1);
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


	// Update all fields for an individual owner in the main database
    public int update(Owner owner)  {
        Integer ID = owner.getId();
        String name = owner.getName();
        String notes = owner.getNotes();

        ContentValues values = new ContentValues();
        values.put(dbHelperMain.COLUMN_O_NAME, name);
        values.put(dbHelperMain.COLUMN_CC_NOTES, notes);

        int numOfRows = dbMain.update(tableNameMain, values, dbHelperMain.COLUMN_CC_ID + "=" + ID, null);
        return numOfRows;
    }

    // Converts database cursor to owner
    private Owner cursorToUser(Cursor cursor)  {
        Integer id = cursor.getInt(0);
        String name = cursor.getString(1);
        String notes = cursor.getString(2);

        Owner owner = new Owner(id, name, notes);
        return owner;
    }

}