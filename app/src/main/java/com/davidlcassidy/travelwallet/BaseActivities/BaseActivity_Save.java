/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.BaseActivities;

import android.view.Menu;
import android.view.MenuItem;

import com.davidlcassidy.travelwallet.R;

/*
BaseActivity_Save is an abstract activity that is extended by ProgramAddEditActivity,
CardAddEditActivity, UserAddEditActivity, and SettingsActivity. It is used to set
the menu items on the toolbar.
 */

public abstract class BaseActivity_Save extends BaseActivity {

	public abstract void menuSaveClicked();

	// Sets menu layout
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
			
			// Back button click closes current activity
            case android.R.id.home:
                finish();
                return true;
				
			// Save button click executes abstract menuSaveClicked() method
            case R.id.menu_save:
                menuSaveClicked();
                return true;
				
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    

}