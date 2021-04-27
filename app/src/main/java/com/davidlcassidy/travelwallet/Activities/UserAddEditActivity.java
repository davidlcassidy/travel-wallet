/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
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
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Database.UserDataSource;
import com.davidlcassidy.travelwallet.R;

/*
UserAddEditActivity is use to add new users and to modify existing users.
For adding new users, it is created by the floating "add" button in the
UserListActivity and provided with an USER_ID = -1. For modifying existing users,
it is created by the menu "edit" button in the UserDetailActivity and provided with
a USER_ID matching the unique user id number in the MainDatabase. This activity is
comprised of several user attribute fields and a handful of value selection dialogs.
 */

public class UserAddEditActivity extends BaseActivity_Save {

    private UserDataSource userDS;
    private Integer userId;

    private TextView nameField, notesField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useraddedit);

        // Gets user ID from intent User ID of -1 means add new card
        userDS = UserDataSource.getInstance(this);
        userId = Integer.parseInt(getIntent().getStringExtra("USER_ID"));

        // Gets UserAddEdit activity fields
        nameField = findViewById(R.id.nameField);
        notesField = (EditText) findViewById(R.id.notesField);

        setClickListeners();
    }

    protected void onResume() {
        super.onResume();

        // If edit user, sets fields to current user values
        if (userId != -1) {
            User user = userDS.getSingle(userId, null, null);
            setTitle("Edit User");

            // Sets activity fields
            String oName = user.getName();
            String oNotes = user.getNotes();
            if (oName != null) {
                nameField.setText(oName);
            }
            if (oNotes != null) {
                notesField.setText(oNotes);
            }

        } else {
            setTitle("Add User");
        }
    }

    // Runs when save button is clicked
    @Override
    public void menuSaveClicked() {
        String userName = nameField.getText().toString().trim();
        String notes = notesField.getText().toString();

        if (userName.equals("")) {
            Toast.makeText(UserAddEditActivity.this, "Please type a name.", Toast.LENGTH_LONG).show();
        } else if (userId == -1) {
            userDS.create(userName, notes);
            appPreferences.setFiltersUpdateRequired(true);
            finish(); //Closes activity
            Toast.makeText(UserAddEditActivity.this, userName + " user added.", Toast.LENGTH_SHORT).show();

        } else {
            // Updates user if existing with new values from fields
            User user = userDS.getSingle(userId, null, null);
            user.setName(userName);
            user.setNotes(notes);
            userDS.update(user);
            appPreferences.setFiltersUpdateRequired(true);
            finish(); //Closes activity
            Toast.makeText(UserAddEditActivity.this, userName + " user updated.", Toast.LENGTH_SHORT).show();
        }
    }

    // Sets all click listeners so all label clicks match actions of field clicks
    private void setClickListeners() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        LinearLayout nameLayout = findViewById(R.id.nameLayout);
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
    }

}