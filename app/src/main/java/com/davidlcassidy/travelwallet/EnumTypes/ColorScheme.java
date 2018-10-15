/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:39 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

import com.davidlcassidy.travelwallet.R;

import java.util.ArrayList;

/*
ID Local Cache : UserPreferences
WARNING : Changing ID values may change user settings to the default value within current app installs.
 */

public enum ColorScheme {

	// Enum members
    Blue(1, "Blue", R.style.Blue_Theme),
    Green(2, "Green", R.style.Green_Theme),
    Red(3, "Red", R.style.Red_Theme),
    Purple(4, "Purple", R.style.Purple_Theme),
    Orange(5, "Orange", R.style.Orange_Theme),
    Gold(99, "Gold (Pro Only)", R.style.Gold_Theme);

	private final int id;
    private final String name;
    private final int ResourceId;

    private ColorScheme(int id, String name, int ResourceId) {
		this.id = id;
        this.name = name;
        this.ResourceId = ResourceId;
    }
	
	// Returns color scheme ID
    public int getId() {
        return id;
    }

    // Returns color scheme name
    public String getName() {
        return name;
    }

    // Returns color scheme style resource ID
    public int getResourceId() {
        return ResourceId;
    }

	// Returns color scheme from ID
    public static ColorScheme fromId(int id) {
        for (ColorScheme colorScheme : ColorScheme.values()) {
            if (colorScheme.getId() == id) {return colorScheme;}
        }
        return null;
    }

    // Returns color scheme from name
    public static ColorScheme fromName(String name) {
        for (ColorScheme colorScheme : ColorScheme.values()) {
            if (colorScheme.getName().equals(name)) {return colorScheme;}
        }
        return null;
    }

    // Returns the names of every language
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList <String>();
        for (ColorScheme colorScheme : ColorScheme.values()) {
            list.add(colorScheme.getName());
        }
        return list;
    }

}
