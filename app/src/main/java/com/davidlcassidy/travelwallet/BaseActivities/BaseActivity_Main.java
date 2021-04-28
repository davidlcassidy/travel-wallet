/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.BaseActivities;

import android.view.Menu;
import android.view.MenuItem;

import com.davidlcassidy.travelwallet.R;

/*
BaseActivity_EditDelete is an abstract activity that is extended by MainActivity and is
used to set the menu items on the toolbar.
 */

public abstract class BaseActivity_Main extends BaseActivity {

    public abstract void menuUsersClicked();

    public abstract void menuDropdownClicked();

    // Sets menu layout
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Sets toolbar button actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Back button click closes current activity
            case android.R.id.home:
                finish();
                return true;

            // Summary button click executes abstract menuSummaryClicked() method
            case R.id.menu_users:
                menuUsersClicked();
                return true;

            // Settings button click executes abstract menuSettingsClicked() method
            case R.id.menu_dropdown:
                menuDropdownClicked();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
