package com.davidlcassidy.travelwallet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.davidlcassidy.travelwallet.Activities.MainActivity;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.Notification;
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
    public LoyaltyProgram create(int programRefId, String number, int points, Date lastActivity, String notes) {
        ContentValues values = new ContentValues();
        values.put(dbHelperMain.COLUMN_LP_REFID, programRefId);
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

	// Deletes specific loyalty program, based on program ID
    public void delete(int programID) {
        dbMain.delete(tableNameMain, dbHelperMain.COLUMN_LP_ID + " = " + programID, null);
    }

	// Returns a list of all loyalty programs in database, sorted by sortField parameter
    public ArrayList <LoyaltyProgram> getAll(ItemField sortField){
        ArrayList<LoyaltyProgram> programList = new ArrayList<LoyaltyProgram>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LoyaltyProgram program = cursorToProgram(cursor);
            if (program != null) {
                programList.add(program);
            }
            cursor.moveToNext();
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

	// Calculates sum total value of all loyalty programs
    public BigDecimal getAllProgramsValue() {
        ArrayList<LoyaltyProgram> programs = getAll(null);
        BigDecimal total = BigDecimal.valueOf(0);
        for (LoyaltyProgram p : programs) {
            total = total.add(p.getTotalValue());
        }
        return total;
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
        Cursor cursor = dbRef.query(tableNameRef, new String[] {"type", "depreciated"}, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String type = cursor.getString(0);
            Boolean depreciated = cursor.getInt(1) == 1;
            if ( !(ignoreDeprecated && depreciated) ) {
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
    public ArrayList<String> getAvailablePrograms(String type, boolean ignoreDeprecated) {
        ArrayList<String> programList = new ArrayList<>();
        Cursor cursor = dbRef.query(tableNameRef, new String[] {"type", "name", "depreciated"}, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String programType = cursor.getString(0);
            String programName = cursor.getString(1);
            Boolean depreciated = cursor.getInt(2) == 1;
            if (programType.equals(type) && !(ignoreDeprecated && depreciated)) {
                programList.add(programName);
            }
            cursor.moveToNext();
        }
        cursor.close();
		
		// Sort alphabetically
        Collections.sort(programList);
		
        return programList;
    }

	// Returns list of programs with notifications
    public ArrayList<LoyaltyProgram> getProgramsWithNotifications() {
        ArrayList<LoyaltyProgram> programList = new ArrayList<LoyaltyProgram>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LoyaltyProgram program = cursorToProgram(cursor);
            if (program != null && program.getNotificationStatus() == NotificationStatus.ON) {
                programList.add(program);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return programList;
    }

	// Update programs notifications
    public void updateProgramsNotifications(){
        NotificationStatus newStatus;
        NotificationStatus currentStatus;

		// Calculates number of days before program points expiration to send notification to user
        Integer notificationDays = null;
        String[] notificationPeriodArray = userPreferences.getSetting_ProgramNotificationPeriod().split(" ");
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

	// Returns program with the next upcoming expiration date
    public LoyaltyProgram getNextExpire(){
        ArrayList<LoyaltyProgram> programList = getAll(ItemField.EXPIRATIONDATE);
        Date today = new Date();
        for (LoyaltyProgram program : programList) {

            // Checks if program monitoring is on and has an expiration date
            NotificationStatus notificationStatus = program.getNotificationStatus();
            Date expDate = program.getExpirationDate();
            boolean hasExpirationDate = program.hasExpirationDate() && expDate != null;
            if (notificationStatus != NotificationStatus.UNMONITORED && hasExpirationDate){

                // Checks program if expiration date is in future and program has points
                int points = program.getPoints();
                if (expDate.compareTo(today) > 0 && points > 0) {
                    return program;
                }
			}
        }
        return null;
    }

	// Update all fields for an individual program in main the database
    public int update(LoyaltyProgram program)  {
        Integer ID = program.getId();
        Integer programRefId = program.getProgramId();
        String number = program.getAccountNumber();
        Integer points = program.getPoints();
        Date lastActivity = program.getLastActivityDate();
        NotificationStatus notificationStatus = program.getNotificationStatus();
        String notes = program.getNotes();

        ContentValues values = new ContentValues();
        values.put(dbHelperMain.COLUMN_LP_REFID, programRefId);
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

	// Converts database cursor to loyalty program
    private LoyaltyProgram cursorToProgram(Cursor cursor)  {
        Integer id = cursor.getInt(0);
        Integer programId = cursor.getInt(1);
        String owner = cursor.getString(2);
        String number = cursor.getString(3);
        Integer points = cursor.getInt(4);
        Date lastActivity = null;
        try {
            lastActivity = dbDateFormat.parse(cursor.getString(5));
        } catch (Exception e) {
            lastActivity = null;
        }
        NotificationStatus notificationStatus = NotificationStatus.fromId(cursor.getInt(6));
        String notes = cursor.getString(7);

        Cursor cursorRef = dbRef.query(tableNameRef, tableColumnsRef, "_id = " + programId, null, null, null, null);
        if (cursorRef.getCount() != 1){
            delete(programId);
            cursorRef.close();
            return null;
        } else {
            cursorRef.moveToFirst();

            String type = cursorRef.getString(1);
            String name = cursorRef.getString(2);
            BigDecimal pointValue = BigDecimal.valueOf(cursorRef.getDouble(3));
            Integer inactivityExpiration = cursorRef.getInt(4);
            String expirationOverride = cursorRef.getString(5);

            cursorRef.close();
            LoyaltyProgram program = new LoyaltyProgram(id, programId, type, name, number, points, pointValue, inactivityExpiration, expirationOverride, lastActivity, notificationStatus, notes);
            return program;
        }
    }

	// Look up program reference ID by card name
    public Integer getProgramRefId(String programName) {
        Cursor cursor = dbRef.query(tableNameRef, new String[] {"_id", "name"}, null, null, null, null, null);
        cursor.moveToFirst();
        Integer programRefId = null;
        while (!cursor.isAfterLast()) {
            Integer id = cursor.getInt(0);
            String name = cursor.getString(1);
            if (name.equals(programName)){
                programRefId = id;
                break;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return programRefId;
    }

	// Look up inactivity expiration by program name
    public Integer getProgramInactivityExpiration(String programName) {
        Cursor cursor = dbRef.query(tableNameRef, new String[] {"name", "inactivityExpiration"}, null, null, null, null, null);
        cursor.moveToFirst();
        Integer programInactivityExpiration = null;
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            Integer inactivityExpiration = cursor.getInt(1);
            if (name.equals(programName)){
                programInactivityExpiration = inactivityExpiration;
                break;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return programInactivityExpiration;
    }
}