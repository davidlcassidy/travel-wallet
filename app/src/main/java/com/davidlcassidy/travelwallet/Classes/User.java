/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Classes;

/*
User class is created by UserDataSource and contains the data for a user
from the MainDatabase.
 */

import com.davidlcassidy.travelwallet.EnumTypes.CardStatus;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static java.util.Calendar.MONTH;

public class User {

    private Integer id;
    private String name;
    private Integer programCount;
    private Integer cardCount;
    private BigDecimal totalProgramValue;
    private BigDecimal totalAF;
    private BigDecimal creditLimit;
    private String chase524Status;
    private String chase524StatusEligibilityDate;
    private String notes;

    public User(Integer id, String name, String notes) {
        this.id = id;
        this.name = name;
        this.notes = notes;
    }

    public void setValues(ArrayList<LoyaltyProgram> userPrograms, ArrayList<CreditCard> userCards, ArrayList<CreditCard> userChase524cards, SimpleDateFormat dateFormat) {

        // Set item counts
        this.programCount = userPrograms.size();
        this.cardCount = userCards.size();

        // Set total program value
        BigDecimal totalProgramValue = BigDecimal.valueOf(0);
        for(LoyaltyProgram lp :userPrograms){
            totalProgramValue = totalProgramValue.add(lp.getTotalValue());
        }
        this.totalProgramValue =totalProgramValue;

        // Set total AF and credit limit
        BigDecimal totalAF = BigDecimal.valueOf(0);
        BigDecimal totalCL = BigDecimal.valueOf(0);
        for (CreditCard cc : userCards) {
            if (cc.getStatus() == CardStatus.OPEN) {
                totalAF = totalAF.add(cc.getAnnualFee());
                totalCL = totalCL.add(cc.getCreditLimit());
            }
        }
        this.totalAF = totalAF;
        this.creditLimit = totalCL;

        // Set Chase 5/24 status
        Integer chase524count = userChase524cards.size();
        this.chase524Status = String.valueOf(chase524count) + "/24";
        String eligibilityDateString = "";
        if (chase524count >= 5) {
            CreditCard fifthCard = userChase524cards.get(chase524count - 5);
            Calendar eligibilityDate = Calendar.getInstance();
            eligibilityDate.setTime(fifthCard.getOpenDate());
            eligibilityDate.add(MONTH, 24);
            eligibilityDateString = dateFormat.format(eligibilityDate.getTime());
        } else {
            eligibilityDateString = "Now";
        }
        this.chase524StatusEligibilityDate =  eligibilityDateString;
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

    public String getChase524Status() {
        return chase524Status;
    }

    public void setChase524Status(String chase524Status) {
        this.chase524Status = chase524Status;
    }

    public String getChase524StatusEligibilityDate() {
        return chase524StatusEligibilityDate;
    }

    public void setChase524StatusEligibilityDate(String chase524StatusEligibilityDate) {
        this.chase524StatusEligibilityDate = chase524StatusEligibilityDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}