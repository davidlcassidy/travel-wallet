package com.davidlcassidy.travelwallet.Classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.davidlcassidy.travelwallet.EnumTypes.AppType;
import com.davidlcassidy.travelwallet.EnumTypes.DatePattern;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.Language;

/*
UserPreferences class is used to read and write values to the shared user preferences. These are used
for filters, settings, and database versions which need to remain consistent across different sessions.
 */

public class UserPreferences {

    public static UserPreferences instance;
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor spEditor;


    // Sets user preference keys
    private static String nUserPreferences = "UserPreferences";
    private static String nAppType = "AppType";

    private static String nFilter_ProgramOwner = "Filter_ProgramOwner";
    private static String nFilter_ProgramType = "Filter_ProgramType";
    private static String nFilter_CardOwner = "Filter_CardOwner";
    private static String nFilter_CardStatus = "Filter_CardStatus";


    private static String nSetting_ProgramNotificationPeriod = "Setting_ProgramNotificationPeriod";
    private static String nSetting_ProgramPrimaryField = "Setting_ProgramPrimaryField";
    private static String nSetting_ProgramSortField = "Setting_ProgramSortField";
    private static String nSetting_CardNotificationPeriod = "Setting_CardNotificationPeriod";
    private static String nSetting_CardPrimaryField = "Setting_CardPrimaryField";
    private static String nSetting_CardSortField = "Setting_CardSortField";
    private static String nSetting_OwnerPrimaryField = "Setting_OwnerPrimaryField";
    private static String nSetting_OwnerSortField = "Setting_OwnerSortField";
    private static String nSetting_InitialSummary = "Setting_InitialSummary";
    private static String nSetting_PhoneNotifications = "Setting_PhoneNotifications";
    private static String nSetting_Language = "Setting_Language";
    private static String nSetting_Currency = "Setting_Currency";
    private static String nSetting_DatePattern = "Setting_DatePattern";

    private static String nDatabase_MainDBVersion = "Database_MainDBVersion";
    private static String nDatabase_RefDBVersion = "Database_RefDBVersion";


    // Sets user preference default values
    private static AppType dAppType = AppType.Lite;

    private static String dFilter_ProgramOwner = "All Owners";
    private static String dFilter_ProgramType = "All Types";
    private static String dFilter_CardOwner = "All Owners";
    private static String dFilter_CardStatus = "All Statuses";

    private static String dSetting_ProgramNotificationPeriod = "4 W";
    private static ItemField dSetting_ProgramPrimaryField = ItemField.ACCOUNTNUMBER;
    private static ItemField dSetting_ProgramSortField = ItemField.PROGRAMNAME;
    private static String dSetting_CardNotificationPeriod = "4 W";
    private static ItemField dSetting_CardPrimaryField = ItemField.OPENDATE;
    private static ItemField dSetting_CardSortField = ItemField.CARDNAME;
    private static ItemField dSetting_OwnerPrimaryField = ItemField.ITEMCOUNTS;
    private static ItemField dSetting_OwnerSortField = ItemField.OWNERNAME;
    private static boolean dSetting_InitialSummary = false;
    private static boolean dSetting_PhoneNotifications = true;
    private static Language dSetting_Language = Language.ENGLISH;
    private static Currency dSetting_Currency = Currency.USD;
    private static DatePattern dSetting_DatePattern = DatePattern.MDY_LONG;

    private static Integer dDatabase_MainDBVersion = 0;
    private static Integer dDatabase_RefDBVersion = 0;


    // Gets instance of UserPreferences. Creates instance if it doesn't exist.
    public static UserPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new UserPreferences(context);
        }
        return instance;
    }


    // Private UserPreferences constructor called by getInstance method
    private UserPreferences(Context context) {
        sharedPref = context.getSharedPreferences(nUserPreferences, Context.MODE_PRIVATE);
        spEditor = sharedPref.edit();
    }

    // AppType UserPreference getter
    public AppType getAppType() {
        return dAppType;
    }

    // Filter UserPreferences setters/getters
    public String getFilter_ProgramOwner() {
        return sharedPref.getString(nFilter_ProgramOwner, dFilter_ProgramOwner);
    }

    public void setFilter_ProgramOwner(String programOwner) {
        spEditor.putString(nFilter_ProgramOwner, programOwner);
        spEditor.commit();
    }

    public String getFilter_ProgramType() {
        return sharedPref.getString(nFilter_ProgramType, dFilter_ProgramType);
    }

    public void setFilter_ProgramType(String programType) {
        spEditor.putString(nFilter_ProgramType, programType);
        spEditor.commit();
    }

    public String getFilter_CardOwner() {
        return sharedPref.getString(nFilter_CardOwner, dFilter_CardOwner);
    }

    public void setFilter_CardOwner(String cardOwner) {
        spEditor.putString(nFilter_CardOwner, cardOwner);
        spEditor.commit();
    }

    public String getFilter_CardStatus() {
        return sharedPref.getString(nFilter_CardStatus, dFilter_CardStatus);
    }

    public void setFilter_CardStatus(String cardStatus) {
        spEditor.putString(nFilter_CardStatus, cardStatus);
        spEditor.commit();
    }

    // Setting UserPreferences setters/getters
    public String getSetting_ProgramNotificationPeriod() {
        return sharedPref.getString(nSetting_ProgramNotificationPeriod, dSetting_ProgramNotificationPeriod);
    }

    public void setSetting_ProgramNotificationPeriod(String programNotificationPeriod) {
        spEditor.putString(nSetting_ProgramNotificationPeriod, programNotificationPeriod);
        spEditor.commit();
    }

    public ItemField getSetting_ProgramPrimaryField() {
        int id = sharedPref.getInt(nSetting_ProgramPrimaryField, dSetting_ProgramPrimaryField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null){
            return itemField;
        } else {
            spEditor.putInt(nSetting_ProgramPrimaryField, dSetting_ProgramPrimaryField.getId());
            return dSetting_ProgramPrimaryField;
        }
    }

    public void setSetting_ProgramPrimaryField(ItemField programPrimaryField) {
        spEditor.putInt(nSetting_ProgramPrimaryField, programPrimaryField.getId());
        spEditor.commit();
    }

    public ItemField getSetting_ProgramSortField() {
        int id = sharedPref.getInt(nSetting_ProgramSortField, dSetting_ProgramSortField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null){
            return itemField;
        } else {
            spEditor.putInt(nSetting_ProgramSortField, dSetting_ProgramSortField.getId());
            return dSetting_ProgramSortField;
        }
    }

    public void setSetting_ProgramSortField(ItemField programSortField) {
        spEditor.putInt(nSetting_ProgramSortField, programSortField.getId());
        spEditor.commit();
    }

    public String getSetting_CardNotificationPeriod() {
        return sharedPref.getString(nSetting_CardNotificationPeriod, dSetting_CardNotificationPeriod);
    }

    public void setSetting_CardNotificationPeriod(String cardNotificationPeriod) {
        spEditor.putString(nSetting_CardNotificationPeriod, cardNotificationPeriod);
        spEditor.commit();
    }

    public ItemField getSetting_CardPrimaryField() {
        int id = sharedPref.getInt(nSetting_CardPrimaryField, dSetting_CardPrimaryField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null){
            return itemField;
        } else {
            spEditor.putInt(nSetting_CardPrimaryField, dSetting_CardPrimaryField.getId());
            return dSetting_CardPrimaryField;
        }
    }

    public void setSetting_CardPrimaryField(ItemField cardPrimaryField) {
        spEditor.putInt(nSetting_CardPrimaryField, cardPrimaryField.getId());
        spEditor.commit();
    }

    public ItemField getSetting_CardSortField() {
        int id = sharedPref.getInt(nSetting_CardSortField, dSetting_CardSortField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null){
            return itemField;
        } else {
            spEditor.putInt(nSetting_CardSortField, dSetting_CardSortField.getId());
            return dSetting_CardSortField;
        }
    }

    public void setSetting_CardSortField(ItemField cardSortField) {
        spEditor.putInt(nSetting_CardSortField, cardSortField.getId());
        spEditor.commit();
    }

    public ItemField getSetting_OwnerPrimaryField() {
        int id = sharedPref.getInt(nSetting_OwnerPrimaryField, dSetting_OwnerPrimaryField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null){
            return itemField;
        } else {
            spEditor.putInt(nSetting_OwnerPrimaryField, dSetting_OwnerPrimaryField.getId());
            return dSetting_OwnerPrimaryField;
        }
    }

    public void setSetting_OwnerPrimaryField(ItemField ownerPrimaryField) {
        spEditor.putInt(nSetting_OwnerPrimaryField, ownerPrimaryField.getId());
        spEditor.commit();
    }

    public ItemField getSetting_OwnerSortField() {
        int id = sharedPref.getInt(nSetting_OwnerSortField, dSetting_OwnerSortField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null){
            return itemField;
        } else {
            spEditor.putInt(nSetting_OwnerSortField, dSetting_OwnerSortField.getId());
            return dSetting_OwnerSortField;
        }
    }

    public void setSetting_OwnerSortField(ItemField OwnerSortField) {
        spEditor.putInt(nSetting_OwnerSortField, OwnerSortField.getId());
        spEditor.commit();
    }

    public boolean getSetting_InitialSummary() {
        int initSummary = (dSetting_InitialSummary) ? 1 : 0;
        return sharedPref.getInt(nSetting_InitialSummary, initSummary) == 1;
    }

    public void setSetting_InitialSummary(boolean initialSummary) {
        int initSummary = (initialSummary) ? 1 : 0;
        spEditor.putInt(nSetting_InitialSummary, initSummary);
        spEditor.commit();
    }

    public boolean getSetting_PhoneNotifications() {
        int phoneNotifications = (dSetting_PhoneNotifications) ? 1 : 0;
        return sharedPref.getInt(nSetting_PhoneNotifications, phoneNotifications) == 1;
    }

    public void setSetting_PhoneNotifications(boolean phoneNotifications) {
        int pn = (phoneNotifications) ? 1 : 0;
        spEditor.putInt(nSetting_PhoneNotifications, pn);
        spEditor.commit();
    }

    public Language getSetting_Language() {
        int id = sharedPref.getInt(nSetting_Language, dSetting_Language.getId());
        Language language = Language.fromId(id);
        if (language != null){
            return language;
        } else {
            spEditor.putInt(nSetting_Language, dSetting_Language.getId());
            return dSetting_Language;
        }
    }

    public void setSetting_Language(Language language) {
        spEditor.putInt(nSetting_Language, language.getId());
        spEditor.commit();
    }

    public Currency getSetting_Currency() {
        int id = sharedPref.getInt(nSetting_Currency, dSetting_Currency.getId());
        Currency currency = Currency.fromId(id);
        if (currency != null){
            return currency;
        } else {
            spEditor.putInt(nSetting_Currency, dSetting_Currency.getId());
            return dSetting_Currency;
        }
    }

    public void setSetting_Currency(Currency currencyFormat) {
        spEditor.putInt(nSetting_Currency, currencyFormat.getId());
        spEditor.commit();
    }

    public DatePattern getSetting_DatePattern() {
        int id = sharedPref.getInt(nSetting_DatePattern, dSetting_DatePattern.getId());
        DatePattern datePattern = DatePattern.fromId(id);
        if (datePattern != null){
            return datePattern;
        } else {
            spEditor.putInt(nSetting_DatePattern, dSetting_DatePattern.getId());
            return dSetting_DatePattern;
        }
    }

    public void setSetting_DatePattern(DatePattern datePattern) {
        spEditor.putInt(nSetting_DatePattern, datePattern.getId());
        spEditor.commit();
    }


    // Database UserPreferences setters/getters
    public Integer getDatabase_MainDBVersion() {
        // return sharedPref.getInt(nDatabase_MainDBVersion, dDatabase_MainDBVersion);

        // Code below added to backward compatibility with old naming convention.
        // This can be removed after a couple versions.
        Integer mainVersion = sharedPref.getInt(nDatabase_MainDBVersion, dDatabase_MainDBVersion);
        if (mainVersion == 0){
            String nOldDatabase_MainDBVersion = "MainDBVersion";
            mainVersion = sharedPref.getInt(nOldDatabase_MainDBVersion, dDatabase_MainDBVersion);
            setDatabase_MainDBVersion(mainVersion);
        }
        return mainVersion;
    }

    public void setDatabase_MainDBVersion(Integer version) {
        spEditor.putInt(nDatabase_MainDBVersion, version);
        spEditor.commit();
    }

    public Integer getDatabase_RefDBVersion() {
        // return sharedPref.getInt(nDatabase_RefDBVersion, dDatabase_RefDBVersion);

        // Code below added to backward compatibility with old naming convention.
        // This can be removed after a couple versions.
        Integer refVersion = sharedPref.getInt(nDatabase_RefDBVersion, dDatabase_RefDBVersion);
        if (refVersion == 0){
            String nOldDatabase_RefDBVersion = "RefDBVersion";
            refVersion = sharedPref.getInt(nOldDatabase_RefDBVersion, dDatabase_RefDBVersion);
            setDatabase_RefDBVersion(refVersion);
        }
        return refVersion;
    }

    public void setDatabase_RefDBVersion(Integer version) {
        spEditor.putInt(nDatabase_RefDBVersion, version);
        spEditor.commit();
    }
}