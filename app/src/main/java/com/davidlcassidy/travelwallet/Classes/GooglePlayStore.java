/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Classes;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.davidlcassidy.travelwallet.Enums.AppType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

/*
GooglePlayStore is a wrapper exposing purchasing functionality of Google Play Billing Library. It
is use to query details and purchase the items from the Google Play Store.
 */

public class GooglePlayStore implements PurchasesUpdatedListener {

    public static final String PRO_PRODUCT_ID = "travelwallet.pro";
    protected BillingClient billingClient;
    private final Context context;
    private final AppPreferences appPreferences;
    AcknowledgePurchaseResponseListener acknowledgePurchase = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // After purchase is acknowledged, update app type and restart activity
                appPreferences.setAppType(AppType.PRO);
                Activity activity = (Activity) context;
                activity.recreate();
            }
        }
    };

    public GooglePlayStore(final Context context) {
        this.context = context;
        this.appPreferences = AppPreferences.getInstance(context);

        // Establish connection to billing client
        billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(INAPP);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    handleCurrentPurchases(queryPurchases);
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(context.getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void purchaseProduct(final String productID) {

        if (billingClient.isReady()) {
            initiateNewPurchase(productID);
        }

        // Establish connection to billing client
        else {
            billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiateNewPurchase(productID);
                    } else {
                        Toast.makeText(context.getApplicationContext(), "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Toast.makeText(context.getApplicationContext(), "Service Disconnected ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void getProductDetails(String productID, final SkuDetailsResponseListener listener) {
        List<String> skuList = new ArrayList<>();
        skuList.add(productID);
        final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);

        if (billingClient.isReady()) {
            billingClient.querySkuDetailsAsync(params.build(), listener);
        }

        // Establish connection to billing client
        else {
            billingClient = BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        billingClient.querySkuDetailsAsync(params.build(), listener);
                    } else {
                        Toast.makeText(context.getApplicationContext(), "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Toast.makeText(context.getApplicationContext(), "Service Disconnected ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initiateNewPurchase(String productId) {
        List<String> skuList = new ArrayList<>();
        skuList.add(productId);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetailsList.get(0))
                                        .build();
                                billingClient.launchBillingFlow((Activity) context, flowParams);
                            } else {
                                Toast.makeText(context.getApplicationContext(), "Item not Found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context.getApplicationContext(),
                                    " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleCurrentPurchases(List<Purchase> purchases) {
        if (purchases.size() == 0) {
            appPreferences.setAppType(AppType.FREE);
        } else {
            for (Purchase purchase : purchases) {
                // Item is purchased
                if (PRO_PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                        // Invalid purchase
                        Toast.makeText(context.getApplicationContext(), "Error : invalid Purchase", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Item is purchased but not acknowledged
                    if (!purchase.isAcknowledged()) {
                        AcknowledgePurchaseParams acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchase.getPurchaseToken())
                                        .build();
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchase);
                    }
                    // Item is purchased and also acknowledged
                    else {
                        // Update app type status and restart activity
                        if (appPreferences.getAppType() == AppType.FREE) {
                            appPreferences.setAppType(AppType.PRO);
                            Activity activity = (Activity) context;
                            activity.recreate();
                        }
                    }
                }
                // Purchase is pending
                else if (PRO_PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                    Toast.makeText(context.getApplicationContext(),
                            "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        // New purchased item
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handleCurrentPurchases(purchases);
        }
        // Already purchased item
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(INAPP);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if (alreadyPurchases != null) {
                handleCurrentPurchases(alreadyPurchases);
            }
        }
        // Canceled purchase
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(context.getApplicationContext(), "Purchase Canceled", Toast.LENGTH_SHORT).show();
        }
        // Any other errors
        else {
            Toast.makeText(context.getApplicationContext(), "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            String base64Key1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAixtsWbAp6HVnO3rwOn1U6eBhen";
            String base64Key2 = "A/Xp/AT7NqALXCCb9dYt7DG+12JW5384Iex+JEM5d8beQoTBzj2hjavL58ex";
            String base64Key3 = "KoMPAlKAGcaJifu7ETnVxnPkwDbT+UpJEBQgd32xrMBZsMU9hbDn+lS6";
            String base64Key4 = "/W/GrmayRcCnJgA4C7tOLGfIrtPOfQrTcacM2OEqpv5EI/Mkwjw6vGhvka9Oa55IOH9bP9WZmOesN";
            String base64Key5 = "yyKS/xA0+F5jFkfHvg4a9Qx+/g4/MnDz5PYolde5eBYfo67ArX9ES7daAChVmkTljZKMi1eonZNJg8E1Q";
            String base64Key6 = "KKINupsOuaeDWgctdR3qYjcl2MOtbiTf98aSLkXN/QIDAQAB";
            String base64Key = base64Key1 + base64Key2 + base64Key3 + base64Key4 + base64Key5 + base64Key6;

            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

}