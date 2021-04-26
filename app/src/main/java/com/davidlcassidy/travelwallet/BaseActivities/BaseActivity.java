/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.BaseActivities;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.davidlcassidy.travelwallet.Classes.GooglePlayStore;
import com.davidlcassidy.travelwallet.Classes.AppPreferences;
import com.davidlcassidy.travelwallet.EnumTypes.ColorScheme;
import com.davidlcassidy.travelwallet.EnumTypes.AppType;
import com.davidlcassidy.travelwallet.R;

/*
BaseActivity is an abstract activity that is extended by all the minor base activities
and therefore, by extension, every activity contained within the app. It is used to
configure the layout scheme and toolbar.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolBar;
    protected AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreferences = AppPreferences.getInstance(this);

        // Checks for pro purchase in Google Play Store
        new GooglePlayStore(this);

        // Set app color scheme
        ColorScheme color = appPreferences.getSetting_ColorScheme();
        setTheme(color.getResourceId());
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
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    protected void onResume(){
        super.onResume();
        boolean recreateRequired = false;

        // If color scheme has been changed, recreate activity with new color scheme
        String currentTheme = getCurrentColorSchemeName();
        String newTheme = appPreferences.getSetting_ColorScheme().getName();
        if (!currentTheme.equals(newTheme)){
            recreateRequired = true;
        }

        if (recreateRequired){
            recreate();
        }
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

    // Gets name of color scheme in current theme
    private String getCurrentColorSchemeName(){
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.themeName, outValue, true);
        return (String) outValue.string;
    }

    // Gets theme specific color of attribute
    protected int getThemeColor(int attribute){
        int[] attributeArray = new int[] {attribute};
        return this.getTheme().obtainStyledAttributes(attributeArray).getColor(0, 0);
    }

    public String getAppName() {
        AppType appType = appPreferences.getAppType();
        if (appType == AppType.FREE) {
            return "Travel Wallet";
        } else {
            return "Travel Wallet Pro";
        }
    }

    public String getAppVersion(){
        try {
            return "v" + this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

}