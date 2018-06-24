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

public enum ItemType {
	
	// Enum members
    LOYALTY_PROGRAM(1, "Loyalty Program"),
    CREDIT_CARD(2, "Credit Card"),
    OWNER(3, "Owner");

    private final int id;
    private final String name;

    private ItemType(int id, String name) {
        this.id = id;
        this.name = name;
    }

	// Returns item type ID
    public int getId() {
        return id;
    }

    // Returns item type name
    public String getName() {
        return name;
    }

	// Returns item type from ID
    public static ItemType fromId(int i) {
        for (ItemType g : ItemType.values()) {
            if (g.getId() == i) {return g;}
        }
        return null;
    }
}
