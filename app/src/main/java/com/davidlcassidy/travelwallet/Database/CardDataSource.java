package com.davidlcassidy.travelwallet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.CardStatus;
import com.davidlcassidy.travelwallet.EnumTypes.NotificationStatus;
import com.davidlcassidy.travelwallet.Classes.Notification;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;

/*
CardDataSource is used to manage and access all local Credit Card data, via access
to the two Credit Card tables in MainDatabase and RefDatabase.
 */

public class CardDataSource {

    public static CardDataSource instance;
    private static UserPreferences userPreferences;
    private static SQLiteDatabase dbMain;
    private static SQLiteDatabase dbRef;
    private static MainDatabaseHelper dbHelperMain;
    private static RefDatabaseHelper dbHelperRef;
    private static String tableNameMain;
    private static String tableNameRef;
    private static String[] tableColumnsMain;
    private static String[] tableColumnsRef;
    private static SimpleDateFormat dbDateFormat = dbHelperMain.DATABASE_DATEFORMAT;

    public static CardDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new CardDataSource(context.getApplicationContext());
        }
        return instance;
    }

    private CardDataSource(Context context) {
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
    public CreditCard create(int cardRefId, CardStatus status, Date openDate, Date afDate, Date closeDate, String notes) {
        ContentValues values = new ContentValues();
        values.put(dbHelperMain.COLUMN_CC_REFID, cardRefId);
        values.put(dbHelperMain.COLUMN_CC_STATUS, status.getId());
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
    public ArrayList <CreditCard> getAll(ItemField sortField){
        ArrayList<CreditCard> cardList = new ArrayList<CreditCard>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CreditCard card = cursorToCard(cursor);
            if (card != null) {
                cardList.add(card);
            }
            cursor.moveToNext();
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

	// Calculates sum total of all cards annual fees
    public BigDecimal getAllCardsAnnualFees() {
        ArrayList<CreditCard> cardList = getAll(null);
        BigDecimal totalAF = BigDecimal.valueOf(0);
        for (CreditCard cc : cardList) {
            totalAF = totalAF.add(cc.getAnnualFee());
        }
        return totalAF;
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

	// Returns list of cards with notifications
    public ArrayList<CreditCard> getCardsWithNotifications() {
        ArrayList<CreditCard> cardList = new ArrayList<CreditCard>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CreditCard card = cursorToCard(cursor);
            if (card != null && card.getNotificationStatus() == NotificationStatus.ON) {
                cardList.add(card);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return cardList;
    }

	// Update cards notifications
    public void updateCardsNotifications(Context context){
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
                if (currentStatus != NotificationStatus.UNMONITORED) {
					
					// Turns off monitoring for cards with no annual fee
                    if (card.hasAnnualFee() == false) {
                        card.setNotificationStatus(NotificationStatus.UNMONITORED);
                        update(card);
						
                    } else if (annualFeeDate != null) {
                        Calendar programNotificationDate = Calendar.getInstance();
                        programNotificationDate.add(Calendar.DATE, notificationDays);
                        Boolean hasAnnualFee = card.hasAnnualFee();
                        if (annualFeeDate.before(programNotificationDate.getTime()) && hasAnnualFee) {
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
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

	// Updates notification status for an individual card
    public void updateCardNotificationStatus(CreditCard card, NotificationStatus newStatus){
        NotificationStatus currentStatus = card.getNotificationStatus();
        if (newStatus != currentStatus) {
            card.setNotificationStatus(newStatus);
            update(card);
        }
    }

	// Returns card with the next upcoming annual fee
    public CreditCard getNextAF() {
        ArrayList<CreditCard> cardList = getAll(ItemField.AFDATE);
        Date today = new Date();
        for (CreditCard card : cardList) {

            // Checks if card monitoring is on and has an annual fee
            NotificationStatus notificationStatus = card.getNotificationStatus();
            Date annualFeeDate = card.getAfDate();
            boolean hasAnnualFee = card.hasAnnualFee() && annualFeeDate != null;
            if (notificationStatus != NotificationStatus.UNMONITORED && hasAnnualFee){

                // Checks program if annual fee date is in future
                if (annualFeeDate.compareTo(today) > 0) {
                    return card;
                }
			}
        }
        return null;
    }

	// Update all fields for an individual card in main the database
    public int update(CreditCard card)  {
        Integer ID = card.getId();
        Integer cardRefId = card.getCardId();
        CardStatus status = card.getStatus();
        Date openDate = card.getOpenDate();
        Date afDate = card.getAfDate();
        Date closeDate = card.getCloseDate();
        NotificationStatus notificationStatus = card.getNotificationStatus();
        String notes = card.getNotes();

        ContentValues values = new ContentValues();
        values.put(dbHelperMain.COLUMN_CC_REFID, cardRefId);
        values.put(dbHelperMain.COLUMN_CC_STATUS, status.getId());
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
        String owner = cursor.getString(2);
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
            CreditCard card = new CreditCard(id, cardId, status, bank, bankId, name, type, annualFee, annualFeeWaived, foreignTransactionFee, openDate, afDate, closeDate, notificationStatus, notes);
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