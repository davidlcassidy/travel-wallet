/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/29/18 2:03 AM
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
            message = "Your points have expired.";
        }
        else if (0 < numOfDays && numOfDays <= 1) {
            message = "Your points will expire in 1 day.";
        }
        else if (1 < numOfDays && numOfDays <= 13) {
            message = "Your points will expire in " + String.valueOf(numOfDays) + " days.";
        }
        else if (13 < numOfDays && numOfDays <= 32) {
            message = "Your points will expire in " + String.valueOf(round(numOfDays/7)) + " weeks.";
        }
        else {
            message = "Your points will expire in " + String.valueOf(round(numOfDays/30.4)) + " months.";;
        }

    }

    public Notification(CreditCard card) {
        this.itemType = ItemType.CREDIT_CARD;
        this.id = card.getId();
        this.icon = card.getBank().getLogoIcon();
        this.date = card.getAfDate();
        this.header = card.getName() + " Card";

		// Generates notification text of time remaining until card annual fee
        Date today = new Date();
        int numOfDays = round((date.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
        if (numOfDays <= 0) {
            message = "Your annual fee is past due.";
        }
        else if (0 < numOfDays && numOfDays <= 1) {
            message = "Your annual fee is due in 1 day.";
        }
        else if (1 < numOfDays && numOfDays <= 32) {
            message = "Your annual fee is due in " + String.valueOf(numOfDays) + " days.";
        }
        else if (15 < numOfDays && numOfDays <= 32) {
            message = "Your annual fee is due in " + String.valueOf(round(numOfDays/7)) + " weeks.";
        }
        else {
            message = "Your annual fee is due in " + String.valueOf(round(numOfDays/30.4)) + " months.";;
        }

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


