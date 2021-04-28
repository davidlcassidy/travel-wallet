/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
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
import com.davidlcassidy.travelwallet.Enums.Country;
import com.davidlcassidy.travelwallet.Enums.Currency;
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

    private LinearLayout antiChurningBoaStatusLayout, antiChurningBoaDateLayout,
            antiChurningCapitalOneStatusLayout, antiChurningCapitalOneDateLayout,
            antiChurningChaseStatusLayout, antiChurningChaseDateLayout;
    private TextView nameText, notesField, programCountField, programValueField,
            cardCountField, cardAFField, cardCreditLimitField, antiChurningTitle,
            antiChurningBoaStatusField, antiChurningBoaDateField, antiChurningCapitalOneStatusField,
            antiChurningCapitalOneDateField, antiChurningChaseStatusField, antiChurningChaseDateField;

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
        antiChurningTitle = findViewById(R.id.antiChurningRulesTitle);
        antiChurningBoaStatusField = findViewById(R.id.antiChurningBoaStatusField);
        antiChurningBoaDateField = findViewById(R.id.antiChurningBoaDateField);
        antiChurningCapitalOneStatusField = findViewById(R.id.antiChurningCapitalOneStatusField);
        antiChurningCapitalOneDateField = findViewById(R.id.antiChurningCapitalOneDateField);
        antiChurningChaseStatusField = findViewById(R.id.antiChurningChaseStatusField);
        antiChurningChaseDateField = findViewById(R.id.antiChurningChaseDateField);

        antiChurningBoaStatusLayout = findViewById(R.id.antiChurningBoaStatusLayout);
        antiChurningBoaDateLayout = findViewById(R.id.antiChurningBoaDateLayout);
        antiChurningCapitalOneStatusLayout = findViewById(R.id.antiChurningCapitalOneStatusLayout);
        antiChurningCapitalOneDateLayout = findViewById(R.id.antiChurningCapitalOneDateLayout);
        antiChurningChaseStatusLayout = findViewById(R.id.antiChurningChaseStatusLayout);
        antiChurningChaseDateLayout = findViewById(R.id.antiChurningChaseDateLayout);
    }

    protected void onResume() {
        super.onResume();

        // Get user field values
        User user = userDS.getSingle(userId, programDS, cardDS);
        String name = user.getName();
        String notes = user.getNotes();
        String numPrograms = String.valueOf(user.getProgramCount());
        String totalProgramValue = currency.formatValue(user.getTotalProgramValue());
        String numCards = String.valueOf(user.getCardCount());
        String totalAF = currency.formatValue(user.getTotalAF());
        String creditLimit = currency.formatValue(user.getCreditLimit());

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

        // Only show anti-churning rules if in the US and with Customized option
        if (appPreferences.getSetting_Country() == Country.USA && appPreferences.getCustom_UserAntiChurningRules()) {
            antiChurningTitle.setVisibility(View.VISIBLE);
            antiChurningChaseStatusLayout.setVisibility(View.VISIBLE);
            antiChurningChaseDateLayout.setVisibility(View.VISIBLE);
            antiChurningBoaStatusLayout.setVisibility(View.VISIBLE);
            antiChurningBoaDateLayout.setVisibility(View.VISIBLE);
            antiChurningCapitalOneStatusLayout.setVisibility(View.VISIBLE);
            antiChurningCapitalOneDateLayout.setVisibility(View.VISIBLE);

            antiChurningBoaStatusField.setText(user.getBoa234Status());
            antiChurningBoaDateField.setText(user.getBoa234EligibilityDate());
            antiChurningCapitalOneStatusField.setText(user.getCapitolOne16Status());
            antiChurningCapitalOneDateField.setText(user.getCapitolOne16EligibilityDate());
            antiChurningChaseStatusField.setText(user.getChase524Status());
            antiChurningChaseDateField.setText(user.getChase524EligibilityDate());
        } else {
            antiChurningTitle.setVisibility(View.GONE);
            antiChurningChaseStatusLayout.setVisibility(View.GONE);
            antiChurningChaseDateLayout.setVisibility(View.GONE);
            antiChurningBoaStatusLayout.setVisibility(View.GONE);
            antiChurningBoaDateLayout.setVisibility(View.GONE);
            antiChurningCapitalOneStatusLayout.setVisibility(View.GONE);
            antiChurningCapitalOneDateLayout.setVisibility(View.GONE);
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