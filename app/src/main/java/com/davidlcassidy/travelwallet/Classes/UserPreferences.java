package com.davidlcassidy.travelwallet.Classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.davidlcassidy.travelwallet.EnumTypes.DatePattern;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.Language;

/*
UserPreferences class is used to read and write to the UserPreferences.
 */

public class UserPreferences {

    public static UserPreferences instance;
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor spEditor;


    //Sets user preference name
    private static String nUserPreference = "UserPreferences";

    //Sets user preference keys
    private static String nProgramNotificationPeriod = "ProgramNotificationPeriod";
    private static String nProgramPrimaryField = "ProgramPrimaryField";
    private static String nProgramSortField = "ProgramSortField";
    private static String nCardNotificationPeriod = "CardNotificationPeriod";
    private static String nCardPrimaryField = "CardPrimaryField";
    private static String nCardSortField = "CardSortField";
    private static String nInitialSummary = "InitialSummary";
    private static String nPhoneNotifications = "PhoneNotifications";
    private static String nLanguage = "Language";
    private static String nCurrency = "Currency";
    private static String nDatePattern = "DatePattern";
    private static String nMainDBVersion = "MainDBVersion";
    private static String nRefDBVersion = "RefDBVersion";
	
    //Sets user preference default values
    private static String dProgramNotificationPeriod = "4 W";
    private static ItemField dProgramPrimaryField = ItemField.ACCOUNTNUMBER;
    private static ItemField dProgramSortField = ItemField.PROGRAMNAME;
    private static String dCardNotificationPeriod = "4 W";
    private static ItemField dCardPrimaryField = ItemField.OPENDATE;
    private static ItemField dCardSortField = ItemField.CARDNAME;
    private static boolean dInitialSummary = false;
    private static boolean dPhoneNotifications = true;
    private static Language dLanguage = Language.ENGLISH;
    private static Currency dCurrency = Currency.USD;
    private static DatePattern dDatePattern = DatePattern.MDY_LONG;
    private static Integer dMainDBVersion = 0;
    private static Integer dRefDBVersion = 0;

    public static UserPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new UserPreferences(context.getApplicationContext());
        }
        return instance;
    }

    private UserPreferences(Context c){
        sharedPref = c.getSharedPreferences(nUserPreference, Context.MODE_PRIVATE);
        spEditor = sharedPref.edit();
    }

    public String getProgramNotificationPeriod() {
        return sharedPref.getString(nProgramNotificationPeriod, dProgramNotificationPeriod);
    }

    public void setProgramNotificationPeriod(String programNotificationPeriod) {
        spEditor.putString(nProgramNotificationPeriod, programNotificationPeriod);
        spEditor.commit();
    }

    public ItemField getProgramPrimaryField() {
        int id = sharedPref.getInt(nProgramPrimaryField, dProgramPrimaryField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null){
            return itemField;
        } else {
            spEditor.putInt(nProgramPrimaryField, dProgramPrimaryField.getId());
            return dProgramPrimaryField;
        }
    }

    public void setProgramPrimaryField(ItemField programPrimaryField) {
        spEditor.putInt(nProgramPrimaryField, programPrimaryField.getId());
        spEditor.commit();
    }

    public ItemField getProgramSortField() {
        int id = sharedPref.getInt(nProgramSortField, dProgramSortField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null){
            return itemField;
        } else {
            spEditor.putInt(nProgramSortField, dProgramSortField.getId());
            return dProgramSortField;
        }
    }

    public void setProgramSortField(ItemField programSortField) {
        spEditor.putInt(nProgramSortField, programSortField.getId());
        spEditor.commit();
    }

    public String getCardNotificationPeriod() {
        return sharedPref.getString(nCardNotificationPeriod, dCardNotificationPeriod);
    }

    public void setCardNotificationPeriod(String cardNotificationPeriod) {
        spEditor.putString(nCardNotificationPeriod, cardNotificationPeriod);
        spEditor.commit();
    }

    public ItemField getCardPrimaryField() {
        int id = sharedPref.getInt(nCardPrimaryField, dCardPrimaryField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null){
            return itemField;
        } else {
            spEditor.putInt(nCardPrimaryField, dCardPrimaryField.getId());
            return dCardPrimaryField;
        }
    }

    public void setCardPrimaryField(ItemField cardPrimaryField) {
        spEditor.putInt(nCardPrimaryField, cardPrimaryField.getId());
        spEditor.commit();
    }

    public ItemField getCardSortField() {
        int id = sharedPref.getInt(nCardSortField, dCardSortField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null){
            return itemField;
        } else {
            spEditor.putInt(nCardSortField, dCardSortField.getId());
            return dCardSortField;
        }
    }

    public void setCardSortField(ItemField cardSortField) {
        spEditor.putInt(nCardSortField, cardSortField.getId());
        spEditor.commit();
    }

    public boolean getInitialSummary() {
        int initSummary = (dInitialSummary) ? 1 : 0;
        return sharedPref.getInt(nInitialSummary, initSummary) == 1;
    }

    public void setInitialSummary(boolean initialSummary) {
        int initSummary = (initialSummary) ? 1 : 0;
        spEditor.putInt(nInitialSummary, initSummary);
        spEditor.commit();
    }

    public boolean getPhoneNotifications() {
        int phoneNotifications = (dPhoneNotifications) ? 1 : 0;
        return sharedPref.getInt(nPhoneNotifications, phoneNotifications) == 1;
    }

    public void setPhoneNotifications(boolean phoneNotifications) {
        int pn = (phoneNotifications) ? 1 : 0;
        spEditor.putInt(nPhoneNotifications, pn);
        spEditor.commit();
    }

    public Language getLanguage() {
        int id = sharedPref.getInt(nLanguage, dLanguage.getId());
        Language language = Language.fromId(id);
        if (language != null){
            return language;
        } else {
            spEditor.putInt(nLanguage, dLanguage.getId());
            return dLanguage;
        }
    }

    public void setLanguage(Language language) {
        spEditor.putInt(nLanguage, language.getId());
        spEditor.commit();
    }

    public Currency getCurrency() {
        int id = sharedPref.getInt(nCurrency, dCurrency.getId());
        Currency currency = Currency.fromId(id);
        if (currency != null){
            return currency;
        } else {
            spEditor.putInt(nCurrency, dCurrency.getId());
            return dCurrency;
        }
    }

    public void setCurrency(Currency currencyFormat) {
        spEditor.putInt(nCurrency, currencyFormat.getId());
        spEditor.commit();
    }

    public DatePattern getDatePattern() {
        int id = sharedPref.getInt(nDatePattern, dDatePattern.getId());
        DatePattern datePattern = DatePattern.fromId(id);
        if (datePattern != null){
            return datePattern;
        } else {
            spEditor.putInt(nDatePattern, dDatePattern.getId());
            return dDatePattern;
        }
    }

    public void setDatePattern(DatePattern datePattern) {
        spEditor.putInt(nDatePattern, datePattern.getId());
        spEditor.commit();
    }

    public Integer getMainDBVersion() {
        return sharedPref.getInt(nMainDBVersion, dMainDBVersion);
    }

    public void setMainDBVersion(Integer version) {
        spEditor.putInt(nMainDBVersion, version);
        spEditor.commit();
    }

    public Integer getRefDBVersion() {
        return sharedPref.getInt(nRefDBVersion, dRefDBVersion);
    }

    public void setRefDBVersion(Integer version) {
        spEditor.putInt(nRefDBVersion, version);
        spEditor.commit();
    }
}