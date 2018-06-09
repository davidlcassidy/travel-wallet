package com.davidlcassidy.travelwallet.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

    private EditText programNotificationField;
    private EditText programPrimaryField;
    private EditText programSortField;
    private EditText cardNotificationField;
    private EditText cardPrimaryField;
    private EditText cardSortField;
    private EditText initialSummaryField;
    private EditText phoneNotificationsField;
    private EditText languageField;
    private EditText currencyField;
    private EditText dateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

		// Gets Settings activity fields
        userPreferences = UserPreferences.getInstance(this);
        programNotificationField = (EditText) findViewById(R.id.programNotificationField);
        programPrimaryField = (EditText) findViewById(R.id.programPrimaryField);
        programSortField = (EditText) findViewById(R.id.programSortField);
        cardNotificationField = (EditText) findViewById(R.id.cardNotificationField);
        cardPrimaryField = (EditText) findViewById(R.id.cardPrimaryField);
        cardSortField = (EditText) findViewById(R.id.cardSortField);
        initialSummaryField = (EditText) findViewById(R.id.initialSummaryField);
        phoneNotificationsField = (EditText) findViewById(R.id.phoneNotificationsField);
        languageField = (EditText) findViewById(R.id.languageField);
        currencyField = (EditText) findViewById(R.id.currencyField);
        dateField = (EditText) findViewById(R.id.dateField);

		// Sets activity label/field click listeners
        setupLabelsClickListeners();
        setupFieldsClickListeners();

    }

    protected void onResume() {
        super.onResume();

		// Gets values from user preferences
        String programNotificationPeriod = userPreferences.getProgramNotificationPeriod();
        ItemField programPrimary = userPreferences.getProgramPrimaryField();
        ItemField programSort = userPreferences.getProgramSortField();
        String cardNotificationPeriod = userPreferences.getCardNotificationPeriod();
        ItemField cardPrimary = userPreferences.getCardPrimaryField();
        ItemField cardSort = userPreferences.getCardSortField();
        String initialSummary = userPreferences.getInitialSummary() ? "Yes" : "No";
        String phoneNotifications = userPreferences.getPhoneNotifications() ? "Yes" : "No";
        Language language = userPreferences.getLanguage();
        Currency currency = userPreferences.getCurrency();
        DatePattern datePattern = userPreferences.getDatePattern();

		// Sets activity fields to values from user preferences
        setNotificationField(ItemType.LOYALTY_PROGRAM, programNotificationPeriod);
        programPrimaryField.setText(programPrimary.getName());
        programSortField.setText(programSort.getName());
        setNotificationField(ItemType.CREDIT_CARD, cardNotificationPeriod);
        cardPrimaryField.setText(cardPrimary.getName());
        cardSortField.setText(cardSort.getName());
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
        String initialSummary = initialSummaryField.getText().toString();
        String phoneNotifications = phoneNotificationsField.getText().toString();
        String language = languageField.getText().toString();
        String date = dateField.getText().toString();
        String currency = currencyField.getText().toString();

		// Saves values from activity fields to user preferences
        userPreferences.setProgramNotificationPeriod(getNotificationField(ItemType.LOYALTY_PROGRAM));
        userPreferences.setProgramPrimaryField(ItemField.fromName(programPrimary));
        userPreferences.setProgramSortField(ItemField.fromName(programSort));
        userPreferences.setCardNotificationPeriod(getNotificationField(ItemType.CREDIT_CARD));
        userPreferences.setCardPrimaryField(ItemField.fromName(cardPrimary));
        userPreferences.setCardSortField(ItemField.fromName(cardSort));
        userPreferences.setInitialSummary(initialSummary.equals("Yes"));
        userPreferences.setPhoneNotifications(phoneNotifications.equals("Yes"));
        userPreferences.setLanguage(Language.fromName(language));
        userPreferences.setCurrency(Currency.fromName(currency));
        userPreferences.setDatePattern(DatePattern.fromSampleDate(date));

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
                ItemField.EXPIRATIONDATE.getName());
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
                ItemField.AFDATE.getName());
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

	// Displays yes/no option list for user selection
    private void initialSummaryFieldClick () {
        String title = "Open Summary on Launch";
        List<String> types = Arrays.asList("Yes", "No");
        fieldSelectDialog(title, types, "initialSummary");
    }

	// Displays yes/no option list for user selection
    private void phoneNotificationsFieldClick () {
        String title = "Phone Notifications";
        List<String> types = Arrays.asList("Yes", "No");
        fieldSelectDialog(title, types, "phoneNotifications");
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
                        case "initialSummary":
                            initialSummaryField.setText(selectedItem);
                            break;
                        case "phoneNotifications":
                            phoneNotificationsField.setText(selectedItem);
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

	// Sets label click listeners so they match actions of field clicks
    private void setupLabelsClickListeners (){
        TextView programNotificationLabel = (TextView) findViewById(R.id.programNotificationLabel);
        programNotificationLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programNotificationFieldClick();
            }});

        TextView programPrimaryLabel = (TextView) findViewById(R.id.programPrimaryLabel);
        programPrimaryLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programPrimaryFieldClick();
            }});

        TextView programSortLabel = (TextView) findViewById(R.id.programSortLabel);
        programSortLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                programSortFieldClick();
            }});

        TextView cardNotificationLabel = (TextView) findViewById(R.id.cardNotificationLabel);
        cardNotificationLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardNotificationFieldClick();
            }});

        TextView cardPrimaryLabel = (TextView) findViewById(R.id.cardPrimaryLabel);
        cardPrimaryLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardPrimaryFieldClick();
            }});

        TextView cardSortLabel = (TextView) findViewById(R.id.cardSortLabel);
        cardSortLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSortFieldClick();
            }});

        TextView initialSummaryLabel = (TextView) findViewById(R.id.initialSummaryLabel);
        initialSummaryLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialSummaryFieldClick();
            }});

        TextView phoneNotificationsLabel = (TextView) findViewById(R.id.phoneNotificationsLabel);
        phoneNotificationsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNotificationsFieldClick();
            }});

        TextView languageLabel = (TextView) findViewById(R.id.languageLabel);
        languageLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageFieldClick();
            }});

        TextView currencyLabel = (TextView) findViewById(R.id.currencyLabel);
        currencyLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencyFieldClick();
            }});

        TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
        dateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFieldClick();
            }});
    }

	// Sets field click listeners
    private void setupFieldsClickListeners () {
        programNotificationField.setInputType(InputType.TYPE_NULL);
        programNotificationField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                programNotificationFieldClick();}
        });
        programNotificationField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    programNotificationField.performClick();}
            }
        });

        programPrimaryField.setInputType(InputType.TYPE_NULL);
        programPrimaryField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                programPrimaryFieldClick();}
        });
        programPrimaryField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    programPrimaryField.performClick();}
            }
        });

        programSortField.setInputType(InputType.TYPE_NULL);
        programSortField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                programSortFieldClick();}
        });
        programSortField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    programSortField.performClick();}
            }
        });

        cardNotificationField.setInputType(InputType.TYPE_NULL);
        cardNotificationField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                cardNotificationFieldClick();}
        });
        cardNotificationField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    cardNotificationField.performClick();}
            }
        });

        cardPrimaryField.setInputType(InputType.TYPE_NULL);
        cardPrimaryField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                cardPrimaryFieldClick();}
        });
        cardPrimaryField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    cardPrimaryField.performClick();}
            }
        });

        cardSortField.setInputType(InputType.TYPE_NULL);
        cardSortField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                cardSortFieldClick();}
        });
        cardSortField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    cardSortField.performClick();}
            }
        });

        initialSummaryField.setInputType(InputType.TYPE_NULL);
        initialSummaryField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                initialSummaryFieldClick();}
        });
        initialSummaryField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    initialSummaryField.performClick();}
            }
        });

        phoneNotificationsField.setInputType(InputType.TYPE_NULL);
        phoneNotificationsField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                phoneNotificationsFieldClick();}
        });
        phoneNotificationsField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    phoneNotificationsField.performClick();}
            }
        });

        languageField.setInputType(InputType.TYPE_NULL);
        languageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                languageFieldClick();}
        });
        languageField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    languageField.performClick();}
            }
        });

        currencyField.setInputType(InputType.TYPE_NULL);
        currencyField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                currencyFieldClick();}
        });
        currencyField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    currencyField.performClick();}
            }
        });

        dateField.setInputType(InputType.TYPE_NULL);
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(SettingsActivity.this);
                dateFieldClick();}
        });
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    hideSoftKeyboard(SettingsActivity.this);
                    dateField.performClick();}
            }
        });

    }

	// Hides keyboard input
    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}