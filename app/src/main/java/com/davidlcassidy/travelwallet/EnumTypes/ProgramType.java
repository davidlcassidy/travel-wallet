/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:39 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

/*
ID Local Cache : RefDatabase
WARNING : Changing ID values requires updates to the database reference field.
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
        for (ProgramType programType : ProgramType.values()) {
            if (programType.getId() == id) {return programType;}
        }
        return null;
    }

    // Returns program type from name
    public static ProgramType fromName(String name) {
        for (ProgramType programType : ProgramType.values()) {
            if (programType.getName().equals(name)) {return programType;}
        }
        return null;
    }

    // Returns the names of every program type
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList <String>();
        for (ProgramType programType : ProgramType.values()) {
            list.add(programType.getName());
        }
        return list;
    }
}
