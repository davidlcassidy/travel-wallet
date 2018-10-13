/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/12/18 11:51 PM
 */

package com.davidlcassidy.travelwallet.IAB;

import android.os.Bundle;

import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Purchase;

/*
PurchasePro is use to purchase Travel Wallet Pro from the Google Play Store.
 */

// TODO: Update class so SKU can be passed as an intent to make it modular
public class PurchasePro extends PurchaseWrapper {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Result is defaulted to cancelled in case anything fails before purchase is complete
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void handleIABSetupSuccess() {
        // Unique product ID matching listing in Google Play Billing Library
        //final String sku = "travelwallet.pro"; //TODO Switch back
        final String sku = "android.test.purchased"; //Used for local testing only
        purchaseItem(sku);
    }

    @Override
    protected void handleIABSetupFailure() {
        finish();
    }

    @Override
    protected void handlePurchaseSuccess(IabResult result, Purchase info) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void handlePurchaseFailed(IabResult result) {
        setResult(RESULT_CANCELED);
        finish();
    }

}
