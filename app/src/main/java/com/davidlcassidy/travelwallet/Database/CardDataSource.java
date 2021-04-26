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
import com.davidlcassidy.travelwallet.Classes.Notification;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.EnumTypes.CardStatus;
import com.davidlcassidy.travelwallet.EnumTypes.Country;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.NotificationStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private static UserDataSource userDS;
    private static Context context;
    private static AppPreferences appPreferences;
    private static SQLiteDatabase dbMain, dbRef;
    private static MainDatabaseHelper dbHelperMain;
    private static RefDatabaseHelper dbHelperRef;
    private static String tableNameMain, tableNameRef;
    private static String[] tableColumnsMain, tableColumnsRef;
    private static final SimpleDateFormat dbDateFormat = MainDatabaseHelper.DATABASE_DATE_FORMAT;

    private CardDataSource(Context c) {
        context = c;
        appPreferences = AppPreferences.getInstance(context);
        dbHelperMain = new MainDatabaseHelper(context);
        dbHelperRef = new RefDatabaseHelper(context);
        dbMain = dbHelperMain.getDB();
        dbRef = dbHelperRef.getDB();
        checkDbVersion(dbMain, dbRef);

        tableNameMain = MainDatabaseHelper.TABLE_CC;
        tableNameRef = RefDatabaseHelper.TABLE_CC_REF;
        Cursor dbCursor1 = dbMain.query(tableNameMain, null, null, null, null, null, null);
        tableColumnsMain = dbCursor1.getColumnNames();
        Cursor dbCursor2 = dbRef.query(tableNameRef, null, null, null, null, null, null);
        tableColumnsRef = dbCursor2.getColumnNames();
        dbCursor1.close();
        dbCursor2.close();

        userDS = UserDataSource.getInstance(context);
    }

    public static CardDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new CardDataSource(context);
        }
        return instance;
    }

    // Check and update local database version if necessary
    public void checkDbVersion(SQLiteDatabase mainDB, SQLiteDatabase refDB) {
        int userMainDbVersion = appPreferences.getDatabase_MainDBVersion();
        int userRefDbVersion = appPreferences.getDatabase_RefDBVersion();
        int currentMainDbVersion = MainDatabaseHelper.DATABASE_VERSION;
        int currentRefDbVersion = RefDatabaseHelper.DATABASE_VERSION;

        if (userMainDbVersion != currentMainDbVersion) {
            dbHelperMain.onUpgrade(mainDB, userMainDbVersion, currentMainDbVersion);
            appPreferences.setDatabase_MainDBVersion(currentMainDbVersion);
        }
        if (userRefDbVersion != currentRefDbVersion) {
            dbHelperRef.onUpgrade(refDB, userRefDbVersion, currentRefDbVersion);
            appPreferences.setDatabase_RefDBVersion(currentRefDbVersion);
        }
    }

    // Creates new user created credit card and inserts card into main database
    public CreditCard create(Integer cardRefId, User user, CardStatus status, BigDecimal creditLimit, Date openDate, Date afDate, Date closeDate, String notes) {
        ContentValues values = new ContentValues();
        values.put(MainDatabaseHelper.COLUMN_CC_REFID, cardRefId);
        if (user != null) {
            values.put(MainDatabaseHelper.COLUMN_CC_USERID, user.getId());
        }
        values.put(MainDatabaseHelper.COLUMN_CC_STATUS, status.getId());
        values.put(MainDatabaseHelper.COLUMN_CC_CREDITLIMIT, String.valueOf(creditLimit));
        if (openDate != null) {
            values.put(MainDatabaseHelper.COLUMN_CC_OPENDATE, dbDateFormat.format(openDate));
        }
        if (afDate != null) {
            values.put(MainDatabaseHelper.COLUMN_CC_AFDATE, dbDateFormat.format(afDate));
        }
        if (closeDate != null) {
            values.put(MainDatabaseHelper.COLUMN_CC_CLOSEDATE, dbDateFormat.format(closeDate));
        }
        values.put(MainDatabaseHelper.COLUMN_CC_NOTIFICATIONSTATUS, String.valueOf(NotificationStatus.OFF.getId()));
        values.put(MainDatabaseHelper.COLUMN_CC_NOTES, notes);

        long insertId = dbMain.insert(tableNameMain, null, values);
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, MainDatabaseHelper.COLUMN_CC_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        CreditCard newCard = cursorToCard(cursor);
        cursor.close();
        return newCard;
    }

    // Deletes all credit cards
    public void deleteAll() {
        dbMain.delete(tableNameMain, null, null);
    }

    // Deletes specific credit card
    public void delete(CreditCard card) {
        int id = card.getId();
        delete(id);
    }

    // Deletes specific credit card, based on ref ID
    public void delete(int refID) {
        dbMain.delete(tableNameMain, MainDatabaseHelper.COLUMN_CC_ID + " = " + refID, null);
    }

    // Returns a list of all credit cards in database, sorted by sortField parameter
    public ArrayList<CreditCard> getAll(User user, ItemField sortField, boolean onlyWithNotifications, boolean excludeClosed) {
        ArrayList<CreditCard> cardList = new ArrayList<CreditCard>();
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CreditCard card = cursorToCard(cursor);
            if (card != null) {
                User cardUser = card.getUser();
                if (user != null && (cardUser == null || cardUser.getId() != user.getId())) {
                    cursor.moveToNext();
                    continue;
                } else if (onlyWithNotifications && card.getNotificationStatus() != NotificationStatus.ON) {
                    cursor.moveToNext();
                    continue;
                } else if (excludeClosed && card.getStatus() == CardStatus.CLOSED) {
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
        if (sortField == null) {
            sortField = ItemField.CARD_NAME;
        }

        // Sorts cards by selected sort field
        final ItemField finalSortField = sortField;
        Collections.sort(cardList, new Comparator<CreditCard>() {
            @Override
            public int compare(CreditCard c1, CreditCard c2) {
                Integer c = null;
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, 100);
                Date farAwayDate = cal.getTime();
                Date c1Date;
                Date c2Date;

                switch (finalSortField) {
                    case CARD_NAME:
                        c = c1.getName().compareTo(c2.getName());
                        break;
                    case BANK:
                        c = c1.getBank().compareTo(c2.getBank());
                        if (c == 0) {
                            c = c1.getName().compareTo(c2.getName());
                        }
                        break;
                    case ANNUAL_FEE:
                        c = c2.getAnnualFee().compareTo(c1.getAnnualFee());
                        if (c == 0) {
                            c = c1.getName().compareTo(c2.getName());
                        }
                        break;
                    case OPEN_DATE:
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
                    case AF_DATE:
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

    // Returns a list of all US credit cards in database that count towards Chase 5/24 status
    public ArrayList<CreditCard> getChase524StatusCards(User user) {
        ArrayList<CreditCard> fullCardList = getAll(user, ItemField.OPEN_DATE, false, false);
        ArrayList<CreditCard> recentCardList = new ArrayList<CreditCard>();

        // Establish cutoff date 24 months before today
        Calendar cutoffDate = Calendar.getInstance();
        cutoffDate.add(Calendar.MONTH, -24);

        // List of issuers whose business cards also count towards 5/24 status
        List<String> businessCardsCreditCheck = Arrays.asList("Capital One", "Discover");

        for (CreditCard card : fullCardList) {
            Date openDate = card.getOpenDate();
            boolean usCard = card.getCountry().getId() == Country.USA.getId();

            // Skips foreign cards and cards with no open date
            if (openDate != null && usCard) {

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
        Cursor cursor = dbMain.query(tableNameMain, tableColumnsMain, MainDatabaseHelper.COLUMN_CC_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        CreditCard card = cursorToCard(cursor);
        cursor.close();
        return card;
    }

    // Returns list of banks with option to ignore depreciated cards
    public ArrayList<String> getAvailableBanks(Country country, boolean ignoreDeprecated) {
        ArrayList<String> bankList = new ArrayList<>();
        Cursor cursor = dbRef.query(tableNameRef, new String[]
                        {RefDatabaseHelper.COLUMN_CC_COUNTRY, RefDatabaseHelper.COLUMN_CC_BANK, RefDatabaseHelper.COLUMN_CC_DEPRECIATED},
                null, null, null, null, null);
        cursor.moveToFirst();
        int refIndex_country = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_COUNTRY);
        int refIndex_bank = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_BANK);
        int refIndex_depreciated = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_DEPRECIATED);
        while (!cursor.isAfterLast()) {
            String cardCountry = cursor.getString(refIndex_country);
            Boolean depreciated = cursor.getInt(refIndex_depreciated) == 1;

            Boolean countryCheck = country == null || country.getName() == Country.OTHER.getName() || country.getName().equals(cardCountry);
            Boolean depreciatedCheck = !(ignoreDeprecated && depreciated);
            if (countryCheck && depreciatedCheck) {
                String bankName = cursor.getString(refIndex_bank);
                bankList.add(bankName);
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
    public ArrayList<String> getAvailableCards(Country country, String bank, boolean ignoreDeprecated) {
        ArrayList<String> cardList = new ArrayList<>();
        Cursor cursor = dbRef.query(tableNameRef, new String[]
                        {RefDatabaseHelper.COLUMN_CC_COUNTRY, RefDatabaseHelper.COLUMN_CC_BANK, RefDatabaseHelper.COLUMN_CC_NAME, RefDatabaseHelper.COLUMN_CC_DEPRECIATED},
                null, null, null, null, null);

        cursor.moveToFirst();
        int refIndex_country = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_COUNTRY);
        int refIndex_bank = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_BANK);
        int refIndex_name = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_NAME);
        int refIndex_depreciated = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_DEPRECIATED);
        while (!cursor.isAfterLast()) {
            String cardCountry = cursor.getString(refIndex_country);
            String cardBank = cursor.getString(refIndex_bank);
            String cardName = cursor.getString(refIndex_name);
            Boolean depreciated = cursor.getInt(refIndex_depreciated) == 1;

            Boolean countryCheck = country == null || country.getName() == Country.OTHER.getName() || country.getName().equals(cardCountry);
            Boolean bankCheck = bank.equals(cardBank);
            Boolean depreciatedCheck = !(ignoreDeprecated && depreciated);
            if (countryCheck && bankCheck && depreciatedCheck) {
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
    public void updateCardsNotifications() {
        NotificationStatus newStatus;
        NotificationStatus currentStatus;

        // Calculates number of days before annual fee to send notification to user
        Integer notificationDays = null;
        String[] notificationPeriodArray = appPreferences.getCustom_CardNotificationPeriod().split(" ");
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
                if (card.getStatus() == CardStatus.CLOSED || card.hasAnnualFee() == false || annualFeeDate == null) {
                    if (currentStatus == NotificationStatus.ON) {
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
    public void changeCardNotificationStatus(CreditCard card, NotificationStatus newStatus) {
        NotificationStatus currentStatus = card.getNotificationStatus();
        if (newStatus != currentStatus) {
            card.setNotificationStatus(newStatus);
            update(card);
        }
    }

    // Update all fields for an individual card in the main database
    public int update(CreditCard card) {
        Integer ID = card.getId();
        Integer refId = card.getRefId();
        User user = card.getUser();
        CardStatus status = card.getStatus();
        BigDecimal creditLimit = card.getCreditLimit();
        Date openDate = card.getOpenDate();
        Date afDate = card.getAfDate();
        Date closeDate = card.getCloseDate();
        NotificationStatus notificationStatus = card.getNotificationStatus();
        String notes = card.getNotes();

        ContentValues values = new ContentValues();
        values.put(MainDatabaseHelper.COLUMN_CC_REFID, refId);
        if (user != null) {
            values.put(MainDatabaseHelper.COLUMN_CC_USERID, user.getId());
        } else {
            values.put(MainDatabaseHelper.COLUMN_CC_USERID, "");
        }
        values.put(MainDatabaseHelper.COLUMN_CC_STATUS, status.getId());
        values.put(MainDatabaseHelper.COLUMN_CC_CREDITLIMIT, String.valueOf(creditLimit));
        if (openDate != null) {
            values.put(MainDatabaseHelper.COLUMN_CC_OPENDATE, dbDateFormat.format(openDate));
        }
        if (afDate != null) {
            values.put(MainDatabaseHelper.COLUMN_CC_AFDATE, dbDateFormat.format(afDate));
        }
        if (closeDate != null) {
            values.put(MainDatabaseHelper.COLUMN_CC_CLOSEDATE, dbDateFormat.format(closeDate));
        }
        values.put(MainDatabaseHelper.COLUMN_CC_NOTIFICATIONSTATUS, String.valueOf(notificationStatus.getId()));
        values.put(MainDatabaseHelper.COLUMN_CC_NOTES, notes);

        int numOfRows = dbMain.update(tableNameMain, values, MainDatabaseHelper.COLUMN_CC_ID + "=" + ID, null);
        return numOfRows;
    }

    // Look up card reference ID by card name
    public Integer getCardRefId(String cardBank, String cardName) {
        Cursor cursor = dbRef.query(tableNameRef, new String[]
                        {RefDatabaseHelper.COLUMN_CC_ID, RefDatabaseHelper.COLUMN_CC_BANK, RefDatabaseHelper.COLUMN_CC_NAME},
                null, null, null, null, null);

        cursor.moveToFirst();
        int refIndex_id = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_ID);
        int refIndex_bank = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_BANK);
        int refIndex_name = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_NAME);
        Integer cardRefId = null;
        while (!cursor.isAfterLast()) {
            Integer id = cursor.getInt(refIndex_id);
            String bank = cursor.getString(refIndex_bank);
            String name = cursor.getString(refIndex_name);
            if (cardBank.equals(bank) && cardName.equals(name)) {
                cardRefId = id;
                break;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return cardRefId;
    }

    // Look up card annual by card name
    public BigDecimal getCardAnnualFee(String cardBank, String cardName) {
        Cursor cursor = dbRef.query(tableNameRef, new String[]
                        {RefDatabaseHelper.COLUMN_CC_BANK, RefDatabaseHelper.COLUMN_CC_NAME, RefDatabaseHelper.COLUMN_CC_AF},
                null, null, null, null, null);

        cursor.moveToFirst();
        int refIndex_bank = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_BANK);
        int refIndex_name = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_NAME);
        int refIndex_af = cursor.getColumnIndex(RefDatabaseHelper.COLUMN_CC_AF);
        BigDecimal cardAnnualFee = null;
        while (!cursor.isAfterLast()) {
            String bank = cursor.getString(refIndex_bank);
            String name = cursor.getString(refIndex_name);
            BigDecimal annualFee = new BigDecimal(cursor.getString(refIndex_af));
            if (cardBank.equals(bank) && cardName.equals(name)) {
                cardAnnualFee = annualFee;
                break;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return cardAnnualFee;
    }

    // Converts database cursor to credit card
    private CreditCard cursorToCard(Cursor cursor) {
        int mainIndex_id = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_ID);
        int mainIndex_refId = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_REFID);
        int mainIndex_userId = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_USERID);
        int mainIndex_status = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_STATUS);
        int mainIndex_number = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_NUMBER);
        int mainIndex_creditLimit = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_CREDITLIMIT);
        int mainIndex_openDate = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_OPENDATE);
        int mainIndex_afDate = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_AFDATE);
        int mainIndex_closeDate = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_CLOSEDATE);
        int mainIndex_notificationStatus = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_NOTIFICATIONSTATUS);
        int mainIndex_notes = cursor.getColumnIndex(MainDatabaseHelper.COLUMN_CC_NOTES);

        Integer id = cursor.getInt(mainIndex_id);
        Integer refId = cursor.getInt(mainIndex_refId);
        User user = userDS.getSingle(cursor.getInt(mainIndex_userId), null, null);
        CardStatus status = CardStatus.fromId(cursor.getInt(mainIndex_status));
        Integer cardNumber = cursor.getInt(mainIndex_number);

        String creditLimitCursor = cursor.getString(mainIndex_creditLimit);
        BigDecimal creditLimit = null;
        if (creditLimitCursor != null) {
            creditLimit = new BigDecimal(creditLimitCursor);
        } else {
            creditLimit = BigDecimal.ZERO;
        }

        Date openDate = null;
        try {
            openDate = dbDateFormat.parse(cursor.getString(mainIndex_openDate));
        } catch (Exception e) {
            openDate = null;
        }
        Date afDate = null;
        try {
            afDate = dbDateFormat.parse(cursor.getString(mainIndex_afDate));
        } catch (Exception e) {
            afDate = null;
        }
        Date closeDate = null;
        try {
            closeDate = dbDateFormat.parse(cursor.getString(mainIndex_closeDate));
        } catch (Exception e) {
            closeDate = null;
        }
        NotificationStatus notificationStatus = NotificationStatus.fromId(cursor.getInt(mainIndex_notificationStatus));
        String notes = cursor.getString(mainIndex_notes);


        Cursor cursorRef = dbRef.query(tableNameRef, tableColumnsRef, "_id = " + refId, null, null, null, null);
        if (cursorRef.getCount() != 1) {
            delete(id);
            cursorRef.close();
            return null;
        } else {
            cursorRef.moveToFirst();
            int refIndex_logoId = cursorRef.getColumnIndex(RefDatabaseHelper.COLUMN_CC_LOGOID);
            int refIndex_country = cursorRef.getColumnIndex(RefDatabaseHelper.COLUMN_CC_COUNTRY);
            int refIndex_bank = cursorRef.getColumnIndex(RefDatabaseHelper.COLUMN_CC_BANK);
            int refIndex_name = cursorRef.getColumnIndex(RefDatabaseHelper.COLUMN_CC_NAME);
            int refIndex_type = cursorRef.getColumnIndex(RefDatabaseHelper.COLUMN_CC_TYPE);
            int refIndex_af = cursorRef.getColumnIndex(RefDatabaseHelper.COLUMN_CC_AF);
            int refIndex_ftf = cursorRef.getColumnIndex(RefDatabaseHelper.COLUMN_CC_FTF);

            String logoId = cursorRef.getString(refIndex_logoId);
            Country country = Country.fromName(cursorRef.getString(refIndex_country));
            String bank = cursorRef.getString(refIndex_bank);
            String name = cursorRef.getString(refIndex_name);
            String type = cursorRef.getString(refIndex_type);
            BigDecimal annualFee = new BigDecimal(cursorRef.getString(refIndex_af));

            // Convert local currencies in DB to USD
            if (country.getId() == Country.CANADA.getId()) {
                annualFee = annualFee.divide(Currency.CAD.getExchangeRate(), 10, RoundingMode.HALF_EVEN);
            }

            BigDecimal foreignTransactionFee = new BigDecimal(cursorRef.getString(refIndex_ftf));

            cursorRef.close();
            CreditCard card = new CreditCard(id, refId, logoId, user, status, country, bank, name, type, creditLimit, annualFee, foreignTransactionFee, openDate, afDate, closeDate, notificationStatus, notes);
            return card;
        }
    }
}