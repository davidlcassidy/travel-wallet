package com.davidlcassidy.travelwallet.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.CardStatus;
import com.davidlcassidy.travelwallet.R;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;

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

    private UserPreferences userPreferences;
    private SimpleDateFormat dateFormat;
    private CardDataSource cardDS;
    private Integer cardId;

    private EditText bankField;
    private EditText cardField;
    private EditText openDateField;
    private EditText afDateField;
    private LinearLayout afDateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardaddedit);

        userPreferences = UserPreferences.getInstance(this);
        dateFormat = userPreferences.getDatePattern().getDateFormat();
        cardDS = CardDataSource.getInstance(this);
		
		// Gets card ID from intent. Card ID of -1 means add new card
        cardId = Integer.parseInt(getIntent().getStringExtra("CARD_ID"));

		// Gets CardAddEdit activity fields
        bankField = (EditText) findViewById(R.id.bankField);
        cardField = (EditText) findViewById(R.id.cardField);
        openDateField = (EditText) findViewById(R.id.openDateField);
        afDateField = (EditText) findViewById(R.id.afDateField);

        setClickListeners();

        afDateLayout = (LinearLayout) findViewById(R.id.afDateLayout);
        afDateLayout.setVisibility(View.GONE);
    }

    protected void onResume() {
        super.onResume();

		// If edit card, sets fields to current card values
        if (cardId != -1) {
            CreditCard card = cardDS.getSingle(cardId);
            setTitle("Edit Credit Card");

			// Sets activity fields
            String cBank = card.getBank();
            String cName = card.getName();
            Date cOpenDate = card.getOpenDate();
            Date cAFDate = card.getAfDate();
            if (cBank != null) {bankField.setText(cBank);}
            if (cName != null) {cardField.setText(cName);}
            if (cOpenDate != null) {openDateField.setText(dateFormat.format(cOpenDate));}
            if (cAFDate != null) {afDateField.setText(dateFormat.format(cAFDate));}

            updateAnnualFeeDateFieldVisibility();

        } else {
            setTitle("Add Credit Card");
        }
    }

	// Hides annual fee date field for cards with no annual fee or when no card is selected
    private void updateAnnualFeeDateFieldVisibility() {
        String cardName = cardField.getText().toString();
        if (cardName.equals("")) {
            afDateLayout.setVisibility(View.GONE);
        } else {
            Double annualFee = cardDS.getCardAnnualFee(cardName);
            if (annualFee > 0) {
                afDateLayout.setVisibility(View.VISIBLE);
            } else {
                afDateLayout.setVisibility(View.GONE);
            }
        }
    }

	// Runs when save button is clicked
    @Override
    public void menuSaveClicked() {
        String cardName = cardField.getText().toString();
        Integer cardRefId = cardDS.getCardRefId(cardName);

        Date openDate = null;
        try {
            openDate = dateFormat.parse(openDateField.getText().toString());
        } catch (ParseException e) {
            openDate = null;
        }

        Date afDate = null;
        try {
            afDate = dateFormat.parse(afDateField.getText().toString());
        } catch (ParseException e) {
            afDate = null;
        }

		// Checks that credit card is selected
        if(cardName.equals("")){
            Toast.makeText(CardAddEditActivity.this, "Please select a credit card.", Toast.LENGTH_LONG).show();
			
		// Creates card if new
        } else if (cardId == -1){
            cardDS.create(cardRefId, CardStatus.OPEN, openDate, afDate, null, "");
            finish(); //Closes activity
            Toast.makeText(CardAddEditActivity.this, cardName + " card added.", Toast.LENGTH_SHORT).show();
			
		// Updates card if existing with new values from fields
        } else {
            CreditCard card = cardDS.getSingle(cardId);
            card.setCardId(cardRefId);
            card.setName(cardName);
            card.setOpenDate(openDate);
            card.setAfDate(afDate);
            cardDS.update(card);
            finish(); //Closes activity
            Toast.makeText(CardAddEditActivity.this, cardName + " card updated.", Toast.LENGTH_SHORT).show();
        }
    }

	// Displays list of banks for user selection
    private void bankFieldClick () {
        String title = "Select Issuing Bank";
        ArrayList<String> types = cardDS.getAvailableBanks(true);
        fieldSelectDialog(title, types, "bank");
    }

	// Displays list of credit cards for user selection
    private void cardFieldClick () {
        String bank = bankField.getText().toString();
        ArrayList<String> cards = cardDS.getAvailableCards(bank, true);
        if (cards.size() > 0){
			String title = "Select " + bank + " Card";
            fieldSelectDialog(title, cards, "card");
        } else {
			
			// Sends user a message if no bank was selected
            Toast.makeText(this, "Please select a bank.", Toast.LENGTH_LONG).show();
        }
    }

	// Displays date picker dialog for user selection of card open date
    private void openDateFieldClick (){

        // Sets currently selected date in dialog to the date in the field
        Calendar cal = Calendar.getInstance();
        Date lastActivityDate = null;
        try {
            lastActivityDate = dateFormat.parse(openDateField.getText().toString());
        } catch (ParseException e) {
            if (cardId != -1) {
                CreditCard program = cardDS.getSingle(cardId);
                lastActivityDate = program.getOpenDate();
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
                        openDateField.setText(dateFormat.format(pickedDate));

                        // If annual fee field is blank, set to one year after open date
                        String currentAfDate = afDateField.getText().toString();
                        if (currentAfDate.equals("")) {
                            cal.add(Calendar.YEAR, 1);
                            Date newAfDate = cal.getTime();
                            afDateField.setText(dateFormat.format(newAfDate));
                        }
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

	// Displays date picker dialog for user selection of card annual fee date
    private void annualFeeDateFieldClick(){

        // Sets currently selected date in dialog to the date in the field
        Calendar cal = Calendar.getInstance();
        Date lastActivityDate = null;
        try {
            lastActivityDate = dateFormat.parse(afDateField.getText().toString());
        } catch (ParseException e) {
            if (cardId != -1) {
                CreditCard program = cardDS.getSingle(cardId);
                lastActivityDate = program.getAfDate();
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

	// Creates standard list selection dialog
    private void fieldSelectDialog(String title, ArrayList<String> items, final String saveField) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CardAddEditActivity.this);
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
                        case "bank":
                            String currentBank = bankField.getText().toString();
                            if (!currentBank.equals(selectedItem)) {
                                bankField.setText(selectedItem);
                                cardField.setText("");
                            }
                            break;
                        case "card":
                            cardField.setText(selectedItem);
                            break;
                    }
                    updateAnnualFeeDateFieldVisibility();
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

        TextView bankLabel = (TextView) findViewById(R.id.bankLabel);
        bankLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankFieldClick();
            }});

        bankField.setInputType(InputType.TYPE_NULL);
        bankField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(CardAddEditActivity.this);
                bankFieldClick();}
        });
        bankField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(CardAddEditActivity.this);
                    bankField.performClick();}
            }
        });

        TextView cardLabel = (TextView) findViewById(R.id.cardLabel);
        cardLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardFieldClick();
            }});

        cardField.setInputType(InputType.TYPE_NULL);
        cardField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(CardAddEditActivity.this);
                cardFieldClick();}
        });
        cardField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(CardAddEditActivity.this);
                    cardField.performClick();}
            }
        });

        TextView openDateLabel = (TextView) findViewById(R.id.openDateLabel);
        openDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateFieldClick();
            }});

        openDateField.setInputType(InputType.TYPE_NULL);
        openDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(CardAddEditActivity.this);
                openDateFieldClick();
            }
        });
        openDateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(CardAddEditActivity.this);
                    openDateField.performClick();
                }
            }
        });

        TextView afDateLabel = (TextView) findViewById(R.id.afDateLabel);
        afDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                annualFeeDateFieldClick();
            }});

        afDateField.setInputType(InputType.TYPE_NULL);
        afDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(CardAddEditActivity.this);
                annualFeeDateFieldClick();
            }
        });
        afDateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(CardAddEditActivity.this);
                    afDateField.performClick();
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