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
    private Integer cardId;
    private CardStatus status;
    private String bank;
    private Integer bankId;
    private String name;
    private String type;
    private BigDecimal annualFee;
    private Boolean annualFeeWaived;
    private BigDecimal foreignTransactionFee;
    private Date openDate;
    private Date afDate;
    private Date closeDate;
    private NotificationStatus notificationStatus;
    private String logoIcon;
    private String notes;

    public CreditCard(Integer id, Integer cardId, CardStatus status, String bank, Integer bankId, String name, String type, BigDecimal annualFee, Boolean annualFeeWaived, BigDecimal foreignTransactionFee, Date openDate, Date AFDate, Date closeDate, NotificationStatus notificationStatus, String notes) {
        this.id = id;
        this.cardId = cardId;
        this.status = status;
        this.bank = bank;
        this.bankId = bankId;
        this.name = name;
        this.type = type;
        this.annualFee = annualFee;
        this.annualFeeWaived = annualFeeWaived;
        this.foreignTransactionFee = foreignTransactionFee;
        this.openDate = openDate;
        this.afDate = AFDate;
        this.closeDate = closeDate;
        this.notificationStatus = notificationStatus;

		// Generates standard icon name from card ID
        String bankIdString = String.format("%03d", this.bankId);
        this.logoIcon = new StringBuilder("bank_").append(bankIdString).append("_icon").toString();

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

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
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

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(BigDecimal annualFee) {
        this.annualFee = annualFee;
    }

    public Boolean getAnnualFeeWaived() {
        return annualFeeWaived;
    }

    public void setAnnualFeeWaived(Boolean annualFeeWaived) {
        this.annualFeeWaived = annualFeeWaived;
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

    public String getLogoIcon() {
        return logoIcon;
    }

    public void setLogoIcon(String logoIcon) {
        this.logoIcon = logoIcon;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}