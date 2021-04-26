/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_BackOnly;
import com.davidlcassidy.travelwallet.Classes.Constants;
import com.davidlcassidy.travelwallet.Classes.GooglePlayStore;
import com.davidlcassidy.travelwallet.EnumTypes.AppType;
import com.davidlcassidy.travelwallet.R;

import java.util.List;

/*
PurchaseProActivity is used to allow user to view the features of Travel Wallet Pro
and contains a button to allowing for purchase from Google Play Store.
 */

public class PurchaseProActivity extends BaseActivity_BackOnly {

    private GooglePlayStore googlePlayStore;
    private TextView title;
    private TextView text;
    private Button button;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googlePlayStore = new GooglePlayStore(this);
        setContentView(R.layout.activity_textbutton);
        setTitle("Upgrade to Pro");

        title = (TextView) findViewById(R.id.title);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        title.setText("Travel Wallet Pro features:");

        text = (TextView) findViewById(R.id.text);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        button = (Button) findViewById(R.id.button);
        button.setText("Close");
        button.setBackgroundColor(getResources().getColor(R.color.gray));
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) { finish(); }
        });

        // Get pro price from store and add to text field
        if (appPreferences.getAppType() == AppType.FREE) {
            title.setText("Loading");
            text.setText("Please be patient while we connect to the Google Play Store.");
            button.setVisibility(View.GONE);

            SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult,
                                                 List<SkuDetails> skuDetailsList) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                            && skuDetailsList != null && skuDetailsList.size() > 0) {

                        SkuDetails skuDetails = skuDetailsList.get(0);
                        String proPrice = skuDetails.getPrice();
                        String currencyCode = skuDetails.getPriceCurrencyCode();

                        // Drop cents if zero
                        if (proPrice.endsWith(".00")) {
                            proPrice = proPrice.substring(0, proPrice.length() - 3);
                        }

                        title.setText("Travel Wallet Pro features:");
                        text.setText(
                                "*  Add unlimited loyalty programs\n" +
                                        "   (Free version limit = " + Constants.FREE_LOYALTY_PROGRAM_LIMIT + ")\n\n" +
                                        "*  Add unlimited credit cards\n" +
                                        "   (Free version limit = " + Constants.FREE_CREDIT_CARD_LIMIT + ")\n\n" +
                                        "*  Add unlimited users\n" +
                                        "   (Free version limit = " + Constants.FREE_USER_LIMIT + ")\n\n" +
                                        "*  Exclusive Gold color scheme\n\n" +
                                        "\nPro features will last forever.\n" +
                                        "All for only " + proPrice + " " + currencyCode + " (plus tax)."
                        );
                        button.setVisibility(View.VISIBLE);
                        button.setText("Purchase Pro");
                        button.setBackgroundColor(getThemeColor(R.attr.colorPrimary));
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                googlePlayStore.purchaseProduct(GooglePlayStore.PRO_PRODUCT_ID);
                            }
                        });
                    } else {
                        title.setText("Error");
                        text.setText("Unable to connect to the Google Play Store. Please try again later.");
                        button.setVisibility(View.VISIBLE);
                    }
                }
            };
            googlePlayStore.getProductDetails(GooglePlayStore.PRO_PRODUCT_ID, skuDetailsResponseListener);


            // Change title and text after successful purchase
        } else if (appPreferences.getAppType() != AppType.FREE) {
            setTitle("Travel Wallet Pro");

            title.setText("Thank you!");
            text.setText("Thank you for purchasing Travel Wallet Pro and supporting the ongoing " +
                    "app development. You can now enjoy all of the benefits of Travel Wallet Pro.");

            button.setVisibility(View.VISIBLE);
        }
    }
}
