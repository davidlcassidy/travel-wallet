package com.davidlcassidy.travelwallet.EnumTypes;

/*
ID Local Cache : UserPreferences

WARNING : Changing the id values may end backwards compatibility and can cause local
app instability within current app installs
 */

import java.util.ArrayList;

public enum CardStatus {

	// Enum members
    OPEN(1, "Open"),
    CLOSED(2, "Closed");

	private final int id;
    private final String name;

    private CardStatus(int id, String name) {
		this.id = id;
        this.name = name;
    }
	
	// Returns card status ID
    public int getId() {
        return id;
    }

    // Returns card status name
    public String getName() {
        return name;
    }

	// Returns card status from ID
    public static CardStatus fromId(int id) {
        for (CardStatus cs : CardStatus.values()) {
            if (cs.getId() == id) {return cs;}
        }
        return null;
    }

    // Returns card status from name
    public static CardStatus fromName(String name) {
        for (CardStatus cs : CardStatus.values()) {
            if (cs.getName().equals(name)) {return cs;}
        }
        return null;
    }

    // Returns the names of every card status
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList <String>();
        for (CardStatus cs : CardStatus.values()) {
            list.add(cs.getName());
        }
        return list;
    }
}
