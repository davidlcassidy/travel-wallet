/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Enums;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/*
ID Local Cache : AppPreferences
WARNING : Changing ID values may change app settings to the default value within current app installs.
 */

public enum Currency {

    // Enum members
    // Exchange rates are hardcoded to maintain 100% offline capability. Must update regularly.
    // Exchange rates pulled from Google Finance on 26 APR 2021
    AUD(1, "Australian Dollar", "$", "L", NumberPattern.COMMADOT, 1.29),
    BRL(2, "Brazilian Real", "$", "L", NumberPattern.DOTCOMMA, 5.94),
    CAD(3, "Canadian Dollar", "$", "L", NumberPattern.COMMADOT, 1.25),
    CNY(4, "Chinese Yuan", "元", "L", NumberPattern.COMMADOT, 6.49),
    EUR(5, "Euro", "€", "R", NumberPattern.DOTCOMMA, 0.83),
    INR(6, "Indian Rupee", "₹", "L", NumberPattern.COMMADOT, 74.71),
    JPY(7, "Japanese Yen", "¥", "L", NumberPattern.COMMADOT, 107.77),
    MXN(8, "Mexican Peso", "$", "L", NumberPattern.COMMADOT, 19.83),
    NZD(9, "New Zealand Dollar", "$", "L", NumberPattern.COMMADOT, 1.39),
    NOK(10, "Norwegian Krone", "kr", "R", NumberPattern.DOTCOMMA, 8.28),
    GPB(11, "Pound Sterling", "£", "L", NumberPattern.COMMADOT, 0.72),
    RUB(12, "Russian Ruble", "₽", "R", NumberPattern.DOTCOMMA, 74.98),
    SAR(13, "Saudi Riyal", "SAR", "R", NumberPattern.DOTCOMMA, 3.75),
    SGD(14, "Singapore Dollar", "$", "L", NumberPattern.COMMADOT, 1.33),
    ZAR(15, "South African Rand", "R", "L", NumberPattern.COMMADOT, 14.27),
    KRW(16, "South Korean Won", "₩", "L", NumberPattern.COMMADOT, 1113.07),
    SEK(17, "Swedish Krona", "kr", "R", NumberPattern.DOTCOMMA, 8.38),
    CHF(18, "Swiss Franc", "Fr", "R", NumberPattern.DOTCOMMA, 0.91),
    USD(0, "United States Dollar", "$", "L", NumberPattern.COMMADOT, 1.00);

    private final int id;
    private final String name;
    private final String symbol;
    private final String symbolPosition;
    private final NumberPattern numberPattern;
    private final BigDecimal exchangeRate;

    Currency(int id, String name, String symbol, String symbolPosition, NumberPattern numberPattern, Double exchangeRate) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.symbolPosition = symbolPosition;
        this.numberPattern = numberPattern;
        this.exchangeRate = BigDecimal.valueOf(exchangeRate);
    }

    // Returns currency from ID
    public static Currency fromId(int id) {
        for (Currency currency : Currency.values()) {
            if (currency.getId() == id) {
                return currency;
            }
        }
        return null;
    }

    // Returns currency from name
    public static Currency fromName(String name) {
        for (Currency currency : Currency.values()) {
            if (currency.getName().equals(name)) {
                return currency;
            }
        }
        return null;
    }

    // Returns the names of every currency
    public static ArrayList<String> getAllNames() {
        ArrayList<String> list = new ArrayList<String>();
        for (Currency currency : Currency.values()) {
            list.add(currency.getName());
        }
        return list;
    }

    // Converts number to specified currency
    public String formatValue(BigDecimal value) {

        String currencyString;

        // Sets number format and includes decimals for smaller currencies
        boolean useDecimal = this.exchangeRate.doubleValue() < 20.0;
        currencyString = this.numberPattern.getNumberFormat(useDecimal).format(value.multiply(this.exchangeRate));

        // Adds currency symbol on correct position
        if (this.symbolPosition.equals("L")) {
            currencyString = this.symbol + " " + currencyString;
        } else if (this.symbolPosition.equals("R")) {
            currencyString = currencyString + " " + this.symbol;
        }
        return currencyString;
    }

    // Returns currency ID
    public int getId() {
        return id;
    }

    // Returns currency name
    public String getName() {
        return name;
    }

    // Returns number pattern
    public NumberPattern getNumberPattern() {
        return numberPattern;
    }

    // Returns currency conversion rate
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

}
