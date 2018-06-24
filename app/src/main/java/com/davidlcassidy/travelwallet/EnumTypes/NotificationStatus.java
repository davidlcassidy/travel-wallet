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

public enum NotificationStatus {

	// Enum members
	UNMONITORED(-1),
    OFF(0),
	ON(1);

    private final int id;

    private NotificationStatus(int id) {
        this.id = id;
    }

	// Returns notification status ID
    public int getId() {
        return id;
    }

	// Returns notification status from ID
    public static NotificationStatus fromId(int id) {
        for (NotificationStatus ns : NotificationStatus.values()) {
            if (ns.getId() == id) {return ns;}
        }
        return null;
    }
}