/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Classes.AppPreferences;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Enums.Currency;
import com.davidlcassidy.travelwallet.Enums.ItemField;
import com.davidlcassidy.travelwallet.Enums.NumberPattern;
import com.davidlcassidy.travelwallet.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {

    private final ProgramDataSource programDS;
    private final CardDataSource cardDS;
    private final AppPreferences appPreferences;
    private final Currency currency;

    public UserListAdapter(Context context, List<User> users) {
        super(context, 0, users);
        programDS = ProgramDataSource.getInstance(context);
        cardDS = CardDataSource.getInstance(context);
        appPreferences = AppPreferences.getInstance(context);
        currency = appPreferences.getSetting_Currency();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_main, parent, false);
        }

        final ItemField primaryField = appPreferences.getCustom_UserPrimaryField();

        // Gets the item at this position
        User user = getItem(position);

        // Gets adapter fields
        ImageView logo = convertView.findViewById(R.id.logo);
        TextView userField = convertView.findViewById(R.id.firstField);
        TextView messageField = convertView.findViewById(R.id.secondField);

        // Sets field values, based on user preferences
        logo.setVisibility(View.GONE);
        userField.setText(user.getName());

        // Sets field values, based on user preferences
        ArrayList<LoyaltyProgram> userPrograms = programDS.getAll(user, null, false);
        ArrayList<CreditCard> userCards = cardDS.getAll(user, null, false, false);
        ArrayList<CreditCard> userChase524Cards = cardDS.getChase524StatusCards(user);
        SimpleDateFormat dateFormat = appPreferences.getSetting_DatePattern().getDateFormat();
        user.setValues(userPrograms, userCards, userChase524Cards, dateFormat);

        switch (primaryField.getName()) {
            case "Item Counts":
                String numPrograms = String.valueOf(user.getProgramCount());
                String numCards = String.valueOf(user.getCardCount());
                messageField.setText("Programs: " + numPrograms + "     Cards: " + numCards);
                break;
            case "Programs Value":
                BigDecimal totalProgramValue = user.getTotalProgramValue();
                String totalProgramValueString = currency.formatValue(totalProgramValue);
                messageField.setText(totalProgramValueString);
                break;
            case "Credit Limit":
                BigDecimal totalCL = user.getCreditLimit();
                String totalCLString = currency.formatValue(totalCL);
                messageField.setText(totalCLString);
                break;
            case "Chase 5/24 Status":
                String status = user.getChase524Status();
                String eligibilityDateString = user.getChase524StatusEligibilityDate();
                messageField.setText(status + "  -  Eligible " + eligibilityDateString);
                break;
            case "Notes":

                // Gets the first 25 characters of the first notes line
                String notes = user.getNotes();
                messageField.setText(notes
                        .split("\\r?\\n")[0]
                        .substring(0, Math.min(notes.length(), 25)));
                break;
        }

        return convertView;
    }
}