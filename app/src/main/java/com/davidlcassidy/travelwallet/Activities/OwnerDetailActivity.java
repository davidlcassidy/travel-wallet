package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.davidlcassidy.travelwallet.Adapters.DetailListAdapter;
import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_EditDelete;
import com.davidlcassidy.travelwallet.Classes.Detail;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.OwnerDataSource;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.NumberPattern;
import com.davidlcassidy.travelwallet.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
OwnerDetailActivity is use to display the details of an individual owner. It is created
by the selection of an owner in the listview in OwnerListActivity and provided with a
OWNER_ID matching the unique owner id number in the MainDatabase. This activity is primarily
composed of a listview utilizing the DetailListAdapter.
 */

public class OwnerDetailActivity extends BaseActivity_EditDelete {

    private OwnerDataSource ownerDS;
    private ProgramDataSource programDS;
    private CardDataSource cardDS;
    private UserPreferences userPreferences;
    private Currency currency;
    private SimpleDateFormat dateFormat;
    private Integer ownerId;

    private List<Detail> detailList;
    private TextView nameText;
    private ListView lv;
    private TextView notesField;
    private ToggleButton notificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailslist);
		setTitle("Owner");

        ownerDS = OwnerDataSource.getInstance(this);
        programDS = ProgramDataSource.getInstance(this);
        cardDS = CardDataSource.getInstance(this);
        userPreferences = UserPreferences.getInstance(this);
        currency = userPreferences.getSetting_Currency();
        dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();

        ownerId = Integer.parseInt(getIntent().getStringExtra("OWNER_ID"));
        detailList = new ArrayList<Detail>();
        final Owner owner = ownerDS.getSingle(ownerId, programDS, cardDS);
        
		// Adds owner name to list header and notification button to list footer
        View header = getLayoutInflater().inflate(R.layout.detaillist_ownerheader, null);
        nameText = (TextView) header.findViewById(R.id.nameText);
        View footer = getLayoutInflater().inflate(R.layout.detaillist_footer, null);
        lv = (ListView) findViewById(R.id.detailsList);
        lv.addHeaderView(header);
        lv.addFooterView(footer);

		// Hides notification button
        notificationButton = (ToggleButton) footer.findViewById(R.id.notificationButton);
        notificationButton.setVisibility(View.GONE);

        notesField = (TextView) footer.findViewById(R.id.notesField);
    }

    protected void onResume() {
        super.onResume();

        Owner owner = ownerDS.getSingle(ownerId, programDS, cardDS);
        nameText.setText(owner.getName());

        // Gets owner field values
        String numPrograms = String.valueOf(owner.getProgramCount());
        String numCards = String.valueOf(owner.getCardCount());
        BigDecimal totalProgramValue = owner.getTotalProgramValue();
        String totalProgramValueString = currency.numToString(totalProgramValue, NumberPattern.COMMADOT);
        BigDecimal creditLimit = owner.getCreditLimit();
        String creditLimitString = currency.numToString(creditLimit, NumberPattern.COMMADOT);
        String chaseStatus = owner.getChase524Status();
        String chaseEligibilityDateString = owner.getChase524StatusEligibilityDate();

		// Creates list of owner field/value pairs
        //TODO: Layout Overhaul
        detailList.clear();
        detailList.add(new Detail("Loyalty Programs", numPrograms));
        detailList.add(new Detail("Credit Cards", numCards));
        detailList.add(new Detail("Programs Value", totalProgramValueString));
        detailList.add(new Detail("Credit Limit", creditLimitString));
        detailList.add(new Detail("5/24 Status", chaseStatus));
        detailList.add(new Detail("5/24 Elgibility Date", chaseEligibilityDateString));

        // Creates adapter using owner details list and sets to list
        DetailListAdapter adapter = new DetailListAdapter(this, detailList);
        lv.setAdapter(adapter);

        String notes = owner.getNotes();
        if (notes == null || notes.equals("")) {
            notesField.setVisibility(View.GONE);
        } else {
            notesField.setVisibility(View.VISIBLE);
            notesField.setText(notes);
        }
    }

	// Runs when edit button is clicked
    @Override
    public void menuEditClicked() {
		// Opens OwnerAddEdit Activity
        Intent intent = new Intent(OwnerDetailActivity.this, OwnerAddEditActivity.class);
        intent.putExtra("OWNER_ID", String.valueOf(ownerId));
        startActivity(intent);
    }

	// Runs when delete button is clicked
    @Override
    public void menuDeleteClicked() {
		
		// Creates delete warning dialog
        Owner owner = ownerDS.getSingle(ownerId, null, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(owner.getName());
		builder.setCancelable(false);
        builder.setMessage("Are you sure you want to delete?");

		// Deletes owner if "Yes" button selected
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                ownerDS.delete(ownerId);
                finish();
            }});

		// Dialog closes with no further action if "Cancel" button is selected
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                ;
            }
        });

		// Creates dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}