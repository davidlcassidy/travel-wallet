/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:29 PM
 */

/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:50 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_BackOnly;
import com.davidlcassidy.travelwallet.Classes.GooglePlayStore;
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
    private static final short REQUEST_QUERY_PRO = 101;
    private static final short REQUEST_PURCHASE_PRO = 102;

    // Unique product ID matching listing in Google Play Billing Library
    final String PRODUCT_ID = "travelwallet.pro";
    //private final String PRODUCT_ID = "android.test.purchased"; //Used for local testing only

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textbutton);
        setTitle("Upgrade to Pro");

        title = (TextView) findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        title.setText("Travel Wallet Pro features:");

        text = (TextView) findViewById(R.id.text);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        Intent intent = new Intent(getApplicationContext(), GooglePlayStore.class);
        intent.putExtra(GooglePlayStore.INTENT_ACTION, GooglePlayStore.ACTION_QUERY);
        intent.putExtra("PRODUCT_ID", PRODUCT_ID);
        startActivityForResult(intent, REQUEST_QUERY_PRO);

        button = (Button) findViewById(R.id.button);
        button.setText("Purchase Pro");
        button.setBackgroundColor(getThemeColor(R.attr.colorPrimary));
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GooglePlayStore.class);
                intent.putExtra(GooglePlayStore.INTENT_ACTION, GooglePlayStore.ACTION_PURCHASE);
                intent.putExtra("PRODUCT_ID", PRODUCT_ID);
                startActivityForResult(intent, REQUEST_PURCHASE_PRO);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Get pro price from store and add to text field
        if (requestCode == REQUEST_QUERY_PRO) {
            if(resultCode == Activity.RESULT_OK){
                // String proTitle = data.getStringExtra(GooglePlayStore.PRODUCT_TITLE);
                // String proDescription = data.getStringExtra(GooglePlayStore.PRODUCT_DESCRIPTION);
                // String proType = data.getStringExtra(GooglePlayStore.PRODUCT_TYPE);
                String proPrice = data.getStringExtra(GooglePlayStore.PRODUCT_PRICE);
                // boolean proPurchased = data.getIntExtra(GooglePlayStore.PRODUCT_PURCHASED, -1)==1;

                // Drop cents if zero
                if (proPrice.substring(proPrice.length() - 3).equals(".00")){
                    proPrice = proPrice.substring(0, proPrice.length() - 3);
                }

                text.setText(
                        "*  Add unlimited loyalty programs\n" +
                        "   (Free version limit = 10)\n\n" +
                        "*  Add unlimited credit cards\n" +
                        "   (Free version limit = 10)\n\n" +
                        "*  Add unlimited owners\n" +
                        "   (Free version limit = 2)\n\n" +
                        "*  Exclusive Gold color scheme\n\n" +
                        "\nPro features will last forever.\n" +
                        "All for only " + proPrice + " USD (plus tax)."
                );

            } else if (resultCode == Activity.RESULT_CANCELED) {
                finish();
                Toast.makeText(PurchaseProActivity.this, "Error connecting to Play Store.", Toast.LENGTH_SHORT).show();
            }

        // Change title and text after successful purchase
        } else if (requestCode == REQUEST_PURCHASE_PRO) {
            if(resultCode == Activity.RESULT_OK){
                userPreferences.setAppType(AppType.Pro);
                setTitle("Travel Wallet Pro");

                title.setText("Thank you!");
                text.setText("Thank you for purchasing Travel Wallet Pro and supporting the ongoing " +
                        "app development. You can now enjoy all of the benifits of Travel Wallet Pro.");

                button.setText("Close");
                button.setBackgroundColor(getResources().getColor(R.color.gray));
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) { finish(); }
                });
            } else if (resultCode == Activity.RESULT_CANCELED) { }
        }
    }
}
