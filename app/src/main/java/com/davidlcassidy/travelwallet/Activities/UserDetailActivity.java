/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_EditDelete;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Database.UserDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.Country;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.NumberPattern;
import com.davidlcassidy.travelwallet.R;

/*
UserDetailActivity is use to display the details of an individual user. It is created
by the selection of an user in the listview in UserListActivity and provided with a
USER_ID matching the unique user id number in the MainDatabase. This activity is primarily
composed of a listview utilizing the DetailListAdapter.
 */

public class UserDetailActivity extends BaseActivity_EditDelete {

    private UserDataSource userDS;
    private ProgramDataSource programDS;
    private CardDataSource cardDS;
    private Currency currency;
    private Integer userId;

    private LinearLayout cardChaseStatusLayout, cardChaseStatusDateLayout;
    private TextView nameText, notesField, programCountField, programValueField,
            cardCountField, cardAFField, cardCreditLimitField, cardChaseStatusField,
            cardChaseStatusDateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdetailslist);
        setTitle("User");

        userDS = UserDataSource.getInstance(this);
        programDS = ProgramDataSource.getInstance(this);
        cardDS = CardDataSource.getInstance(this);
        currency = appPreferences.getSetting_Currency();

        userId = Integer.parseInt(getIntent().getStringExtra("USER_ID"));

        nameText = findViewById(R.id.nameText);
        notesField = findViewById(R.id.notesField);
        programCountField = findViewById(R.id.programCountField);
        programValueField = findViewById(R.id.programValueField);
        cardCountField = findViewById(R.id.cardCountField);
        cardAFField = findViewById(R.id.cardAFField);
        cardCreditLimitField = findViewById(R.id.cardCreditLimitField);
        cardChaseStatusField = findViewById(R.id.cardChaseStatusField);
        cardChaseStatusDateField = findViewById(R.id.cardChaseStatusDateField);

        cardChaseStatusLayout = findViewById(R.id.cardChaseStatusLayout);
        cardChaseStatusDateLayout = findViewById(R.id.cardChaseStatusDateLayout);
    }

    protected void onResume() {
        super.onResume();

        // Get user field values
        User user = userDS.getSingle(userId, programDS, cardDS);
        String name = user.getName();
        String notes = user.getNotes();
        String numPrograms = String.valueOf(user.getProgramCount());
        String totalProgramValue = currency.numToString(user.getTotalProgramValue(), NumberPattern.COMMADOT);
        String numCards = String.valueOf(user.getCardCount());
        String totalAF = currency.numToString(user.getTotalAF(), NumberPattern.COMMADOT);
        String creditLimit = currency.numToString(user.getCreditLimit(), NumberPattern.COMMADOT);

        // Set user field values
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

        // Only show Chase 524 status in US
        Country country = appPreferences.getSetting_Country();
        if (country == Country.USA) {
            cardChaseStatusLayout.setVisibility(View.VISIBLE);
            cardChaseStatusDateLayout.setVisibility(View.VISIBLE);
            String chaseStatus = user.getChase524Status();
            String chaseEligibilityDateString = user.getChase524StatusEligibilityDate();
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
        // Opens UserAddEdit Activity
        Intent intent = new Intent(UserDetailActivity.this, UserAddEditActivity.class);
        intent.putExtra("USER_ID", String.valueOf(userId));
        startActivity(intent);
    }

    // Runs when delete button is clicked
    @Override
    public void menuDeleteClicked() {

        // Creates delete warning dialog
        User user = userDS.getSingle(userId, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(user.getName());
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to delete?");

        // Deletes user if "Yes" button selected
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                userDS.delete(userId);
                appPreferences.setFiltersUpdateRequired(true);
                finish();
            }
        });

        // Dialog closes with no further action if "Cancel" button is selected
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
            }
        });

        // Creates dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}