package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_EditDelete;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.Detail;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Adapters.DetailListAdapter;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.NotificationStatus;
import com.davidlcassidy.travelwallet.EnumTypes.NumberPattern;
import com.davidlcassidy.travelwallet.R;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CardDetailActivity extends BaseActivity_EditDelete {

    private UserPreferences userPreferences;
    private CardDataSource cardDS;
    private Integer cardId;
    private List<Detail> detailList;

    private ListView lv;
    private ImageView logo;
    private TextView logoText;
    private ToggleButton notificationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailslist);
		setTitle("Credit Card");

        userPreferences = UserPreferences.getInstance(this);
        cardDS = CardDataSource.getInstance(this);
        cardId = Integer.parseInt(getIntent().getStringExtra("CARD_ID"));
        detailList = new ArrayList<Detail>();
        final CreditCard card = cardDS.getSingle(cardId);
        
		// Adds image to list header and notification button to list footer
        View header = getLayoutInflater().inflate(R.layout.detaillist_header, null);
        logo = (ImageView) header.findViewById(R.id.Logo);
        logoText = (TextView) header.findViewById(R.id.LogoText);
        View footer = getLayoutInflater().inflate(R.layout.detaillist_footer, null);
        lv = (ListView) findViewById(R.id.detailsList);
        lv.addHeaderView(header);
        lv.addFooterView(footer);

		// Sets text for notification button
        notificationButton = (ToggleButton) footer.findViewById(R.id.notificationButton);
        notificationButton.setTextOn("Monitoring : OFF");
        notificationButton.setTextOff("Monitoring : ON");
		
		// Sets click listener for notification button. When clicked, the card's notification status
		// will be updated and notification button text will change to reflect.
        notificationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (card.getNotificationStatus() == NotificationStatus.UNMONITORED){
                    cardDS.updateCardNotificationStatus(card, NotificationStatus.OFF);
                    notificationButton.setChecked(false);
                } else {
                    cardDS.updateCardNotificationStatus(card, NotificationStatus.UNMONITORED);
                    notificationButton.setChecked(true);
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();

        SimpleDateFormat dateFormat = userPreferences.getDatePattern().getDateFormat();
        NumberPattern numberPattern = NumberPattern.COMMADOT;
        DecimalFormat decimalFormat = numberPattern.getDecimalFormat();
        Currency currency = userPreferences.getCurrency();

        // Gets card annual fee and formats as currency string
        CreditCard card = cardDS.getSingle(cardId);
        BigDecimal cardAF = card.getAnnualFee();
        String cardAFString = currency.numToString(cardAF, numberPattern);

		// Sets card image and card name text for list header
        int logoNum = this.getResources().getIdentifier("card_000_image", "drawable", this.getPackageName());
        logo.setImageResource(logoNum);
        logoText.setText(card.getName());

		// Creates list of credit card field/value pairs
        detailList.clear();
        detailList.add(new Detail("Name", card.getName()));
        detailList.add(new Detail("Bank", card.getBank()));
        detailList.add(new Detail("Annual Fee", cardAFString));
        detailList.add(new Detail("Foreign Fee", decimalFormat.format(card.getForeignTransactionFee()) + " %"));
		Date openDate = card.getOpenDate();
        if (openDate != null) {detailList.add(new Detail("Open Date", dateFormat.format(openDate))); }
        boolean hasAnnualFee = card.hasAnnualFee();
        Date afDate = card.getAfDate();
        if (afDate != null && hasAnnualFee) {detailList.add(new Detail("Annual Fee Date", dateFormat.format(afDate)));}

		// Creates adapter using card details list and sets to list
        DetailListAdapter adapter = new DetailListAdapter(this, detailList);
        lv.setAdapter(adapter);

		// Updates notification button test and visability
        if (card.getNotificationStatus() == NotificationStatus.UNMONITORED){
            notificationButton.setChecked(true);
        } else {
            notificationButton.setChecked(false);
        }
        notificationButton.setVisibility(View.VISIBLE);
        if (!hasAnnualFee) {
            notificationButton.setVisibility(View.GONE);
        }
    }

	// Runs when edit button is clicked
    @Override
    public void menuEditClicked() {
		// Opens CardAddEdit Activity
        Intent intent = new Intent(CardDetailActivity.this, CardAddEditActivity.class);
        intent.putExtra("CARD_ID", String.valueOf(cardId));
        startActivity(intent);
    }

	// Runs when delete button is clicked
    @Override
    public void menuDeleteClicked() {
		
		// Creates delete warning dialog
        CreditCard card = cardDS.getSingle(cardId);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(card.getName());
		builder.setCancelable(false);
        builder.setMessage("Are you sure you want to delete?");
		
		// Deletes card if "Yes" button selected
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                cardDS.delete(cardId);
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