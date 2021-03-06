/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Classes;

import com.davidlcassidy.travelwallet.Enums.NotificationStatus;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/*
LoyaltyProgram class is created by ProgramDataSource and contains the data for a
loyalty program from both the MainDatabase and the RefDatabase.
 */

public class LoyaltyProgram implements Comparable<LoyaltyProgram> {

    private Integer id;
    private Integer refId;
    private User user;
    private String type;
    private String company;
    private String name;
    private String accountNumber;
    private Integer points;
    private BigDecimal pointValue;
    private BigDecimal totalValue;
    private Integer inactivityExpiration;
    private Date lastActivityDate;
    private Date expirationDate;
    private String expirationOverride;
    private NotificationStatus notificationStatus;
    private String logoImage;
    private String logoIcon;
    private String notes;

    public LoyaltyProgram(Integer id, Integer refId, User user, String type, String company, String name, String accountNumber, Integer points, BigDecimal pointValue, Integer inactivityExpiration, String expirationOverride, Date lastActivityDate, NotificationStatus notificationStatus, String notes) {
        this.id = id;
        this.refId = refId;
        this.user = user;
        this.type = type;
        this.company = company;
        this.name = name;
        this.accountNumber = accountNumber;
        this.points = points;
        this.pointValue = pointValue;

        // Calculates total program value by multiplying the number of points with the point value
        this.totalValue = pointValue.multiply(new BigDecimal(points)).divide(BigDecimal.valueOf(100));

        this.inactivityExpiration = inactivityExpiration;
        this.lastActivityDate = lastActivityDate;
        updateExpirationDate();
        this.expirationOverride = expirationOverride;
        this.notificationStatus = notificationStatus;
        setLogos();
        this.notes = notes;
    }

    // Sets expiration date is set by taking the last activity date and adding the program specific
    // monthsExpire which indicates the time period allowed of inactivity by each program before
    // closing the account
    public void updateExpirationDate() {
        if (lastActivityDate == null || !hasExpirationDate()) {
            expirationDate = null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(lastActivityDate);
            c.add(Calendar.MONTH, inactivityExpiration);
            expirationDate = c.getTime();
        }
    }

    // Generates standard icon and header image names from program ID
    private void setLogos() {
        String programIdString = String.format("%03d", this.refId);
        this.logoImage = new StringBuilder("program_").append(programIdString).append("_image").toString();
        this.logoIcon = new StringBuilder("program_").append(programIdString).append("_icon").toString();
    }

    // Defines default sort order
    public int compareTo(LoyaltyProgram lp) {
        return (this.name.compareTo(lp.name));
    }

    public boolean hasExpirationDate() {
        return inactivityExpiration != 999;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = Math.max(points, 0);
    }

    public BigDecimal getPointValue() {
        return pointValue;
    }

    public void setPointValue(BigDecimal pointValue) {
        this.pointValue = pointValue;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public Integer getInactivityExpiration() {
        return inactivityExpiration;
    }

    public void setInactivityExpiration(Integer monthsExpire) {
        this.inactivityExpiration = monthsExpire;
    }

    public Date getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
        updateExpirationDate();
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getExpirationOverride() {
        return expirationOverride;
    }

    public void setExpirationOverride(String expirationOverride) {
        this.expirationOverride = expirationOverride;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
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
