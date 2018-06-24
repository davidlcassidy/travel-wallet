/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/24/18 2:17 PM
 */

package com.davidlcassidy.travelwallet.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.R;

import java.util.ArrayList;

public class FilterSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private ArrayList<String> items;

    public FilterSpinnerAdapter(Context activity, ArrayList<String> items) {
        this.items=items;
        this.activity = activity;
    }

    public int getCount()
    {
        return items.size();
    }

    public Object getItem(int i)
    {
        return items.get(i);
    }

    public long getItemId(int i)
    {
        return (long)i;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(activity);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(items.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

    public View getView(int i, View view, ViewGroup viewgroup) {
        TextView txt = new TextView(activity);
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(16);
        if (getCount() > 1) {
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
        }
        txt.setText(items.get(i));
        txt.setMaxLines(1);
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

}
