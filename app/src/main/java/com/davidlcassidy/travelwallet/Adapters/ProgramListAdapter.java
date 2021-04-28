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
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Enums.Currency;
import com.davidlcassidy.travelwallet.Enums.ItemField;
import com.davidlcassidy.travelwallet.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProgramListAdapter extends ArrayAdapter<LoyaltyProgram> {

    private final AppPreferences appPreferences;

    public ProgramListAdapter(Context context, List<LoyaltyProgram> programs) {
        super(context, 0, programs);
        appPreferences = AppPreferences.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_main, parent, false);
        }

        final ItemField primaryField = appPreferences.getCustom_ProgramPrimaryField();
        Currency currency = appPreferences.getSetting_Currency();
        DecimalFormat numberFormat = currency.getNumberPattern().getNumberFormat(false);
        SimpleDateFormat dateFormat = appPreferences.getSetting_DatePattern().getDateFormat();

        // Gets the item at this position
        LoyaltyProgram program = getItem(position);

        // Gets adapter fields
        ImageView logo = convertView.findViewById(R.id.logo);
        TextView programField = convertView.findViewById(R.id.firstField);
        TextView messageField = convertView.findViewById(R.id.secondField);

        // Gets logo resource from name
        Context context = logo.getContext();
        int logoNum = context.getResources().getIdentifier(program.getLogoIcon(), "drawable", context.getPackageName());
        logo.setImageResource(logoNum);

        // Sets field values, based on user preferences
        programField.setText(program.getName());
        switch (primaryField) {
            case ACCOUNT_NUMBER:
                String accountNumber =  program.getAccountNumber();
                messageField.setText(accountNumber);
                break;
            case POINTS:
                messageField.setText(numberFormat.format(program.getPoints()));
                break;
            case VALUE:
                BigDecimal programValue = program.getTotalValue();
                String programValueString = currency.formatValue(programValue);
                messageField.setText(programValueString);
                break;
            case EXPIRATION_DATE:
                Date lastActivityDate = program.getLastActivityDate();
                Date expirationDate = program.getExpirationDate();
                String expirationOverride = program.getExpirationOverride();
                String message = null;

                // Uses expiration override value if there is no expiration date
                if (!program.hasExpirationDate() && expirationOverride != null) {
                    message = expirationOverride;
                } else if (expirationDate != null && lastActivityDate != null) {
                    message = dateFormat.format(expirationDate);
                }
                messageField.setText(message);
                break;
            case PROGRAM_NOTES:
                // Gets the first 25 characters of the first notes line
                String notes = program.getNotes();
                messageField.setText(notes
                        .split("\\r?\\n")[0]
                        .substring(0, Math.min(notes.length(), 25)));
                break;
        }
        return convertView;
    }
}