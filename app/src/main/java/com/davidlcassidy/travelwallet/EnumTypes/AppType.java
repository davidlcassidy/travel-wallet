/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/24/18 2:17 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

/*
ID Local Cache : UserPreferences

WARNING : Changing the id values may end backwards compatibility and can cause local
app instability within current app installs
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
        for (AppType cs : AppType.values()) {
            if (cs.getId() == id) {return cs;}
        }
        return null;
    }
}
