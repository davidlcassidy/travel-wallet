/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:29 PM
 */

/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:40 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/*
ID Local Cache : None
 */

public enum NumberPattern {
	
	// Enum members
    COMMADOT(1, ",.", "1,234.00"),
    DOTCOMMA(2, ".,", "1.234,00");

	private final int id;
    private final String symbols;
    private final String sampleNumber;
    private final DecimalFormat numberFormat;
    private final DecimalFormat decimalFormat;

    private NumberPattern(int id, String symbols, String sampleNumber) {
		this.id = id;
        this.symbols = symbols;
        this.sampleNumber = sampleNumber;
        
        DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(Locale.getDefault());
        decimalSymbols.setGroupingSeparator(symbols.charAt(0));
        decimalSymbols.setDecimalSeparator(symbols.charAt(1));

        this.numberFormat = new DecimalFormat("#,###", decimalSymbols);
        this.decimalFormat = new DecimalFormat("#,##0.00", decimalSymbols);
    }
	
	// Returns number pattern ID
    public int getId() {
        return id;
    }

    // Returns number format from number pattern
    public DecimalFormat getNumberFormat() {return numberFormat;}

    // Returns decimal format from number pattern
    public DecimalFormat getDecimalFormat() {return decimalFormat;}

    // Returns number pattern from ID
    public static NumberPattern fromId(int id) {
        for (NumberPattern numberPattern : NumberPattern.values()) {
            if (numberPattern.getId() == id) {return numberPattern;}
        }
        return null;
    }
}