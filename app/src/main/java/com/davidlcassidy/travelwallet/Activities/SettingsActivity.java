/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.Adapters.SingleChoiceAdapter;
import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Save;
import com.davidlcassidy.travelwallet.Enums.AppType;
import com.davidlcassidy.travelwallet.Enums.ColorScheme;
import com.davidlcassidy.travelwallet.Enums.Country;
import com.davidlcassidy.travelwallet.Enums.Currency;
import com.davidlcassidy.travelwallet.Enums.DatePattern;
import com.davidlcassidy.travelwallet.Enums.Language;
import com.davidlcassidy.travelwallet.R;

import java.util.ArrayList;

/*
SettingsActivity is use to allow user to view and modify the settings in AppPreferencess. It is
created by the dropdown menu in the MainActivity and is comprised of several user preferences
fields and a handful of value selection dialogs.
 */

public class SettingsActivity extends BaseActivity_Save {

    private TextView initialSummaryField;
    private TextView phoneNotificationsField;
    private TextView countryField;
    private TextView languageField;
    private TextView currencyField;
    private TextView dateField;
    private TextView colorSchemeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        // Gets Settings activity fields
        initialSummaryField = findViewById(R.id.initialSummaryField);
        phoneNotificationsField = findViewById(R.id.phoneNotificationsField);
        countryField = findViewById(R.id.countryField);
        languageField = findViewById(R.id.languageField);
        currencyField = findViewById(R.id.currencyField);
        dateField = findViewById(R.id.dateField);
        colorSchemeField = findViewById(R.id.colorSchemeField);

        // Sets activity click listeners
        setClickListeners();

    }

    protected void onResume() {
        super.onResume();

        // Gets values from user preferences
        String initialSummary = appPreferences.getSetting_InitialSummary() ? "ON" : "OFF";
        String phoneNotifications = appPreferences.getSetting_PhoneNotifications() ? "ON" : "OFF";
        Country country = appPreferences.getSetting_Country();
        Language language = appPreferences.getSetting_Language();
        Currency currency = appPreferences.getSetting_Currency();
        DatePattern datePattern = appPreferences.getSetting_DatePattern();
        ColorScheme colorScheme = appPreferences.getSetting_ColorScheme();

        // Sets activity fields to values from user preferences
        initialSummaryField.setText(initialSummary);
        phoneNotificationsField.setText(phoneNotifications);
        countryField.setText(country.getName());
        languageField.setText(language.getName());
        currencyField.setText(currency.getName());
        dateField.setText(datePattern.getSampleDate());
        colorSchemeField.setText(colorScheme.getName());
    }

    // Updates user settings when save button is clicked
    @Override
    public void menuSaveClicked() {

        // Gets values from activity fields
        boolean initialSummary = initialSummaryField.getText().toString().equals("ON");
        boolean phoneNotifications = phoneNotificationsField.getText().toString().equals("ON");
        Country country = Country.fromName(countryField.getText().toString());
        Language language = Language.fromName(languageField.getText().toString());
        Currency currency = Currency.fromName(currencyField.getText().toString());
        DatePattern date = DatePattern.fromSampleDate(dateField.getText().toString());
        ColorScheme colorScheme = ColorScheme.fromName(colorSchemeField.getText().toString());

        // Saves values from activity fields to user preferences
        appPreferences.setSetting_InitialSummary(initialSummary);
        appPreferences.setSetting_PhoneNotifications(phoneNotifications);
        appPreferences.setSetting_Country(country);
        appPreferences.setSetting_Language(language);
        appPreferences.setSetting_Currency(currency);
        appPreferences.setSetting_DatePattern(date);
        appPreferences.setSetting_ColorScheme(colorScheme);

        appPreferences.setFiltersUpdateRequired(true);

        //Closes activity and sends success message to user
        finish();
        Toast.makeText(SettingsActivity.this, "Settings updated.", Toast.LENGTH_SHORT).show();
    }

    // Toggles initial summary on/off
    private void initialSummaryFieldClick() {
        String currentValue = initialSummaryField.getText().toString();
        if (currentValue.equals("ON")) {
            initialSummaryField.setText("OFF");
        } else if (currentValue.equals("OFF")) {
            initialSummaryField.setText("ON");
        }
    }

    // Toggles phone notifications on/off
    private void phoneNotificationsFieldClick() {
        String currentValue = phoneNotificationsField.getText().toString();
        if (currentValue.equals("ON")) {
            phoneNotificationsField.setText("OFF");
        } else if (currentValue.equals("OFF")) {
            phoneNotificationsField.setText("ON");
        }
    }

    // Creates selection dialog
    private void fieldSelectDialog(final String saveField) {

        // Set dialog title and selection items
        String title = null;
        ArrayList<String> selectionList = null;
        switch (saveField) {
            case "country":
                title = "Set Country";
                selectionList = Country.getAllNames();
                break;
            case "language":
                title = "Set Language";
                selectionList = Language.getAllNames();
                break;
            case "currency":
                title = "Set Currency";
                selectionList = Currency.getAllNames();
                break;
            case "date":
                title = "Set Date Format";
                selectionList = DatePattern.getAllSampleDates();
                break;
            case "color":
                title = "Set Color Scheme";
                selectionList = ColorScheme.getAllNames();
                break;
        }
        final ArrayList<String> finalSelectionList = selectionList;

        // Set adapter to listview in layout
        View layout = getLayoutInflater().inflate(R.layout.list_singlechoice, null);
        final ListView listView = layout.findViewById(R.id.listview_singlechoice);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final SingleChoiceAdapter adapter = new SingleChoiceAdapter(this, selectionList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedIndex(position);
                adapter.notifyDataSetChanged();
            }
        });

        // Creates dialog and set properties
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setView(layout);

        // Sets button actions
        builder.setNeutralButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                selected = listView.getCheckedItemPosition();
                if (selected != -1) {

                    // Sets field text to selected value
                    String selectedItem = finalSelectionList.get(selected);
                    switch (saveField) {
                        case "country":
                            countryField.setText(selectedItem);
                            break;
                        case "language":
                            languageField.setText(selectedItem);
                            break;
                        case "currency":
                            currencyField.setText(selectedItem);
                            break;
                        case "date":
                            dateField.setText(selectedItem);
                            break;
                        case "color":
                            boolean proApp = appPreferences.getAppType() != AppType.FREE;
                            boolean goldColorScheme = selectedItem == ColorScheme.Gold.getName();
                            if (proApp || !goldColorScheme) {
                                colorSchemeField.setText(selectedItem);
                            }
                            break;
                    }
                }
            }
        });

        // Creates dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Dims background while dialog is active
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    // Sets layout click listeners
    private void setClickListeners() {

        LinearLayout initialSummaryLayout = findViewById(R.id.initialSummaryLayout);
        initialSummaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialSummaryFieldClick();
            }
        });

        LinearLayout phoneNotificationsLayout = findViewById(R.id.phoneNotificationsLayout);
        phoneNotificationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNotificationsFieldClick();
            }
        });

        LinearLayout countryLayout = findViewById(R.id.countryLayout);
        countryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("country");
            }
        });

        LinearLayout languageLayout = findViewById(R.id.languageLayout);
        languageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("language");
            }
        });

        LinearLayout currencyLayout = findViewById(R.id.currencyLayout);
        currencyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("currency");
            }
        });

        LinearLayout dateLayout = findViewById(R.id.dateLayout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("date");
            }
        });

        LinearLayout colorLayout = findViewById(R.id.colorSchemeLayout);
        colorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("color");
            }
        });
    }
}