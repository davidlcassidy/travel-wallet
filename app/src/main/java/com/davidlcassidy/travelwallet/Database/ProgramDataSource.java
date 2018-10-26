/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:40 PM
 */

package com.davidlcassidy.travelwallet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.davidlcassidy.travelwallet.Adapters.SingleChoiceAdapter;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.Notification;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.NotificationStatus;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/*
ProgramDataSource is used to manage and access all local Loyalty Program data, via
access to the two Loyalty Program tables in MainDatabase and RefDatabase.
 */

public class ProgramDataSource {

    private static ProgramDataSource instance;
    private static OwnerDataSource ownerDS;
    private static Context context;
    private static UserPreferences userPreferences;
    private static SQLiteDatabase dbMain, dbRef;
    private static MainDatabaseHelper dbHelperMain;
    private static RefDatabaseHelper dbHelperRef;
    private static String tableNameMain, tableNameRef;
    private static String[] tableColumnsMain, tableColumnsRef;
    private static SimpleDateFormat dbDateFormat = dbHelperMain.DATABASE_DATEFORMAT;

    public static ProgramDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new ProgramDataSource(context);
        }
        return instance;
    }

    private ProgramDataSource(Context c) {
        context = c;
        userPreferences = UserPreferences.getInstance(context);
        dbHelperMain = new MainDatabaseHelper(context);
        dbHelperRef = new RefDatabaseHelper(context);
        dbMain = dbHelperMain.getDB();
        dbRef = dbHelperRef.getDB();
        checkDbVersion(dbMain, dbRef);

        tableNameMain = dbHelperMain.TABLE_LP;
        tableNameRef = dbHelperRef.TABLE_LP_REF;
        Cursor dbCursor1 = dbMain.query(tableNameMain, null, null, null, null, null, null);
        tableColumnsMain = dbCursor1.getColumnNames();
        Cursor dbCursor2 = dbRef.query(tableNameRef, null, null, null, null, null, null);
        tableColumnsRef = dbCursor2.getColumnNames();
        dbCursor1.close(); dbCursor2.close();

        ownerDS = OwnerDataSource.getInstance(context);
    }

	// Check and update local database version if necessary
    public void checkDbVersion(SQLiteDatabase mainDB, SQLiteDatabase refDB) {
        int userMainDbVersion = userPreferences.getDatabase_MainDBVersion();
        int userRefDbVersion = userPreferences.getDatabase_RefDBVersion();
        int currentMainDbVersion = dbHelperMain.DATABASE_VERSION;
        int currentRefDbVersion = dbHelperRef.DATABASE_VERSION;

        if (userMainDbVersion != currentMainDbVersion) {
            dbHelperMain.onUpgrade(mainDB, userMainDbVersion, currentMainDbVersion);
            userPreferences.setDatabase_MainDBVersion(currentMainDbVersion);
        }
        if (userRefDbVersion != currentRefDbVersion) {
            dbHelperRef.onUpgrade(refDB, userRefDbVersion, currentRefDbVersion);
            userPreferences.setDatabase_RefDBVersion(currentRefDbVersion);
        }
    }

	// Creates new user created loyalty program and inserts program into main database
    public LoyaltyProgram create(Integer programRefId, Owner owner, String number, int points, Date lastActivity, String notes) {
        ContentValues values = new ContentValues();
        values.put(dbHelperMain.COLUMN_LP_REFID, programRefId);
        if (owner != null){
            values.put(dbHelperMain.COLUMN_CC_OWNERID, owner.getId());
        }
        values.put(dbHelperMain.COLUMN_LP_ACCOUNTNUMBER, number);
        values.put(dbHelperMain.COLUMN_LP_POINTS, points);
        if (lastActivity != null){
            values.put(dbHelperMain.COLUMN_LP_LASTACTIVITY, dbDateFormat.format(lastActivity));
        }
        values.put(dbHelperMain.COLUMN_LP_NOTIFICATIONSTATUS, String.valueOf(NotificationStatus.OFF.getId()));
        values.put(dbHelperMain.COLUMN_LP_NOTES, notes);

        long insertId = dbMain.insert(tableNameMain, null, values);
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, dbHelperMain.COLUMN_LP_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        LoyaltyProgram newProgram = cursorToProgram(cursor);
        cursor.close();
        return newProgram;
    }

	// Deletes all loyalty programs
    public void deleteAll(){
        dbMain.delete(tableNameMain, null, null);
    }

	// Deletes specific loyalty program
    public void delete(LoyaltyProgram program) {
        int id = program.getId();
        delete(id);
    }

	// Deletes specific loyalty program, based on ref ID
    public void delete(int refID) {
        dbMain.delete(tableNameMain, dbHelperMain.COLUMN_LP_ID + " = " + refID, null);
    }

	// Returns a list of all loyalty programs in database, sorted by sortField parameter
    public ArrayList <LoyaltyProgram> getAll(Owner owner, ItemField sortField, boolean onlyWithNotifications){
        ArrayList<LoyaltyProgram> programList = new ArrayList<LoyaltyProgram>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LoyaltyProgram program = cursorToProgram(cursor);
            Owner programOwner = program.getOwner();
            if (program != null) {
                if (owner != null && (programOwner == null || programOwner.getId() != owner.getId()) ) {
                    cursor.moveToNext();
                    continue;
                } else if (onlyWithNotifications && program.getNotificationStatus() != NotificationStatus.ON){
                    cursor.moveToNext();
                    continue;
                } else {
                    programList.add(program);
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();

		//Defines default sort order
        if (sortField == null){
            sortField = ItemField.PROGRAMNAME;
        }

		// Sorts programs by selected sort field
        final String sortBy = sortField.getName();
        Collections.sort(programList, new Comparator<LoyaltyProgram>() {
            @Override
            public int compare(LoyaltyProgram p1, LoyaltyProgram p2) {
                Integer c = null;

                switch (sortBy) {
                    case "Program Name":
                        c = p1.getName().compareTo(p2.getName());
                        if (c == 0) {
                            c = p1.getAccountNumber().compareTo(p2.getAccountNumber());
                        }
                        break;
                    case "Points":
                        c = p2.getPoints().compareTo(p1.getPoints());
                        if (c == 0) {
                            c = p1.getName().compareTo(p2.getName());
                        }
                        break;
                    case "Value":
                        c = p2.getTotalValue().compareTo(p1.getTotalValue());
                        if (c == 0) {
                            c = p1.getName().compareTo(p2.getName());
                        }
                        break;
                    case "Expiration Date":
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.YEAR, 100);
                        Date farAwayDate = cal.getTime();

                        Date p1Date = p1.getExpirationDate();
                        Date p2Date = p2.getExpirationDate();
                        if (p1Date == null) {
                            p1Date = farAwayDate;
                        }
                        if (p2Date == null) {
                            p2Date = farAwayDate;
                        }
                        c = p1Date.compareTo(p2Date);
                        if (c == 0) {
                            String p1Override = p1.getExpirationOverride();
                            String p2Override = p2.getExpirationOverride();

                            // Gets first word of override value. If no override, defaults to "LAST"
                            if (p1Override != null){
                                p1Override = p1Override.split(" ")[0];
                            } else {
                                p1Override = "LAST";
                            }
                            if (p2Override != null){
                                p2Override = p2Override.split(" ")[0];
                            } else {
                                p2Override = "LAST";
                            }

                            p2Override = p2Override.split(" ")[0];
                            List<String> overrideSortOrder = Arrays.asList("36", "48", "60", "120", "When", "Never", "LAST");
                            int p1Index = overrideSortOrder.indexOf(p1Override);
                            int p2Index = overrideSortOrder.indexOf(p2Override);
                            c = Integer.valueOf(p1Index).compareTo(p2Index);
                        }
                        if (c == 0) {
                            c = p1.getName().compareTo(p2.getName());
                        }
                        break;
                }
                return c;
            }
        });
        return programList;
    }

	// Returns a single loyalty program by program ID
    public LoyaltyProgram getSingle(int id) {
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, dbHelperMain.COLUMN_LP_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        LoyaltyProgram program = cursorToProgram(cursor);
        cursor.close();
        return program;
    }

	// Returns list of program types with option to ignore depreciated programs
    public ArrayList<String> getAvailableTypes(boolean ignoreDeprecated) {
        ArrayList<String> typeList = new ArrayList<>();
        Cursor cursor = dbRef.query(tableNameRef, new String[]
                {dbHelperRef.COLUMN_LP_TYPE, dbHelperRef.COLUMN_LP_DEPRECIATED},
                null, null, null, null, null);

        cursor.moveToFirst();
        int refIndex_type = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_TYPE);
        int refIndex_depreciated = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_DEPRECIATED);
        while (!cursor.isAfterLast()) {
            Boolean depreciated = cursor.getInt(refIndex_depreciated) == 1;

            Boolean depreciatedCheck = !(ignoreDeprecated && depreciated);
            if (depreciatedCheck) {
                String type = cursor.getString(refIndex_type);
                typeList.add(type);
            }
            cursor.moveToNext();
        }
        cursor.close();
       
		// Sort alphabetically and remove duplicates
        Collections.sort(typeList);
        typeList = new ArrayList<String>(new LinkedHashSet<String>(typeList));
	   
        return typeList;
    }

    // Returns list of programs with option to ignore depreciated programs
    public ArrayList<String> getAvailablePrograms(String type, boolean twoLines, boolean ignoreDeprecated) {
        ArrayList<String> programList = new ArrayList<>();
        Cursor cursor = dbRef.query(tableNameRef, new String[]
                        {dbHelperRef.COLUMN_LP_COMPANY, dbHelperRef.COLUMN_LP_TYPE, dbHelperRef.COLUMN_LP_NAME, dbHelperRef.COLUMN_LP_DEPRECIATED},
                null, null, null, null, null);
        cursor.moveToFirst();
        int refIndex_type = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_TYPE);
        int refIndex_company = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_COMPANY);
        int refIndex_name = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_NAME);
        int refIndex_depreciated = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_DEPRECIATED);
        while (!cursor.isAfterLast()) {
            String programType = cursor.getString(refIndex_type);
            String programName = cursor.getString(refIndex_name);
            Boolean depreciated = cursor.getInt(refIndex_depreciated) == 1;

            Boolean typeCheck = type.equals(programType);
            Boolean depreciatedCheck = !(ignoreDeprecated && depreciated);
            if (typeCheck && depreciatedCheck) {
                if (!twoLines){
                    programList.add(programName);
                } else {
                    String programCompany = cursor.getString(refIndex_company);
                    programList.add(programName + SingleChoiceAdapter.getDelimiter() + programCompany);
                }
            }
            cursor.moveToNext();
        }
        cursor.close();

        // Sort alphabetically
        Collections.sort(programList);

        return programList;
    }

	// Update programs notifications
    public void updateProgramsNotifications(){
        NotificationStatus newStatus;
        NotificationStatus currentStatus;

		// Calculates number of days before program points expiration to send notification to user
        Integer notificationDays = null;
        String[] notificationPeriodArray = userPreferences.getCustom_ProgramNotificationPeriod().split(" ");
        Integer value = Integer.valueOf(notificationPeriodArray[0]);
        String period = notificationPeriodArray[1];
        switch (period) {
            case "D":
                notificationDays = value;
                break;
            case "W":
                notificationDays = value * 7;
                break;
            case "M":
                notificationDays = value * 30;
                break;
        }

        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LoyaltyProgram program = cursorToProgram(cursor);
            if (program != null){
                currentStatus = program.getNotificationStatus();
                Date expirationDate = program.getExpirationDate();
                if (program.hasExpirationDate() == false || expirationDate == null) {
                    if (currentStatus == NotificationStatus.ON) {
                        program.setNotificationStatus(NotificationStatus.OFF);
                        update(program);
                    }
                } else if (currentStatus != NotificationStatus.UNMONITORED) {
                    Calendar programNotificationDate = Calendar.getInstance();
                    programNotificationDate.add(Calendar.DATE, notificationDays);
                    if (expirationDate.before(programNotificationDate.getTime()) && program.getPoints() > 0) {
                        newStatus = NotificationStatus.ON;
                    } else {
                        newStatus = NotificationStatus.OFF;
                    }

                    // If status has changed, updates program and creates new notification
                    if (newStatus != currentStatus) {
                        program.setNotificationStatus(newStatus);
                        update(program);
                        if (newStatus == NotificationStatus.ON) {
                            Notification notification = new Notification(program);
                            notification.sendPhoneNotification(context);
                        }
                    }
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

	// Updates notification status for an individual program
    public void changeProgramNotificationStatus(LoyaltyProgram program, NotificationStatus newStatus){
        NotificationStatus currentStatus = program.getNotificationStatus();
        if (newStatus != currentStatus) {
            program.setNotificationStatus(newStatus);
            update(program);
        }
    }

	// Update all fields for an individual program in the main database
    public int update(LoyaltyProgram program)  {
        Integer ID = program.getId();
        Integer refId = program.getRefId();
        Owner owner = program.getOwner();
        String number = program.getAccountNumber();
        Integer points = program.getPoints();
        Date lastActivity = program.getLastActivityDate();
        NotificationStatus notificationStatus = program.getNotificationStatus();
        String notes = program.getNotes();

        ContentValues values = new ContentValues();
        values.put(dbHelperMain.COLUMN_LP_REFID, refId);
        if (owner != null) {
            values.put(dbHelperMain.COLUMN_LP_OWNERID, owner.getId());
        } else {
            values.put(dbHelperMain.COLUMN_LP_OWNERID, "");
        }
        values.put(dbHelperMain.COLUMN_LP_ACCOUNTNUMBER, number);
        values.put(dbHelperMain.COLUMN_LP_POINTS, points);
        if (lastActivity != null){
            values.put(dbHelperMain.COLUMN_LP_LASTACTIVITY, dbDateFormat.format(lastActivity));
        }
        values.put(dbHelperMain.COLUMN_LP_NOTIFICATIONSTATUS, String.valueOf(notificationStatus.getId()));
        values.put(dbHelperMain.COLUMN_LP_NOTES, notes);

        int numOfRows = dbMain.update(tableNameMain, values, dbHelperMain.COLUMN_LP_ID + "=" + ID, null);
        return numOfRows;
    }

    // Look up program reference ID by program name
    public Integer getProgramRefId(String programType, String programName) {
        Cursor cursor = dbRef.query(tableNameRef, new String[]
                {dbHelperRef.COLUMN_LP_ID, dbHelperRef.COLUMN_LP_TYPE, dbHelperRef.COLUMN_LP_NAME},
                null, null, null, null, null);

        cursor.moveToFirst();
        int refIndex_id = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_ID);
        int refIndex_type = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_TYPE);
        int refIndex_name = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_NAME);
        Integer programRefId = null;
        while (!cursor.isAfterLast()) {
            Integer id = cursor.getInt(refIndex_id);
            String type = cursor.getString(refIndex_type);
            String name = cursor.getString(refIndex_name);
            if (programType.equals(type) && programName.equals(name)){
                programRefId = id;
                break;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return programRefId;
    }

    // Look up inactivity expiration by program name
    public Integer getProgramInactivityExpiration(String programType, String programName) {
        Cursor cursor = dbRef.query(tableNameRef, new String[]
                {dbHelperRef.COLUMN_LP_TYPE, dbHelperRef.COLUMN_LP_NAME, dbHelperRef.COLUMN_LP_INACTIVITYEXPIRATION},
                null, null, null, null, null);

        cursor.moveToFirst();
        int refIndex_type = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_TYPE);
        int refIndex_name = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_NAME);
        int refIndex_inactivityExpiration = cursor.getColumnIndex(dbHelperRef.COLUMN_LP_INACTIVITYEXPIRATION);
        Integer programInactivityExpiration = null;
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(refIndex_name);
            String type = cursor.getString(refIndex_type);
            Integer inactivityExpiration = cursor.getInt(refIndex_inactivityExpiration);
            if (programType.equals(type) && programName.equals(name)){
                programInactivityExpiration = inactivityExpiration;
                break;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return programInactivityExpiration;
    }

	// Converts database cursor to loyalty program
    private LoyaltyProgram cursorToProgram(Cursor cursor)  {
        int mainIndex_id = cursor.getColumnIndex(dbHelperMain.COLUMN_LP_ID);
        int mainIndex_refId = cursor.getColumnIndex(dbHelperMain.COLUMN_LP_REFID);
        int mainIndex_ownerId = cursor.getColumnIndex(dbHelperMain.COLUMN_LP_OWNERID);
        int mainIndex_number = cursor.getColumnIndex(dbHelperMain.COLUMN_LP_ACCOUNTNUMBER);
        int mainIndex_points = cursor.getColumnIndex(dbHelperMain.COLUMN_LP_POINTS);
        int mainIndex_lastActivity = cursor.getColumnIndex(dbHelperMain.COLUMN_LP_LASTACTIVITY);
        int mainIndex_notificationStatus = cursor.getColumnIndex(dbHelperMain.COLUMN_LP_NOTIFICATIONSTATUS);
        int mainIndex_notes = cursor.getColumnIndex(dbHelperMain.COLUMN_LP_NOTES);
        
        Integer id = cursor.getInt(mainIndex_id);
        Integer refId = cursor.getInt(mainIndex_refId);
        Owner owner = ownerDS.getSingle(cursor.getInt(mainIndex_ownerId), null, null);
        String number = cursor.getString(mainIndex_number);
        Integer points = cursor.getInt(mainIndex_points);
        Date lastActivity = null;
        try {
            lastActivity = dbDateFormat.parse(cursor.getString(mainIndex_lastActivity));
        } catch (Exception e) {
            lastActivity = null;
        }
        NotificationStatus notificationStatus = NotificationStatus.fromId(cursor.getInt(mainIndex_notificationStatus));
        String notes = cursor.getString(mainIndex_notes);

        Cursor cursorRef = dbRef.query(tableNameRef, tableColumnsRef, "_id = " + refId, null, null, null, null);
        if (cursorRef.getCount() != 1){
            delete(id);
            cursorRef.close();
            return null;
        } else {
            cursorRef.moveToFirst();
            int refIndex_type = cursorRef.getColumnIndex(dbHelperRef.COLUMN_LP_TYPE);
            int refIndex_company = cursorRef.getColumnIndex(dbHelperRef.COLUMN_LP_COMPANY);
            int refIndex_name = cursorRef.getColumnIndex(dbHelperRef.COLUMN_LP_NAME);
            int refIndex_pointValue = cursorRef.getColumnIndex(dbHelperRef.COLUMN_LP_POINTVALUE);
            int refIndex_inactivityExpiration = cursorRef.getColumnIndex(dbHelperRef.COLUMN_LP_INACTIVITYEXPIRATION);
            int refIndex_expirationOverride = cursorRef.getColumnIndex(dbHelperRef.COLUMN_LP_EXPIRATIONOVERRIDE);

            String type = cursorRef.getString(refIndex_type);
            String company = cursorRef.getString(refIndex_company);
            String name = cursorRef.getString(refIndex_name);
            BigDecimal pointValue = new BigDecimal(cursorRef.getString(refIndex_pointValue));
            Integer inactivityExpiration = cursorRef.getInt(refIndex_inactivityExpiration);
            String expirationOverride = cursorRef.getString(refIndex_expirationOverride);

            cursorRef.close();
            LoyaltyProgram program = new LoyaltyProgram(id, refId, owner, type, company, name, number, points, pointValue, inactivityExpiration, expirationOverride, lastActivity, notificationStatus, notes);
            return program;
        }
    }

}