package com.davidlcassidy.travelwallet.IAB;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Purchase;

/*
PurchaseWrapper is a wrapper exposing purchasing functionality of IabHelper. This class
should be the parent of any activity requiring an app purchase.

 */

public abstract class PurchaseWrapper extends AppCompatActivity implements IabHelper.OnIabSetupFinishedListener, IabHelper.OnIabPurchaseFinishedListener {

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
            handleIABSetupSuccess();
        } else {
            handleIABSetupFailure();
        }
    }

    protected abstract void handleIABSetupSuccess();

    protected abstract void handleIABSetupFailure();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        billingHelper.handleActivityResult(requestCode, resultCode, data);
    }

    protected void purchaseItem(String sku) {
        short requestCode = 123;
        billingHelper.launchPurchaseFlow(this, sku, requestCode, this, DeveloperPayload);
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if (result.isSuccess()) {
            // Consume purchase after purchase
            billingHelper.consumeAsync(info, null);
            // For security, check info matches developer payload
            if (info.getDeveloperPayload().equals(DeveloperPayload)) {
                handlePurchaseSuccess(result, info);
            } else {
                handlePurchaseFailed(result);
            }
        } else {
            handlePurchaseFailed(result);
        }
        finish();
    }


    protected abstract void handlePurchaseSuccess(IabResult result, Purchase info);

    protected abstract void handlePurchaseFailed(IabResult result);


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
        String base64Key1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArFKsWOCguKlHkKngnwp3JFU/VF+ChVUNSgNlWdOXPKoW+";
        String base64Key2 = "ZEHZsWRD9CReNhiJoLvnkhlGioWyl729J9uNZF0j3qOAzTIBJpkDfYcG";
        String base64Key3 = "z8nYmUJrWcPxzGuQ7OMcIArJYJvpnd3bxafomhdqc1OzEhK/fEX26lbzh4HHvOpcFmrhfUjgkLvwzO/+";
        String base64Key4 = "mlvHAv9wUWMNplnEMrAOJkMyewz3qZuzAHY04m6OQYu/Xqp6cGtBXfntzx3UtaSY9bpjE";
        String base64Key5 = "0c3dGZQsf+4J9JtLqsGEfMf7JvAiqpBLeYwfNhYMrL7eeBlqTEN/pTkqJRsRdP24jxePWgA0VMK3R8ueh55/Tl0cvN3QIDAQAB";
        String fullBase64Key = base64Key1 + base64Key2 + base64Key3 + base64Key4 + base64Key5;

        char[] chars = fullBase64Key.toCharArray();
        for (int i = 0; i < chars.length; i++){
            char c = chars[i];
            if (Character.isUpperCase(c)) {
                chars[i] = Character.toLowerCase(c);
            } else if (Character.isLowerCase(c)) {
                chars[i] = Character.toUpperCase(c);
            }
        }
        return new String(chars);
    }
}
