/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:40 PM
 */

package com.davidlcassidy.travelwallet.Classes;

import android.content.Context;

import com.davidlcassidy.travelwallet.EnumTypes.ItemType;

import java.util.Date;
import static java.lang.Math.round;

/*
Notification class is used connection with the NotificationListAdapter to display the
notifications for loyalty programs and credit cards in the NotificationFragment. It
also contains the method used to create instances of the PhoneNotification class.
 */

public class Notification {
    private ItemType itemType;
    private Integer id;
    private String icon;
    private Date date;
    private String header;
    private String message;

    public Notification(LoyaltyProgram program) {
        this.itemType = ItemType.LOYALTY_PROGRAM;
        this.id = program.getId();
        this.icon = program.getLogoIcon();
        this.date = program.getExpirationDate();
        this.header = program.getName() + " Program";

		// Generates notification text of time remaining until program points expiring
        Date today = new Date();
        int numOfDays = round((date.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
        if (numOfDays <= 0) {
            message = "Points have expired.";
        } else {
                message = "Points will expire in " + getDurationString(numOfDays) + ".";
        }
    }

    public Notification(CreditCard card) {
        this.itemType = ItemType.CREDIT_CARD;
        this.id = card.getId();
        this.icon = getBankLogoName(card);
        this.date = card.getAfDate();
        this.header = card.getName() + " Card";

		// Generates notification text of time remaining until card annual fee
        Date today = new Date();
        int numOfDays = round((date.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
        if (numOfDays <= 0) {
            message = "Annual fee is past due.";
        } else {
            message = "Annual fee is due in " + getDurationString(numOfDays) + ".";
        }
    }

    private String getBankLogoName (CreditCard card){ // TODO Change naming format
        String bankNoSpaces = card.getBank().replaceAll("\\s","").toLowerCase();
        String logoName = "bank_" + bankNoSpaces.substring(0, Math.min(bankNoSpaces.length(), 3)).toLowerCase() + "_icon";
        return logoName;
    }

    // Converts integer number to days to duration string
    private String getDurationString(int numOfDays) {
        int count;
        String unit;
        if (0 < numOfDays && numOfDays <= 13) {
            count = numOfDays;
            unit = "day";
        } else if (13 < numOfDays && numOfDays <= 30) {
            count = round(numOfDays / 7);
            unit = "week";
        } else {
            count = (int) round(numOfDays / 30.4);
            unit = "month";
        }
        String duration = String.valueOf(count) + " " + unit;

        // Handles plurals
        if (count > 1){
            duration = duration + "s";
        }

        return duration;
    }

	// Sends notification to device if configured in user settings
    public PhoneNotification sendPhoneNotification(Context context){
        UserPreferences userPreferences = UserPreferences.getInstance(context);
        if (userPreferences.getSetting_PhoneNotifications()) {
            PhoneNotification pn = new PhoneNotification(context, this);
            return pn;
        } else{
            return null;
        }
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


