/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:29 PM
 */

/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:50 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.Adapters.SingleChoiceAdapter;
import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Save;
import com.davidlcassidy.travelwallet.EnumTypes.Country;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.ItemType;
import com.davidlcassidy.travelwallet.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
CustomizeActivity is use to allow user to view and modify the customization UserPreferencess. It is
created by the dropdown menu in the MainActivity and is comprised of several user preferences
fields and a handful of value selection dialogs.
 */

public class CustomizeActivity extends BaseActivity_Save {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
        setTitle("Customize");

		// Gets Customizations activity fields
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

		// Sets activity click listeners
        setClickListeners();

    }

    protected void onResume() {
        super.onResume();

		// Gets values from user preferences
        ItemField ownerPrimary = userPreferences.getCustom_OwnerPrimaryField();
        ItemField ownerSort = userPreferences.getCustom_OwnerSortField();
        ItemField programPrimary = userPreferences.getCustom_ProgramPrimaryField();
        ItemField programSort = userPreferences.getCustom_ProgramSortField();
        String programNotificationPeriod = userPreferences.getCustom_ProgramNotificationPeriod();
        String programFilters = userPreferences.getCustom_ProgramFilters() ? "ON" : "OFF";
        ItemField cardPrimary = userPreferences.getCustom_CardPrimaryField();
        ItemField cardSort = userPreferences.getCustom_CardSortField();
        String cardNotificationPeriod = userPreferences.getCustom_CardNotificationPeriod();
        String cardFilters = userPreferences.getCustom_CardFilters() ? "ON" : "OFF";

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

		// Saves values from activity fields to user preferences
        userPreferences.setCustom_OwnerPrimaryField(ownerPrimary);
        userPreferences.setCustom_OwnerSortField(ownerSort);
        userPreferences.setCustom_ProgramPrimaryField(programPrimary);
        userPreferences.setCustom_ProgramSortField(programSort);
        userPreferences.setCustom_ProgramNotificationPeriod(programNotificationPeriod);
        userPreferences.setCustom_ProgramFilters(programFilters);
        userPreferences.setCustom_CardPrimaryField(cardPrimary);
        userPreferences.setCustom_CardSortField(cardSort);
        userPreferences.setCustom_CardNotificationPeriod(cardNotificationPeriod);
        userPreferences.setCustom_CardFilters(cardFilters);

        userPreferences.setFiltersUpdateRequired(true);

		//Closes activity and sends success message to user
        finish();
        Toast.makeText(CustomizeActivity.this, "Customizations updated.", Toast.LENGTH_SHORT).show();
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

    // Displays time period spinner dialog for user selection
    private void cardNotificationFieldClick(){
        String selectionList = "Set Card AF Notice";
        spinnerDialog(selectionList, ItemType.CREDIT_CARD);
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

    // Creates selection dialog
    private void fieldSelectDialog(final String saveField) {

        // Set dialog title and selection items
        String title = null;
        ArrayList<String> selectionList = null;
        switch (saveField) {
            case "ownerPrimary":
                title = "Set Owner Primary Field";
                Country country = userPreferences.getSetting_Country();
                if (country == Country.USA) {
                    selectionList = new ArrayList<String>() {{
                        add(ItemField.ITEMCOUNTS.getName());
                        add(ItemField.PROGRAMSVALUE.getName());
                        add(ItemField.CREDITLIMIT.getName());
                        add(ItemField.CHASESTATUS.getName());
                        add(ItemField.OWNERNOTES.getName());
                    }};
                } else {
                    selectionList = new ArrayList<String>() {{
                        add(ItemField.ITEMCOUNTS.getName());
                        add(ItemField.PROGRAMSVALUE.getName());
                        add(ItemField.CREDITLIMIT.getName());
                        add(ItemField.OWNERNOTES.getName());
                    }};
                }
                break;
            case "ownerSort":
                title = "Set Owner Sort Field";
                selectionList = new ArrayList<String>() {{
                    add(ItemField.OWNERNAME.getName());
                    add(ItemField.PROGRAMSVALUE.getName());
                    add(ItemField.CREDITLIMIT.getName());
                }};
                break;
            case "programPrimary":
                title = "Set Program Primary Field";
                selectionList = new ArrayList<String>() {{
                    add(ItemField.ACCOUNTNUMBER.getName());
                    add(ItemField.POINTS.getName());
                    add(ItemField.VALUE.getName());
                    add(ItemField.EXPIRATIONDATE.getName());
                    add(ItemField.PROGRAMNOTES.getName());
                }};
                break;
            case "programSort":
                title = "Set Program Sort Field";
                selectionList = new ArrayList<String>() {{
                    add(ItemField.PROGRAMNAME.getName());
                    add(ItemField.POINTS.getName());
                    add(ItemField.VALUE.getName());
                    add(ItemField.EXPIRATIONDATE.getName());
                }};
                break;
            case "cardPrimary":
                title = "Set Card Primary Field";
                selectionList = new ArrayList<String>() {{
                    add(ItemField.ANNUALFEE.getName());
                    add(ItemField.OPENDATE.getName());
                    add(ItemField.AFDATE.getName());
                    add(ItemField.CARDNOTES.getName());
                }};
                break;
            case "cardSort":
                title = "Set Card Sort Field";
                selectionList = new ArrayList<String>() {{
                    add(ItemField.CARDNAME.getName());
                    add(ItemField.BANK.getName());
                    add(ItemField.ANNUALFEE.getName());
                    add(ItemField.OPENDATE.getName());
                    add(ItemField.AFDATE.getName());
                    List<String> selectionList = Arrays.asList(
                            ItemField.CARDNAME.getName(),
                            ItemField.BANK.getName(),
                            ItemField.ANNUALFEE.getName(),
                            ItemField.OPENDATE.getName(),
                            ItemField.AFDATE.getName());
                }};
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(CustomizeActivity.this);
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

	// Creates standard time period spinner dialog
    private void spinnerDialog(String title, final ItemType itemType) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CustomizeActivity.this);
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
                fieldSelectDialog("ownerPrimary");
            }});

        LinearLayout ownerSortLayout = (LinearLayout) findViewById(R.id.ownerSortLayout);
        ownerSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("ownerSort");
            }});

        LinearLayout programPrimaryLayout = (LinearLayout) findViewById(R.id.programPrimaryLayout);
        programPrimaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("programPrimary");
            }});

        LinearLayout programSortLayout = (LinearLayout) findViewById(R.id.programSortLayout);
        programSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("programSort");
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
                fieldSelectDialog("cardPrimary");
            }});

        LinearLayout cardSortLayout = (LinearLayout) findViewById(R.id.cardSortLayout);
        cardSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("cardSort");
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
    }
}