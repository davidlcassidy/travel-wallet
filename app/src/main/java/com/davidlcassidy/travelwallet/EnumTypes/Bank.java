/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/24/18 2:17 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

/*
ID Local Cache : MainDatabase

WARNING : Changing the id values may end backwards compatibility and can cause local
app instability within current app installs
 */

import java.util.ArrayList;

public enum Bank {

	// Enum members
    AMERICANEXPRESS(1, "American Express"),
    BANKOFAMERICA(2, "BOA"),
    BARCLAYS(3, "Barclays"),
    CAPITALONE(4, "Capital One"),
    CHASE(5, "Chase"),
    CITI(6, "Citi"),
    DISCOVER(7, "Discover"),
    USBANK(8, "US Bank"),
    WELLSFARGO(9, "Wells Fargo");

	private final int id;
    private final String name;
    private final String logoIcon;

    private Bank(int id, String name) {
		this.id = id;
        this.name = name;

        // Generates standard icon name from bank
        String bankIdString = String.format("%03d", id);
        this.logoIcon = new StringBuilder("bank_").append(bankIdString).append("_icon").toString();
    }
	
	// Returns bank ID
    public int getId() {
        return id;
    }

    // Returns bank name
    public String getName() {
        return name;
    }

    // Returns bank name
    public String getLogoIcon() {
        return logoIcon;
    }

	// Returns bank from ID
    public static Bank fromId(int id) {
        for (Bank cs : Bank.values()) {
            if (cs.getId() == id) {return cs;}
        }
        return null;
    }

    // Returns bank from name
    public static Bank fromName(String name) {
        for (Bank cs : Bank.values()) {
            if (cs.getName().equals(name)) {return cs;}
        }
        return null;
    }

    // Returns the names of every bank
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList <String>();
        for (Bank cs : Bank.values()) {
            list.add(cs.getName());
        }
        return list;
    }
}
