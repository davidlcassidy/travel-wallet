/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:50 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_EditDelete;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.OwnerDataSource;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.Country;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.NumberPattern;
import com.davidlcassidy.travelwallet.R;

/*
OwnerDetailActivity is use to display the details of an individual owner. It is created
by the selection of an owner in the listview in OwnerListActivity and provided with a
OWNER_ID matching the unique owner id number in the MainDatabase. This activity is primarily
composed of a listview utilizing the DetailListAdapter.
 */

public class OwnerDetailActivity extends BaseActivity_EditDelete {

    private OwnerDataSource ownerDS;
    private ProgramDataSource programDS;
    private CardDataSource cardDS;
    private Currency currency;
    private Integer ownerId;

    private LinearLayout cardChaseStatusLayout, cardChaseStatusDateLayout;
    private TextView nameText, notesField, programCountField, programValueField,
            cardCountField, cardAFField, cardCreditLimitField, cardChaseStatusField,
            cardChaseStatusDateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ownerdetailslist);
		setTitle("Owner");

        ownerDS = OwnerDataSource.getInstance(this);
        programDS = ProgramDataSource.getInstance(this);
        cardDS = CardDataSource.getInstance(this);
        currency = userPreferences.getSetting_Currency();

        ownerId = Integer.parseInt(getIntent().getStringExtra("OWNER_ID"));
        final Owner owner = ownerDS.getSingle(ownerId, programDS, cardDS);

        nameText = (TextView) findViewById(R.id.nameText);
        notesField = (TextView) findViewById(R.id.notesField);
        programCountField  = (TextView) findViewById(R.id.programCountField);
        programValueField  = (TextView) findViewById(R.id.programValueField);
        cardCountField = (TextView) findViewById(R.id.cardCountField);
        cardAFField  = (TextView) findViewById(R.id.cardAFField);
        cardCreditLimitField = (TextView) findViewById(R.id.cardCreditLimitField);
        cardChaseStatusField = (TextView) findViewById(R.id.cardChaseStatusField);
        cardChaseStatusDateField = (TextView) findViewById(R.id.cardChaseStatusDateField);

        cardChaseStatusLayout = (LinearLayout) findViewById(R.id.cardChaseStatusLayout);
        cardChaseStatusDateLayout = (LinearLayout) findViewById(R.id.cardChaseStatusDateLayout);
    }

    protected void onResume() {
        super.onResume();

        // Get owner field values
        Owner owner = ownerDS.getSingle(ownerId, programDS, cardDS);
        String name = owner.getName();
        String notes = owner.getNotes();
        String numPrograms = String.valueOf(owner.getProgramCount());
        String totalProgramValue = currency.numToString(owner.getTotalProgramValue(), NumberPattern.COMMADOT);
        String numCards = String.valueOf(owner.getCardCount());
        String totalAF = currency.numToString(owner.getTotalAF(), NumberPattern.COMMADOT);
        String creditLimit = currency.numToString(owner.getCreditLimit(), NumberPattern.COMMADOT);

        // Set owner field values
        nameText.setText(name);
        if (notes == null || notes.equals("")) {
            notesField.setVisibility(View.GONE);
        } else {
            notesField.setVisibility(View.VISIBLE);
            notesField.setText(notes);
        }
        programCountField.setText(numPrograms);
        programValueField.setText(totalProgramValue);
        cardCountField.setText(numCards);
        cardAFField.setText(totalAF);
        cardCreditLimitField.setText(creditLimit);

        Country country = userPreferences.getSetting_Country();
        if (country == Country.USA) {
            cardChaseStatusLayout.setVisibility(View.VISIBLE);
            cardChaseStatusDateLayout.setVisibility(View.VISIBLE);
            String chaseStatus = owner.getChase524Status();
            String chaseEligibilityDateString = owner.getChase524StatusEligibilityDate();
            cardChaseStatusField.setText(chaseStatus);
            cardChaseStatusDateField.setText(chaseEligibilityDateString);
        } else {
            cardChaseStatusLayout.setVisibility(View.GONE);
            cardChaseStatusDateLayout.setVisibility(View.GONE);
        }
    }

	// Runs when edit button is clicked
    @Override
    public void menuEditClicked() {
		// Opens OwnerAddEdit Activity
        Intent intent = new Intent(OwnerDetailActivity.this, OwnerAddEditActivity.class);
        intent.putExtra("OWNER_ID", String.valueOf(ownerId));
        startActivity(intent);
    }

	// Runs when delete button is clicked
    @Override
    public void menuDeleteClicked() {
		
		// Creates delete warning dialog
        Owner owner = ownerDS.getSingle(ownerId, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(owner.getName());
		builder.setCancelable(false);
        builder.setMessage("Are you sure you want to delete?");

		// Deletes owner if "Yes" button selected
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                ownerDS.delete(ownerId);
                userPreferences.setFiltersUpdateRequired(true);
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