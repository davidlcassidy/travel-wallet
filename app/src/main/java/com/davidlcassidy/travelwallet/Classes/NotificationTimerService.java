/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Classes;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;

import java.util.Timer;
import java.util.TimerTask;

/*
NotificationTimerService class is used to update the notifications automatically after
regular time intervals defined by NOTIFY_INTERVAL.
 */

public class NotificationTimerService extends Service {

    // Sets time interval: 10 * 1000 = 10 seconds
    public static final long NOTIFY_INTERVAL = 10 * 1000;

    // Creates new thread for TimerService to run on to avoid crash
    private final Handler mHandler = new Handler();

    private Timer mTimer = null;
    private ProgramDataSource programDS;
    private CardDataSource cardDS;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        programDS = ProgramDataSource.getInstance(this);
        cardDS = CardDataSource.getInstance(this);

        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
        mTimer.scheduleAtFixedRate(new NotificationTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class NotificationTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    // When timer service is run, update program and card notifications
                    programDS.updateProgramsNotifications();
                    cardDS.updateCardsNotifications();

                }
            });
        }
    }
}