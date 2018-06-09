package com.davidlcassidy.travelwallet.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Save;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
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

    private UserPreferences userPreferences;
    private SimpleDateFormat dateFormat;
    private ProgramDataSource programDS;
    private Integer programId;

    private EditText typeField;
    private EditText programField;
    private EditText accountNumberField;
    private EditText pointsField;
    private EditText lastActivityField;
    private LinearLayout lastActivityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programaddedit);

        userPreferences = UserPreferences.getInstance(this);
        dateFormat = userPreferences.getDatePattern().getDateFormat();
        programDS = ProgramDataSource.getInstance(this);
		
		// Gets program ID from intent. Program ID of -1 means add new program
        programId = Integer.parseInt(getIntent().getStringExtra("PROGRAM_ID"));

		// Gets ProgramAddEdit activity fields
        typeField = (EditText) findViewById(R.id.typeField);
        programField = (EditText) findViewById(R.id.programField);
        accountNumberField = (EditText) findViewById(R.id.accountNumberField);
        pointsField = (EditText) findViewById(R.id.pointsField);
        lastActivityField = (EditText) findViewById(R.id.lastActivityField);

        setClickListeners();

        lastActivityLayout = (LinearLayout) findViewById(R.id.lastActivityLayout);
        lastActivityLayout.setVisibility(View.GONE);
    }

    protected void onResume() {
        super.onResume();

		// If edit program, sets fields to current program values
        if (programId != -1) {
            LoyaltyProgram program = programDS.getSingle(programId);
            setTitle("Edit Loyalty Program");

			// Sets activity fields
            String pType = program.getType();
            String pName = program.getName();
            String pAccountNumber = program.getAccountNumber();
            Integer pPoints = program.getPoints();
            Date pLastActivityDate = program.getLastActivityDate();
            if (pType != null) {typeField.setText(pType);}
            if (pName != null) {programField.setText(pName);}
            if (pAccountNumber != null) {accountNumberField.setText(pAccountNumber);}
            if (pPoints != null) {pointsField.setText(String.valueOf(pPoints));}
            if (pLastActivityDate != null) {lastActivityField.setText(dateFormat.format(pLastActivityDate));}

            updateLastActivityFieldVisibility();

        } else {
            setTitle("Add Loyalty Program");
        }
    }

	// Hides last activity date field for programs with no expiration date or when no program is selected
    private void updateLastActivityFieldVisibility() {
        String programName = programField.getText().toString();
        if (programName.equals("")) {
            lastActivityLayout.setVisibility(View.GONE);
        } else {
            Integer inactivityExpiration = programDS.getProgramInactivityExpiration(programName);
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
        String programName = programField.getText().toString();
        Integer programRefId = programDS.getProgramRefId(programName);
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

		// Checks that loyalty program is selected
        if(programName.equals("")){
            Toast.makeText(ProgramAddEditActivity.this, "Please select a loyalty program.", Toast.LENGTH_LONG).show();
			
		// Creates program if new
        } else if (programId == -1){
            programDS.create(programRefId, accountNumber, points, lastActivityDate, "");
            finish(); //Closes activity
            Toast.makeText(ProgramAddEditActivity.this, programName + " program added.", Toast.LENGTH_SHORT).show();
			
		// Updates program if existing with new values from fields
        } else {
            LoyaltyProgram program = programDS.getSingle(programId);
            program.setProgramId(programRefId);
            program.setAccountNumber(accountNumber);
            program.setPoints(points);
            program.setLastActivityDate(lastActivityDate);
            programDS.update(program);
            finish(); //Closes activity
            Toast.makeText(ProgramAddEditActivity.this, programName + " program updated.", Toast.LENGTH_SHORT).show();
        }
    }

	// Displays list of program types for user selection
    private void typeFieldClick () {
        String title = "Select Program Type";
        ArrayList<String> types = programDS.getAvailableTypes(true);
        fieldSelectDialog(title, types, "type");
    }

	// Displays list of program types for user selection
    private void programFieldClick () {
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
        DialogFragment dialogFragment = new lastActivityDatePicker();
        dialogFragment.show(getFragmentManager(), "start_date_picker");
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
                        case "type":
                            String currentType = typeField.getText().toString();
                            if (!currentType.equals(selectedItem)) {
                                typeField.setText(selectedItem);
                                programField.setText("");
                            }
                            break;
                        case "program":
                            programField.setText(selectedItem);
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

	// Creates date picker dialog for program last activity date
    @SuppressLint("ValidFragment")
    private class lastActivityDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
			
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
			
			// Creates dialog
            DatePickerDialog dialog = new DatePickerDialog(ProgramAddEditActivity.this, this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            return dialog;
        }
		
		// Saves selected date to last activity date field
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.YEAR, year);
            Date pickedDate = cal.getTime();
            lastActivityField.setText(dateFormat.format(pickedDate));
        }
    }

	// Sets all click listeners so all label clicks match actions of field clicks
    private void setClickListeners(){
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        TextView typeLabel = (TextView) findViewById(R.id.typeLabel);
        typeLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeFieldClick();
            }});

        typeField.setInputType(InputType.TYPE_NULL);
        typeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(ProgramAddEditActivity.this);
                typeFieldClick();}
        });
        typeField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(ProgramAddEditActivity.this);
                    typeField.performClick();}
            }
        });

        TextView programLabel = (TextView) findViewById(R.id.programLabel);
        programLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programFieldClick();
            }});

        programField.setInputType(InputType.TYPE_NULL);
        programField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(ProgramAddEditActivity.this);
                programFieldClick();}
        });
        programField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(ProgramAddEditActivity.this);
                    programField.performClick();}
            }
        });

        TextView accountNumberLabel = (TextView) findViewById(R.id.accountNumberLabel);
        accountNumberLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountNumberField.performClick();
                accountNumberField.requestFocus();
                imm.showSoftInput(accountNumberField, InputMethodManager.SHOW_IMPLICIT);
            }});

        TextView pointsLabel = (TextView) findViewById(R.id.pointsLabel);
        pointsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointsField.performClick();
                pointsField.requestFocus();
                imm.showSoftInput(pointsField, InputMethodManager.SHOW_IMPLICIT);
            }});

        TextView lastActivityLabel = (TextView) findViewById(R.id.lastActivityLabel);
        lastActivityLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastActivityDateFieldClick();
            }});

        lastActivityField.setInputType(InputType.TYPE_NULL);
        lastActivityField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(ProgramAddEditActivity.this);
                lastActivityDateFieldClick();
            }
        });
        lastActivityField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(ProgramAddEditActivity.this);
                    lastActivityField.performClick();
                }
            }
        });
    };

	// Hides keyboard input
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}