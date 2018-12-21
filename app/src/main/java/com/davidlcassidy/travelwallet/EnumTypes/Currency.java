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
    // Exchange rates are hardcoded to maintain offline ability. Must update regularly.
	// Exchange rates pulled from Google Finance on 13 OCT 2018
    AUD(1, "$", "L", "Australian Dollar", 1.41),
    BRL(2, "$", "L", "Brazilian Real", 3.87),
    CAD(3, "$", "L", "Canadian Dollar", 1.35),
    CNY(4, "元", "L", "Chinese Yuan", 6.90),
    EUR(5, "€", "R", "Euro", 0.88),
    INR(6, "₹", "L", "Indian Rupee", 70.08),
    JPY(7, "¥", "L", "Japanese Yen", 111.07),
    MXN(8, "$", "L", "Mexican Peso", 19.90),
    NZD(9, "$", "L", "New Zealand Dollar", 1.48),
    GPB(10, "£", "L", "Pound Sterling", 0.79),
    ZAR(11, "R", "L", "South African Rand", 14.35),
    KRW(12, "₩", "L", "South Korean Won", 1124.70),
    CHF(13, "Fr", "R", "Swiss Franc", 0.99),
    USD(0, "$", "L", "United States Dollar", 1.00);

	private final int id;
    private final String symbol;
    private final String symbolSide;
    private final String name;
    private final BigDecimal exchangeRate;

    private Currency(int id, String symbol, String symbolSide, String name, Double exchangeRate) {
		this.id = id;
        this.symbol = symbol;
        this.symbolSide = symbolSide;
        this.name = name;
        this.exchangeRate = BigDecimal.valueOf(exchangeRate);
    }

    // Converts number to specified currency
    public String numToString (BigDecimal value, NumberPattern numberPattern){

        String currencyString;

        // Sets number format and includes decimals for smaller currencies
        DecimalFormat format = null;
        if (this.exchangeRate.doubleValue() > 20.0){
            format = numberPattern.getNumberFormat();
        } else {
            format = numberPattern.getDecimalFormat();
        }
        currencyString = format.format(value.multiply(this.exchangeRate));

        // Adds currency symbol on correct side
        if (this.symbolSide.equals("L")){
            currencyString = this.symbol + " " + currencyString;
        } else if (this.symbolSide.equals("R")) {
            currencyString = currencyString + " " + this.symbol;
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