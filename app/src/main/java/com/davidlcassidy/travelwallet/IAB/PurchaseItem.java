/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:40 PM
 */

package com.davidlcassidy.travelwallet.IAB;

import android.os.Bundle;

import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Purchase;

/*
PurchaseItem class is use to purchase items from the Google Play Store.
 */

public class PurchaseItem extends PurchaseWrapper {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Result is defaulted to cancelled in case anything fails before purchase is complete
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void handleIABSetupSuccess() {
        String productId = getIntent().getStringExtra("PRODUCT_ID");
        purchaseItem(productId);
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
