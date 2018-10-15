/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:40 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

/*
ID Local Cache : UserPreferences
WARNING : Changing ID values may change user settings to the default value within current app installs.
 */

public enum AppType {

	// Enum members
    Free(1, "Free"),
    Pro(2, "Pro");

	private final int id;
    private final String text;

    private AppType(int id, String text) {
		this.id = id;
        this.text = text;
    }
	
	// Returns app type ID
    public int getId() {
        return id;
    }

	// Returns app type from ID
    public static AppType fromId(int id) {
        for (AppType at : AppType.values()) {
            if (at.getId() == id) {return at;}
        }
        return null;
    }
}
