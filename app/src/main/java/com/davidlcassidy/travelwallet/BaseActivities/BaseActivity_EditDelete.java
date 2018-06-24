/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/24/18 2:17 PM
 */

package com.davidlcassidy.travelwallet.BaseActivities;

import android.view.Menu;
import android.view.MenuItem;

import com.davidlcassidy.travelwallet.R;

/*
BaseActivity_EditDelete is an abstract activity that is extended by
ProgramDetailActivity, CardDetailActivity, and OwnerDetailActivity.
It is used to set the menu items on the toolbar.
 */

public abstract class BaseActivity_EditDelete extends BaseActivity {

	public abstract void menuEditClicked();
    public abstract void menuDeleteClicked();

	// Sets menu layout
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editdelete, menu);
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
				
			// Edit button click executes abstract menuEditClicked() method
            case R.id.menu_edit:
                menuEditClicked();
                return true;
				
			// Delete button click executes abstract menuDeleteClicked() method
            case R.id.menu_delete:
                menuDeleteClicked();
                return true;
				
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}