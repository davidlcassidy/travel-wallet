/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:09 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Save;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;
import com.davidlcassidy.travelwallet.EnumTypes.AppType;
import com.davidlcassidy.travelwallet.EnumTypes.ColorScheme;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.DatePattern;
import com.davidlcassidy.travelwallet.EnumTypes.ItemType;
import com.davidlcassidy.travelwallet.EnumTypes.Language;
import com.davidlcassidy.travelwallet.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
SettingsActivity is use to allow user to view and modify UserPreferencess. It is
created by the menu "edit" button in the MainActivity and is comprised of several
user preferences fields and a handful of value selection dialogs.
 */

public class SettingsActivity extends BaseActivity_Save {

    private TextView ownerPrimaryField;
    private TextView ownerSortField;
    private TextView programPrimaryField;
    private TextView programSortField;
    private TextView programNotificationField;
    private TextView programFiltersField;
    private TextView cardPrimaryField;
    private TextView cardSortField;
    private TextView cardNotificationField;
    private TextView cardFiltersField;
    private TextView initialSummaryField;
    private TextView phoneNotificationsField;
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
        ownerPrimaryField = (TextView) findViewById(R.id.ownerPrimaryField);
        ownerSortField = (TextView) findViewById(R.id.ownerSortField);
        programPrimaryField = (TextView) findViewById(R.id.programPrimaryField);
        programSortField = (TextView) findViewById(R.id.programSortField);
        programNotificationField = (TextView) findViewById(R.id.programNotificationField);
        programFiltersField = (TextView) findViewById(R.id.programFiltersField);
        cardPrimaryField = (TextView) findViewById(R.id.cardPrimaryField);
        cardSortField = (TextView) findViewById(R.id.cardSortField);
        cardNotificationField = (TextView) findViewById(R.id.cardNotificationField);
        cardFiltersField = (TextView) findViewById(R.id.cardFiltersField);
        initialSummaryField = (TextView) findViewById(R.id.initialSummaryField);
        phoneNotificationsField = (TextView) findViewById(R.id.phoneNotificationsField);
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
        ItemField ownerPrimary = userPreferences.getSetting_OwnerPrimaryField();
        ItemField ownerSort = userPreferences.getSetting_OwnerSortField();
        ItemField programPrimary = userPreferences.getSetting_ProgramPrimaryField();
        ItemField programSort = userPreferences.getSetting_ProgramSortField();
        String programNotificationPeriod = userPreferences.getSetting_ProgramNotificationPeriod();
        String programFilters = userPreferences.getSetting_ProgramFilters() ? "ON" : "OFF";
        ItemField cardPrimary = userPreferences.getSetting_CardPrimaryField();
        ItemField cardSort = userPreferences.getSetting_CardSortField();
        String cardNotificationPeriod = userPreferences.getSetting_CardNotificationPeriod();
        String cardFilters = userPreferences.getSetting_CardFilters() ? "ON" : "OFF";
        String initialSummary = userPreferences.getSetting_InitialSummary() ? "ON" : "OFF";
        String phoneNotifications = userPreferences.getSetting_PhoneNotifications() ? "ON" : "OFF";
        Language language = userPreferences.getSetting_Language();
        Currency currency = userPreferences.getSetting_Currency();
        DatePattern datePattern = userPreferences.getSetting_DatePattern();
        ColorScheme colorScheme = userPreferences.getSetting_ColorScheme();

		// Sets activity fields to values from user preferences
        ownerPrimaryField.setText(ownerPrimary.getName());
        ownerSortField.setText(ownerSort.getName());
        programPrimaryField.setText(programPrimary.getName());
        programSortField.setText(programSort.getName());
        setNotificationField(ItemType.LOYALTY_PROGRAM, programNotificationPeriod);
        programFiltersField.setText(programFilters);
        cardPrimaryField.setText(cardPrimary.getName());
        cardSortField.setText(cardSort.getName());
        setNotificationField(ItemType.CREDIT_CARD, cardNotificationPeriod);
        cardFiltersField.setText(cardFilters);
        initialSummaryField.setText(initialSummary);
        phoneNotificationsField.setText(phoneNotifications);
        languageField.setText(language.getName());
        currencyField.setText(currency.getName());
        dateField.setText(datePattern.getSampleDate());
        colorSchemeField.setText(colorScheme.getName());
    }

	// Runs when save button is clicked
    @Override
    public void menuSaveClicked() {

		// Gets values from activity fields
        ItemField ownerPrimary = ItemField.fromName(ownerPrimaryField.getText().toString());
        ItemField ownerSort = ItemField.fromName(ownerSortField.getText().toString());
        ItemField programPrimary = ItemField.fromName(programPrimaryField.getText().toString());
        ItemField programSort = ItemField.fromName(programSortField.getText().toString());
        String programNotificationPeriod = getNotificationField(ItemType.LOYALTY_PROGRAM);
        boolean programFilters = programFiltersField.getText().toString().equals("ON");
        ItemField cardPrimary = ItemField.fromName(cardPrimaryField.getText().toString());
        ItemField cardSort = ItemField.fromName(cardSortField.getText().toString());
        String cardNotificationPeriod = getNotificationField(ItemType.CREDIT_CARD);
        boolean cardFilters = cardFiltersField.getText().toString().equals("ON");
        boolean initialSummary = initialSummaryField.getText().toString().equals("ON");
        boolean phoneNotifications = phoneNotificationsField.getText().toString().equals("ON");
        Language language = Language.fromName(languageField.getText().toString());
        Currency currency = Currency.fromName(currencyField.getText().toString());
        DatePattern date = DatePattern.fromSampleDate(dateField.getText().toString());
        ColorScheme colorScheme = ColorScheme.fromName(colorSchemeField.getText().toString());

		// Saves values from activity fields to user preferences
        userPreferences.setSetting_OwnerPrimaryField(ownerPrimary);
        userPreferences.setSetting_OwnerSortField(ownerSort);
        userPreferences.setSetting_ProgramPrimaryField(programPrimary);
        userPreferences.setSetting_ProgramSortField(programSort);
        userPreferences.setSetting_ProgramNotificationPeriod(programNotificationPeriod);
        userPreferences.setSetting_ProgramFilters(programFilters);
        userPreferences.setSetting_CardPrimaryField(cardPrimary);
        userPreferences.setSetting_CardSortField(cardSort);
        userPreferences.setSetting_CardNotificationPeriod(cardNotificationPeriod);
        userPreferences.setSetting_CardFilters(cardFilters);
        userPreferences.setSetting_InitialSummary(initialSummary);
        userPreferences.setSetting_PhoneNotifications(phoneNotifications);
        userPreferences.setSetting_Language(language);
        userPreferences.setSetting_Currency(currency);
        userPreferences.setSetting_DatePattern(date);
        userPreferences.setSetting_ColorScheme(colorScheme);

        userPreferences.setFiltersUpdateRequired(true);

		//Closes activity and sends success message to user
        finish();
        Toast.makeText(SettingsActivity.this, "Settings updated.", Toast.LENGTH_SHORT).show();
    }

    // Displays list of owner fields for user selection
    private void ownerPrimaryFieldClick () {
        String title = "Set Owner Primary Field";
        List<String> types = Arrays.asList(
                ItemField.ITEMCOUNTS.getName(),
                ItemField.PROGRAMSVALUE.getName(),
                ItemField.CREDITLIMIT.getName(),
                ItemField.CHASESTATUS.getName(),
                ItemField.OWNERNOTES.getName());
        fieldSelectDialog(title, types, "ownerPrimary");
    }

    // Displays list of owner fields for user selection
    private void ownerSortFieldClick () {
        String title = "Set Owner Sort Field";
        List<String> types = Arrays.asList(
                ItemField.OWNERNAME.getName(),
                ItemField.PROGRAMSVALUE.getName(),
                ItemField.CREDITLIMIT.getName());
        fieldSelectDialog(title, types, "ownerSort");
    }

	// Displays list of program fields for user selection
    private void programPrimaryFieldClick () {
        String title = "Set Program Primary Field";
        List<String> types = Arrays.asList(
                ItemField.ACCOUNTNUMBER.getName(),
                ItemField.POINTS.getName(),
                ItemField.VALUE.getName(),
                ItemField.EXPIRATIONDATE.getName(),
                ItemField.PROGRAMNOTES.getName());
        fieldSelectDialog(title, types, "programPrimary");
    }

	// Displays list of program fields for user selection
    private void programSortFieldClick () {
        String title = "Set Program Sort Field";
        List<String> types = Arrays.asList(
                ItemField.PROGRAMNAME.getName(),
                ItemField.POINTS.getName(),
                ItemField.VALUE.getName(),
                ItemField.EXPIRATIONDATE.getName());
        fieldSelectDialog(title, types, "programSort");
    }

    // Displays time period spinner dialog for user selection
    private void programNotificationFieldClick(){
        String title = "Set Program Expiration Notice";
        spinnerDialog(title, ItemType.LOYALTY_PROGRAM);
    }

    // Toggles program filters on/off
    private void programFiltersFieldClick () {
        String currentValue = programFiltersField.getText().toString();
        if (currentValue.equals("ON")){
            programFiltersField.setText("OFF");
        } else if (currentValue.equals("OFF")){
            programFiltersField.setText("ON");
        }
    }

	// Displays list of card fields for user selection
    private void cardPrimaryFieldClick () {
        String title = "Set Card Primary Field";
        List<String> types = Arrays.asList(
                ItemField.ANNUALFEE.getName(),
                ItemField.OPENDATE.getName(),
                ItemField.AFDATE.getName(),
                ItemField.CARDNOTES.getName());
        fieldSelectDialog(title, types, "cardPrimary");
    }

	// Displays list of card fields for user selection
    private void cardSortFieldClick () {
        String title = "Set Card Sort Field";
        List<String> types = Arrays.asList(
                ItemField.CARDNAME.getName(),
                ItemField.BANK.getName(),
                ItemField.ANNUALFEE.getName(),
                ItemField.OPENDATE.getName(),
                ItemField.AFDATE.getName());
        fieldSelectDialog(title, types, "cardSort");
    }

    // Displays time period spinner dialog for user selection
    private void cardNotificationFieldClick(){
        String title = "Set Card AF Notice";
        spinnerDialog(title, ItemType.CREDIT_CARD);
    }

    // Toggles card filters on/off
    private void cardFiltersFieldClick () {
        String currentValue = cardFiltersField.getText().toString();
        if (currentValue.equals("ON")){
            cardFiltersField.setText("OFF");
        } else if (currentValue.equals("OFF")){
            cardFiltersField.setText("ON");
        }
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

	// Displays list of languages for user selection
    private void languageFieldClick () {
        String title = "Set Language";
        List<String> types = Language.getAllNames();
        fieldSelectDialog(title, types, "language");
    }

	// Displays list of currencies for user selection
    private void currencyFieldClick () {
        String title = "Set Currency";
        List<String> types = Currency.getAllNames();
        fieldSelectDialog(title, types, "currency");
    }

	// Displays list of date formats for user selection
    private void dateFieldClick () {
        String title = "Set Date Format";
        List<String> types = DatePattern.getAllSampleDates();
        fieldSelectDialog(title, types, "date");
    }

    // Displays list of date formats for user selection
    private void colorFieldClick () {
        String title = "Set Color Scheme";
        List<String> types = ColorScheme.getAllNames();
        fieldSelectDialog(title, types, "color");
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
                        case "ownerPrimary":
                            ownerPrimaryField.setText(selectedItem);
                            break;
                        case "ownerSort":
                            ownerSortField.setText(selectedItem);
                            break;
                        case "programPrimary":
                            programPrimaryField.setText(selectedItem);
                            break;
                        case "programSort":
                            programSortField.setText(selectedItem);
                            break;
                        case "cardPrimary":
                            cardPrimaryField.setText(selectedItem);
                            break;
                        case "cardSort":
                            cardSortField.setText(selectedItem);
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

	// Creates standard time period spinner dialog
    private void spinnerDialog(String title, final ItemType itemType) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
		builder.setTitle(title);
        
		// Gets and inflates layout
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View npView = inflater.inflate(R.layout.dialog_alertperiodpicker, null);
		builder.setView(npView);
		
		// Creates new number picker
        final NumberPicker numPicker = (NumberPicker) npView.findViewById(R.id.numPicker);
        numPicker.setMinValue(1);
        numPicker.setMaxValue(50);
        final NumberPicker periodPicker = (NumberPicker) npView.findViewById(R.id.periodPicker);
        final ArrayList<String> periodList = new ArrayList<String>() {{
            add("Days");
            add("Weeks");
            add("Months");
        }};
        periodPicker.setMinValue(0);
        periodPicker.setMaxValue(periodList.size()-1);

		// Sets currently selected time period in dialog to the time period in the notification field
        String notificationPeriod = getNotificationField(itemType);
        String[] notificationArray = notificationPeriod.split(" ");
        numPicker.setValue(Integer.valueOf(notificationArray[0]));
        switch (notificationArray[1]) {
            case "D":
                periodPicker.setValue(0);
                break;
            case "W":
                periodPicker.setValue(1);
                break;
            case "M":
                periodPicker.setValue(2);
                break;
        }

		// Sets text in period picker
        periodPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return periodList.get(value);
            }
        });

        // Ugly bug fix for the missing formatting on first selected value in period picker
        Method method = null;
        try {
            method = periodPicker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(periodPicker, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Runs with "Ok" button is clicked
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
				// Sets notification field text to selected time period value
                String selectedNum = String.valueOf(numPicker.getValue());
                String selectedPeriod = String.valueOf(periodList.get(periodPicker.getValue()));
                if (!selectedNum.equals(-1) && !selectedPeriod.equals(-1)) {
                    setNotificationField(itemType, selectedNum + " " + selectedPeriod.charAt(0));
                }
            }
        });
		
		// Runs with "Cancel" button is clicked
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			// Dialog closes with no further action
            @Override
            public void onClick(DialogInterface dialog, int which){
                ;
            }
        });

		// Creates dialog
        AlertDialog dialog = builder.create();
        dialog.show();

		// Dims background while dialog is active
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

	// Sets notification field value from string. This was created to handle plurals.
    private void setNotificationField(ItemType itemType, String value) {
        String newValue = "";
		
		// Format time period value
        String[] fieldValueArray = value.split(" ");
        Integer num = Integer.valueOf(fieldValueArray[0]);
        switch (fieldValueArray[1]) {
            case "D":
                if (num == 1) {
                    newValue = "1 Day";
                } else {
                    newValue = num.toString() + " Days";
                }
                break;
            case "W":
                if (num == 1) {
                    newValue = "1 Week";
                } else {
                    newValue = num.toString() + " Weeks";
                }
                break;
            case "M":
                if (num == 1) {
                    newValue = "1 Month";
                } else {
                    newValue = num.toString() + " Months";
                }
                break;
        }

		// Save to notification field, depending on group
        switch (itemType.getName()) {
            case "Loyalty Program":
                programNotificationField.setText(newValue);
                break;
            case "Credit Card":
                cardNotificationField.setText(newValue);
                break;
        }
    }

	// Gets string value from notification field.  This was created to handle plurals.
    private String getNotificationField (ItemType itemType){
        String fieldValue = "";

		// Get value from notification field, depending on group
        switch (itemType.getName()) {
            case "Loyalty Program":
                fieldValue = programNotificationField.getText().toString();
                break;
            case "Credit Card":
                fieldValue = cardNotificationField.getText().toString();
                break;
        }

		// Format time period value
        String[] fieldValueArray = fieldValue.split(" ");
        fieldValueArray[1] = String.valueOf(fieldValueArray[1].charAt(0));
        return fieldValueArray[0] + " " + fieldValueArray[1];
    }

	// Sets layout click listeners
    private void setClickListeners (){

        LinearLayout ownerPrimaryLayout = (LinearLayout) findViewById(R.id.ownerPrimaryLayout);
        ownerPrimaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ownerPrimaryFieldClick();
            }});

        LinearLayout ownerSortLayout = (LinearLayout) findViewById(R.id.ownerSortLayout);
        ownerSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ownerSortFieldClick();
            }});

        LinearLayout programPrimaryLayout = (LinearLayout) findViewById(R.id.programPrimaryLayout);
        programPrimaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programPrimaryFieldClick();
            }});

        LinearLayout programSortLayout = (LinearLayout) findViewById(R.id.programSortLayout);
        programSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programSortFieldClick();
            }});

        LinearLayout programNotificationLayout = (LinearLayout) findViewById(R.id.programNotificationLayout);
        programNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programNotificationFieldClick();
            }});

        LinearLayout programFiltersLayout = (LinearLayout) findViewById(R.id.programFiltersLayout);
        programFiltersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programFiltersFieldClick();
            }});

        LinearLayout cardPrimaryLayout = (LinearLayout) findViewById(R.id.cardPrimaryLayout);
        cardPrimaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardPrimaryFieldClick();
            }});

        LinearLayout cardSortLayout = (LinearLayout) findViewById(R.id.cardSortLayout);
        cardSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSortFieldClick();
            }});

        LinearLayout cardNotificationLayout = (LinearLayout) findViewById(R.id.cardNotificationLayout);
        cardNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardNotificationFieldClick();
            }});

        LinearLayout cardFiltersLayout = (LinearLayout) findViewById(R.id.cardFiltersLayout);
        cardFiltersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardFiltersFieldClick();
            }});

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