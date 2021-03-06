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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.davidlcassidy.travelwallet.Adapters.DetailListAdapter;
import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_EditDelete;
import com.davidlcassidy.travelwallet.Classes.Detail;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Enums.Currency;
import com.davidlcassidy.travelwallet.Enums.NotificationStatus;
import com.davidlcassidy.travelwallet.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
ProgramDetailActivity is use to display the details of an individual loyalty program.
It is created by the selection of a program in the listview in ProgramListFragment
(MainActivity) and provided with a PROGRAM_ID matching the unique program id
number in the MainDatabase. This activity is primarily composed of a listview utilizing
the DetailListAdapter.
 */

public class ProgramDetailActivity extends BaseActivity_EditDelete {

    private ProgramDataSource programDS;
    private Integer programId;
    private List<Detail> detailList;

    private ListView lv;
    private ImageView logo;
    private TextView notesField;
    private ToggleButton notificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailslist);
        setTitle("Loyalty Program");

        programDS = ProgramDataSource.getInstance(this);
        programId = getIntent().getIntExtra("PROGRAM_ID", -1);
        detailList = new ArrayList<Detail>();
        final LoyaltyProgram program = programDS.getSingle(programId);

        // Adds image to list header and notification button to list footer
        View header = getLayoutInflater().inflate(R.layout.detaillist_header, null);
        logo = header.findViewById(R.id.Logo);
        View footer = getLayoutInflater().inflate(R.layout.detaillist_footer, null);
        lv = findViewById(R.id.detailsList);
        lv.addHeaderView(header);
        lv.addFooterView(footer);

        // Sets text and colors for notification button
        notificationButton = footer.findViewById(R.id.notificationButton);
        notificationButton.setTextOn("Notifications : ON");
        notificationButton.setTextOff("Notifications : OFF");
        if (program.getNotificationStatus() == NotificationStatus.UNMONITORED) {
            notificationButton.setBackgroundColor(getResources().getColor(R.color.gray));
        } else {
            notificationButton.setBackgroundColor(getThemeColor(R.attr.colorPrimary));
        }

        // Sets click listener for notification button. When clicked, the program's notification status
        // will be updated and notification button text will change to reflect.
        notificationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (program.getNotificationStatus() == NotificationStatus.UNMONITORED) {
                    programDS.changeProgramNotificationStatus(program, NotificationStatus.OFF);
                    notificationButton.setChecked(true);
                    notificationButton.setBackgroundColor(getThemeColor(R.attr.colorPrimary));
                } else {
                    programDS.changeProgramNotificationStatus(program, NotificationStatus.UNMONITORED); //Change to non static
                    notificationButton.setChecked(false);
                    notificationButton.setBackgroundColor(getResources().getColor(R.color.gray));
                }
            }
        });

        notesField = footer.findViewById(R.id.notesField);
    }

    protected void onResume() {
        super.onResume();

        SimpleDateFormat dateFormat = appPreferences.getSetting_DatePattern().getDateFormat();
        Currency currency = appPreferences.getSetting_Currency();

        // Gets loyalty program value and formats as currency string
        LoyaltyProgram program = programDS.getSingle(programId);
        BigDecimal value = program.getTotalValue();
        String programValueString = currency.formatValue(value);

        // Sets program image for list header
        int logoNum = this.getResources().getIdentifier(program.getLogoImage(), "drawable", this.getPackageName());
        logo.setImageResource(logoNum);

        // Creates list of loyalty program field/value pairs
        detailList.clear();

        User user = program.getUser();
        if (user != null) {
            detailList.add(new Detail("User", user.getName()));
        }

        detailList.add(new Detail("Name", program.getName()));
        detailList.add(new Detail("Type", program.getType()));
        detailList.add(new Detail("Account Number", program.getAccountNumber()));
        detailList.add(new Detail("Points", getNumberPattern().getNumberFormat(false).format(program.getPoints())));
        detailList.add(new Detail("Value", programValueString));

        // Creates adapter using program details list and sets to list
        DetailListAdapter adapter = new DetailListAdapter(this, detailList);
        lv.setAdapter(adapter);

        notificationButton.setChecked(program.getNotificationStatus() != NotificationStatus.UNMONITORED);

        // Updates notification button test and visability
        Date lastActivityDate = program.getLastActivityDate();
        Date expirationDate = program.getExpirationDate();
        String expirationOverride = program.getExpirationOverride();
        notificationButton.setVisibility(View.VISIBLE);
        if (!program.hasExpirationDate()) {
            notificationButton.setVisibility(View.GONE);
            if (expirationOverride != null) {
                detailList.add(new Detail("Expiration", expirationOverride));
            }
        } else if (expirationDate != null && lastActivityDate != null) {
            detailList.add(new Detail("Last Activity", dateFormat.format(lastActivityDate)));
            detailList.add(new Detail("Expiration", dateFormat.format(expirationDate)));
        }

        String notes = program.getNotes();
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
        // Opens ProgramAddEdit Activity
        Intent intent = new Intent(ProgramDetailActivity.this, ProgramAddEditActivity.class);
        intent.putExtra("PROGRAM_ID", programId);
        startActivity(intent);
    }

    // Runs when delete button is clicked
    @Override
    public void menuDeleteClicked() {

        // Creates delete warning dialog
        LoyaltyProgram program = programDS.getSingle(programId);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(program.getName());
        builder.setCancelable(false);
        builder.setMessage("Are you sure you want to delete?");

        // Deletes program if "Yes" button selected
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                appPreferences.setProgramFiltersUpdateRequired(true);
                programDS.delete(programId);
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