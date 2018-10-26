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
    AUD(11, "$", "L", "Australian Dollar", 1.42),
    BRL(12, "$", "L", "Brazilian Real", 3.71),
    CAD(13, "$", "L", "Canadian Dollar", 1.31),
    CNY(14, "元", "L", "Chinese Yuan", 6.96),
    EUR(15, "€", "R", "Euro", 0.88),
    INR(16, "₹", "L", "Indian Rupee", 73.43),
    JPY(17, "¥", "L", "Japanese Yen", 112.17),
    MXN(18, "$", "L", "Mexican Peso", 19.54),
    NZD(19, "$", "L", "New Zealand Dollar", 1.54),
    GPB(20, "£", "L", "Pound Sterling", 0.78),
    ZAR(21, "R", "L", "South African Rand", 14.67),
    KRW(22, "₩", "L", "South Korean Won", 1143.76),
    CHF(23, "Fr", "R", "Swiss Franc", 1.00),
    USD(00, "$", "L", "United States Dollar", 1.00);

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
        if (this.exchangeRate.doubleValue() > 15.0){
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