/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:50 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_BackOnly;
import com.davidlcassidy.travelwallet.IAB.PurchaseItem;
import com.davidlcassidy.travelwallet.EnumTypes.AppType;
import com.davidlcassidy.travelwallet.R;

/*
PurchaseProActivity is used to allow user to view the features of Travel Wallet Pro
and contains a button to allowing for purchase from Google Play Store.
 */

public class PurchaseProActivity extends BaseActivity_BackOnly {

    private TextView title;
    private TextView text;
    private Button button;
    private static final short REQUEST_PRO_PURCHASE = 101;

    // Unique product ID matching listing in Google Play Billing Library
    //final String PRODUCT_ID = "travelwallet.pro"; //TODO Switch back
    private final String PRODUCT_ID = "android.test.purchased"; //Used for local testing only

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpro);
        setTitle("Upgrade to Pro");

        title = (TextView) findViewById(R.id.getProTitle);
        title.setText("Travel Wallet Pro includes:");

        text = (TextView) findViewById(R.id.getProText);
        text.setText(
                "*  Unlimited loyalty programs\n" +
                "*  Unlimited credit cards\n" +
                "*  Unlimited owners\n" +
                "*  Exclusive Gold color scheme\n" +
                "*  More pro features coming soon!\n" +
                "\nPro features will last forever.\n" +
                "All for only $3.99 USD."
        );

        button = (Button) findViewById(R.id.getProButton);
        button.setBackgroundColor(getThemeColor(R.attr.colorPrimary));
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PurchaseItem.class);
                intent.putExtra("PRODUCT_ID", PRODUCT_ID);
                startActivityForResult(intent, REQUEST_PRO_PURCHASE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PRO_PURCHASE) {
            if(resultCode == Activity.RESULT_OK){
                userPreferences.setAppType(AppType.Pro);
                setTitle("Travel Wallet Pro");
                button.setVisibility(View.GONE);
                title.setText("Thank you!");
                text.setText("Thank you for purchasing Travel Wallet Pro and supporting our ongoing " +
                        "development. Please click the back button above to go back to " +
                        "the main screen and start enjoying all the benifits of Travel Wallet " +
                        "Pro.");
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }

    }

}
