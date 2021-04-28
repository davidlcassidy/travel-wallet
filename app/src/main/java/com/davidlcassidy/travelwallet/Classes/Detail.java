/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Classes;

/*
Detail class is created by both the ProgramDetailActivity and the CardDetailActivity.
It is used in connection with the DetailListAdapter to display the details for these
two activities.
 */

public class Detail {
    private Integer id;
    private String name;
    private String value;

    public Detail(String name, String value) {
        this.id = null;
        this.name = name;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
