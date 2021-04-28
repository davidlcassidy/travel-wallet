/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Enums;

/*
ID Local Cache : MainDatabase
WARNING : Changing ID values requires updates to the database reference field.
 */

import java.util.ArrayList;

public enum CardStatus {

    // Enum members
    OPEN(1, "Open"),
    CLOSED(2, "Closed");

    private final int id;
    private final String name;

    CardStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Returns card status from ID
    public static CardStatus fromId(int id) {
        for (CardStatus cardStatus : CardStatus.values()) {
            if (cardStatus.getId() == id) {
                return cardStatus;
            }
        }
        return null;
    }

    // Returns card status from name
    public static CardStatus fromName(String name) {
        for (CardStatus cardStatus : CardStatus.values()) {
            if (cardStatus.getName().equals(name)) {
                return cardStatus;
            }
        }
        return null;
    }

    // Returns the names of every card status
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList<>();
        for (CardStatus cardStatus : CardStatus.values()) {
            list.add(cardStatus.getName());
        }
        return list;
    }

    // Returns card status ID
    public int getId() {
        return id;
    }

    // Returns card status name
    public String getName() {
        return name;
    }
}
