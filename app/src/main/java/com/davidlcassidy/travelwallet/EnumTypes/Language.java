/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

import java.util.ArrayList;

/*
ID Local Cache : AppPreferences
WARNING : Changing ID values may change app settings to the default value within current app installs.
 */

public enum Language {

    // Enum members
    ENGLISH(1, "EN", "English");

    private final int id;
    private final String code;
    private final String name;

    Language(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    // Returns language from ID
    public static Language fromId(int id) {
        for (Language language : Language.values()) {
            if (language.getId() == id) {
                return language;
            }
        }
        return null;
    }

    // Returns language from name
    public static Language fromName(String name) {
        for (Language language : Language.values()) {
            if (language.getName().equals(name)) {
                return language;
            }
        }
        return null;
    }

    // Returns the names of every language
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList<String>();
        for (Language language : Language.values()) {
            list.add(language.getName());
        }
        return list;
    }

    // Returns language ID
    public int getId() {
        return id;
    }

    // Returns language name
    public String getName() {
        return name;
    }
}
