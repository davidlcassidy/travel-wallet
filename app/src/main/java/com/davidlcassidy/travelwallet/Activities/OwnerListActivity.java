/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:50 PM
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

import com.davidlcassidy.travelwallet.Adapters.OwnerListAdapter;
import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_BackOnly;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.OwnerDataSource;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.AppType;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.R;

import java.util.ArrayList;

/*
OwnerListActivity displays a summary of the user added owners. It is primarily
composed of a listview utilizing the OwnerListAdapter. It also contains a
floating "add" button which creates OwnerAddEditActivity to allow new owners
to be added. Finally, there a textview that is only visible to the user when
there are no owners (empty listview), directing users to add new owners with
the "add" button.
 */

public class OwnerListActivity extends BaseActivity_BackOnly {

    private OwnerDataSource ownerDS;
    private ProgramDataSource programDS;
    private CardDataSource cardDS;
    private ArrayList<Owner> ownerList;
    private TextView emptyListText;
    private ListView lv;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ownerlist);
        setTitle("Current Owners");

        ownerDS = OwnerDataSource.getInstance(this);
        programDS = ProgramDataSource.getInstance(this);
        cardDS = CardDataSource.getInstance(this);

        // Sets the text used when owner list is empty
        emptyListText = (TextView) findViewById(R.id.emptyListText);
        emptyListText.setText("Click the + button below to add a owner.");

        // Sets owner click listener
        lv = (ListView) findViewById(R.id.ownerList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Opens CardDetail Activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ownerID = ownerList.get(position).getId().toString();
                Intent intent = new Intent(OwnerListActivity.this, OwnerDetailActivity.class);
                intent.putExtra("OWNER_ID", ownerID);
                startActivity(intent);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fabPlus);
        fab.setOnClickListener(new View.OnClickListener() {
            // Clicking floating "plus" button opens ProgramAddEdit Activity.
            // Program ID of -1 indicates a adding a new program instead of editing an existing one
            @Override
            public void onClick(View v) {
                AppType appType = userPreferences.getAppType();
                if (ownerList.size() >= 2 && appType == AppType.Free) {
                    showOwnerLimitPopup();
                } else {
                    Intent intent = new Intent(OwnerListActivity.this, OwnerAddEditActivity.class);
                    intent.putExtra("OWNER_ID", String.valueOf(-1));
                    startActivity(intent);
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();

        // Gets all owners sorted by field defined in user preferences
        ItemField sortField = userPreferences.getSetting_OwnerSortField();
        ownerList = ownerDS.getAll(sortField, programDS, cardDS);

        // Hides list and shows empty list text if there are no owners
        if (ownerList.size() == 0){
            emptyListText.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);

        } else {

            // Add owners to list view
            emptyListText.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);

            // Sets adaptor to list view
            OwnerListAdapter adapter = new OwnerListAdapter(OwnerListActivity.this, ownerList);
            lv.setAdapter(adapter);
        }
    }

    // Opens Owner Limit Popup
    public void showOwnerLimitPopup() {
        // Gets dialog layout
        LayoutInflater inflater = (LayoutInflater) this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_upgrade, null);

        // Creates dialog
        final AlertDialog diag = new AlertDialog.Builder(this).setView(v).create();

        // Sets title in dialog toolbar on top
        Toolbar toolBar1 = (Toolbar) v.findViewById(R.id.toolbar);
        toolBar1.setTitle("Owner Limit Reached");

        // Sets text in dialog
        TextView mainText = (TextView) v.findViewById(R.id.text);
        String text =
                "Travel Wallet it limited to only two owners.\n\nTo add additional owners and " +
                        "support our ongoing development, please upgrade to Travel Wallet Pro.";
        mainText.setText(text);

        // Runs with "Upgrade" button is clicked
        Button upgradeButton = (Button) v.findViewById(R.id.upgradeButton);
        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diag.dismiss();
                Intent intent = new Intent(OwnerListActivity.this, PurchaseProActivity.class);
                startActivity(intent);
            }
        });

        // Runs with "Close" button is clicked
        Button closeButton = (Button) v.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dialog is destroyed
                diag.dismiss();
            }
        });

        // Displays dialog
        diag.show();

        // Dims background while dialog is active
        diag.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

}