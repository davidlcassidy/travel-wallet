/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.davidlcassidy.travelwallet.Activities.MainActivity;
import com.davidlcassidy.travelwallet.Enums.ItemType;
import com.davidlcassidy.travelwallet.R;

import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

/*
PhoneNotification class is created by Notification class and used to send a
notification to the user's mobile device.
 */

public class PhoneNotification {

    private static NotificationManager mNotificationManager;
    private static int NOTFICATION_ID;
    private final Context context;

    public PhoneNotification(Context parmContext, Notification notification) {

        // Generates a unique phone notification ID
        if (notification.getItemType() == ItemType.LOYALTY_PROGRAM) {
            NOTFICATION_ID = notification.getId() + 10000;
        } else if (notification.getItemType() == ItemType.CREDIT_CARD) {
            NOTFICATION_ID = notification.getId() + 20000;
        }

        // Collects two icons
        // Small Icon: Displayed in device status bar
        // Large Icon: Displayed in device notification
        int smallIcon = R.drawable.airplane_icon;
        String largeIconName = notification.getIcon();

        // Builds notification
        context = parmContext;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "channel_1")
                        .setSmallIcon(smallIcon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(largeIconName, "drawable", context.getPackageName())))
                        .setContentTitle(notification.getHeader())
                        .setContentText(notification.getMessage())
                        .setPriority(PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        // Sends notification
        mNotificationManager.notify(NOTFICATION_ID, mBuilder.build());

    }

    // Removes all phone notifications
    public static void ClearAll() {
        mNotificationManager.cancelAll();
    }

    // Removes specific phone notification, based on ID
    public void Clear() {
        mNotificationManager.cancel(NOTFICATION_ID);
    }

}
