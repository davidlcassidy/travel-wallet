/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
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
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.Adapters.SingleChoiceAdapter;
import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Save;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Database.UserDataSource;
import com.davidlcassidy.travelwallet.Enums.ItemField;
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

    private ProgramDataSource programDS;
    private UserDataSource userDS;
    private Integer programId;

    private SimpleDateFormat dateFormat;

    private TextView userField, typeField, nameField, lastActivityField;
    private EditText accountNumberField, pointsField, notesField;
    private LinearLayout userLayout, lastActivityLayout;

    // Hides keyboard input
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programaddedit);

        programDS = ProgramDataSource.getInstance(this);
        userDS = UserDataSource.getInstance(this);

        // Gets program ID from intent. Program ID of -1 means add new program
        programId = getIntent().getIntExtra("PROGRAM_ID", -1);

        // Gets user defined data format
        dateFormat = appPreferences.getSetting_DatePattern().getDateFormat();

        // Gets ProgramAddEdit activity fields
        userField = findViewById(R.id.userField);
        typeField = findViewById(R.id.typeField);
        nameField = findViewById(R.id.nameField);
        accountNumberField = findViewById(R.id.accountNumberField);
        pointsField = findViewById(R.id.pointsField);
        lastActivityField = findViewById(R.id.lastActivityField);
        notesField = findViewById(R.id.notesField);

        setClickListeners();

        lastActivityLayout = findViewById(R.id.lastActivityLayout);
        lastActivityLayout.setVisibility(View.GONE);

        userLayout = findViewById(R.id.userLayout);
        int numberOfUsers = userDS.getAll(null, null, null).size();
        if (numberOfUsers > 0) {
            userLayout.setVisibility(View.VISIBLE);
        } else {
            userLayout.setVisibility(View.GONE);
        }
    }

    protected void onResume() {
        super.onResume();

        // If edit program, sets fields to current program values
        if (programId != -1) {
            LoyaltyProgram program = programDS.getSingle(programId);
            setTitle("Edit Loyalty Program");

            // Sets activity fields
            User pUser = program.getUser();
            String pType = program.getType();
            String pName = program.getName();
            String pAccountNumber = program.getAccountNumber();
            Integer pPoints = program.getPoints();
            Date pLastActivityDate = program.getLastActivityDate();
            String pNotes = program.getNotes();
            if (pUser != null) {
                userField.setText(pUser.getName());
            }
            if (pType != null) {
                typeField.setText(pType);
            }
            if (pName != null) {
                nameField.setText(pName);
            }
            if (pAccountNumber != null) {
                accountNumberField.setText(pAccountNumber);
            }
            if (pPoints != null) {
                pointsField.setText(String.valueOf(pPoints));
            }
            if (pLastActivityDate != null) {
                lastActivityField.setText(dateFormat.format(pLastActivityDate));
            }
            if (pNotes != null) {
                notesField.setText(pNotes);
            }


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
        String userName = userField.getText().toString();
        User user = userDS.getSingle(userName, null, null);

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

        } else if (programId == -1) {
            // Creates program if new
            programDS.create(programRefId, user, accountNumber, points, lastActivityDate, notes);
            appPreferences.setProgramFiltersUpdateRequired(true);
            finish(); //Closes activity
            Toast.makeText(ProgramAddEditActivity.this, programName + " program added.", Toast.LENGTH_SHORT).show();

        } else {
            // Updates program if existing with new values from fields
            LoyaltyProgram program = programDS.getSingle(programId);
            program.setRefId(programRefId);
            program.setUser(user);
            program.setAccountNumber(accountNumber);
            program.setPoints(points);
            program.setLastActivityDate(lastActivityDate);
            program.setNotes(notes);
            programDS.update(program);
            appPreferences.setProgramFiltersUpdateRequired(true);
            finish(); //Closes activity
            Toast.makeText(ProgramAddEditActivity.this, programName + " program updated.", Toast.LENGTH_SHORT).show();
        }
    }

    // Creates selection dialog
    private void fieldSelectDialog(final ItemField saveField) {

        // Set dialog title and selection items
        String title = null;
        ArrayList<String> selectionList = null;
        switch (saveField) {
            case USER_NAME:
                title = "Select User";
                selectionList = userDS.getAllNames();
                break;
            case TYPE:
                title = "Select Program Type";
                selectionList = programDS.getAvailableTypes(true);
                break;
            case PROGRAM_NAME:
                String type = typeField.getText().toString();
                selectionList = programDS.getAvailablePrograms(type, true, true);
                if (selectionList.size() > 0) {
                    title = "Select " + type + " Program";
                } else {
                    // Sends user a message if no program type was selected
                    Toast.makeText(this, "Please select a program type.", Toast.LENGTH_LONG).show();
                    return;
                }
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(ProgramAddEditActivity.this);
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
                        case USER_NAME:
                            userField.setText(selectedItem);
                            break;
                        case TYPE:
                            String currentType = typeField.getText().toString();
                            if (!currentType.equals(selectedItem)) {
                                typeField.setText(selectedItem);
                                nameField.setText("");
                            }
                            updateLastActivityFieldVisibility();
                            break;
                        case PROGRAM_NAME:
                            String selectedItem1 = selectedItem.split(SingleChoiceAdapter.getDelimiter())[0];
                            nameField.setText(selectedItem1);
                            updateLastActivityFieldVisibility();
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

    // Displays date picker dialog for user selection of last activity date
    private void lastActivityDateFieldClick() {

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
        if (lastActivityDate != null) {
            cal.setTime(lastActivityDate);
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Creates date picker
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
                }, year, month, day); // Sets date picker to current date

        datePicker.show();

        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }

    // Sets all click listeners so all label clicks match actions of field clicks
    private void setClickListeners() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final RelativeLayout mainLayout = findViewById(R.id.mainLayout);

        LinearLayout userLayout = findViewById(R.id.userLayout);
        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(ProgramAddEditActivity.this);
                fieldSelectDialog(ItemField.USER_NAME);
            }
        });

        LinearLayout typeLayout = findViewById(R.id.typeLayout);
        typeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(ProgramAddEditActivity.this);
                fieldSelectDialog(ItemField.TYPE);
            }
        });

        LinearLayout nameLayout = findViewById(R.id.nameLayout);
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(ProgramAddEditActivity.this);
                fieldSelectDialog(ItemField.PROGRAM_NAME);
            }
        });

        LinearLayout accountNumberLayout = findViewById(R.id.accountNumberLayout);
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

        LinearLayout pointsLayout = findViewById(R.id.pointsLayout);
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

        LinearLayout lastActivityLayout = findViewById(R.id.lastActivityLayout);
        lastActivityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(ProgramAddEditActivity.this);
                lastActivityDateFieldClick();
            }
        });
    }

}