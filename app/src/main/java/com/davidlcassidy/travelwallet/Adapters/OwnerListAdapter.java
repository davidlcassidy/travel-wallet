package com.davidlcassidy.travelwallet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.NumberPattern;
import com.davidlcassidy.travelwallet.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OwnerListAdapter extends ArrayAdapter<Owner> {

    private ProgramDataSource programDS;
    private CardDataSource cardDS;
    private UserPreferences userPreferences;
    private Currency currency;
    private SimpleDateFormat dateFormat;

    public OwnerListAdapter(Context context, List<Owner> owners) {
        super(context, 0, owners);
        programDS = ProgramDataSource.getInstance(context);
        cardDS = CardDataSource.getInstance(context);
        userPreferences = UserPreferences.getInstance(context);
        currency = userPreferences.getSetting_Currency();
        dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_main, parent, false);
        }

        final ItemField primaryField = userPreferences.getSetting_OwnerPrimaryField();
        
		// Gets the item at this position
		Owner owner = getItem(position);

		// Gets adapter fields
        ImageView logo = (ImageView) convertView.findViewById(R.id.logo);
        TextView ownerField = (TextView) convertView.findViewById(R.id.firstField);
        TextView messageField = (TextView) convertView.findViewById(R.id.secondField);

		// Sets field values, based on owner preferences
        logo.setVisibility(View.GONE);
        ownerField.setText(owner.getName());

        // Sets field values, based on user preferences
        ArrayList<LoyaltyProgram> userPrograms = programDS.getAll(owner, null, false);
        ArrayList<CreditCard> userCards = cardDS.getAll(owner, null, false, false);
        ArrayList<CreditCard> userChase524Cards = cardDS.getChase524StatusCards(owner);
        SimpleDateFormat dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();
        owner.setValues(userPrograms, userCards, userChase524Cards, dateFormat);

        switch (primaryField.getName()) {
            case "Item Counts":
                String numPrograms = String.valueOf(owner.getProgramCount());
                String numCards = String.valueOf(owner.getCardCount());
                messageField.setText("Programs: " + numPrograms + "     Cards: " + numCards);
                break;
            case "Programs Value":
                BigDecimal totalProgramValue = owner.getTotalProgramValue();
                String totalProgramValueString = currency.numToString(totalProgramValue, NumberPattern.COMMADOT);
                messageField.setText(totalProgramValueString);
                break;
            case "Credit Limit":
                BigDecimal totalCL = owner.getCreditLimit();
                String totalCLString = currency.numToString(totalCL, NumberPattern.COMMADOT);
                messageField.setText(totalCLString);
                break;
            case "Chase 5/24 Status":
                String status = owner.getChase524Status();
                String eligibilityDateString = owner.getChase524StatusEligibilityDate();
                messageField.setText(status + "  -  Eligibile " + eligibilityDateString);
                break;
            case "Notes":

                // Gets the first 25 characters of the first notes line
                String notes = owner.getNotes();
                messageField.setText(notes
                        .split("\\r?\\n")[0]
                        .substring(0, Math.min(notes.length(), 25)));
                break;
        }

        return convertView;
    }
}