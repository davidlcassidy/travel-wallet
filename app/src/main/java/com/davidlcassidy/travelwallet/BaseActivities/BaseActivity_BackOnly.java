/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/24/18 2:17 PM
 */

package com.davidlcassidy.travelwallet.BaseActivities;

import android.view.Menu;
import android.view.MenuItem;

/*
BaseActivity_BackOnly is an abstract activity that is extended by OwnerListActivity.
It is used to set the back menu item on the toolbar.
 */

public abstract class BaseActivity_BackOnly extends BaseActivity {


	// Sets menu layout
    public boolean onCreateOptionsMenu(Menu menu) {
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
				
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}