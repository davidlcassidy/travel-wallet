/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 10:47 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_EditDelete;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.Detail;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Adapters.DetailListAdapter;
import com.davidlcassidy.travelwallet.EnumTypes.CardStatus;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.NotificationStatus;
import com.davidlcassidy.travelwallet.EnumTypes.NumberPattern;
import com.davidlcassidy.travelwallet.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
CardDetailActivity is use to display the details of an individual credit card.
It is created by the selection of a card in the listview in CardListFragment
(MainActivity) and provided with a Card_ID matching the unique card id
number in the MainDatabase. This activity is primarily composed of a listview utilizing
the DetailListAdapter.
 */

public class CardDetailActivity extends BaseActivity_EditDelete {

    private CardDataSource cardDS;
    private Integer cardId;
    private List<Detail> detailList;

    private ListView lv;
    private ImageView logo;
    private TextView logoText;
    private TextView notesField;
    private ToggleButton notificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailslist);
		setTitle("Credit Card");

        cardDS = CardDataSource.getInstance(this);
        cardId = getIntent().getIntExtra("CARD_ID", -1);
        detailList = new ArrayList<Detail>();
        final CreditCard card = cardDS.getSingle(cardId);
        
		// Adds image to list header and notification button to list footer
        View header = getLayoutInflater().inflate(R.layout.detaillist_header, null);
        logo = (ImageView) header.findViewById(R.id.Logo);
        logoText = (TextView) header.findViewById(R.id.LogoText);
        View footer = getLayoutInflater().inflate(R.layout.detaillist_footer, null);
        lv = (ListView) findViewById(R.id.detailsList);
        lv.addHeaderView(header);
        lv.addFooterView(footer);

		// Sets text and colors for notification button
        notificationButton = (ToggleButton) footer.findViewById(R.id.notificationButton);
        notificationButton.setTextOn("Monitoring : ON");
        notificationButton.setTextOff("Monitoring : OFF");
        if (card.getNotificationStatus() == NotificationStatus.UNMONITORED){
            notificationButton.setBackgroundColor(getResources().getColor(R.color.gray));
        } else {
            notificationButton.setBackgroundColor(getThemeColor(R.attr.colorPrimary));
        }
		
		// Sets click listener for notification button. When clicked, the card's notification status
		// will be updated and notification button text will change to reflect.
        notificationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (card.getNotificationStatus() == NotificationStatus.UNMONITORED){
                    cardDS.changeCardNotificationStatus(card, NotificationStatus.OFF);
                    notificationButton.setChecked(true);
                    notificationButton.setBackgroundColor(getThemeColor(R.attr.colorPrimary));
                } else {
                    cardDS.changeCardNotificationStatus(card, NotificationStatus.UNMONITORED);
                    notificationButton.setChecked(false);
                    notificationButton.setBackgroundColor(getResources().getColor(R.color.gray));
                }
            }
        });

        notesField = (TextView) footer.findViewById(R.id.notesField);
    }

    protected void onResume() {
        super.onResume();

        SimpleDateFormat dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();
        NumberPattern numberPattern = NumberPattern.COMMADOT;
        DecimalFormat decimalFormat = numberPattern.getDecimalFormat();
        Currency currency = userPreferences.getSetting_Currency();

        // Gets card annual fee and formats as currency string
        CreditCard card = cardDS.getSingle(cardId);
        BigDecimal cardAF = card.getAnnualFee();
        String cardAFString = currency.numToString(cardAF, numberPattern);

        // Get card credit limit and formats as currency string
        BigDecimal creditlimit = card.getCreditLimit();
        String creditLimitString = currency.numToString(creditlimit, numberPattern);

		// Sets card image and card name text for list header #TODO Consider removing images to reduce app size
        int logoNum = this.getResources().getIdentifier("card_000_image", "drawable", this.getPackageName());
        logo.setImageResource(logoNum);
        logoText.setText(card.getName());

		// Creates list of credit card field/value pairs
        detailList.clear();
        Owner owner = card.getOwner();
        if (owner != null){
            detailList.add(new Detail("Owner", owner.getName()));
        }
        detailList.add(new Detail("Name", card.getName()));
        detailList.add(new Detail("Bank", card.getBank()));
        CardStatus status = card.getStatus();
        detailList.add(new Detail("Status", status.getName()));
        if (creditlimit.compareTo(BigDecimal.ZERO)!=0) {
            detailList.add(new Detail("Credit Limit", creditLimitString));
        }
        detailList.add(new Detail("Annual Fee", cardAFString));
		Date openDate = card.getOpenDate();
        if (openDate != null) {
            detailList.add(new Detail("Open Date", dateFormat.format(openDate)));
        }
        boolean hasAnnualFee = card.hasAnnualFee();
        Date afDate = card.getAfDate();
        if (afDate != null && hasAnnualFee) {
            detailList.add(new Detail("Annual Fee Date", dateFormat.format(afDate)));
        }
        Date closeDate = card.getCloseDate();
        if (closeDate != null && status == CardStatus.CLOSED) {
            detailList.add(new Detail("Close Date", dateFormat.format(closeDate)));
        }
        detailList.add(new Detail("Foreign Fee", decimalFormat.format(card.getForeignTransactionFee()) + " %"));

		// Creates adapter using card details list and sets to list
        DetailListAdapter adapter = new DetailListAdapter(this, detailList);
        lv.setAdapter(adapter);

		// Updates notification button test and visability
        if (card.getNotificationStatus() == NotificationStatus.UNMONITORED){
            notificationButton.setChecked(false);
        } else {
            notificationButton.setChecked(true);
        }
        notificationButton.setVisibility(View.VISIBLE);
        if (!hasAnnualFee || card.getStatus() == CardStatus.CLOSED) {
            notificationButton.setVisibility(View.GONE);
        }

        String notes = card.getNotes();
        if (notes == null || notes.equals("")) {
            notesField.setVisibility(View.GONE);
        } else {
            notesField.setVisibility(View.VISIBLE);
            notesField.setText(notes);
        }
    }

	// Runs when edit button is clicked
    @Override
    public void menuEditClicked() {
		// Opens CardAddEdit Activity
        Intent intent = new Intent(CardDetailActivity.this, CardAddEditActivity.class);
        intent.putExtra("CARD_ID", cardId);
        startActivity(intent);
    }

	// Runs when delete button is clicked
    @Override
    public void menuDeleteClicked() {
		
		// Creates delete warning dialog
        CreditCard card = cardDS.getSingle(cardId);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(card.getName());
		builder.setCancelable(false);
        builder.setMessage("Are you sure you want to delete?");
		
		// Deletes card if "Yes" button selected
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                cardDS.delete(cardId);
                userPreferences.setCardFiltersUpdateRequired(true);
                finish();
            }});

		// Dialog closes with no further action if "Cancel" button is selected
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                ;
            }
        });

		// Creates dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}