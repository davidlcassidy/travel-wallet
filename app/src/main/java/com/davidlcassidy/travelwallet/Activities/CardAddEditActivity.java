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
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.UserDataSource;
import com.davidlcassidy.travelwallet.Enums.CardStatus;
import com.davidlcassidy.travelwallet.Enums.Country;
import com.davidlcassidy.travelwallet.Enums.Currency;
import com.davidlcassidy.travelwallet.Enums.ItemField;
import com.davidlcassidy.travelwallet.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
CardAddEditActivity is use to add new credit cards and to modify existing credit
cards. For adding new cards, it is created by the floating "add" button in the
CardListFragment (MainActivity) and provided with a CARD_ID = -1. For modifying
existing cards, it is created by the menu "edit" button in the CardDetailActivity
and provided with a CARD_ID matching the unique card id number in the MainDatabase.
This activity is comprised of several card attribute fields and a handful of value
selection dialogs.
 */

public class CardAddEditActivity extends BaseActivity_Save {

    private CardDataSource cardDS;
    private UserDataSource usersDS;
    private Integer cardId;

    private Country country;
    private Currency currency;
    private SimpleDateFormat dateFormat;

    private TextView userField, bankField, nameField, statusField, openDateField,
            afDateField, closeDateField;
    private EditText creditLimitField, notesField;
    private LinearLayout userLayout, afDateLayout, closeDateLayout;

    // Hides keyboard input
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardaddedit);

        cardDS = CardDataSource.getInstance(this);
        usersDS = UserDataSource.getInstance(this);

        // Gets card ID from intent. Card ID of -1 means add new card
        cardId = getIntent().getIntExtra("CARD_ID", -1);

        // Gets user defined data format
        dateFormat = appPreferences.getSetting_DatePattern().getDateFormat();

        // Gets CardAddEdit activity fields
        userField = findViewById(R.id.userField);
        bankField = findViewById(R.id.bankField);
        nameField = findViewById(R.id.nameField);
        statusField = findViewById(R.id.statusField);
        creditLimitField = findViewById(R.id.creditLimitField);
        openDateField = findViewById(R.id.openDateField);
        afDateField = findViewById(R.id.afDateField);
        closeDateField = findViewById(R.id.closeDateField);
        notesField = findViewById(R.id.notesField);

        setClickListeners();

        // Dynamically set layout visibility
        afDateLayout = findViewById(R.id.afDateLayout);
        afDateLayout.setVisibility(View.GONE);

        closeDateLayout = findViewById(R.id.closeDateLayout);
        closeDateLayout.setVisibility(View.GONE);

        userLayout = findViewById(R.id.userLayout);
        int numberOfUsers = usersDS.getAll(null, null, null).size();
        if (numberOfUsers > 0) {
            userLayout.setVisibility(View.VISIBLE);
        } else {
            userLayout.setVisibility(View.GONE);
        }
    }

    protected void onResume() {
        super.onResume();

        // Gets user defined country
        country = appPreferences.getSetting_Country();
        currency = appPreferences.getSetting_Currency();

        // If edit card, sets fields to current card values
        if (cardId != -1) {
            CreditCard card = cardDS.getSingle(cardId);
            setTitle("Edit Credit Card");

            // Sets activity fields
            User cUser = card.getUser();
            String cBank = card.getBank();
            String cName = card.getName();
            CardStatus cStatus = card.getStatus();
            BigDecimal cCreditLimit = card.getCreditLimit();
            Date cOpenDate = card.getOpenDate();
            Date cAFDate = card.getAfDate();
            Date cCloseDate = card.getCloseDate();
            String cNotes = card.getNotes();

            if (cUser != null) {
                userField.setText(cUser.getName());
            }
            if (cBank != null) {
                bankField.setText(cBank);
            }
            if (cName != null) {
                nameField.setText(cName);
            }
            if (cStatus != null) {
                statusField.setText(cStatus.getName());
            }

            if (cCreditLimit != null) {
                // Convert credit limit to user currency
                BigDecimal cLocalCreditLimit = cCreditLimit.multiply(currency.getExchangeRate()).setScale(0, RoundingMode.HALF_EVEN);
                creditLimitField.setText(String.valueOf(cLocalCreditLimit));
            }

            if (cOpenDate != null) {
                openDateField.setText(dateFormat.format(cOpenDate));
            }
            if (cAFDate != null) {
                afDateField.setText(dateFormat.format(cAFDate));
            }
            if (cCloseDate != null) {
                closeDateField.setText(dateFormat.format(cCloseDate));
            }
            if (cNotes != null) {
                notesField.setText(cNotes);
            }

            updateAnnualFeeDateFieldVisibility();
            updateCloseDateFieldVisibility();

        } else {
            setTitle("Add Credit Card");
            statusField.setText(CardStatus.OPEN.getName());
        }

    }

    // Hides close date field when card status is Open
    private void updateCloseDateFieldVisibility() {
        CardStatus cardStatus = CardStatus.fromName(statusField.getText().toString());
        if (cardStatus == CardStatus.OPEN) {
            closeDateLayout.setVisibility(View.GONE);
        } else {
            closeDateLayout.setVisibility(View.VISIBLE);
        }
    }

    // Hides annual fee date field for cards with no annual fee or when no card is selected
    private void updateAnnualFeeDateFieldVisibility() {
        String bankName = bankField.getText().toString();
        String cardName = nameField.getText().toString();
        if (cardName.equals("")) {
            afDateLayout.setVisibility(View.GONE);
        } else {
            BigDecimal annualFee = cardDS.getCardAnnualFee(bankName, cardName);
            if (annualFee.compareTo(BigDecimal.ZERO) == 0) {
                afDateLayout.setVisibility(View.GONE);
            } else {
                afDateLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    // Runs when save button is clicked
    @Override
    public void menuSaveClicked() {
        String userName = userField.getText().toString();
        User user = usersDS.getSingle(userName, null, null);

        String cardName = nameField.getText().toString();
        CardStatus cardStatus = CardStatus.fromName(statusField.getText().toString());
        if (cardStatus == null) {
            cardStatus = CardStatus.OPEN;
        }
        String creditLimitString = creditLimitField.getText().toString();
        BigDecimal creditLimit;
        if (creditLimitString.equals("")) {
            creditLimit = new BigDecimal(0);
        } else {
            creditLimit = new BigDecimal(creditLimitField.getText().toString());

            // Convert to USD for database
            creditLimit = creditLimit.divide(currency.getExchangeRate(), 8, RoundingMode.HALF_EVEN);
        }
        Date openDate;
        try {
            openDate = dateFormat.parse(openDateField.getText().toString());
        } catch (ParseException e) {
            openDate = null;
        }
        Date afDate;
        try {
            afDate = dateFormat.parse(afDateField.getText().toString());
        } catch (ParseException e) {
            afDate = null;
        }
        Date closeDate;
        try {
            closeDate = dateFormat.parse(closeDateField.getText().toString());
        } catch (ParseException e) {
            closeDate = null;
        }
        String notes = notesField.getText().toString();

        String bankString = String.valueOf(bankField.getText());
        Integer cardRefId = cardDS.getCardRefId(bankString, cardName);

        if (cardName.equals("")) {
            // Checks that credit card is selected
            Toast.makeText(CardAddEditActivity.this, "Please select a credit card.", Toast.LENGTH_LONG).show();

        } else if (cardId == -1) {
            // Creates card if new
            cardDS.create(cardRefId, user, cardStatus, creditLimit, openDate, afDate, closeDate, notes);
            appPreferences.setCardFiltersUpdateRequired(true);
            finish(); //Closes activity
            Toast.makeText(CardAddEditActivity.this, cardName + " card added.", Toast.LENGTH_SHORT).show();

        } else {
            // Updates card if existing with new values from fields
            CreditCard card = cardDS.getSingle(cardId);
            card.setRefId(cardRefId);
            card.setUser(user);
            card.setName(cardName);
            card.setStatus(cardStatus);
            card.setCreditLimit(creditLimit);
            card.setOpenDate(openDate);
            card.setAfDate(afDate);
            card.setCloseDate(closeDate);
            card.setNotes(notes);
            cardDS.update(card);
            appPreferences.setCardFiltersUpdateRequired(true);
            finish(); //Closes activity
            Toast.makeText(CardAddEditActivity.this, cardName + " card updated.", Toast.LENGTH_SHORT).show();
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
                selectionList = usersDS.getAllNames();
                break;
            case BANK:
                title = "Select Issuing Bank";
                selectionList = cardDS.getAvailableBanks(country, true);
                break;
            case CARD_NAME:
                String bank = bankField.getText().toString();
                selectionList = cardDS.getAvailableCards(country, bank, true);
                if (selectionList.size() > 0) {
                    title = "Select " + bank + " Card";
                } else {
                    // Sends user a message if no bank was selected
                    Toast.makeText(this, "Please select a bank.", Toast.LENGTH_LONG).show();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(CardAddEditActivity.this);
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
                        case BANK:
                            String currentBank = bankField.getText().toString();
                            if (!currentBank.equals(selectedItem)) {
                                bankField.setText(selectedItem);
                                nameField.setText("");
                            }
                            updateAnnualFeeDateFieldVisibility();
                            break;
                        case CARD_NAME:
                            nameField.setText(selectedItem);
                            updateAnnualFeeDateFieldVisibility();
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

    // Toggles between card statuses
    private void statusFieldClick() {
        CardStatus currentValue = CardStatus.fromName(statusField.getText().toString());
        if (currentValue == CardStatus.OPEN) {
            statusField.setText(CardStatus.CLOSED.getName());
        } else if (currentValue == CardStatus.CLOSED) {
            statusField.setText(CardStatus.OPEN.getName());
        }
        updateCloseDateFieldVisibility();
    }

    // Displays date picker dialog for user selection of card open date
    private void openDateFieldClick() {

        // Sets currently selected date in dialog to the date in the field
        Calendar cal = Calendar.getInstance();
        Date lastActivityDate = null;
        try {
            lastActivityDate = dateFormat.parse(openDateField.getText().toString());
        } catch (ParseException e) {
            if (cardId != -1) {
                CreditCard card = cardDS.getSingle(cardId);
                lastActivityDate = card.getOpenDate();
            }
        }
        if (lastActivityDate != null) {
            cal.setTime(lastActivityDate);
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Create date picker
        DatePickerDialog datePicker =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    // Save date to card field
                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month,
                                          final int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.YEAR, year);
                        Date pickedDate = cal.getTime();
                        openDateField.setText(dateFormat.format(pickedDate));

                        // If annual fee field is blank, sets to one year after open date
                        String currentAfDate = afDateField.getText().toString();
                        if (currentAfDate.equals("")) {
                            cal.add(Calendar.YEAR, 1);
                            Date newAfDate = cal.getTime();
                            afDateField.setText(dateFormat.format(newAfDate));
                        }
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

    // Displays date picker dialog for user selection of card annual fee date
    private void annualFeeDateFieldClick() {

        // Sets currently selected date in dialog to the date in the field
        Calendar cal = Calendar.getInstance();
        Date annualFeeDate = null;
        try {
            annualFeeDate = dateFormat.parse(afDateField.getText().toString());
        } catch (ParseException e) {
            if (cardId != -1) {
                CreditCard card = cardDS.getSingle(cardId);
                annualFeeDate = card.getAfDate();
            }
        }
        if (annualFeeDate != null) {
            cal.setTime(annualFeeDate);
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Create date picker
        DatePickerDialog datePicker =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    // Save date to card field
                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month,
                                          final int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.YEAR, year);
                        Date pickedDate = cal.getTime();
                        afDateField.setText(dateFormat.format(pickedDate));
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

    // Displays date picker dialog for user selection of card close date
    private void closeDateFieldClick() {

        // Sets currently selected date in dialog to the date in the field
        Calendar cal = Calendar.getInstance();
        Date closeDate = null;
        try {
            closeDate = dateFormat.parse(closeDateField.getText().toString());
        } catch (ParseException e) {
            if (cardId != -1) {
                CreditCard card = cardDS.getSingle(cardId);
                closeDate = card.getCloseDate();
            }
        }
        if (closeDate != null) {
            cal.setTime(closeDate);
        }
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Create date picker
        DatePickerDialog datePicker =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    // Save date to card field
                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month,
                                          final int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.YEAR, year);
                        Date pickedDate = cal.getTime();
                        closeDateField.setText(dateFormat.format(pickedDate));
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

    // Sets all click listeners so all label clicks match actions of field clicks
    private void setClickListeners() {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final RelativeLayout mainLayout = findViewById(R.id.mainLayout);

        LinearLayout userLayout = findViewById(R.id.userLayout);
        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(CardAddEditActivity.this);
                fieldSelectDialog(ItemField.USER_NAME);
            }
        });

        LinearLayout bankLayout = findViewById(R.id.bankLayout);
        bankLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(CardAddEditActivity.this);
                fieldSelectDialog(ItemField.BANK);
            }
        });

        LinearLayout nameLayout = findViewById(R.id.nameLayout);
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(CardAddEditActivity.this);
                fieldSelectDialog(ItemField.CARD_NAME);
            }
        });

        LinearLayout statusLayout = findViewById(R.id.statusLayout);
        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(CardAddEditActivity.this);
                statusFieldClick();
            }
        });

        LinearLayout creditLimitLayout = findViewById(R.id.creditLimitLayout);
        creditLimitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditLimitField.requestFocusFromTouch();
                imm.showSoftInput(creditLimitField, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        creditLimitField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditLimitField.requestFocusFromTouch();
                imm.showSoftInput(creditLimitField, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        LinearLayout openDateLayout = findViewById(R.id.openDateLayout);
        openDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(CardAddEditActivity.this);
                openDateFieldClick();
            }
        });

        LinearLayout afDateLayout = findViewById(R.id.afDateLayout);
        afDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(CardAddEditActivity.this);
                annualFeeDateFieldClick();
            }
        });

        LinearLayout closeDateLayout = findViewById(R.id.closeDateLayout);
        closeDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.requestFocus();
                hideSoftKeyboard(CardAddEditActivity.this);
                closeDateFieldClick();
            }
        });
    }

}