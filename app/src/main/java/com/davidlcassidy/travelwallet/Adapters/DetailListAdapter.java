/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:29 PM
 */

/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/24/18 2:17 PM
 */

package com.davidlcassidy.travelwallet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Classes.Detail;
import com.davidlcassidy.travelwallet.R;

import java.util.List;

public class DetailListAdapter extends ArrayAdapter<Detail> {

    public DetailListAdapter(Context context, List<Detail> details) {
        super(context, 0, details);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_detail, parent, false);
        }
		
		// Gets the item at this position
        Detail detail = getItem(position);
		
        // Gets adapter fields
        TextView name = (TextView) convertView.findViewById(R.id.fieldName);
        TextView value = (TextView) convertView.findViewById(R.id.fieldValue);
        
		// Sets field values
        name.setText(detail.getName());
        value.setText(detail.getValue());
		
        return convertView;
    }

}