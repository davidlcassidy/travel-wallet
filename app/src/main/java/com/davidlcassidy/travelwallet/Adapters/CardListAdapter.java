/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/24/18 2:17 PM
 */

package com.davidlcassidy.travelwallet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.NumberPattern;
import com.davidlcassidy.travelwallet.R;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CardListAdapter extends ArrayAdapter<CreditCard> {

    private UserPreferences userPreferences;

    public CardListAdapter(Context context, List<CreditCard> cards) {
        super(context, 0, cards);
        userPreferences = UserPreferences.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_main, parent, false);
        }

        SimpleDateFormat dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();
        final ItemField primaryField = userPreferences.getSetting_CardPrimaryField();
        
		// Gets the item at this position
		CreditCard card = getItem(position);

		// Gets adapter fields
        ImageView logo = (ImageView) convertView.findViewById(R.id.logo);
        TextView cardField = (TextView) convertView.findViewById(R.id.firstField);
        TextView messageField = (TextView) convertView.findViewById(R.id.secondField);

		// Gets logo resource from name
        Context context = logo.getContext();
        int logoNum = context.getResources().getIdentifier(card.getLogoIcon(), "drawable", context.getPackageName());
        logo.setImageResource(logoNum);

		// Sets field values, based on user preferences
        cardField.setText(card.getName());
        switch (primaryField.getName()) {
            case "Annual Fee":
                Currency currency = userPreferences.getSetting_Currency();
                BigDecimal annualFee = card.getAnnualFee();
                String annualFeeString = currency.numToString(annualFee, NumberPattern.COMMADOT);
                messageField.setText(annualFeeString);
                break;
            case "Open Date":
                Date openDate = card.getOpenDate();
                if (openDate != null) {
                    messageField.setText(dateFormat.format(openDate));
                }
                break;
            case "Annual Fee Date":
                Date annualFeeDate = card.getAfDate();
                if (annualFeeDate != null) {
                    messageField.setText(dateFormat.format(annualFeeDate));
                }
                break;
            case "Notes":

                // Gets the first 25 characters of the first notes line
                String notes = card.getNotes();
                messageField.setText(notes
                        .split("\\r?\\n")[0]
                        .substring(0, Math.min(notes.length(), 25)));
                break;
        }

        return convertView;
    }
}