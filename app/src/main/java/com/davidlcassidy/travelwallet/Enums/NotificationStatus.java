/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Enums;

/*
ID Local Cache : MainDatabase
WARNING : Changing the ID values may end backwards compatibility and can cause local
app instability within current app installs.
 */

public enum NotificationStatus {

    // Enum members
    UNMONITORED(-1),
    OFF(0),
    ON(1);

    private final int id;

    NotificationStatus(int id) {
        this.id = id;
    }

    // Returns notification status from ID
    public static NotificationStatus fromId(int id) {
        for (NotificationStatus status : NotificationStatus.values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        return null;
    }

    // Returns notification status ID
    public int getId() {
        return id;
    }
}