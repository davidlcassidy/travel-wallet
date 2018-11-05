/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/15/18 1:18 AM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_BackOnly;
import com.davidlcassidy.travelwallet.EnumTypes.AppType;
import com.davidlcassidy.travelwallet.R;

/*
DevelopersNoteActivity is used to allow user to view the developer's notes for the app.
 */

public class DevelopersNoteActivity extends BaseActivity_BackOnly {

    private TextView title;
    private TextView text;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textbutton);
        setTitle("Developer's Note");

        // Set app name
        String appName;
        AppType appType = userPreferences.getAppType();
        if (appType == AppType.Pro) {
            appName = "Travel Wallet Pro";
        } else {
            appName = "Travel Wallet";
        }

        // Set app version
        String appVersion = null;
        try {
            appVersion = "v" + this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        title = (TextView) findViewById(R.id.title);
        title.setText(appName + " " + appVersion);

        text = (TextView) findViewById(R.id.text);
        text.setText(
                "I hope you find this app helpful for tracking your loyalty programs and " +
                        "credit cards. Please let me know if you find any value in this app. I " +
                        "love hearing back about my projects!\n" +
                        "\n" +
                        "Maintaining all of the backend data in the ever changing field of loyalty " +
                        "programs and credit cards is extremely time consuming and manually " +
                        "intensive. If you notice anything missing or incorrect, please feel " +
                        "free to shoot me an email so I can fix it.\n" +
                        "\n" +
                        "Also if you like the app, please leave a review on the app store " +
                        "and consider purchasing Travel Wallet Pro. It is a cheap one time " +
                        "purchase.\n" +
                        "\n" +
                        "Thank you and safe travels!\n" +
                        "\n" +
                        "Email:  travelwallet@davidlcassidy.com\n" +
                        "Website:  www.DavidLCassidy.com"
        );
        Linkify.addLinks(text, Linkify.ALL);

        button = (Button) findViewById(R.id.button);
        button.setVisibility(View.GONE);

    }
}