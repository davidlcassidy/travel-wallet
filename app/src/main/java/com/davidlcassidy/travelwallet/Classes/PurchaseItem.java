/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 11:29 PM
 */

package com.davidlcassidy.travelwallet.Classes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Purchase;

/*
PurchaseItem is a wrapper exposing purchasing functionality of IabHelper. It is use to purchase
the item with the unique product ID of PRODUCT_ID from the Google Play Store.
 */

public class PurchaseItem extends AppCompatActivity implements IabHelper.OnIabSetupFinishedListener, IabHelper.OnIabPurchaseFinishedListener {

    private IabHelper billingHelper;
    private String DeveloperPayload = "Tr4v3l_W411et_4pp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        billingHelper = new IabHelper(this, getBase64Key());
        billingHelper.startSetup(this);
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        if (result.isSuccess()) {
            purchaseItem();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        billingHelper.handleActivityResult(requestCode, resultCode, data);
    }

    protected void purchaseItem() {
        String productId = getIntent().getStringExtra("PRODUCT_ID");
        short requestCode = 123;
        billingHelper.launchPurchaseFlow(this, productId, requestCode, this, DeveloperPayload);
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if (result.isSuccess()) {

            // Consume purchase immediately after purchase
            billingHelper.consumeAsync(info, null);

            // For security, check info matches developer payload
            if (info.getDeveloperPayload().equals(DeveloperPayload)) {
                setResult(RESULT_OK);
                finish();
            } else {
                handlePurchaseFailed(result);
            }
        } else {
            handlePurchaseFailed(result);
        }
        finish();
    }

    protected void handlePurchaseFailed(IabResult result) {
        setResult(RESULT_CANCELED);
        finish();
    }


    @Override
    protected void onDestroy() {
        disposeBillingHelper();
        super.onDestroy();
    }

    private void disposeBillingHelper() {
        if (billingHelper != null) {
            billingHelper.dispose();
        }
        billingHelper = null;
    }

    // Generate base 64 key string
    public static String getBase64Key(){
        String base64Key1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAixtsWbAp6HVnO3rwOn1U6eBhen";
        String base64Key2 = "A/Xp/AT7NqALXCCb9dYt7DG+12JW5384Iex+JEM5d8beQoTBzj2hjavL58ex";
        String base64Key3 = "KoMPAlKAGcaJifu7ETnVxnPkwDbT+UpJEBQgd32xrMBZsMU9hbDn+lS6";
        String base64Key4 = "/W/GrmayRcCnJgA4C7tOLGfIrtPOfQrTcacM2OEqpv5EI/Mkwjw6vGhvka9Oa55IOH9bP9WZmOesN";
        String base64Key5 = "yyKS/xA0+F5jFkfHvg4a9Qx+/g4/MnDz5PYolde5eBYfo67ArX9ES7daAChVmkTljZKMi1eonZNJg8E1Q";
        String base64Key6 = "KKINupsOuaeDWgctdR3qYjcl2MOtbiTf98aSLkXN/QIDAQAB";
        return base64Key1 + base64Key2 + base64Key3 + base64Key4  + base64Key5 + base64Key6;
    }

}
