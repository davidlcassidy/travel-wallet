/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:40 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

/*
ID Local Cache : None
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
        for (ItemType itemType : ItemType.values()) {
            if (itemType.getId() == i) {return itemType;}
        }
        return null;
    }
}
