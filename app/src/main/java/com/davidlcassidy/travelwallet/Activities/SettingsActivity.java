/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:50 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Save;
import com.davidlcassidy.travelwallet.EnumTypes.AppType;
import com.davidlcassidy.travelwallet.EnumTypes.ColorScheme;
import com.davidlcassidy.travelwallet.EnumTypes.Country;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.DatePattern;
import com.davidlcassidy.travelwallet.EnumTypes.Language;
import com.davidlcassidy.travelwallet.R;

import java.util.Arrays;
import java.util.List;

/*
SettingsActivity is use to allow user to view and modify the settings UserPreferencess. It is
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
        initialSummaryField = (TextView) findViewById(R.id.initialSummaryField);
        phoneNotificationsField = (TextView) findViewById(R.id.phoneNotificationsField);
        countryField = (TextView) findViewById(R.id.countryField);
        languageField = (TextView) findViewById(R.id.languageField);
        currencyField = (TextView) findViewById(R.id.currencyField);
        dateField = (TextView) findViewById(R.id.dateField);
        colorSchemeField = (TextView) findViewById(R.id.colorSchemeField);

		// Sets activity click listeners
        setClickListeners();

    }

    protected void onResume() {
        super.onResume();

		// Gets values from user preferences
        String initialSummary = userPreferences.getSetting_InitialSummary() ? "ON" : "OFF";
        String phoneNotifications = userPreferences.getSetting_PhoneNotifications() ? "ON" : "OFF";
        Country country = userPreferences.getSetting_Country();
        Language language = userPreferences.getSetting_Language();
        Currency currency = userPreferences.getSetting_Currency();
        DatePattern datePattern = userPreferences.getSetting_DatePattern();
        ColorScheme colorScheme = userPreferences.getSetting_ColorScheme();

		// Sets activity fields to values from user preferences
        initialSummaryField.setText(initialSummary);
        phoneNotificationsField.setText(phoneNotifications);
        countryField.setText(country.getName());
        languageField.setText(language.getName());
        currencyField.setText(currency.getName());
        dateField.setText(datePattern.getSampleDate());
        colorSchemeField.setText(colorScheme.getName());
    }

	// Runs when save button is clicked
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
        userPreferences.setSetting_InitialSummary(initialSummary);
        userPreferences.setSetting_PhoneNotifications(phoneNotifications);
        userPreferences.setSetting_Country(country);
        userPreferences.setSetting_Language(language);
        userPreferences.setSetting_Currency(currency);
        userPreferences.setSetting_DatePattern(date);
        userPreferences.setSetting_ColorScheme(colorScheme);

        userPreferences.setFiltersUpdateRequired(true);

		//Closes activity and sends success message to user
        finish();
        Toast.makeText(SettingsActivity.this, "Settings updated.", Toast.LENGTH_SHORT).show();
    }

	// Toggles initial summary on/off
    private void initialSummaryFieldClick () {
        String currentValue = initialSummaryField.getText().toString();
        if (currentValue.equals("ON")){
            initialSummaryField.setText("OFF");
        } else if (currentValue.equals("OFF")){
            initialSummaryField.setText("ON");
        }
    }

    // Toggles phone notifications on/off
    private void phoneNotificationsFieldClick () {
        String currentValue = phoneNotificationsField.getText().toString();
        if (currentValue.equals("ON")){
            phoneNotificationsField.setText("OFF");
        } else if (currentValue.equals("OFF")){
            phoneNotificationsField.setText("ON");
        }
    }

    // Displays list of countries for user selection
    private void countryFieldClick () {
        String title = "Set Country";
        List<String> selectionList = Arrays.asList(
                Country.USA.getName(),
                Country.CANADA.getName());
        fieldSelectDialog(title, selectionList, "country");
    }

	// Displays list of languages for user selection
    private void languageFieldClick () {
        String title = "Set Language";
        List<String> selectionList = Language.getAllNames();
        fieldSelectDialog(title, selectionList, "language");
    }

	// Displays list of currencies for user selection
    private void currencyFieldClick () {
        String title = "Set Currency";
        List<String> selectionList = Currency.getAllNames();
        fieldSelectDialog(title, selectionList, "currency");
    }

	// Displays list of date formats for user selection
    private void dateFieldClick () {
        String title = "Set Date Format";
        List<String> selectionList = DatePattern.getAllSampleDates();
        fieldSelectDialog(title, selectionList, "date");
    }

    // Displays list of color schemes for user selection
    private void colorFieldClick () {
        String title = "Set Color Scheme";
        List<String> selectionList = ColorScheme.getAllNames();
        fieldSelectDialog(title, selectionList, "color");
    }

	// Creates standard list selection dialog
    private void fieldSelectDialog(String title, List<String> items, final String saveField) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(title);
		builder.setCancelable(false);
        
		// Sets items available for selection
		final String [] itemsArray = items.toArray(new String[items.size()]);
		builder.setSingleChoiceItems(itemsArray, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                ;
            }
        });

		// Runs with "Ok" button is clicked
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                ListView lw = ((AlertDialog) dialog).getListView();
                selected = lw.getCheckedItemPosition();
                if (selected != -1) {
					
					// Sets field text to selected value
                    String selectedItem = itemsArray[selected];
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
                            boolean appTypeFree = userPreferences.getAppType() == AppType.Free;
                            boolean goldColorScheme = selectedItem == ColorScheme.Gold.getName();
                            if (!(appTypeFree && goldColorScheme)) {
                                colorSchemeField.setText(selectedItem);
                            }
                            break;
                    }
                }
            }});

		// Runs with "Cancel" button is clicked
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
			// Dialog closes with no further action
            @Override
            public void onClick(DialogInterface dialog, int selected) {
				;
			}
        });

		// Creates dialog
        AlertDialog dialog = builder.create();
        dialog.show();

		// Dims background while dialog is active
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

	// Sets layout click listeners
    private void setClickListeners (){

        LinearLayout initialSummaryLayout = (LinearLayout) findViewById(R.id.initialSummaryLayout);
        initialSummaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialSummaryFieldClick();
            }});

        LinearLayout phoneNotificationsLayout = (LinearLayout) findViewById(R.id.phoneNotificationsLayout);
        phoneNotificationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNotificationsFieldClick();
            }});

        LinearLayout countryLayout = (LinearLayout) findViewById(R.id.countryLayout);
        countryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryFieldClick();
            }});

        LinearLayout languageLayout = (LinearLayout) findViewById(R.id.languageLayout);
        languageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageFieldClick();
            }});

        LinearLayout currencyLayout = (LinearLayout) findViewById(R.id.currencyLayout);
        currencyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencyFieldClick();
            }});

        LinearLayout dateLayout = (LinearLayout) findViewById(R.id.dateLayout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFieldClick();
            }});

        LinearLayout colorLayout = (LinearLayout) findViewById(R.id.colorSchemeLayout);
        colorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorFieldClick();
            }});
    }
}