package com.davidlcassidy.travelwallet.EnumTypes;

/*
ID Local Cache : UserPreferences

WARNING : Changing the id values may end backwards compatibility and can cause local
app instability within current app installs
 */

public enum ItemField {

	// Enum members
    PROGRAMNAME(1, ItemType.LOYALTY_PROGRAM, "Program Name"),
    ACCOUNTNUMBER(2, ItemType.LOYALTY_PROGRAM, "Account Number"),
    POINTS(3, ItemType.LOYALTY_PROGRAM, "Points"),
    VALUE(4, ItemType.LOYALTY_PROGRAM, "Value"),
    EXPIRATIONDATE(5, ItemType.LOYALTY_PROGRAM, "Expiration Date"),
    PROGRAMNOTES(6, ItemType.LOYALTY_PROGRAM, "Notes"),
	
	BANK(21, ItemType.CREDIT_CARD, "Bank"),
    CARDNAME(22, ItemType.CREDIT_CARD, "Card Name"),
    OPENDATE(23, ItemType.CREDIT_CARD, "Open Date"),
    AFDATE(24, ItemType.CREDIT_CARD, "Annual Fee Date"),
    ANNUALFEE(25, ItemType.CREDIT_CARD, "Annual Fee"),
    CARDNOTES(26, ItemType.CREDIT_CARD, "Notes"),

    OWNERNAME(31, ItemType.OWNER, "Owner Name"),
    ITEMCOUNTS(32, ItemType.OWNER, "Item Counts"),
    PROGRAMSVALUE(33, ItemType.OWNER, "Programs Value"),
    CREDITLIMIT(34, ItemType.OWNER, "Credit Limit"),
    CHASESTATUS(35, ItemType.OWNER, "Chase 5/24 Status"),
    OWNERNOTES(36, ItemType.OWNER, "Notes");

	private final int id;
    private final String name;
    private final ItemType itemType;

    private ItemField(int id, ItemType itemType, String name) {
		this.id = id;
		this.itemType = itemType;
        this.name = name;
    }
	
	// Returns item field ID
    public int getId() {
        return id;
    }

    // Returns item field name
    public String getName() {
        return name;
    }

	// Returns item status from ID
    public static ItemField fromId(int id) {
        for (ItemField i : ItemField.values()) {
            if (i.getId() == id) {return i;}
        }
        return null;
    }

    // Returns item field from name
    public static ItemField fromName(String name) {
        for (ItemField i : ItemField.values()) {
            if (i.getName().equals(name)) {return i;}
        }
        return null;
    }
}