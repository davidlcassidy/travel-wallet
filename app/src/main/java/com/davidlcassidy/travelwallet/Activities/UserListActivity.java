/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Adapters.UserListAdapter;
import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_BackOnly;
import com.davidlcassidy.travelwallet.Classes.Constants;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Database.UserDataSource;
import com.davidlcassidy.travelwallet.Enums.AppType;
import com.davidlcassidy.travelwallet.Enums.ItemField;
import com.davidlcassidy.travelwallet.R;

import java.util.ArrayList;

/*
UserListActivity displays a summary of the all added users. It is primarily
composed of a listview utilizing the UserListAdapter. It also contains a
floating "add" button which creates UserAddEditActivity to allow new users
to be added. Finally, there a textview that is only visible to the user when
there are no users (empty listview), directing the app users to add new users with
the "add" button.
 */

public class UserListActivity extends BaseActivity_BackOnly {

    private UserDataSource userDS;
    private ProgramDataSource programDS;
    private CardDataSource cardDS;
    private ArrayList<User> userList;
    private TextView emptyListText;
    private ListView lv;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);
        setTitle("Current Users");

        userDS = UserDataSource.getInstance(this);
        programDS = ProgramDataSource.getInstance(this);
        cardDS = CardDataSource.getInstance(this);

        // Sets the text used when user list is empty
        emptyListText = findViewById(R.id.emptyListText);
        emptyListText.setText("Click the + button below to add a user.");

        // Sets user click listener
        lv = findViewById(R.id.userList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Opens CardDetail Activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userID = userList.get(position).getId().toString();
                Intent intent = new Intent(UserListActivity.this, UserDetailActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
            }
        });

        fab = findViewById(R.id.fabPlus);
        fab.setOnClickListener(new View.OnClickListener() {
            // Clicking floating "plus" button opens ProgramAddEdit Activity.
            // Program ID of -1 indicates a adding a new program instead of editing an existing one
            @Override
            public void onClick(View v) {
                AppType appType = appPreferences.getAppType();
                if (userList.size() >= Constants.FREE_USER_LIMIT && appType == AppType.FREE) {
                    showUserLimitPopup();
                } else {
                    Intent intent = new Intent(UserListActivity.this, UserAddEditActivity.class);
                    intent.putExtra("USER_ID", String.valueOf(-1));
                    startActivity(intent);
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();

        // Gets all users sorted by field defined in user preferences
        ItemField sortField = appPreferences.getCustom_UserSortField();
        userList = userDS.getAll(sortField, programDS, cardDS);

        // Hides list and shows empty list text if there are no users
        if (userList.size() == 0) {
            emptyListText.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);

        } else {

            // Add users to list view
            emptyListText.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);

            // Sets adaptor to list view
            UserListAdapter adapter = new UserListAdapter(UserListActivity.this, userList);
            lv.setAdapter(adapter);
        }
    }

    // Opens User Limit Popup
    public void showUserLimitPopup() {
        // Gets dialog layout
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_upgrade, null);

        // Creates dialog
        final AlertDialog diag = new AlertDialog.Builder(this).setView(v).create();

        // Sets title in dialog toolbar on top
        Toolbar toolBar1 = v.findViewById(R.id.toolbar);
        toolBar1.setTitle("User Limit Reached");

        // Sets text in dialog
        TextView mainText = v.findViewById(R.id.text);
        String text =
                "Travel Wallet it limited to only " + Constants.FREE_USER_LIMIT +
                        " users.\n\nTo add additional users and support the ongoing app " +
                        "development, please upgrade to Travel Wallet Pro.";
        mainText.setText(text);

        // Opens PurchaseProActivity when "Upgrade" button is clicked
        Button upgradeButton = v.findViewById(R.id.upgradeButton);
        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diag.dismiss();
                Intent intent = new Intent(UserListActivity.this, PurchaseProActivity.class);
                startActivity(intent);
            }
        });

        // Closes dialog when "Close" button is clicked
        Button closeButton = v.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diag.dismiss();
            }
        });

        // Displays dialog
        diag.show();

        // Dims background while dialog is active
        diag.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

}