package com.davidlcassidy.travelwallet.EnumTypes;

/*
ID Local Cache : UserPreferences

WARNING : Changing the id values may end backwards compatibility and can cause local
app instability within current app installs
 */

public enum CardStatus {

	// Enum members
    OPEN(1, "Open"),
    CLOSED(2, "Closed");

	private final int id;
    private final String text;

    private CardStatus(int id, String text) {
		this.id = id;
        this.text = text;
    }
	
	// Returns card status ID
    public int getId() {
        return id;
    }

	// Returns card status from ID
    public static CardStatus fromId(int id) {
        for (CardStatus cs : CardStatus.values()) {
            if (cs.getId() == id) {return cs;}
        }
        return null;
    }

}
