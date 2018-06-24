package com.davidlcassidy.travelwallet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.CardStatus;
import com.davidlcassidy.travelwallet.EnumTypes.NotificationStatus;
import com.davidlcassidy.travelwallet.Classes.Notification;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;

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
CardDataSource is used to manage and access all local Credit Card data, via access
to the two Credit Card tables in MainDatabase and RefDatabase.
 */

public class CardDataSource {

    private static CardDataSource instance;
    private static OwnerDataSource ownerDS;
    private static Context context;
    private static UserPreferences userPreferences;
    private static SQLiteDatabase dbMain, dbRef;
    private static MainDatabaseHelper dbHelperMain;
    private static RefDatabaseHelper dbHelperRef;
    private static String tableNameMain, tableNameRef;
    private static String[] tableColumnsMain, tableColumnsRef;
    private static SimpleDateFormat dbDateFormat = dbHelperMain.DATABASE_DATEFORMAT;

    public static CardDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new CardDataSource(context);
        }
        return instance;
    }

    private CardDataSource(Context c) {
        context = c;
        userPreferences = UserPreferences.getInstance(context);
        dbHelperMain = new MainDatabaseHelper(context);
        dbHelperRef = new RefDatabaseHelper(context);
        dbMain = dbHelperMain.getDB();
        dbRef = dbHelperRef.getDB();
        checkDbVersion(dbMain, dbRef);

        tableNameMain = dbHelperMain.TABLE_CC;
        tableNameRef = dbHelperRef.TABLE_CC_REF;
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

	// Creates new user created credit card and inserts card into main database
    public CreditCard create(Integer cardRefId, Owner owner, CardStatus status, BigDecimal creditLimit, Date openDate, Date afDate, Date closeDate, String notes) {
        ContentValues values = new ContentValues();
        values.put(dbHelperMain.COLUMN_CC_REFID, cardRefId);
        if (owner != null){
            values.put(dbHelperMain.COLUMN_CC_OWNERID, owner.getId());
        }
        values.put(dbHelperMain.COLUMN_CC_STATUS, status.getId());
        values.put(dbHelperMain.COLUMN_CC_CREDITLIMIT, String.valueOf(creditLimit));
        if (openDate != null){
            values.put(dbHelperMain.COLUMN_CC_OPENDATE, dbDateFormat.format(openDate));
        }
        if (afDate != null){
            values.put(dbHelperMain.COLUMN_CC_AFDATE, dbDateFormat.format(afDate));
        }
        if (closeDate != null){
            values.put(dbHelperMain.COLUMN_CC_CLOSEDATE, dbDateFormat.format(closeDate));
        }
        values.put(dbHelperMain.COLUMN_CC_NOTIFICATIONSTATUS, String.valueOf(NotificationStatus.OFF.getId()));
        values.put(dbHelperMain.COLUMN_CC_NOTES, notes);

        long insertId = dbMain.insert(tableNameMain, null, values);
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, dbHelperMain.COLUMN_CC_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        CreditCard newCard = cursorToCard(cursor);
        cursor.close();
        return newCard;
    }

	// Deletes all credit cards
    public void deleteAll(){
        dbMain.delete(tableNameMain, null, null);
    }

	// Deletes specific credit card
    public void delete(CreditCard card) {
        int id = card.getId();
        delete(id);
    }

	// Deletes specific credit card, based on card ID
    public void delete(int cardID) {
        dbMain.delete(tableNameMain, dbHelperMain.COLUMN_CC_ID + " = " + cardID, null);
    }

	// Returns a list of all credit cards in database, sorted by sortField parameter
    public ArrayList <CreditCard> getAll(Owner owner, ItemField sortField, boolean onlyWithNotifications, boolean excludeClosed){
        ArrayList<CreditCard> cardList = new ArrayList<CreditCard>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CreditCard card = cursorToCard(cursor);
            Owner cardOwner = card.getOwner();
            if (card != null) {
                if (owner != null && (cardOwner == null || cardOwner.getId() != owner.getId()) ) {
                    cursor.moveToNext();
                    continue;
                } else if (onlyWithNotifications && card.getNotificationStatus() != NotificationStatus.ON) {
                    cursor.moveToNext();
                    continue;
                } else if (excludeClosed && card.getStatus() == CardStatus.CLOSED){
                        cursor.moveToNext();
                        continue;
                } else {
                    cardList.add(card);
                    cursor.moveToNext();
                }
            }
        }
        cursor.close();

		//Defines default sort order
        if (sortField == null){
            sortField = ItemField.CARDNAME;
        }

		// Sorts cards by selected sort field
        final String sortBy = sortField.getName();
        Collections.sort(cardList, new Comparator<CreditCard>() {
            @Override
            public int compare(CreditCard c1, CreditCard c2) {
                Integer c = null;
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, 100);
                Date farAwayDate = cal.getTime();
                Date c1Date;
                Date c2Date;

                switch (sortBy) {
                    case "Card Name":
                        c = c1.getName().compareTo(c2.getName());
                        break;
                    case "Bank":
                        c = c1.getBank().compareTo(c2.getBank());
                        if (c == 0) {
                            c = c1.getName().compareTo(c2.getName());
                        }
                        break;
                    case "Annual Fee":
                        c = c2.getAnnualFee().compareTo(c1.getAnnualFee());
                        if (c == 0) {
                            c = c1.getName().compareTo(c2.getName());
                        }
                        break;
                    case "Open Date":
                        c1Date = c1.getOpenDate();
                        c2Date = c2.getOpenDate();
                        if (c1Date == null) {
                            c1Date = farAwayDate;
                        }
                        if (c2Date == null) {
                            c2Date = farAwayDate;
                        }
                        c = c1Date.compareTo(c2Date);
                        if (c == 0) {
                            c = c1.getName().compareTo(c2.getName());
                        }
                        break;
                    case "Annual Fee Date":
                        c1Date = c1.getAfDate();
                        c2Date = c2.getAfDate();
                        if (c1Date == null) {
                            c1Date = farAwayDate;
                        }
                        if (c2Date == null) {
                            c2Date = farAwayDate;
                        }
                        c = c1Date.compareTo(c2Date);
                        if (c == 0) {
                            c = c1.getName().compareTo(c2.getName());
                        }
                        break;
                }
                return c;
            }
        });
        return cardList;
    }

    // Returns a list of all credit cards in database that count towards Chase 5/24 status
    public ArrayList <CreditCard> getChase524StatusCards(Owner owner){
        ArrayList<CreditCard> fullCardList = getAll(owner, ItemField.OPENDATE, false,false);
        ArrayList<CreditCard> recentCardList = new ArrayList<CreditCard>();

        // Establish cutoff date 24 months before today
        Calendar cutoffDate = Calendar.getInstance();
        cutoffDate.add(Calendar.MONTH, -24);

        // List of issuers whose business cards also count towards 5/24 status
        List<String> businessCardsCreditCheck = Arrays.asList(new String[]{"Capital One", "Discover"});

        for (CreditCard card : fullCardList) {
            Date openDate = card.getOpenDate();

            // Skips cards with no open date
            if (openDate != null) {
                Calendar openDateCal = Calendar.getInstance();
                openDateCal.setTime(openDate);
                if (openDateCal.after(cutoffDate)) {
                    if (card.getType().equals("P")) {
                        recentCardList.add(card);
                    } else if (card.getType().equals("B") && businessCardsCreditCheck.contains(card.getBank())) {
                        recentCardList.add(card);
                    }
                }
            }
        }
        return recentCardList;
    }

	// Returns a single credit card by card ID
    public CreditCard getSingle(int id) {
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, dbHelperMain.COLUMN_CC_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        CreditCard card = cursorToCard(cursor);
        cursor.close();
        return card;
    }

	// Returns list of banks with option to ignore depreciated cards
    public ArrayList<String> getAvailableBanks(boolean ignoreDeprecated) {
        ArrayList<String> bankList = new ArrayList<>();
        Cursor cursor = dbRef.query(tableNameRef, new String[] {"bank", "depreciated"}, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String bank = cursor.getString(0);
            Boolean depreciated = cursor.getInt(1) == 1;
            if ( !(ignoreDeprecated && depreciated) ) {
                bankList.add(bank);
            }
            cursor.moveToNext();
        }
        cursor.close();
		
		// Sort alphabetically and remove duplicates
        Collections.sort(bankList);
        bankList = new ArrayList<String>(new LinkedHashSet<String>(bankList));
		
        return bankList;
    }

	// Returns list of cards with option to ignore depreciated cards
    public ArrayList<String> getAvailableCards(String bank, boolean ignoreDeprecated) {
        ArrayList<String> cardList = new ArrayList<>();
        Cursor cursor = dbRef.query(tableNameRef, new String[] {"bank", "name", "depreciated"}, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String cardBank = cursor.getString(0);
            String cardName = cursor.getString(1);
            Boolean depreciated = cursor.getInt(2) == 1;
            if (cardBank.equals(bank) && !(ignoreDeprecated && depreciated) ) {
                cardList.add(cardName);
            }
            cursor.moveToNext();
        }
        cursor.close();
		
		// Sort alphabetically
        Collections.sort(cardList);
		
        return cardList;
    }

	// Update cards notifications
    public void updateCardsNotifications(){
        NotificationStatus newStatus;
        NotificationStatus currentStatus;

		// Calculates number of days before annual fee to send notification to user
        Integer notificationDays = null;
        String[] notificationPeriodArray = userPreferences.getSetting_CardNotificationPeriod().split(" ");
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
            CreditCard card = cursorToCard(cursor);
            if (card != null) {
                currentStatus = card.getNotificationStatus();
                Date annualFeeDate = card.getAfDate();
                if (card.getStatus() == CardStatus.CLOSED || card.hasAnnualFee() == false || annualFeeDate == null){
                    if (currentStatus == NotificationStatus.ON){
                        card.setNotificationStatus(NotificationStatus.OFF);
                        update(card);
                    }
                } else if (currentStatus != NotificationStatus.UNMONITORED) {
                    Calendar cardNotificationDate = Calendar.getInstance();
                    cardNotificationDate.add(Calendar.DATE, notificationDays);
                    Boolean hasAnnualFee = card.hasAnnualFee();
                    if (annualFeeDate.before(cardNotificationDate.getTime()) && hasAnnualFee) {
                        newStatus = NotificationStatus.ON;
                    } else {
                        newStatus = NotificationStatus.OFF;
                    }

                    // If status has changed, updates card and creates new notification
                    if (newStatus != currentStatus) {
                        card.setNotificationStatus(newStatus);
                        update(card);
                        if (newStatus == NotificationStatus.ON) {
                            Notification notification = new Notification(card);
                            notification.sendPhoneNotification(context);

                        }
                    }
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

	// Updates notification status for an individual card
    public void changeCardNotificationStatus(CreditCard card, NotificationStatus newStatus){
        NotificationStatus currentStatus = card.getNotificationStatus();
        if (newStatus != currentStatus) {
            card.setNotificationStatus(newStatus);
            update(card);
        }
    }

	// Update all fields for an individual card in the main database
    public int update(CreditCard card)  {
        Integer ID = card.getId();
        Integer cardRefId = card.getCardId();
        Owner owner = card.getOwner();
        CardStatus status = card.getStatus();
        BigDecimal creditLimit = card.getCreditLimit();
        Date openDate = card.getOpenDate();
        Date afDate = card.getAfDate();
        Date closeDate = card.getCloseDate();
        NotificationStatus notificationStatus = card.getNotificationStatus();
        String notes = card.getNotes();

        ContentValues values = new ContentValues();
        values.put(dbHelperMain.COLUMN_CC_REFID, cardRefId);
        if (owner != null) {
            values.put(dbHelperMain.COLUMN_CC_OWNERID, owner.getId());
        } else {
            values.put(dbHelperMain.COLUMN_CC_OWNERID, "");
        }
        values.put(dbHelperMain.COLUMN_CC_STATUS, status.getId());
        values.put(dbHelperMain.COLUMN_CC_CREDITLIMIT, String.valueOf(creditLimit));
        if (openDate != null){
            values.put(dbHelperMain.COLUMN_CC_OPENDATE, dbDateFormat.format(openDate));
        }
        if (afDate != null){
            values.put(dbHelperMain.COLUMN_CC_AFDATE, dbDateFormat.format(afDate));
        }
        if (closeDate != null){
            values.put(dbHelperMain.COLUMN_CC_CLOSEDATE, dbDateFormat.format(closeDate));
        }
        values.put(dbHelperMain.COLUMN_CC_NOTIFICATIONSTATUS, String.valueOf(notificationStatus.getId()));
        values.put(dbHelperMain.COLUMN_CC_NOTES, notes);

        int numOfRows = dbMain.update(tableNameMain, values, dbHelperMain.COLUMN_CC_ID + "=" + ID, null);
        return numOfRows;
    }

	// Converts database cursor to credit card
    private CreditCard cursorToCard(Cursor cursor)  {
        Integer id = cursor.getInt(0);
        Integer cardId = cursor.getInt(1);
        Integer ownerId = cursor.getInt(2);
        Owner owner = ownerDS.getSingle(ownerId, null, null);
        CardStatus status = CardStatus.fromId(cursor.getInt(3));
        Integer cardNumber = cursor.getInt(4);
        Date openDate = null;
        try {
            openDate = dbDateFormat.parse(cursor.getString(5));
        } catch (Exception e) {
            openDate = null;
        }
        Date afDate = null;
        try {
            afDate = dbDateFormat.parse(cursor.getString(6));
        } catch (Exception e) {
            afDate = null;
        }
        Date closeDate = null;
        try {
            closeDate = dbDateFormat.parse(cursor.getString(7));
        } catch (Exception e) {
            closeDate = null;
        }
        NotificationStatus notificationStatus = NotificationStatus.fromId(cursor.getInt(8));
        String notes = cursor.getString(9);

        String creditLimitCursor = cursor.getString(10);
        BigDecimal creditLimit = null;
        if (creditLimitCursor != null) {
            creditLimit = new BigDecimal(creditLimitCursor);
        } else {
            creditLimit = new BigDecimal("0");
        }

        Cursor cursorRef = dbRef.query(tableNameRef, tableColumnsRef, "_id = " + cardId, null, null, null, null);
        if (cursorRef.getCount() != 1){
            delete(cardId);
            cursorRef.close();
            return null;
        } else {
            cursorRef.moveToFirst();

            String bank = cursorRef.getString(1);
            Integer bankId = cursorRef.getInt(2);
            String name = cursorRef.getString(3);
            String type = cursorRef.getString(4);
            BigDecimal annualFee = BigDecimal.valueOf(cursorRef.getDouble(5));
            Boolean annualFeeWaived = cursorRef.getInt(6) == 1;
            BigDecimal foreignTransactionFee = BigDecimal.valueOf(cursorRef.getDouble(7));

            cursorRef.close();
            CreditCard card = new CreditCard(id, cardId, owner, status, bankId, bank,  name, type, creditLimit, annualFee, annualFeeWaived, foreignTransactionFee, openDate, afDate, closeDate, notificationStatus, notes);
            return card;
        }
    }

	// Look up card reference ID by card name
    public Integer getCardRefId(String cardName) {
        Cursor cursor = dbRef.query(tableNameRef, new String[] {"_id", "name"}, null, null, null, null, null);
        cursor.moveToFirst();
        Integer cardRefId = null;
        while (!cursor.isAfterLast()) {
            Integer id = cursor.getInt(0);
            String name = cursor.getString(1);
            if (name.equals(cardName)){
                cardRefId = id;
                break;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return cardRefId;
    }

	// Look up card annual by card name
    public Double getCardAnnualFee(String cardName) {
        Cursor cursor = dbRef.query(tableNameRef, new String[] {"name", "annualFee"}, null, null, null, null, null);
        cursor.moveToFirst();
        Double cardAnnualFee = null;
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(0);
            Double annualFee = cursor.getDouble(1);
            if (name.equals(cardName)){
                cardAnnualFee = annualFee;
                break;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return cardAnnualFee;
    }
}