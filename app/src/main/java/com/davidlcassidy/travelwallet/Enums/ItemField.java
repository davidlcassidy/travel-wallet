/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Enums;

/*
ID Local Cache : AppPreferences
WARNING : Changing ID values may change app settings to the default value within current app installs.
 */

public enum ItemField {

    // Enum members
    TYPE(11, ItemType.LOYALTY_PROGRAM, "Type"),
    PROGRAM_NAME(12, ItemType.LOYALTY_PROGRAM, "Program Name"),
    ACCOUNT_NUMBER(13, ItemType.LOYALTY_PROGRAM, "Account Number"),
    POINTS(14, ItemType.LOYALTY_PROGRAM, "Points"),
    VALUE(15, ItemType.LOYALTY_PROGRAM, "Value"),
    EXPIRATION_DATE(16, ItemType.LOYALTY_PROGRAM, "Expiration Date"),
    PROGRAM_NOTES(17, ItemType.LOYALTY_PROGRAM, "Program Notes"),

    BANK(21, ItemType.CREDIT_CARD, "Bank"),
    CARD_NAME(22, ItemType.CREDIT_CARD, "Card Name"),
    OPEN_DATE(23, ItemType.CREDIT_CARD, "Open Date"),
    AF_DATE(24, ItemType.CREDIT_CARD, "Annual Fee Date"),
    ANNUAL_FEE(25, ItemType.CREDIT_CARD, "Annual Fee"),
    CARD_NOTES(26, ItemType.CREDIT_CARD, "Card Notes"),

    USER_NAME(31, ItemType.USER, "User Name"),
    ITEM_COUNTS(32, ItemType.USER, "Item Counts"),
    PROGRAMS_VALUE(33, ItemType.USER, "Programs Value"),
    CREDIT_LIMIT(34, ItemType.USER, "Credit Limit"),
    USER_NOTES(35, ItemType.USER, "User Notes");

    private final int id;
    private final ItemType itemType;
    private final String name;


    ItemField(int id, ItemType itemType, String name) {
        this.id = id;
        this.itemType = itemType;
        this.name = name;
    }

    // Returns item status from ID
    public static ItemField fromId(int id) {
        for (ItemField itemField : ItemField.values()) {
            if (itemField.getId() == id) {
                return itemField;
            }
        }
        return null;
    }

    // Returns item field from name
    public static ItemField fromName(String name) {
        for (ItemField itemField : ItemField.values()) {
            if (itemField.getName().equals(name)) {
                return itemField;
            }
        }
        return null;
    }

    // Returns item field ID
    public int getId() {
        return id;
    }

    // Returns item field name
    public String getName() {
        return name;
    }
}