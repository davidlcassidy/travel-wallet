package com.davidlcassidy.travelwallet.Activities;

import android.os.Bundle;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity;
import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_BackOnly;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static java.util.Calendar.MONTH;

/*
ChaseStatusActivity is use to display user's Chase 5/24 statusis calculated from a list of
eligible credit cards in the database. It is created by the selection of the "Chase 5/24 Status"
item in the popup menu of MainActivitys
 */

public class ChaseStatusActivity extends BaseActivity_BackOnly {

    private UserPreferences userPreferences;
    private SimpleDateFormat dateFormat;
    private CardDataSource cardDS;

    private TextView descriptionTitle, description, statusField, dateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chasestatus);
        setTitle("Chase 5/24 Status");

        userPreferences = UserPreferences.getInstance(this);
        dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();
        cardDS = CardDataSource.getInstance(this);

        descriptionTitle = (TextView) findViewById(R.id.descriptionTitle);
        description = (TextView) findViewById(R.id.description);
        statusField = (TextView) findViewById(R.id.statusField);
        dateField = (TextView) findViewById(R.id.dateField);

        // Set description title and text
        descriptionTitle.setText("What is Chase's 5/24 Rule?");
        String desc =
                "Chase will typically not approval you for new credit cards if you have opened " +
                        "five or more credit cards in the last 24 months. This usually applies " +
                        "to personal cards from all issuers as well as business cards from Capital " +
                        "One and Discover. \n" +
                        "Only certain cards issued by Chase are subject to the 5/24 rule. The " +
                        "following are a couple of Chase cards are not subject to 5/24 for " +
                        "approval:\n\n" +
                        "•\tBritish Airways Visa Signature Card\n" +
                        "•\tDisney Premier Visa Card\n" +
                        "•\tIHG Rewards Club Select Credit Card\n" +
                        "•\tMarriott Rewards Premier Business Card\n" +
                        "•\tRitz-Carlton Rewards Card\n" +
                        "•\tHyatt Card\n";
        description.setText(desc);
    }

    protected void onResume() {
        super.onResume();

        // Retrieve list of cards which count toward 5/24 status
        ArrayList<CreditCard> chase524cards = cardDS.getChase524Cards();
        Integer chase524count = chase524cards.size();

        // Set status field value
        String status = String.valueOf(chase524count) + "/24";
        statusField.setText(status);

        // Set eligibility date field value
        if (chase524count >= 5) {
            CreditCard fifthCard = chase524cards.get(chase524count - 5);
            Calendar eligibilityDate = Calendar.getInstance();
            eligibilityDate.setTime(fifthCard.getOpenDate());
            eligibilityDate.add(MONTH, 24);
            dateField.setText(dateFormat.format(eligibilityDate.getTime()));
        } else {
            dateField.setText("Now");
        }
    }
}