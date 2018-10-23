/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:40 PM
 */

package com.davidlcassidy.travelwallet.EnumTypes;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

/*
ID Local Cache : UserPreferences
WARNING : Changing ID values may change user settings to the default value within current app installs.
 */

public enum Currency {

	// Enum members
	// Exchange rates pulled from Google Finance on 13 OCT 2018
    USD(1, "$", "US Dollar", 1.0),
    CAD(2, "$", "Canadian Dollar", 1.31),
    EUR(3, "€", "Euro", 0.87),
    GPB(4, "£", "British Pound", 0.77),
    AUD(5, "$", "Australian Dollar", 1.42),
    CNY(6, "元", "Chinese Yuan", 6.95),
    JPY(7, "¥", "Japanese Yen", 112.72),
    INR(8, "₹", "Indian Rupee", 73.56),
    MXN(9, "$", "Mexican Peso", 19.42);

	private final int id;
    private final String symbol;
    private final String name;
    private final BigDecimal exchangeRate;

    private Currency(int id, String symbol, String name, Double exchangeRate) {
		this.id = id;
        this.symbol = symbol;
        this.name = name;

        // Exchange rates are hardcoded to maintain offline ability. Must update regularly.
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
        for (Currency currency : Currency.values()) {
            if (currency.getId() == id) {return currency;}
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
        for (Currency currency : Currency.values()) {
            if (currency.getName().equals(name)) {return currency;}
        }
        return null;
    }

	// Returns the names of every currency
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList <String>();
        for (Currency currency : Currency.values()) {
            list.add(currency.getName());
        }
        return list;
    }
}