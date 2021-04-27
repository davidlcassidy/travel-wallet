/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Enums;

import android.content.Context;

import com.davidlcassidy.travelwallet.Classes.AppPreferences;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/*
ID Local Cache : None
 */

public enum NumberPattern {

    // Enum members
    COMMADOT(1, ',', '.'),
    DOTCOMMA(2, '.', ',');

    private final int id;
    private final DecimalFormat numberFormat;
    private final DecimalFormat decimalFormat;

    NumberPattern(int id, Character groupingSeparator, Character decimalSeparator) {
        this.id = id;

        DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(Locale.getDefault());
        decimalSymbols.setGroupingSeparator(groupingSeparator);
        decimalSymbols.setDecimalSeparator(decimalSeparator);

        this.numberFormat = new DecimalFormat("#,###", decimalSymbols);
        this.decimalFormat = new DecimalFormat("#,##0.00", decimalSymbols);
    }

    // Returns number pattern from ID
    public static NumberPattern fromId(int id) {
        for (NumberPattern numberPattern : NumberPattern.values()) {
            if (numberPattern.getId() == id) {
                return numberPattern;
            }
        }
        return null;
    }

    // Returns number pattern ID
    public int getId() {
        return id;
    }

    // Returns number format from number pattern
    public DecimalFormat getNumberFormat(boolean withDecimal) {
        if(withDecimal){
            return decimalFormat;
        } else {
            return numberFormat;
        }
    }
}