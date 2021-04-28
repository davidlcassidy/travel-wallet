/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Classes;

/*
User class is created by UserDataSource and contains the data for a user
from the MainDatabase.
 */

import com.davidlcassidy.travelwallet.Enums.CardStatus;
import com.davidlcassidy.travelwallet.Enums.Country;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.MONTH;

public class User {

    private Integer id;
    private String name;
    private Integer programCount;
    private Integer cardCount;
    private BigDecimal totalProgramValue;
    private BigDecimal totalAF;
    private BigDecimal creditLimit;
    private String boa234Status;
    private String boa234EligibilityDate;
    private String capitolOne16Status;
    private String capitolOne16EligibilityDate;
    private String chase524Status;
    private String chase524EligibilityDate;
    private String notes;

    public User(Integer id, String name, String notes) {
        this.id = id;
        this.name = name;
        this.notes = notes;
    }

    public void setValues(ArrayList<LoyaltyProgram> userPrograms, ArrayList<CreditCard> userCards, SimpleDateFormat dateFormat) {

        // Set item counts
        this.programCount = userPrograms.size();
        this.cardCount = userCards.size();

        // Set total program value
        BigDecimal totalProgramValue = BigDecimal.valueOf(0);
        for (LoyaltyProgram lp : userPrograms) {
            totalProgramValue = totalProgramValue.add(lp.getTotalValue());
        }
        this.totalProgramValue = totalProgramValue;

        BigDecimal totalAF = BigDecimal.valueOf(0);
        BigDecimal totalCL = BigDecimal.valueOf(0);
        List<Date> boa2Dates = new ArrayList<>();
        List<Date> boa3Dates = new ArrayList<>();
        List<Date> boa4Dates = new ArrayList<>();
        List<Date> capitolOne16Dates = new ArrayList<>();
        List<Date> chase524Dates = new ArrayList<>();
        Calendar boa2CutoffDate = Calendar.getInstance();
        boa2CutoffDate.add(Calendar.MONTH, -2);
        Calendar boa3CutoffDate = Calendar.getInstance();
        boa3CutoffDate.add(Calendar.MONTH, -12);
        Calendar boa4CutoffDate = Calendar.getInstance();
        boa4CutoffDate.add(Calendar.MONTH, -24);
        Calendar capitolOne16CutoffDate = Calendar.getInstance();
        capitolOne16CutoffDate.add(Calendar.MONTH, -6);
        Calendar chase524CutoffDate = Calendar.getInstance();
        chase524CutoffDate.add(Calendar.MONTH, -24);
        for (CreditCard cc : userCards) {
            if (cc.getStatus() == CardStatus.OPEN) {
                // Calculate total AF and credit limit
                totalAF = totalAF.add(cc.getAnnualFee());
                totalCL = totalCL.add(cc.getCreditLimit());
            }

            // Determines anti-churning rules eligible cards (by open date)
            Date openDate = cc.getOpenDate();
            boolean usCard = cc.getCountry() == Country.USA;
            if (openDate != null && usCard) {
                Calendar openDateCal = Calendar.getInstance();
                openDateCal.setTime(openDate);

                if (cc.getBank().equals("Bank of America")) {
                    if (openDateCal.after(boa2CutoffDate)) {
                        boa2Dates.add(openDate);
                    }
                    if (openDateCal.after(boa3CutoffDate)) {
                        boa3Dates.add(openDate);
                    }
                    if (openDateCal.after(boa4CutoffDate)) {
                        boa4Dates.add(openDate);
                    }
                }
                if (cc.getBank().equals("Capital One")) {
                    if (openDateCal.after(capitolOne16CutoffDate)) {
                        capitolOne16Dates.add(openDate);
                    }
                }
                if (cc.getType().equals("P") || cc.getBank().equals("Capital One") || cc.getBank().equals("Discover")) {
                    if (openDateCal.after(chase524CutoffDate)) {
                        chase524Dates.add(openDate);
                    }
                }
            }
        }
        Collections.sort(boa2Dates);
        Collections.sort(boa3Dates);
        Collections.sort(boa4Dates);
        Collections.sort(capitolOne16Dates);
        Collections.sort(chase524Dates);

        // Calculates anti-churning rules statuses and eligibility dates
        Calendar eligibilityDate = Calendar.getInstance();
        List<Date> boaEligibilityDates = new ArrayList<>();
        this.boa234Status = boa2Dates.size()  + "/" + boa3Dates.size()  + "/" + boa4Dates.size();
        if (boa2Dates.size() >= 2) {
            eligibilityDate.setTime(boa2Dates.get(boa2Dates.size() - 2));
            eligibilityDate.add(MONTH, 2);
            boaEligibilityDates.add(eligibilityDate.getTime());
        }
        if (boa3Dates.size() >= 3) {
            eligibilityDate.setTime(boa3Dates.get(boa3Dates.size() - 3));
            eligibilityDate.add(MONTH, 12);
            boaEligibilityDates.add(eligibilityDate.getTime());
        }
        if (boa4Dates.size() >= 4) {
            eligibilityDate.setTime(boa4Dates.get(boa4Dates.size() - 4));
            eligibilityDate.add(MONTH, 24);
            boaEligibilityDates.add(eligibilityDate.getTime());
        }
        if (boaEligibilityDates.size() > 0) {
            Collections.sort(boaEligibilityDates);
            this.boa234EligibilityDate = dateFormat.format(boaEligibilityDates.get(boaEligibilityDates.size() - 1).getTime());
        } else {
            this.boa234EligibilityDate = "NOW";
        }
        this.capitolOne16Status = capitolOne16Dates.size()  + "/6";
        if (capitolOne16Dates.size() >= 1){
            eligibilityDate.setTime(capitolOne16Dates.get(capitolOne16Dates.size() - 1));
            eligibilityDate.add(MONTH, 6);
            this.capitolOne16EligibilityDate = dateFormat.format(eligibilityDate.getTime());
        } else {
            this.capitolOne16EligibilityDate = "NOW";
        }
        this.chase524Status = chase524Dates.size()  + "/24";
        if (chase524Dates.size() >= 5){
            eligibilityDate.setTime(chase524Dates.get(chase524Dates.size() - 5));
            eligibilityDate.add(MONTH, 24);
            this.chase524EligibilityDate = dateFormat.format(eligibilityDate.getTime());
        } else {
            this.chase524EligibilityDate = "NOW";
        }

        this.totalAF = totalAF;
        this.creditLimit = totalCL;

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

    public Integer getProgramCount() {
        return programCount;
    }

    public void setProgramCount(Integer programCount) {
        this.programCount = programCount;
    }

    public Integer getCardCount() {
        return cardCount;
    }

    public void setCardCount(Integer cardCount) {
        this.cardCount = cardCount;
    }

    public BigDecimal getTotalProgramValue() {
        return totalProgramValue;
    }

    public void setTotalProgramValue(BigDecimal totalProgramValue) {
        this.totalProgramValue = totalProgramValue;
    }

    public BigDecimal getTotalAF() {
        return totalAF;
    }

    public void setTotalAF(BigDecimal totalAF) {
        this.totalAF = totalAF;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getBoa234Status() {
        return boa234Status;
    }

    public void setBoa234Status(String boa234Status) {
        this.boa234Status = boa234Status;
    }

    public String getBoa234EligibilityDate() {
        return boa234EligibilityDate;
    }

    public void setBoa234EligibilityDate(String boa234EligibilityDate) {
        this.boa234EligibilityDate = boa234EligibilityDate;
    }

    public String getCapitolOne16Status() {
        return capitolOne16Status;
    }

    public void setCapitolOne16Status(String capitolOne16Status) {
        this.capitolOne16Status = capitolOne16Status;
    }

    public String getCapitolOne16EligibilityDate() {
        return capitolOne16EligibilityDate;
    }

    public void setCapitolOne16EligibilityDate(String capitolOne16EligibilityDate) {
        this.capitolOne16EligibilityDate = capitolOne16EligibilityDate;
    }

    public String getChase524Status() {
        return chase524Status;
    }

    public void setChase524Status(String chase524Status) {
        this.chase524Status = chase524Status;
    }

    public String getChase524EligibilityDate() {
        return chase524EligibilityDate;
    }

    public void setChase524EligibilityDate(String chase524EligibilityDate) {
        this.chase524EligibilityDate = chase524EligibilityDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}