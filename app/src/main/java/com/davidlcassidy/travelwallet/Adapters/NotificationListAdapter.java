/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Classes.Notification;
import com.davidlcassidy.travelwallet.R;

import java.util.List;

public class NotificationListAdapter extends ArrayAdapter<Notification> {

    public NotificationListAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_notification, parent, false);
        }
		
		// Gets the item at this position
        Notification notification = getItem(position);
        
		// Gets adapter fields
        ImageView logo = (ImageView) convertView.findViewById(R.id.logo);
        TextView name = (TextView) convertView.findViewById(R.id.notification);
        TextView message = (TextView) convertView.findViewById(R.id.message);

		// Gets logo resource from name
        Context context = logo.getContext();
        int logoNum = context.getResources().getIdentifier(notification.getIcon(), "drawable", context.getPackageName());
        logo.setImageResource(logoNum);

		// Sets field values, based on user preferences
        name.setText(notification.getHeader());
        message.setText(notification.getMessage());

        return convertView;
    }
}