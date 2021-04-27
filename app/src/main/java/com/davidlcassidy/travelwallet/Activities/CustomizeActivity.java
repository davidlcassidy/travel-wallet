/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
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
import com.davidlcassidy.travelwallet.Enums.Country;
import com.davidlcassidy.travelwallet.Enums.ItemField;
import com.davidlcassidy.travelwallet.Enums.ItemType;
import com.davidlcassidy.travelwallet.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
CustomizeActivity is use to allow user to view and modify the customization AppPreferencess. It is
created by the dropdown menu in the MainActivity and is comprised of several user preferences
fields and a handful of value selection dialogs.
 */

public class CustomizeActivity extends BaseActivity_Save {

    private TextView userPrimaryField;
    private TextView userSortField;
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
        userPrimaryField = findViewById(R.id.userPrimaryField);
        userSortField = findViewById(R.id.userSortField);
        programPrimaryField = findViewById(R.id.programPrimaryField);
        programSortField = findViewById(R.id.programSortField);
        programNotificationField = findViewById(R.id.programNotificationField);
        programFiltersField = findViewById(R.id.programFiltersField);
        cardPrimaryField = findViewById(R.id.cardPrimaryField);
        cardSortField = findViewById(R.id.cardSortField);
        cardNotificationField = findViewById(R.id.cardNotificationField);
        cardFiltersField = findViewById(R.id.cardFiltersField);

        // Sets activity click listeners
        setClickListeners();

    }

    protected void onResume() {
        super.onResume();

        // Gets values from user preferences
        ItemField userPrimary = appPreferences.getCustom_UserPrimaryField();
        ItemField userSort = appPreferences.getCustom_UserSortField();
        ItemField programPrimary = appPreferences.getCustom_ProgramPrimaryField();
        ItemField programSort = appPreferences.getCustom_ProgramSortField();
        String programNotificationPeriod = appPreferences.getCustom_ProgramNotificationPeriod();
        String programFilters = appPreferences.getCustom_ProgramFilters() ? "ON" : "OFF";
        ItemField cardPrimary = appPreferences.getCustom_CardPrimaryField();
        ItemField cardSort = appPreferences.getCustom_CardSortField();
        String cardNotificationPeriod = appPreferences.getCustom_CardNotificationPeriod();
        String cardFilters = appPreferences.getCustom_CardFilters() ? "ON" : "OFF";

        // Sets activity fields to values from user preferences
        userPrimaryField.setText(userPrimary.getName());
        userSortField.setText(userSort.getName());
        programPrimaryField.setText(programPrimary.getName());
        programSortField.setText(programSort.getName());
        setNotificationField(ItemType.LOYALTY_PROGRAM, programNotificationPeriod);
        programFiltersField.setText(programFilters);
        cardPrimaryField.setText(cardPrimary.getName());
        cardSortField.setText(cardSort.getName());
        setNotificationField(ItemType.CREDIT_CARD, cardNotificationPeriod);
        cardFiltersField.setText(cardFilters);
    }

    // Updates user customizations when save button is clicked
    @Override
    public void menuSaveClicked() {

        // Gets values from activity fields
        ItemField userPrimary = ItemField.fromName(userPrimaryField.getText().toString());
        ItemField userSort = ItemField.fromName(userSortField.getText().toString());
        ItemField programPrimary = ItemField.fromName(programPrimaryField.getText().toString());
        ItemField programSort = ItemField.fromName(programSortField.getText().toString());
        String programNotificationPeriod = getNotificationField(ItemType.LOYALTY_PROGRAM);
        boolean programFilters = programFiltersField.getText().toString().equals("ON");
        ItemField cardPrimary = ItemField.fromName(cardPrimaryField.getText().toString());
        ItemField cardSort = ItemField.fromName(cardSortField.getText().toString());
        String cardNotificationPeriod = getNotificationField(ItemType.CREDIT_CARD);
        boolean cardFilters = cardFiltersField.getText().toString().equals("ON");

        // Saves values from activity fields to user preferences
        appPreferences.setCustom_UserPrimaryField(userPrimary);
        appPreferences.setCustom_UserSortField(userSort);
        appPreferences.setCustom_ProgramPrimaryField(programPrimary);
        appPreferences.setCustom_ProgramSortField(programSort);
        appPreferences.setCustom_ProgramNotificationPeriod(programNotificationPeriod);
        appPreferences.setCustom_ProgramFilters(programFilters);
        appPreferences.setCustom_CardPrimaryField(cardPrimary);
        appPreferences.setCustom_CardSortField(cardSort);
        appPreferences.setCustom_CardNotificationPeriod(cardNotificationPeriod);
        appPreferences.setCustom_CardFilters(cardFilters);

        appPreferences.setFiltersUpdateRequired(true);

        //Closes activity and sends success message to user
        finish();
        Toast.makeText(CustomizeActivity.this, "Customizations updated.", Toast.LENGTH_SHORT).show();
    }

    // Displays time period spinner dialog for user selection
    private void programNotificationFieldClick() {
        String title = "Set Program Expiration Notice";
        spinnerDialog(title, ItemType.LOYALTY_PROGRAM);
    }

    // Toggles program filters on/off
    private void programFiltersFieldClick() {
        String currentValue = programFiltersField.getText().toString();
        if (currentValue.equals("ON")) {
            programFiltersField.setText("OFF");
        } else if (currentValue.equals("OFF")) {
            programFiltersField.setText("ON");
        }
    }

    // Displays time period spinner dialog for user selection
    private void cardNotificationFieldClick() {
        String selectionList = "Set Card AF Notice";
        spinnerDialog(selectionList, ItemType.CREDIT_CARD);
    }

    // Toggles card filters on/off
    private void cardFiltersFieldClick() {
        String currentValue = cardFiltersField.getText().toString();
        if (currentValue.equals("ON")) {
            cardFiltersField.setText("OFF");
        } else if (currentValue.equals("OFF")) {
            cardFiltersField.setText("ON");
        }
    }

    // Creates selection dialog
    private void fieldSelectDialog(final String saveField) {

        // Set dialog title and selection items
        String title = null;
        ArrayList<String> selectionList = new ArrayList<>();
        switch (saveField) {
            case "userPrimary":
                title = "Set User Primary Field";
                Country country = appPreferences.getSetting_Country();
                selectionList.add(ItemField.ITEM_COUNTS.getName());
                selectionList.add(ItemField.PROGRAMS_VALUE.getName());
                selectionList.add(ItemField.CREDIT_LIMIT.getName());
                selectionList.add(ItemField.CREDIT_LIMIT.getName());
                if (country == Country.USA) {
                    selectionList.add(ItemField.CHASE_STATUS.getName());
                }
                selectionList.add(ItemField.USER_NOTES.getName());
                break;
            case "userSort":
                title = "Set User Sort Field";
                selectionList.add(ItemField.USER_NAME.getName());
                selectionList.add(ItemField.PROGRAMS_VALUE.getName());
                selectionList.add(ItemField.CREDIT_LIMIT.getName());
                break;
            case "programPrimary":
                title = "Set Program Primary Field";
                selectionList.add(ItemField.ACCOUNT_NUMBER.getName());
                selectionList.add(ItemField.POINTS.getName());
                selectionList.add(ItemField.VALUE.getName());
                selectionList.add(ItemField.EXPIRATION_DATE.getName());
                selectionList.add(ItemField.PROGRAM_NOTES.getName());
                break;
            case "programSort":
                title = "Set Program Sort Field";
                selectionList.add(ItemField.PROGRAM_NAME.getName());
                selectionList.add(ItemField.POINTS.getName());
                selectionList.add(ItemField.VALUE.getName());
                selectionList.add(ItemField.EXPIRATION_DATE.getName());
                break;
            case "cardPrimary":
                title = "Set Card Primary Field";
                selectionList.add(ItemField.ANNUAL_FEE.getName());
                selectionList.add(ItemField.OPEN_DATE.getName());
                selectionList.add(ItemField.AF_DATE.getName());
                selectionList.add(ItemField.CARD_NOTES.getName());
                break;
            case "cardSort":
                title = "Set Card Sort Field";
                selectionList.add(ItemField.CARD_NAME.getName());
                selectionList.add(ItemField.BANK.getName());
                selectionList.add(ItemField.ANNUAL_FEE.getName());
                selectionList.add(ItemField.OPEN_DATE.getName());
                selectionList.add(ItemField.AF_DATE.getName());
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
                        case "userPrimary":
                            userPrimaryField.setText(selectedItem);
                            break;
                        case "userSort":
                            userSortField.setText(selectedItem);
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
        final NumberPicker numPicker = npView.findViewById(R.id.numPicker);
        numPicker.setMinValue(1);
        numPicker.setMaxValue(50);
        final NumberPicker periodPicker = npView.findViewById(R.id.periodPicker);
        final List<String> periodList = Arrays.asList("Days", "Weeks", "Months");
        periodPicker.setMinValue(0);
        periodPicker.setMaxValue(periodList.size() - 1);

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

        // Sets notification field text to selected time period value when "Ok" button is clicked
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedNum = String.valueOf(numPicker.getValue());
                String selectedPeriod = String.valueOf(periodList.get(periodPicker.getValue()));
                if (!selectedNum.equals(-1) && !selectedPeriod.equals(-1)) {
                    setNotificationField(itemType, selectedNum + " " + selectedPeriod.charAt(0));
                }
            }
        });


        // Closes dialog with no action when "Cancel" button is clicked
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        switch (itemType) {
            case LOYALTY_PROGRAM:
                programNotificationField.setText(newValue);
                break;
            case CREDIT_CARD:
                cardNotificationField.setText(newValue);
                break;
        }
    }

    // Gets string value from notification field.  This was created to handle plurals.
    private String getNotificationField(ItemType itemType) {
        String fieldValue = "";

        // Get value from notification field, depending on group
        switch (itemType) {
            case LOYALTY_PROGRAM:
                fieldValue = programNotificationField.getText().toString();
                break;
            case CREDIT_CARD:
                fieldValue = cardNotificationField.getText().toString();
                break;
        }

        // Format time period value
        String[] fieldValueArray = fieldValue.split(" ");
        fieldValueArray[1] = String.valueOf(fieldValueArray[1].charAt(0));
        return fieldValueArray[0] + " " + fieldValueArray[1];
    }

    // Sets layout click listeners
    private void setClickListeners() {

        LinearLayout userPrimaryLayout = findViewById(R.id.userPrimaryLayout);
        userPrimaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("userPrimary");
            }
        });

        LinearLayout userSortLayout = findViewById(R.id.userSortLayout);
        userSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("userSort");
            }
        });

        LinearLayout programPrimaryLayout = findViewById(R.id.programPrimaryLayout);
        programPrimaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("programPrimary");
            }
        });

        LinearLayout programSortLayout = findViewById(R.id.programSortLayout);
        programSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("programSort");
            }
        });

        LinearLayout programNotificationLayout = findViewById(R.id.programNotificationLayout);
        programNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programNotificationFieldClick();
            }
        });

        LinearLayout programFiltersLayout = findViewById(R.id.programFiltersLayout);
        programFiltersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programFiltersFieldClick();
            }
        });

        LinearLayout cardPrimaryLayout = findViewById(R.id.cardPrimaryLayout);
        cardPrimaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("cardPrimary");
            }
        });

        LinearLayout cardSortLayout = findViewById(R.id.cardSortLayout);
        cardSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fieldSelectDialog("cardSort");
            }
        });

        LinearLayout cardNotificationLayout = findViewById(R.id.cardNotificationLayout);
        cardNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardNotificationFieldClick();
            }
        });

        LinearLayout cardFiltersLayout = findViewById(R.id.cardFiltersLayout);
        cardFiltersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardFiltersFieldClick();
            }
        });
    }
}