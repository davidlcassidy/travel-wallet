package com.davidlcassidy.travelwallet.EnumTypes;

import java.util.ArrayList;

/*
ID Local Cache : UserPreferences

WARNING : Changing the id values may end backwards compatibility and can cause local
app instability within current app installs
 */

public enum Language {

	// Enum members
    ENGLISH(1, "EN", "English");

	private final int id; 
    private final String code;
    private final String name;

    private Language(int id, String code, String name) {
		this.id = id;
        this.code = code;
        this.name = name;
    }
	
	// Returns language ID
    public int getId() {
        return id;
    }

	// Returns language name
    public String getName() {
        return name;
    }

    // Returns language from ID
    public static Language fromId(int id) {
        for (Language l : Language.values()) {
            if (l.getId() == id) {return l;}
        }
        return null;
    }

	// Returns language from name
    public static Language fromName(String name) {
        for (Language l : Language.values()) {
            if (l.getName().equals(name)) {return l;}
        }
        return null;
    }

	// Returns the names of every language
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList <String>();
        for (Language l : Language.values()) {
            list.add(l.getName());
        }
        return list;
    }
}
