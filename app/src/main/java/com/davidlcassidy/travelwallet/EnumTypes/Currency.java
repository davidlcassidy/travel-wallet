/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/24/18 2:17 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

/*
ID Local Cache : UserPreferences

WARNING : Changing the id values may end backwards compatibility and can cause local
app instability within current app installs
 */

public enum Currency {

	// Enum members
	// Exchange rates pulled from Google Finance on 25 JULY 2018
    USD(1, "$", "US Dollar", 1.0),
    EUR(2, "€", "Euro", 0.85),
    GPB(3, "£", "British Pound", 0.76),
    CAD(4, "CA$", "Canadian Dollar", 1.31),
    AUD(5, "AU$", "Australian Dollar", 1.35),
    CNY(6, "元", "Chinese Yuan", 6.76),
    JPY(7, "¥", "Japanese Yen", 111.15),
    INR(8, "₹", "Indian Rupee", 68.71),
    MXN(9, "Mex$", "Mexican Peso", 18.81);

	private final int id;
    private final String symbol;
    private final String name;
    private final BigDecimal exchangeRate;

    private Currency(int id, String symbol, String name, Double exchangeRate) {
		this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.exchangeRate = BigDecimal.valueOf(exchangeRate);
    }

    // Converts number to specified currency
    public String numToString (BigDecimal value, NumberPattern numberPattern){

        String currencyString;

        // Sets number format and includes decimals for smaller currencies
        if (this.exchangeRate.doubleValue() > 15.0){
            DecimalFormat numberFormat = numberPattern.getNumberFormat();
            currencyString = numberFormat.format(value.multiply(this.exchangeRate));
        } else {
            DecimalFormat decimalFormat = numberPattern.getDecimalFormat();
            currencyString = decimalFormat.format(value.multiply(this.exchangeRate));
        }

        // Adds currency symbol - Euros on right side, all others on left
        if (this.symbol.equals("€")){
            currencyString = currencyString + " " + this.symbol;
        } else {
            currencyString = this.symbol + " " + currencyString;
        }
        return currencyString;
    }
	
	// Returns currency ID
    public int getId() {
        return id;
    }

	// Returns currency from ID
    public static Currency fromId(int id) {
        for (Currency c : Currency.values()) {
            if (c.getId() == id) {return c;}
        }
        return null;
    }

	// Returns currency symbol
    public String getSymbol() {
        return symbol;
    }

	// Returns currency name
    public String getName() {
        return name;
    }

	// Returns currency conversion rate
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    // Returns currency from name
    public static Currency fromName(String name) {
        for (Currency c : Currency.values()) {
            if (c.getName().equals(name)) {return c;}
        }
        return null;
    }

	// Returns the names of every currency
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList <String>();
        for (Currency c : Currency.values()) {
            list.add(c.getName());
        }
        return list;
    }
}