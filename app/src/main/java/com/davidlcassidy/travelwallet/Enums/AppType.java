/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Enums;

/*
ID Local Cache : AppPreferences
WARNING : Changing ID values may change app settings to the default value within current app installs.
 */

public enum AppType {

    // Enum members
    FREE(1, "Free"),
    PRO(2, "Pro");

    private final int id;
    private final String text;

    AppType(int id, String text) {
        this.id = id;
        this.text = text;
    }

    // Returns app type from ID
    public static AppType fromId(int id) {
        for (AppType at : AppType.values()) {
            if (at.getId() == id) {
                return at;
            }
        }
        return null;
    }

    // Returns app type ID
    public int getId() {
        return id;
    }
}
