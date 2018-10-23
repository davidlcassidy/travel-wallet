/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 10:51 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Save;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Database.OwnerDataSource;
import com.davidlcassidy.travelwallet.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
ProgramAddEditActivity is use to add new loyalty programs and to modify existing
loyalty programs. For adding new programs, it is created by the floating "add" button
in the ProgramListFragment (MainActivity) and provided with a PROGRAM_ID = -1. For
modifying existing programs, it is created by the menu "edit" button in the
ProgramDetailActivity and provided with a PROGRAM_ID matching the unique program id
number in the MainDatabase. This activity is comprised of several program attribute
fields and a handful of value selection dialogs.
 */

public class ProgramAddEditActivity extends BaseActivity_Save {

    private SimpleDateFormat dateFormat;
    private ProgramDataSource programDS;
    private OwnerDataSource ownerDS;
    private Integer programId;

    private TextView ownerField, typeField, nameField, lastActivityField;
    private EditText accountNumberField, pointsField, notesField;
    private LinearLayout ownerLayout,lastActivityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programaddedit);

        dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();
        programDS = ProgramDataSource.getInstance(this);
        ownerDS = OwnerDataSource.getInstance(this);
		
		// Gets program ID from intent. Program ID of -1 means add new program
        programId = getIntent().getIntExtra("PROGRAM_ID", -1);

		// Gets ProgramAddEdit activity fields
        ownerField = (TextView) findViewById(R.id.ownerField);
        typeField = (TextView) findViewById(R.id.typeField);
        nameField = (TextView) findViewById(R.id.nameField);
        accountNumberField = (EditText) findViewById(R.id.accountNumberField);
        pointsField = (EditText) findViewById(R.id.pointsField);
        lastActivityField = (TextView) findViewById(R.id.lastActivityField);
        notesField = (EditText) findViewById(R.id.notesField);

        setClickListeners();

        lastActivityLayout = (LinearLayout) findViewById(R.id.lastActivityLayout);
        lastActivityLayout.setVisibility(View.GONE);

        ownerLayout = (LinearLayout) findViewById(R.id.ownerLayout);
        int numberOfOwners = ownerDS.getAll(null, null, null).size();
        if (numberOfOwners > 0){
            ownerLayout.setVisibility(View.VISIBLE);
        } else {
            ownerLayout.setVisibility(View.GONE);
        }
    }

    protected void onResume() {
        super.onResume();

		// If edit program, sets fields to current program values
        if (programId != -1) {
            LoyaltyProgram program = programDS.getSingle(programId);
            setTitle("Edit Loyalty Program");

			// Sets activity fields
            Owner pOwner = program.getOwner();
            String pType = program.getType();
            String pName = program.getName();
            String pAccountNumber = program.getAccountNumber();
            Integer pPoints = program.getPoints();
            Date pLastActivityDate = program.getLastActivityDate();
            String pNotes = program.getNotes();
            if (pOwner != null) {ownerField.setText(pOwner.getName());}
            if (pType != null) {typeField.setText(pType);}
            if (pName != null) {nameField.setText(pName);}
            if (pAccountNumber != null) {accountNumberField.setText(pAccountNumber);}
            if (pPoints != null) {pointsField.setText(String.valueOf(pPoints));}
            if (pLastActivityDate != null) {lastActivityField.setText(dateFormat.format(pLastActivityDate));}
            if (pNotes != null) {notesField.setText(pNotes);}


            updateLastActivityFieldVisibility();

        } else {
            setTitle("Add Loyalty Program");
            pointsField.setText(String.valueOf(0));
        }
    }

	// Hides last activity date field for programs with no expiration date or when no program is selected
    private void updateLastActivityFieldVisibility() {
        String programType = typeField.getText().toString();
        String programName = nameField.getText().toString();
        if (programName.equals("")) {
            lastActivityLayout.setVisibility(View.GONE);
        } else {
            Integer inactivityExpiration = programDS.getProgramInactivityExpiration(programType, programName);
            if (inactivityExpiration != 999) {
                lastActivityLayout.setVisibility(View.VISIBLE);
            } else {
                lastActivityLayout.setVisibility(View.GONE);
            }
        }
    }

	// Runs when save button is clicked
    @Override
    public void menuSaveClicked() {
        String ownerName = ownerField.getText().toString();
        Owner owner = ownerDS.getSingle(ownerName, null, null);

        String programName = nameField.getText().toString();
        Integer programRefId = programDS.getProgramRefId(String.valueOf(typeField.getText()), programName);
        String accountNumber = accountNumberField.getText().toString();

        int points = 0;
        String pointsString = pointsField.getText().toString();
        if (!pointsString.equals("")) {
            points = Integer.valueOf(pointsField.getText().toString());
        }

        Date lastActivityDate = null;
        try {
            lastActivityDate = dateFormat.parse(lastActivityField.getText().toString());
        } catch (ParseException e) {
            lastActivityDate = null;
        }

        String notes = notesField.getText().toString();

        // Checks that loyalty program is selected
        if (programName.equals("")) {
            Toast.makeText(ProgramAddEditActivity.this, "Please select a loyalty program.", Toast.LENGTH_LONG).show();
			
		// Creates program if new
        } else if (programId == -1) {
            programDS.create(programRefId, owner, accountNumber, points, lastActivityDate, notes);
            userPreferences.setProgramFiltersUpdateRequired(true);
            finish(); //Closes activity
            Toast.makeText(ProgramAddEditActivity.this, programName + " program added.", Toast.LENGTH_SHORT).show();
			
		// Updates program if existing with new values from fields
        } else {
            LoyaltyProgram program = programDS.getSingle(programId);
            program.setRefId(programRefId);
            program.setOwner(owner);
            program.setAccountNumber(accountNumber);
            program.setPoints(points);
            program.setLastActivityDate(lastActivityDate);
            program.setNotes(notes);
            programDS.update(program);
            userPreferences.setProgramFiltersUpdateRequired(true);
            finish(); //Closes activity
            Toast.makeText(ProgramAddEditActivity.this, programName + " program updated.", Toast.LENGTH_SHORT).show();
        }
    }

    // Displays list of owners for user selection
    private void ownerFieldClick () {
        String title = "Select Owner";
        ArrayList<String> types = ownerDS.getAllNames();
        fieldSelectDialog(title, types, "owner");
    }

    // Displays list of program types for user selection
    private void typeFieldClick () {
        String title = "Select Program Type";
        ArrayList<String> types = programDS.getAvailableTypes(true);
        fieldSelectDialog(title, types, "type");
    }

	// Displays list of program types for user selection
    private void nameFieldClick () {
        String type = typeField.getText().toString();
        ArrayList<String> programs = programDS.getAvailablePrograms(type, true);
        if (programs.size() > 0){
			String title = "Select " + type + " Program";
            fieldSelectDialog(title, programs, "program");
        } else {
			
			// Sends user a message if no program type was selected
            Toast.makeText(this, "Please select a program type.", Toast.LENGTH_LONG).show();
        }
    }

	// Displays date picker dialog for user selection of last activity date
    private void lastActivityDateFieldClick(){

        // Sets currently selected date in dialog to the date in the field
        Calendar cal = Calendar.getInstance();
        Date lastActivityDate = null;
        try {
            lastActivityDate = dateFormat.parse(lastActivityField.getText().toString());
        } catch (ParseException e) {
            if (programId != -1) {
                LoyaltyProgram program = programDS.getSingle(programId);
                lastActivityDate = program.getLastActivityDate();
            }
        }
        if (lastActivityDate != null){
            cal.setTime(lastActivityDate);
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Create date picker
        DatePickerDialog datePicker =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    // Save date to program field
                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month,
                                          final int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.YEAR, year);
                        Date pickedDate = cal.getTime();
                        lastActivityField.setText(dateFormat.format(pickedDate));
                    }
                }, year, month, day); // set date picker to current date

        datePicker.show();

        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }

	// Creates standard list selection dialog
    private void fieldSelectDialog(String title, ArrayList<String> items, final String saveField) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProgramAddEditActivity.this);
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
                        case "owner":
                            ownerField.setText(selectedItem);
                            break;
                        case "type":
                            String currentType = typeField.getText().toString();
                            if (!currentType.equals(selectedItem)) {
                                typeField.setText(selectedItem);
                                nameField.setText("");
                            }
                            break;
                        case "program":
                            nameField.setText(selectedItem);
                            break;
                    }

                    updateLastActivityFieldVisibility();
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

	// Sets all click listeners so all label clicks match actions of field clicks
    private void setClickListeners(){
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        LinearLayout ownerLayout = (LinearLayout) findViewById(R.id.ownerLayout);
        ownerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(ProgramAddEditActivity.this);
                ownerFieldClick();
            }
        });

        LinearLayout typeLayout = (LinearLayout) findViewById(R.id.typeLayout);
        typeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(ProgramAddEditActivity.this);
                typeFieldClick();
            }
        });

        LinearLayout nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(ProgramAddEditActivity.this);
                nameFieldClick();
            }
        });

        LinearLayout accountNumberLayout = (LinearLayout) findViewById(R.id.accountNumberLayout);
        accountNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountNumberField.requestFocusFromTouch();
                imm.showSoftInput(accountNumberField, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        accountNumberField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountNumberField.requestFocusFromTouch();
                imm.showSoftInput(accountNumberField, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        LinearLayout pointsLayout = (LinearLayout) findViewById(R.id.pointsLayout);
        pointsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointsField.requestFocusFromTouch();
                imm.showSoftInput(pointsField, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        pointsField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointsField.requestFocusFromTouch();
                imm.showSoftInput(pointsField, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        LinearLayout lastActivityLayout = (LinearLayout) findViewById(R.id.lastActivityLayout);
        lastActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(ProgramAddEditActivity.this);
                lastActivityDateFieldClick();
            }
        });
    };

    // Hides keyboard input
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}