/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Enums;

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
    private final DecimalFormatSymbols decimalSymbols;

    NumberPattern(int id, Character groupingSeparator, Character decimalSeparator) {
        this.id = id;

        this.decimalSymbols = new DecimalFormatSymbols(Locale.getDefault());
        this.decimalSymbols.setGroupingSeparator(groupingSeparator);
        this.decimalSymbols.setDecimalSeparator(decimalSeparator);
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
            return new DecimalFormat("#,##0.00", this.decimalSymbols);
        } else {
            return new DecimalFormat("#,###", this.decimalSymbols);
        }
    }
}