/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_BackOnly;
import com.davidlcassidy.travelwallet.R;

/*
DevelopersNoteActivity is used to allow user to view the developer's notes for the app.
 */

public class DevelopersNoteActivity extends BaseActivity_BackOnly {

    private TextView title;
    private TextView text;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textbutton);
        setTitle("Developer's Note");

        title = findViewById(R.id.title);
        title.setText(getAppName() + " " + getAppVersion());

        text = findViewById(R.id.text);
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
                        "Email:  evilsushipirate@gmail.com"
        );
        Linkify.addLinks(text, Linkify.ALL);

        findViewById(R.id.button).setVisibility(View.GONE);

    }
}