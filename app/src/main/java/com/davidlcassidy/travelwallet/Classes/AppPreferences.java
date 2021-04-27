/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.davidlcassidy.travelwallet.Enums.AppType;
import com.davidlcassidy.travelwallet.Enums.ColorScheme;
import com.davidlcassidy.travelwallet.Enums.Country;
import com.davidlcassidy.travelwallet.Enums.Currency;
import com.davidlcassidy.travelwallet.Enums.DatePattern;
import com.davidlcassidy.travelwallet.Enums.ItemField;
import com.davidlcassidy.travelwallet.Enums.Language;
import com.davidlcassidy.travelwallet.Enums.NumberPattern;

/*
AppPreferences class is used to read and write values to the shared user preferences. These are used
for filters, settings, and database versions which need to remain consistent across different sessions.
 */

public class AppPreferences {

    public static AppPreferences instance;
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor spEditor;


    // Sets app preference keys
    private static final String nAppPreferences = "AppPreferences";
    private static final String nAppType = "AppType";
    private static final String nFirstAppLaunch = "FirstAppLaunch";
    private static final String nProgramFiltersUpdateRequired = "ProgramFilterUpdateRequired";
    private static final String nCardFiltersUpdateRequired = "CardFilterUpdateRequired";

    private static final String nFilter_ProgramUser = "Filter_ProgramUser";
    private static final String nFilter_ProgramType = "Filter_ProgramType";
    private static final String nFilter_CardUser = "Filter_CardUser";
    private static final String nFilter_CardStatus = "Filter_CardStatus";

    private static final String nCustom_UserPrimaryField = "Custom_UserPrimaryField";
    private static final String nCustom_UserSortField = "Custom_UserSortField";
    private static final String nCustom_ProgramPrimaryField = "Custom_ProgramPrimaryField";
    private static final String nCustom_ProgramSortField = "Custom_ProgramSortField";
    private static final String nCustom_ProgramNotificationPeriod = "Custom_ProgramNotificationPeriod";
    private static final String nCustom_ProgramFilters = "Custom_ProgramFilters";
    private static final String nCustom_CardPrimaryField = "Custom_CardPrimaryField";
    private static final String nCustom_CardSortField = "Custom_CardSortField";
    private static final String nCustom_CardNotificationPeriod = "Custom_CardNotificationPeriod";
    private static final String nCustom_CardFilters = "Custom_CardFilters";

    private static final String nSetting_InitialSummary = "Setting_InitialSummary";
    private static final String nSetting_PhoneNotifications = "Setting_PhoneNotifications";
    private static final String nSetting_Country = "Setting_Country";
    private static final String nSetting_Language = "Setting_Language";
    private static final String nSetting_Currency = "Setting_Currency";
    private static final String nSetting_DatePattern = "Setting_DatePattern";
    private static final String nSetting_ColorScheme = "Setting_ColorScheme";

    private static final String nDatabase_MainDBVersion = "Database_MainDBVersion";
    private static final String nDatabase_RefDBVersion = "Database_RefDBVersion";

    // Sets user preference default values
    private static final AppType dAppType = AppType.FREE;
    private static final boolean dFirstAppLaunch = true;
    private static final boolean dProgramFiltersUpdateRequired = true;
    private static final boolean dCardFiltersUpdateRequired = true;

    private static final String dFilter_ProgramUser = "All User";
    private static final String dFilter_ProgramType = "All Types";
    private static final String dFilter_CardUser = "All User";
    private static final String dFilter_CardStatus = "All Statuses";

    private static final ItemField dCustom_UserPrimaryField = ItemField.ITEM_COUNTS;
    private static final ItemField dCustom_UserSortField = ItemField.USER_NAME;
    private static final ItemField dCustom_ProgramPrimaryField = ItemField.ACCOUNT_NUMBER;
    private static final ItemField dCustom_ProgramSortField = ItemField.PROGRAM_NAME;
    private static final String dCustom_ProgramNotificationPeriod = "4 W";
    private static final boolean dCustom_ProgramFilters = true;
    private static final ItemField dCustom_CardPrimaryField = ItemField.OPEN_DATE;
    private static final ItemField dCustom_CardSortField = ItemField.CARD_NAME;
    private static final String dCustom_CardNotificationPeriod = "4 W";
    private static final boolean dCustom_CardFilters = true;

    private static final boolean dSetting_InitialSummary = false;
    private static final boolean dSetting_PhoneNotifications = true;
    private static final Country dSetting_Country = Country.USA;
    private static final Language dSetting_Language = Language.ENGLISH;
    private static final Currency dSetting_Currency = Currency.USD;
    private static final DatePattern dSetting_DatePattern = DatePattern.MDY_LONG;
    private static final ColorScheme dSetting_ColorScheme = ColorScheme.Blue;

    private static final Integer dDatabase_MainDBVersion = 0;
    private static final Integer dDatabase_RefDBVersion = 0;

    // Private AppPreferences constructor called by getInstance method
    private AppPreferences(Context context) {
        sharedPref = context.getSharedPreferences(nAppPreferences, Context.MODE_PRIVATE);
        spEditor = sharedPref.edit();
    }

    // Gets instance of AppPreferences. Creates instance if it doesn't exist.
    public static AppPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreferences(context);
        }
        return instance;
    }

    public AppType getAppType() {
        int id = sharedPref.getInt(nAppType, dAppType.getId());
        AppType appType = AppType.fromId(id);
        if (appType != null) {
            return appType;
        } else {
            spEditor.putInt(nAppType, dAppType.getId());
            return dAppType;
        }
    }

    public void setAppType(AppType appType) {
        spEditor.putInt(nAppType, appType.getId());
        spEditor.commit();
    }

    public boolean getFirstAppLaunch() {
        int dFirstAppLaunchInt = (dFirstAppLaunch) ? 1 : 0;
        boolean firstAppLaunch = sharedPref.getInt(nFirstAppLaunch, dFirstAppLaunchInt) == 1;
        if (firstAppLaunch){
            // Update value after first app launch
            spEditor.putInt(nFirstAppLaunch, 0);
            spEditor.commit();
        }
        return firstAppLaunch;
    }

    public boolean getProgramFiltersUpdateRequired() {
        int dProgramFiltersUpdateRequiredInt = (dProgramFiltersUpdateRequired) ? 1 : 0;
        return sharedPref.getInt(nProgramFiltersUpdateRequired, dProgramFiltersUpdateRequiredInt) == 1;
    }

    public void setProgramFiltersUpdateRequired(boolean programFiltersUpdateRequired) {
        int programFiltersUpdateRequiredInt = (programFiltersUpdateRequired) ? 1 : 0;
        spEditor.putInt(nProgramFiltersUpdateRequired, programFiltersUpdateRequiredInt);
        spEditor.commit();
    }

    public boolean getCardFiltersUpdateRequired() {
        int dCardFiltersUpdateRequiredInt = (dCardFiltersUpdateRequired) ? 1 : 0;
        return sharedPref.getInt(nCardFiltersUpdateRequired, dCardFiltersUpdateRequiredInt) == 1;
    }

    public void setCardFiltersUpdateRequired(boolean cardFiltersUpdateRequired) {
        int cardFiltersUpdateRequiredInt = (cardFiltersUpdateRequired) ? 1 : 0;
        spEditor.putInt(nCardFiltersUpdateRequired, cardFiltersUpdateRequiredInt);
        spEditor.commit();
    }

    public void setFiltersUpdateRequired(boolean filtersUpdateRequired) {
        int filtersUpdateRequiredInt = (filtersUpdateRequired) ? 1 : 0;
        spEditor.putInt(nProgramFiltersUpdateRequired, filtersUpdateRequiredInt);
        spEditor.putInt(nCardFiltersUpdateRequired, filtersUpdateRequiredInt);
        spEditor.commit();
    }

    // Filter AppPreferences setters/getters
    public String getFilter_ProgramUser() {
        return sharedPref.getString(nFilter_ProgramUser, dFilter_ProgramUser);
    }

    public void setFilter_ProgramUser(String programUser) {
        spEditor.putString(nFilter_ProgramUser, programUser);
        spEditor.commit();
    }

    public String getFilter_ProgramType() {
        return sharedPref.getString(nFilter_ProgramType, dFilter_ProgramType);
    }

    public void setFilter_ProgramType(String programType) {
        spEditor.putString(nFilter_ProgramType, programType);
        spEditor.commit();
    }

    public String getFilter_CardUser() {
        return sharedPref.getString(nFilter_CardUser, dFilter_CardUser);
    }

    public void setFilter_CardUser(String cardUser) {
        spEditor.putString(nFilter_CardUser, cardUser);
        spEditor.commit();
    }

    public String getFilter_CardStatus() {
        return sharedPref.getString(nFilter_CardStatus, dFilter_CardStatus);
    }

    public void setFilter_CardStatus(String cardStatus) {
        spEditor.putString(nFilter_CardStatus, cardStatus);
        spEditor.commit();
    }

    public ItemField getCustom_UserPrimaryField() {
        int id = sharedPref.getInt(nCustom_UserPrimaryField, dCustom_UserPrimaryField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null) {
            return itemField;
        } else {
            spEditor.putInt(nCustom_UserPrimaryField, dCustom_UserPrimaryField.getId());
            return dCustom_UserPrimaryField;
        }
    }

    public void setCustom_UserPrimaryField(ItemField userPrimaryField) {
        spEditor.putInt(nCustom_UserPrimaryField, userPrimaryField.getId());
        spEditor.commit();
    }

    public ItemField getCustom_UserSortField() {
        int id = sharedPref.getInt(nCustom_UserSortField, dCustom_UserSortField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null) {
            return itemField;
        } else {
            spEditor.putInt(nCustom_UserSortField, dCustom_UserSortField.getId());
            return dCustom_UserSortField;
        }
    }

    public void setCustom_UserSortField(ItemField userSortField) {
        spEditor.putInt(nCustom_UserSortField, userSortField.getId());
        spEditor.commit();
    }

    public ItemField getCustom_ProgramPrimaryField() {
        int id = sharedPref.getInt(nCustom_ProgramPrimaryField, dCustom_ProgramPrimaryField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null) {
            return itemField;
        } else {
            spEditor.putInt(nCustom_ProgramPrimaryField, dCustom_ProgramPrimaryField.getId());
            return dCustom_ProgramPrimaryField;
        }
    }

    public void setCustom_ProgramPrimaryField(ItemField programPrimaryField) {
        spEditor.putInt(nCustom_ProgramPrimaryField, programPrimaryField.getId());
        spEditor.commit();
    }

    public ItemField getCustom_ProgramSortField() {
        int id = sharedPref.getInt(nCustom_ProgramSortField, dCustom_ProgramSortField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null) {
            return itemField;
        } else {
            spEditor.putInt(nCustom_ProgramSortField, dCustom_ProgramSortField.getId());
            return dCustom_ProgramSortField;
        }
    }

    public void setCustom_ProgramSortField(ItemField programSortField) {
        spEditor.putInt(nCustom_ProgramSortField, programSortField.getId());
        spEditor.commit();
    }

    public String getCustom_ProgramNotificationPeriod() {
        return sharedPref.getString(nCustom_ProgramNotificationPeriod, dCustom_ProgramNotificationPeriod);
    }

    public void setCustom_ProgramNotificationPeriod(String programNotificationPeriod) {
        spEditor.putString(nCustom_ProgramNotificationPeriod, programNotificationPeriod);
        spEditor.commit();
    }

    public boolean getCustom_ProgramFilters() {
        int dProgramFilters = (dCustom_ProgramFilters) ? 1 : 0;
        return sharedPref.getInt(nCustom_ProgramFilters, dProgramFilters) == 1;
    }

    public void setCustom_ProgramFilters(boolean programFilters) {
        int programFiltersInt = (programFilters) ? 1 : 0;
        spEditor.putInt(nCustom_ProgramFilters, programFiltersInt);
        spEditor.commit();
    }

    public ItemField getCustom_CardPrimaryField() {
        int id = sharedPref.getInt(nCustom_CardPrimaryField, dCustom_CardPrimaryField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null) {
            return itemField;
        } else {
            spEditor.putInt(nCustom_CardPrimaryField, dCustom_CardPrimaryField.getId());
            return dCustom_CardPrimaryField;
        }
    }

    public void setCustom_CardPrimaryField(ItemField cardPrimaryField) {
        spEditor.putInt(nCustom_CardPrimaryField, cardPrimaryField.getId());
        spEditor.commit();
    }

    public ItemField getCustom_CardSortField() {
        int id = sharedPref.getInt(nCustom_CardSortField, dCustom_CardSortField.getId());
        ItemField itemField = ItemField.fromId(id);
        if (itemField != null) {
            return itemField;
        } else {
            spEditor.putInt(nCustom_CardSortField, dCustom_CardSortField.getId());
            return dCustom_CardSortField;
        }
    }

    public void setCustom_CardSortField(ItemField cardSortField) {
        spEditor.putInt(nCustom_CardSortField, cardSortField.getId());
        spEditor.commit();
    }

    public String getCustom_CardNotificationPeriod() {
        return sharedPref.getString(nCustom_CardNotificationPeriod, dCustom_CardNotificationPeriod);
    }

    public void setCustom_CardNotificationPeriod(String cardNotificationPeriod) {
        spEditor.putString(nCustom_CardNotificationPeriod, cardNotificationPeriod);
        spEditor.commit();
    }

    public boolean getCustom_CardFilters() {
        int dCardFilters = (dCustom_CardFilters) ? 1 : 0;
        return sharedPref.getInt(nCustom_CardFilters, dCardFilters) == 1;
    }

    public void setCustom_CardFilters(boolean cardFilters) {
        int cardFiltersInt = (cardFilters) ? 1 : 0;
        spEditor.putInt(nCustom_CardFilters, cardFiltersInt);
        spEditor.commit();
    }

    public boolean getSetting_InitialSummary() {
        int dInitialSummary = (dSetting_InitialSummary) ? 1 : 0;
        return sharedPref.getInt(nSetting_InitialSummary, dInitialSummary) == 1;
    }

    public void setSetting_InitialSummary(boolean initialSummary) {
        int initialSummaryInt = (initialSummary) ? 1 : 0;
        spEditor.putInt(nSetting_InitialSummary, initialSummaryInt);
        spEditor.commit();
    }

    public boolean getSetting_PhoneNotifications() {
        int dPhoneNotifications = (dSetting_PhoneNotifications) ? 1 : 0;
        return sharedPref.getInt(nSetting_PhoneNotifications, dPhoneNotifications) == 1;
    }

    public void setSetting_PhoneNotifications(boolean phoneNotifications) {
        int phoneNotificationsInt = (phoneNotifications) ? 1 : 0;
        spEditor.putInt(nSetting_PhoneNotifications, phoneNotificationsInt);
        spEditor.commit();
    }

    public Country getSetting_Country() {
        int id = sharedPref.getInt(nSetting_Country, dSetting_Country.getId());
        Country country = Country.fromId(id);
        if (country != null) {
            return country;
        } else {
            spEditor.putInt(nSetting_Country, dSetting_Country.getId());
            return dSetting_Country;
        }
    }

    public void setSetting_Country(Country country) {
        spEditor.putInt(nSetting_Country, country.getId());
        spEditor.commit();
    }

    public Language getSetting_Language() {
        int id = sharedPref.getInt(nSetting_Language, dSetting_Language.getId());
        Language language = Language.fromId(id);
        if (language != null) {
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
        if (currency != null) {
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
        if (datePattern != null) {
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

    public ColorScheme getSetting_ColorScheme() {
        int id = sharedPref.getInt(nSetting_ColorScheme, dSetting_ColorScheme.getId());
        ColorScheme colorScheme = ColorScheme.fromId(id);
        if (colorScheme != null) {
            return colorScheme;
        } else {
            spEditor.putInt(nSetting_ColorScheme, dSetting_ColorScheme.getId());
            return dSetting_ColorScheme;
        }
    }

    public void setSetting_ColorScheme(ColorScheme colorScheme) {
        spEditor.putInt(nSetting_ColorScheme, colorScheme.getId());
        spEditor.commit();
    }


    // Database AppPreferences setters/getters
    public Integer getDatabase_MainDBVersion() {
        return sharedPref.getInt(nDatabase_MainDBVersion, dDatabase_MainDBVersion);
    }

    public void setDatabase_MainDBVersion(Integer version) {
        spEditor.putInt(nDatabase_MainDBVersion, version);
        spEditor.commit();
    }

    public Integer getDatabase_RefDBVersion() {
        return sharedPref.getInt(nDatabase_RefDBVersion, dDatabase_RefDBVersion);
    }

    public void setDatabase_RefDBVersion(Integer version) {
        spEditor.putInt(nDatabase_RefDBVersion, version);
        spEditor.commit();
    }
}