/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 6/24/18 2:17 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Save;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.DatePattern;
import com.davidlcassidy.travelwallet.EnumTypes.ItemType;
import com.davidlcassidy.travelwallet.EnumTypes.Language;
import com.davidlcassidy.travelwallet.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
SettingsActivity is use to allow user to view and modify UserPreferencess. It is
created by the menu "edit" button in the MainActivity and is comprised of several
user preferences fields and a handful of value selection dialogs.
 */

public class SettingsActivity extends BaseActivity_Save {

    private UserPreferences userPreferences;

    private TextView programNotificationField;
    private TextView programPrimaryField;
    private TextView programSortField;
    private TextView cardNotificationField;
    private TextView cardPrimaryField;
    private TextView cardSortField;
    private TextView ownerPrimaryField;
    private TextView ownerSortField;
    private TextView initialSummaryField;
    private TextView phoneNotificationsField;
    private TextView languageField;
    private TextView currencyField;
    private TextView dateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

		// Gets Settings activity fields
        userPreferences = UserPreferences.getInstance(this);
        programNotificationField = (TextView) findViewById(R.id.programNotificationField);
        programPrimaryField = (TextView) findViewById(R.id.programPrimaryField);
        programSortField = (TextView) findViewById(R.id.programSortField);
        cardNotificationField = (TextView) findViewById(R.id.cardNotificationField);
        cardPrimaryField = (TextView) findViewById(R.id.cardPrimaryField);
        cardSortField = (TextView) findViewById(R.id.cardSortField);
        ownerPrimaryField = (TextView) findViewById(R.id.ownerPrimaryField);
        ownerSortField = (TextView) findViewById(R.id.ownerSortField);
        initialSummaryField = (TextView) findViewById(R.id.initialSummaryField);
        phoneNotificationsField = (TextView) findViewById(R.id.phoneNotificationsField);
        languageField = (TextView) findViewById(R.id.languageField);
        currencyField = (TextView) findViewById(R.id.currencyField);
        dateField = (TextView) findViewById(R.id.dateField);

		// Sets activity click listeners
        setClickListeners();

    }

    protected void onResume() {
        super.onResume();

		// Gets values from user preferences
        String programNotificationPeriod = userPreferences.getSetting_ProgramNotificationPeriod();
        ItemField programPrimary = userPreferences.getSetting_ProgramPrimaryField();
        ItemField programSort = userPreferences.getSetting_ProgramSortField();
        String cardNotificationPeriod = userPreferences.getSetting_CardNotificationPeriod();
        ItemField cardPrimary = userPreferences.getSetting_CardPrimaryField();
        ItemField cardSort = userPreferences.getSetting_CardSortField();
        ItemField ownerPrimary = userPreferences.getSetting_OwnerPrimaryField();
        ItemField ownerSort = userPreferences.getSetting_OwnerSortField();
        String initialSummary = userPreferences.getSetting_InitialSummary() ? "Yes" : "No";
        String phoneNotifications = userPreferences.getSetting_PhoneNotifications() ? "Yes" : "No";
        Language language = userPreferences.getSetting_Language();
        Currency currency = userPreferences.getSetting_Currency();
        DatePattern datePattern = userPreferences.getSetting_DatePattern();

		// Sets activity fields to values from user preferences
        setNotificationField(ItemType.LOYALTY_PROGRAM, programNotificationPeriod);
        programPrimaryField.setText(programPrimary.getName());
        programSortField.setText(programSort.getName());
        setNotificationField(ItemType.CREDIT_CARD, cardNotificationPeriod);
        cardPrimaryField.setText(cardPrimary.getName());
        cardSortField.setText(cardSort.getName());
        ownerPrimaryField.setText(ownerPrimary.getName());
        ownerSortField.setText(ownerSort.getName());
        initialSummaryField.setText(initialSummary);
        phoneNotificationsField.setText(phoneNotifications);
        languageField.setText(language.getName());
        currencyField.setText(currency.getName());
        dateField.setText(datePattern.getSampleDate());
    }

	// Runs when save button is clicked
    @Override
    public void menuSaveClicked() {

		// Gets values from activity fields
        String programPrimary = programPrimaryField.getText().toString();
        String programSort = programSortField.getText().toString();
        String cardPrimary = cardPrimaryField.getText().toString();
        String cardSort = cardSortField.getText().toString();
        String ownerPrimary = ownerPrimaryField.getText().toString();
        String ownerSort = ownerSortField.getText().toString();
        String initialSummary = initialSummaryField.getText().toString();
        String phoneNotifications = phoneNotificationsField.getText().toString();
        String language = languageField.getText().toString();
        String date = dateField.getText().toString();
        String currency = currencyField.getText().toString();

		// Saves values from activity fields to user preferences
        userPreferences.setSetting_ProgramNotificationPeriod(getNotificationField(ItemType.LOYALTY_PROGRAM));
        userPreferences.setSetting_ProgramPrimaryField(ItemField.fromName(programPrimary));
        userPreferences.setSetting_ProgramSortField(ItemField.fromName(programSort));
        userPreferences.setSetting_CardNotificationPeriod(getNotificationField(ItemType.CREDIT_CARD));
        userPreferences.setSetting_CardPrimaryField(ItemField.fromName(cardPrimary));
        userPreferences.setSetting_CardSortField(ItemField.fromName(cardSort));
        userPreferences.setSetting_OwnerPrimaryField(ItemField.fromName(ownerPrimary));
        userPreferences.setSetting_OwnerSortField(ItemField.fromName(ownerSort));
        userPreferences.setSetting_InitialSummary(initialSummary.equals("Yes"));
        userPreferences.setSetting_PhoneNotifications(phoneNotifications.equals("Yes"));
        userPreferences.setSetting_Language(Language.fromName(language));
        userPreferences.setSetting_Currency(Currency.fromName(currency));
        userPreferences.setSetting_DatePattern(DatePattern.fromSampleDate(date));

		//Closes activity and sends success message to user
        finish();
        Toast.makeText(SettingsActivity.this, "Settings updated.", Toast.LENGTH_SHORT).show();
    }

	// Displays time period spinner dialog for user selection
    private void programNotificationFieldClick(){
        String title = "Select Program Expiration Notice";
        spinnerDialog(title, ItemType.LOYALTY_PROGRAM);
    }

	// Displays list of program fields for user selection
    private void programPrimaryFieldClick () {
        String title = "Select Program Primary Detail";
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
        String title = "Select Program Sort Detail";
        List<String> types = Arrays.asList(
                ItemField.PROGRAMNAME.getName(),
                ItemField.POINTS.getName(),
                ItemField.VALUE.getName(),
                ItemField.EXPIRATIONDATE.getName());
        fieldSelectDialog(title, types, "programSort");
    }

	// Displays time period spinner dialog for user selection
    private void cardNotificationFieldClick(){
        String title = "Select Card Expiration Notice";
        spinnerDialog(title, ItemType.CREDIT_CARD);
    }

	// Displays list of card fields for user selection
    private void cardPrimaryFieldClick () {
        String title = "Select Card Primary Detail";
        List<String> types = Arrays.asList(
                ItemField.ANNUALFEE.getName(),
                ItemField.OPENDATE.getName(),
                ItemField.AFDATE.getName(),
                ItemField.CARDNOTES.getName());
        fieldSelectDialog(title, types, "cardPrimary");
    }

	// Displays list of card fields for user selection
    private void cardSortFieldClick () {
        String title = "Select Card Sort Detail";
        List<String> types = Arrays.asList(
                ItemField.CARDNAME.getName(),
                ItemField.BANK.getName(),
                ItemField.ANNUALFEE.getName(),
                ItemField.OPENDATE.getName(),
                ItemField.AFDATE.getName());
        fieldSelectDialog(title, types, "cardSort");
    }

    // Displays list of owner fields for user selection
    private void ownerPrimaryFieldClick () {
        String title = "Select Owner Primary Detail";
        List<String> types = Arrays.asList(
                ItemField.ITEMCOUNTS.getName(),
                ItemField.PROGRAMSVALUE.getName(),
                ItemField.CREDITLIMIT.getName(),
                ItemField.CHASESTATUS.getName());
        fieldSelectDialog(title, types, "ownerPrimary");
    }

    // Displays list of owner fields for user selection
    private void ownerSortFieldClick () {
        String title = "Select Owner Sort Detail";
        List<String> types = Arrays.asList(
                ItemField.OWNERNAME.getName(),
                ItemField.PROGRAMSVALUE.getName(),
                ItemField.CREDITLIMIT.getName());
        fieldSelectDialog(title, types, "ownerSort");
    }

	// Displays yes/no option list for user selection
    private void initialSummaryFieldClick () {
        String currentValue = initialSummaryField.getText().toString();
        if (currentValue.equals("Yes")){
            initialSummaryField.setText("No");
        } else if (currentValue.equals("No")){
            initialSummaryField.setText("Yes");
        }
    }

	// Displays yes/no option list for user selection
    private void phoneNotificationsFieldClick () {
        String currentValue = phoneNotificationsField.getText().toString();
        if (currentValue.equals("Yes")){
            phoneNotificationsField.setText("No");
        } else if (currentValue.equals("No")){
            phoneNotificationsField.setText("Yes");
        }
    }

	// Displays list of languages for user selection
    private void languageFieldClick () {
        String title = "Select Language";
        List<String> types = Language.getAllNames();
        fieldSelectDialog(title, types, "language");
    }

	// Displays list of currencies for user selection
    private void currencyFieldClick () {
        String title = "Select Currency";
        List<String> types = Currency.getAllNames();
        fieldSelectDialog(title, types, "currency");
    }

	// Displays list of date formats for user selection
    private void dateFieldClick () {
        String title = "Select Date Format";
        List<String> types = DatePattern.getAllSampleDates();
        fieldSelectDialog(title, types, "date");
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
                        case "ownerPrimary":
                            ownerPrimaryField.setText(selectedItem);
                            break;
                        case "ownerSort":
                            ownerSortField.setText(selectedItem);
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

        LinearLayout programNotificationLayout = (LinearLayout) findViewById(R.id.programNotificationLayout);
        programNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programNotificationFieldClick();
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

        LinearLayout cardNotificationLayout = (LinearLayout) findViewById(R.id.cardNotificationLayout);
        cardNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardNotificationFieldClick();
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
    }
}