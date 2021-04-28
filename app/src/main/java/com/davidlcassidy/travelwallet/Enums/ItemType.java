/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Enums;

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
