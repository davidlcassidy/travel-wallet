/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Enums;

import java.util.ArrayList;

/*
ID Local Cache : AppPreferences
WARNING : Changing ID values may change app settings to the default value within current app installs.
 */

public enum Country {

    // Enum members
    USA(1, "United States"),
    CANADA(2, "Canada"),
    OTHER(99, "Other");

    private final int id;
    private final String name;

    Country(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Returns country from ID
    public static Country fromId(int id) {
        for (Country country : Country.values()) {
            if (country.getId() == id) {
                return country;
            }
        }
        return null;
    }

    // Returns country from name
    public static Country fromName(String name) {
        for (Country country : Country.values()) {
            if (country.getName().equals(name)) {
                return country;
            }
        }
        return null;
    }

    // Returns the names of every country
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList<>();
        for (Country country : Country.values()) {
            list.add(country.getName());
        }
        return list;
    }

    // Returns country ID
    public int getId() {
        return id;
    }

    // Returns country name
    public String getName() {
        return name;
    }
}
