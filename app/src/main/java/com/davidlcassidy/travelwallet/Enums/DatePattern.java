/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Enums;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/*
ID Local Cache : AppPreferences
WARNING : Changing ID values may change app settings to the default value within current app installs.
 */

public enum DatePattern {

    // Enum members
    DMY_LONG(1, "dd MMMM yyyy", "31 January 2005"),
    DMY_MED(2, "dd MMM yyyy", "31 Jan 2005"),
    DMY_SHORT(3, "dd/MM/yyyy", "31/01/2005"),
    MDY_LONG(4, "MMMM dd, yyyy", "January 31, 2005"),
    MDY_MED(5, "MMM dd, yyyy", "Jan 31, 2005"),
    MDY_SHORT(6, "MM/dd/yyyy", "01/31/2005"),
    YMD_SHORT(7, "yyyy-MM-dd", "2005-01-31");

    private final int id;
    private final String sampleDate;
    private final SimpleDateFormat dateFormat;

    DatePattern(int id, String formatString, String sampleDate) {
        this.id = id;
        this.sampleDate = sampleDate;
        this.dateFormat = new SimpleDateFormat(formatString);
    }

    // Returns date pattern from ID
    public static DatePattern fromId(int id) {
        for (DatePattern datePattern : DatePattern.values()) {
            if (datePattern.getId() == id) {
                return datePattern;
            }
        }
        return null;
    }

    // Returns date pattern from sample date
    public static DatePattern fromSampleDate(String sampleDate) {
        for (DatePattern datePattern : DatePattern.values()) {
            if (datePattern.getSampleDate().equals(sampleDate)) {
                return datePattern;
            }
        }
        return null;
    }

    // Returns sample dates of every date pattern
    public static ArrayList<String> getAllSampleDates() {
        ArrayList<String> list = new ArrayList<>();
        for (DatePattern datePattern : DatePattern.values()) {
            list.add(datePattern.getSampleDate());
        }
        return list;
    }

    // Returns date pattern ID
    public int getId() {
        return id;
    }

    // Returns date pattern sample
    public String getSampleDate() {
        return sampleDate;
    }

    // Returns date pattern as SimpleDateFormat
    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}