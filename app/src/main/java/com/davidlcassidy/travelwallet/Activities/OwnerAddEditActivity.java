/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:50 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Save;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.Database.OwnerDataSource;
import com.davidlcassidy.travelwallet.R;

/*
OwnerAddEditActivity is use to add new owners and to modify existing owners.
For adding new owners, it is created by the floating "add" button in the
OwnerListActivity and provided with an OWNER_ID = -1. For modifying existing owners,
it is created by the menu "edit" button in the OwnerDetailActivity and provided with
a OWNER_ID matching the unique owner id number in the MainDatabase. This activity is
comprised of several owner attribute fields and a handful of value selection dialogs.
 */

public class OwnerAddEditActivity extends BaseActivity_Save {

    private OwnerDataSource ownerDS;
    private Integer ownerId;

    private TextView nameField, notesField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owneraddedit);

		// Gets owner ID from intent. Owner ID of -1 means add new card
        ownerDS = OwnerDataSource.getInstance(this);
        ownerId = Integer.parseInt(getIntent().getStringExtra("OWNER_ID"));

		// Gets OwnerAddEdit activity fields
        nameField = (TextView) findViewById(R.id.nameField);
        notesField = (EditText) findViewById(R.id.notesField);

        setClickListeners();
    }

    protected void onResume() {
        super.onResume();

		// If edit owner, sets fields to current owner values
        if (ownerId != -1) {
            Owner owner = ownerDS.getSingle(ownerId, null, null);
            setTitle("Edit Owner");

			// Sets activity fields
            String oName = owner.getName();
            String oNotes = owner.getNotes();
            if (oName != null) {nameField.setText(oName);}
            if (oNotes != null) {notesField.setText(oNotes);}

        } else {
            setTitle("Add Owner");
        }
    }

	// Runs when save button is clicked
    @Override
    public void menuSaveClicked() {
        String ownerName = nameField.getText().toString().trim();
        String notes = notesField.getText().toString();

        if (ownerName.equals("")){
            Toast.makeText(OwnerAddEditActivity.this, "Please type a name.", Toast.LENGTH_LONG).show();
        }
		else if (ownerId == -1){
            ownerDS.create(ownerName, notes);
            userPreferences.setFiltersUpdateRequired(true);
            finish(); //Closes activity
            Toast.makeText(OwnerAddEditActivity.this, ownerName + " owner added.", Toast.LENGTH_SHORT).show();
			
		// Updates owner if existing with new values from fields
        } else {
            Owner owner = ownerDS.getSingle(ownerId, null, null);
            owner.setName(ownerName);
            owner.setNotes(notes);
            ownerDS.update(owner);
            userPreferences.setFiltersUpdateRequired(true);
            finish(); //Closes activity
            Toast.makeText(OwnerAddEditActivity.this, ownerName + " owner updated.", Toast.LENGTH_SHORT).show();
        }
    }

    // Sets all click listeners so all label clicks match actions of field clicks
    private void setClickListeners(){
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        LinearLayout nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameField.requestFocusFromTouch();
                imm.showSoftInput(nameField, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameField.requestFocusFromTouch();
                imm.showSoftInput(nameField, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    };
}