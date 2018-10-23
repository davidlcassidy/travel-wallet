/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:40 PM
 */

package com.davidlcassidy.travelwallet.Classes;

import com.davidlcassidy.travelwallet.EnumTypes.CardStatus;
import com.davidlcassidy.travelwallet.EnumTypes.NotificationStatus;

import java.math.BigDecimal;
import java.util.Date;

/*
CreditCard class is created by CardDataSource and contains the data for a credit
card from both the MainDatabase and the RefDatabase.
 */

public class CreditCard {

    private Integer id;
    private Integer refId;
    private Owner owner;
    private CardStatus status;
    private String bank;
    private String name;
    private String type;
    private BigDecimal creditLimit;
    private BigDecimal annualFee;
    private BigDecimal foreignTransactionFee;
    private Date openDate;
    private Date afDate;
    private Date closeDate;
    private NotificationStatus notificationStatus;
    private String notes;

    public CreditCard(Integer id, Integer refId, Owner owner, CardStatus status, String bank, String name, String type, BigDecimal creditLimit, BigDecimal annualFee, BigDecimal foreignTransactionFee, Date openDate, Date AFDate, Date closeDate, NotificationStatus notificationStatus, String notes) {
        this.id = id;
        this.refId = refId;
        this.owner = owner;
        this.status = status;
        this.bank = bank;
        this.name = name;
        this.type = type;
        this.creditLimit = creditLimit;
        this.annualFee = annualFee;
        this.foreignTransactionFee = foreignTransactionFee;
        this.openDate = openDate;
        this.afDate = AFDate;
        this.closeDate = closeDate;
        this.notificationStatus = notificationStatus;
        this.notes = notes;
    }

    public boolean hasAnnualFee(){
        return this.annualFee.compareTo(BigDecimal.ZERO) != 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(BigDecimal annualFee) {
        this.annualFee = annualFee;
    }

    public BigDecimal getForeignTransactionFee() {
        return foreignTransactionFee;
    }

    public void setForeignTransactionFee(BigDecimal foreignTransactionFee) {
        this.foreignTransactionFee = foreignTransactionFee;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getAfDate() {
        return afDate;
    }

    public void setAfDate(Date afDate) {
        this.afDate = afDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}