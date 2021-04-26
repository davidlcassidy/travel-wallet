/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

/*
ID Local Cache : None
 */

public enum ItemType {

    // Enum members
    USER(1, "User"),
    LOYALTY_PROGRAM(2, "Loyalty Program"),
    CREDIT_CARD(3, "Credit Card");


    private final int id;
    private final String name;

    ItemType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Returns item type from ID
    public static ItemType fromId(int i) {
        for (ItemType itemType : ItemType.values()) {
            if (itemType.getId() == i) {
                return itemType;
            }
        }
        return null;
    }

    // Returns item type ID
    public int getId() {
        return id;
    }

    // Returns item type name
    public String getName() {
        return name;
    }
}
