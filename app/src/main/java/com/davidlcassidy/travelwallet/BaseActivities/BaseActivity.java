package com.davidlcassidy.travelwallet.BaseActivities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.davidlcassidy.travelwallet.R;

/*
BaseActivity is an abstract activity that is extended by all the minor base activities
and therefore, by extension, every activity contained within the app. It is used to
configure the layout scheme and toolbar.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
		
		// Configures base activity layout
        LinearLayout baseActivityLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) baseActivityLayout.findViewById(R.id.contentFrame);
        activityContainer.setBackgroundColor(Color.WHITE);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(baseActivityLayout);
		
		// Configures toolbar
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        setDisplayHomeEnabled(true);
		
		// Configures navigation icon (back button) on toolbar
        Drawable drawable = toolBar.getNavigationIcon();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
    }

	// Sets if action bar is enabled
	public void setDisplayHomeEnabled(boolean b) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(b);
        }
    }
	
	// Sets title in toolbar
    @Override
    public void setTitle(CharSequence title) {
        toolBar.setTitle(title);
        setSupportActionBar(toolBar);
    }
}