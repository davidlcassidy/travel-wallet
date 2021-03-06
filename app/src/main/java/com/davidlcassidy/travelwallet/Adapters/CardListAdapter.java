/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
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
import com.davidlcassidy.travelwallet.Enums.Currency;
import com.davidlcassidy.travelwallet.Enums.ItemField;
import com.davidlcassidy.travelwallet.Enums.NumberPattern;
import com.davidlcassidy.travelwallet.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CardListAdapter extends ArrayAdapter<CreditCard> {

    private final AppPreferences appPreferences;

    public CardListAdapter(Context context, List<CreditCard> cards) {
        super(context, 0, cards);
        appPreferences = AppPreferences.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_main, parent, false);
        }

        SimpleDateFormat dateFormat = appPreferences.getSetting_DatePattern().getDateFormat();
        final ItemField primaryField = appPreferences.getCustom_CardPrimaryField();

        // Gets the item at this position
        CreditCard card = getItem(position);

        // Gets adapter fields
        ImageView logo = convertView.findViewById(R.id.logo);
        TextView cardField = convertView.findViewById(R.id.firstField);
        TextView messageField = convertView.findViewById(R.id.secondField);

        // Gets logo resource from name
        Context context = logo.getContext();
        String logoName = card.getLogoIcon();
        int logoNum = context.getResources().getIdentifier(logoName, "drawable", context.getPackageName());
        logo.setImageResource(logoNum);

        // Sets field values, based on user preferences
        cardField.setText(card.getName());
        switch (primaryField) {
            case ANNUAL_FEE:
                Currency currency = appPreferences.getSetting_Currency();
                BigDecimal annualFee = card.getAnnualFee();
                String annualFeeString = currency.formatValue(annualFee);
                messageField.setText(annualFeeString);
                break;
            case OPEN_DATE:
                Date openDate = card.getOpenDate();
                if (openDate != null) {
                    messageField.setText(dateFormat.format(openDate));
                }
                break;
            case AF_DATE:
                Date annualFeeDate = card.getAfDate();
                if (annualFeeDate != null) {
                    messageField.setText(dateFormat.format(annualFeeDate));
                }
                break;
            case CARD_NOTES:
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