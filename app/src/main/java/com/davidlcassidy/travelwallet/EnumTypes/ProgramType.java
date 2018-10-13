/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/29/18 2:17 AM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

/*
ID Local Cache : MainDatabase

WARNING : Changing the id values may end backwards compatibility and can cause local
app instability within current app installs
 */

import java.util.ArrayList;

public enum ProgramType {

	// Enum members
    AIRLINE(1, "Airline"),
    CREDITCARD(2, "Credit Card"),
    HOTEL(3, "Hotel"),
    RAILWAY(4, "Railway"),
    RENTALCAR(5, "Rental Car"),
    STORE(6, "Store");

	private final int id;
    private final String name;

    private ProgramType(int id, String name) {
		this.id = id;
        this.name = name;
    }
	
	// Returns program type ID
    public int getId() {
        return id;
    }

    // Returns program type name
    public String getName() {
        return name;
    }

	// Returns program type from ID
    public static ProgramType fromId(int id) {
        for (ProgramType cs : ProgramType.values()) {
            if (cs.getId() == id) {return cs;}
        }
        return null;
    }

    // Returns program type from name
    public static ProgramType fromName(String name) {
        for (ProgramType cs : ProgramType.values()) {
            if (cs.getName().equals(name)) {return cs;}
        }
        return null;
    }

    // Returns the names of every program type
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList <String>();
        for (ProgramType cs : ProgramType.values()) {
            list.add(cs.getName());
        }
        return list;
    }
}
