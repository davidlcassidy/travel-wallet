/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 10/14/18 9:50 PM
 */

package com.davidlcassidy.travelwallet.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Main;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.AppType;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.EnumTypes.NotificationStatus;
import com.davidlcassidy.travelwallet.EnumTypes.NumberPattern;
import com.davidlcassidy.travelwallet.Fragments.CardListFragment;
import com.davidlcassidy.travelwallet.Fragments.NotificationsListFragment;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Fragments.ProgramListFragment;
import com.davidlcassidy.travelwallet.R;
import com.davidlcassidy.travelwallet.Classes.NotificationTimerService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
MainActivity is the primary activity of the application and the first activity loaded.
It is comprised of three fragments: NotificationsListFragment, ProgramListFragment,
and CardListFragment. These three fragments are contained within a tab layout.
 */

public class MainActivity extends BaseActivity_Main {

    private CardDataSource cardDS;
    private ProgramDataSource programDS;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDisplayHomeEnabled(false);
        cardDS = CardDataSource.getInstance(this);
        programDS = ProgramDataSource.getInstance(this);

		// Starts Notification Timer Service
        startService(new Intent(this, NotificationTimerService.class));

		// Attaches fragments to main activity
        viewPager = (ViewPager) findViewById(R.id.mainView);
        setupViewPagerAdapter(viewPager);

        // Creates tab layout for navigation between fragments
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(getThemeColor(R.attr.colorPrimaryLight));

        // Hide FAB by default
        fab = (FloatingActionButton) findViewById(R.id.fabPlus);
        fab.hide();

        // Opens summary dialog if configured in user settings
        if (userPreferences.getSetting_InitialSummary()){
            showSummary();
        }
		
		// Sets actions of floating "plus" button, depending on displayed fragment
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				;
            }
            @Override
            public void onPageSelected(int position) {
				// Position indicates fragment position. Note that position has a zero index.
                switch (position) {
					
					//Loyalty Program Fragment
                    case 1:
                        fab.show();
                        fab.setOnClickListener(new View.OnClickListener() {
							// Clicking floating "plus" button opens ProgramAddEdit Activity.
							// Program ID of -1 indicates a adding a new program instead of editing an existing one
                            @Override
                            public void onClick(View v) {
                                AppType appType = userPreferences.getAppType();
                                int programCount = programDS.getAll(null, null, false).size();
                                if (programCount >= 10 && appType == AppType.Free) {
                                    String limitTitle = "Program Limit Reached";
                                    String limitText = "Travel Wallet it limited to only ten " +
                                            "loyalty programs.\n\nTo add additional programs and support the " +
                                            "ongoing app development, please upgrade to Travel Wallet " +
                                            "Pro.";
                                    showLimitPopup(limitTitle, limitText);
                                } else {
                                    Intent intent = new Intent(MainActivity.this, ProgramAddEditActivity.class);
                                    intent.putExtra("PROGRAM_ID", String.valueOf(-1));
                                    startActivity(intent);
                                }
                            }
                        });
                        break;

					// Credit Card Fragment
                    case 2:
                        fab.show();
                        fab.setOnClickListener(new View.OnClickListener() {
							// Clicking floating "plus" button opens CardAddEdit Activity.
							// Card ID of -1 indicates a adding a new card instead of editing an existing one
                            @Override
                            public void onClick(View v) {
                                AppType appType = userPreferences.getAppType();
                                int cardCount = cardDS.getAll(null, null, false, false).size();
                                if (cardCount >= 10 && appType == AppType.Free) {
                                    String limitTitle = "Card Limit Reached";
                                    String limitText = "Travel Wallet it limited to only ten " +
                                            "credit cards.\n\nTo add additional cards and support the " +
                                            "ongoing app development, please upgrade to Travel Wallet " +
                                            "Pro.";
                                    showLimitPopup(limitTitle, limitText);
                                } else {
                                    Intent intent = new Intent(MainActivity.this, CardAddEditActivity.class);
                                    intent.putExtra("CARD_ID", String.valueOf(-1));
                                    startActivity(intent);
                                }
                            }
                        });
                        break;

					// Default (Should only apply to Notifications Fragment)
                    default:
						// Floating "plus" button will be hidden and not user accessible.
                        fab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
				;
            }
        });

    }

    // Attaches fragments to main activity
    private void setupViewPagerAdapter(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NotificationsListFragment(), "Notifications");
        adapter.addFragment(new ProgramListFragment(), "Loyalty\nPrograms");
        adapter.addFragment(new CardListFragment(), "Credit\nCards");
        viewPager.setAdapter(adapter);
    }

	// Adapter used to add fragments to viewPager
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // Runs when owners button is clicked
    @Override
    public void menuOwnersClicked() {
        // Opens OwnerListActivity
        Intent intent2 = new Intent(MainActivity.this, OwnerListActivity.class);
        startActivity(intent2);
    }

    // Runs when dropdown button is clicked
    @Override
    public void menuDropdownClicked() {
        View menuView = findViewById(R.id.menu_dropdown);
        PopupMenu popupMenu = new PopupMenu(this, menuView);
        AppType appType = userPreferences.getAppType();
        if (appType == AppType.Pro){
            popupMenu.inflate(R.menu.dropdown_pro);
        } else {
            popupMenu.inflate(R.menu.dropdown_free);
        }

        // Sets menu click listeners
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String selectedItemName = (String) item.getTitle();

                switch (selectedItemName) {
                    case "Settings":
                        // Opens Settings Activity
                        Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent1);
                        break;
                    case "Summary":
                        // Opens Summary Popup
                        showSummary();
                        break;
                    case "Developer's Note":
                        // Opens DevelopersNote Activity
                        Intent intent2 = new Intent(MainActivity.this, DevelopersNoteActivity.class);
                        startActivity(intent2);
                        break;
                    case "Upgrade to Pro":
                        // Opens PurchaseItem Activity
                        Intent intent3 = new Intent(MainActivity.this, PurchaseProActivity.class);
                        startActivity(intent3);
                        break;
                }
                return true;
            }
        });

        popupMenu.show();
    }

    // Opens Summary Popup
    public void showSummary() {
        NumberPattern numberPattern = NumberPattern.COMMADOT;
        Currency currency = userPreferences.getSetting_Currency();

        ProgramDataSource programDS = ProgramDataSource.getInstance(this);
        CardDataSource cardDS = CardDataSource.getInstance(this);
        Date today = new Date();

        // Gets dialog layout
        LayoutInflater inflater = (LayoutInflater) this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_summary, null);

        // Creates dialog
        final AlertDialog diag = new AlertDialog.Builder(this).setView(v).create();

        // Gets summary layout fields
        TextView programCount = (TextView) v.findViewById(R.id.programCountField);
        TextView programNotifications = (TextView) v.findViewById(R.id.programNotificationsField);
        TextView programValue = (TextView) v.findViewById(R.id.programValueField);
        TextView cardCount = (TextView) v.findViewById(R.id.cardCountField);
        TextView cardNotifications = (TextView) v.findViewById(R.id.cardNotificationsField);
        TextView cardAF = (TextView) v.findViewById(R.id.cardAFField);

        // Sets title in dialog toolbar on top
        Toolbar toolBar2 = (Toolbar) v.findViewById(R.id.toolbar);
        toolBar2.setTitle("Summary");

        // Retrieves programs total value data and saves to summary dialog fields
        SimpleDateFormat dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();
        programCount.setText(String.valueOf(programDS.getAll(null, null, false).size()));
        programNotifications.setText(String.valueOf(programDS.getAll(null,null,true).size()));
        ArrayList<LoyaltyProgram> programs = programDS.getAll(null, null, false);
        BigDecimal total = BigDecimal.valueOf(0);
        for (LoyaltyProgram p : programs) {
            total = total.add(p.getTotalValue());
        }
        programValue.setText(currency.numToString(total, numberPattern));

        // Retrieves programs next expiration data and saves to summary dialog fields
        for (LoyaltyProgram program : programDS.getAll(null, ItemField.EXPIRATIONDATE, false)) {

            // Checks if program monitoring is on and has an expiration date
            NotificationStatus notificationStatus = program.getNotificationStatus();
            Date expDate = program.getExpirationDate();
            boolean hasExpirationDate = program.hasExpirationDate() && expDate != null;
            if (notificationStatus != NotificationStatus.UNMONITORED && hasExpirationDate){

                // Checks program if expiration date is in future and program has points
                int points = program.getPoints();
                if (expDate.compareTo(today) > 0 && points > 0) {
                    TextView  programNextExpire = (TextView) v.findViewById(R.id.programNextExpireField);
                    programNextExpire.setText(dateFormat.format(program.getExpirationDate()));
                }
            }
        }

        // Retrieves cards total AF data and saves to summary dialog field
        cardCount.setText(String.valueOf(cardDS.getAll(null, null, false,false).size()));
        cardNotifications.setText(String.valueOf(cardDS.getAll(null, null, true, true).size()));
        ArrayList<CreditCard> cardList = cardDS.getAll(null, null, false, true);
        BigDecimal totalAF = BigDecimal.valueOf(0);
        for (CreditCard cc : cardList) {
            totalAF = totalAF.add(cc.getAnnualFee());
        }
        cardAF.setText(currency.numToString(totalAF, numberPattern));

        // Retrieves cards next AF data and saves to summary dialog field
        for (CreditCard card : cardDS.getAll(null, ItemField.AFDATE, false, true)) {

            // Checks if card monitoring is on and has an annual fee
            NotificationStatus notificationStatus = card.getNotificationStatus();
            Date annualFeeDate = card.getAfDate();
            boolean hasAnnualFee = card.hasAnnualFee() && annualFeeDate != null;
            if (notificationStatus != NotificationStatus.UNMONITORED && hasAnnualFee){

                // Checks program if annual fee date is in future
                if (annualFeeDate.compareTo(today) > 0) {
                    TextView  cardNextAF = (TextView) v.findViewById(R.id.cardNextAFField);
                    cardNextAF.setText(dateFormat.format(card.getAfDate()));
                }
            }
        }

        // Runs with "Close" button is clicked
        Button closeButton2 = (Button) v.findViewById(R.id.closeButton);
        closeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dialog is destroyed
                diag.dismiss();
            }
        });

        // Displays dialog
        diag.show();

        // Dims background while dialog is active
        diag.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    // Opens Owner Limit Popup
    public void showLimitPopup(String limitTitle, String limitText) {
        // Gets dialog layout
        LayoutInflater inflater = (LayoutInflater) this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_upgrade, null);

        // Creates dialog
        final AlertDialog diag = new AlertDialog.Builder(this).setView(v).create();

        // Sets title in dialog toolbar on top
        Toolbar toolBar1 = (Toolbar) v.findViewById(R.id.toolbar);
        toolBar1.setTitle(limitTitle);

        // Sets text in dialog
        TextView mainText = (TextView) v.findViewById(R.id.text);
        mainText.setText(limitText);

        // Runs with "Upgrade" button is clicked
        Button upgradeButton = (Button) v.findViewById(R.id.upgradeButton);
        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diag.dismiss();
                Intent intent = new Intent(MainActivity.this, PurchaseProActivity.class);
                startActivity(intent);
            }
        });

        // Runs with "Close" button is clicked
        Button closeButton = (Button) v.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dialog is destroyed
                diag.dismiss();
            }
        });

        // Displays dialog
        diag.show();

        // Dims background while dialog is active
        diag.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Set toolbar title
        AppType appType = userPreferences.getAppType();
        if (appType == AppType.Pro) {
            setTitle("Travel Wallet Pro");
        } else {
            setTitle("Travel Wallet");
        }
    }
}