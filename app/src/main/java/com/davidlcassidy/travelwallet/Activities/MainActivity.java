/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
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
import com.davidlcassidy.travelwallet.Classes.Constants;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.NotificationTimerService;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Database.UserDataSource;
import com.davidlcassidy.travelwallet.Enums.AppType;
import com.davidlcassidy.travelwallet.Enums.CardStatus;
import com.davidlcassidy.travelwallet.Enums.Currency;
import com.davidlcassidy.travelwallet.Enums.ItemField;
import com.davidlcassidy.travelwallet.Enums.NotificationStatus;
import com.davidlcassidy.travelwallet.Fragments.CardListFragment;
import com.davidlcassidy.travelwallet.Fragments.NotificationsListFragment;
import com.davidlcassidy.travelwallet.Fragments.ProgramListFragment;
import com.davidlcassidy.travelwallet.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.icu.text.DateTimePatternGenerator.DAY;
import static java.util.Calendar.YEAR;

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

        // TEST MODE:
        // Launches app with one of every available loyalty program and credit card
        boolean testMode = true; //TODO Change back
        if (testMode) {
            addAllCardsAndPrograms(true, true);
        }

        // Starts Notification Timer Service
        startService(new Intent(this, NotificationTimerService.class));

        // Attaches fragments to main activity
        viewPager = findViewById(R.id.mainView);
        setupViewPagerAdapter(viewPager);

        // Creates tab layout for navigation between fragments
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(getThemeColor(R.attr.colorPrimaryLight));

        // Hide FAB by default
        fab = findViewById(R.id.fabPlus);
        fab.hide();


        if (!appPreferences.getFirstAppLaunch() && appPreferences.getAppType() == AppType.FREE && Math.random() < 0.08) {
            // Randomly opens upgrade prompt for free users, except on first launch
            showUpgradePromptPopup();
        } else if (appPreferences.getSetting_InitialSummary()) {
            // Opens summary dialog if configured in user settings
            showSummaryPopup();
        }

        // Sets actions of floating "plus" button, depending on displayed fragment
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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
                                AppType appType = appPreferences.getAppType();
                                int programCount = programDS.getAll(null, null, false).size();
                                if (programCount >= Constants.FREE_LOYALTY_PROGRAM_LIMIT && appType == AppType.FREE) {
                                    String limitTitle = "Program Limit Reached";
                                    String limitText = "Travel Wallet it limited to only " +
                                            Constants.FREE_LOYALTY_PROGRAM_LIMIT +
                                            " loyalty programs.\n\nTo add additional programs and support the " +
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
                                AppType appType = appPreferences.getAppType();
                                int cardCount = cardDS.getAll(null, null, false, false).size();
                                if (cardCount >= Constants.FREE_CREDIT_CARD_LIMIT && appType == AppType.FREE) {
                                    String limitTitle = "Card Limit Reached";
                                    String limitText = "Travel Wallet it limited to only " +
                                            Constants.FREE_CREDIT_CARD_LIMIT +
                                            " credit cards.\n\nTo add additional cards and support the " +
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

    // Runs when users button is clicked
    @Override
    public void menuUsersClicked() {
        // Opens UserListActivity
        Intent intent2 = new Intent(MainActivity.this, UserListActivity.class);
        startActivity(intent2);
    }

    // Runs when dropdown button is clicked
    @Override
    public void menuDropdownClicked() {
        View menuView = findViewById(R.id.menu_dropdown);
        PopupMenu popupMenu = new PopupMenu(this, menuView);
        AppType appType = appPreferences.getAppType();
        if (appType == AppType.FREE) {
            popupMenu.inflate(R.menu.dropdown_pro);
        } else {
            popupMenu.inflate(R.menu.dropdown_free);
        }

        // Sets menu click listeners
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String selectedItemName = (String) item.getTitle();
                switch (selectedItemName) {
                    case "Summary":
                        // Opens Summary Popup
                        showSummaryPopup();
                        break;
                    case "Customize":
                        // Opens Customize Activity
                        Intent customizeIntent = new Intent(MainActivity.this, CustomizeActivity.class);
                        startActivity(customizeIntent);
                        break;
                    case "Settings":
                        // Opens Settings Activity
                        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settingsIntent);
                        break;
                    case "Developer's Note":
                        // Opens DevelopersNote Activity
                        Intent developersNoteIntent = new Intent(MainActivity.this, DevelopersNoteActivity.class);
                        startActivity(developersNoteIntent);
                        break;
                    case "Upgrade to Pro":
                        // Opens PurchasePro Activity
                        Intent purchaseProIntent = new Intent(MainActivity.this, PurchaseProActivity.class);
                        startActivity(purchaseProIntent);
                        break;
                }
                return true;
            }
        });

        popupMenu.show();
    }

    // Opens Upgrade Prompt Popup
    public void showUpgradePromptPopup() {
        // Gets dialog layout
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_upgrade, null);

        // Creates dialog
        final AlertDialog diag = new AlertDialog.Builder(this).setView(v).create();

        // Sets title in dialog toolbar on top
        Toolbar toolBar1 = v.findViewById(R.id.toolbar);
        toolBar1.setTitle("Enjoying Travel Wallet?");

        // Sets text in dialog
        TextView mainText = v.findViewById(R.id.text);
        String text =
                "If you like Travel Wallet, please consider upgrading to Travel Wallet Pro to " +
                        "support our ongoing development effort. \n\nIt is a low, one time cost " +
                        "and will forever remove all of the limitations of the free version of " +
                        "Travel Wallet. \n\nThank you!\n";
        mainText.setText(text);

        // Opens PurchaseProActivity when "Upgrade" button is clicked
        Button upgradeButton = v.findViewById(R.id.upgradeButton);
        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diag.dismiss();
                Intent intent = new Intent(MainActivity.this, PurchaseProActivity.class);
                startActivity(intent);
            }
        });

        // Closes dialog when "Close" button is clicked
        Button closeButton = v.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diag.dismiss();
            }
        });

        // Displays dialog
        diag.show();

        // Dims background while dialog is active
        diag.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    // Opens Summary Popup
    public void showSummaryPopup() {
        Currency currency = appPreferences.getSetting_Currency();

        ProgramDataSource programDS = ProgramDataSource.getInstance(this);
        CardDataSource cardDS = CardDataSource.getInstance(this);
        Date today = new Date();

        // Gets dialog layout
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_summary, null);

        // Creates dialog
        final AlertDialog diag = new AlertDialog.Builder(this).setView(v).create();

        // Gets summary layout fields
        TextView programCount = v.findViewById(R.id.programCountField);
        TextView programNotifications = v.findViewById(R.id.programNotificationsField);
        TextView programValue = v.findViewById(R.id.programValueField);
        TextView cardCount = v.findViewById(R.id.cardCountField);
        TextView cardNotifications = v.findViewById(R.id.cardNotificationsField);
        TextView cardAF = v.findViewById(R.id.cardAFField);

        // Sets title in dialog toolbar on top
        Toolbar toolBar2 = v.findViewById(R.id.toolbar);
        toolBar2.setTitle("Summary");

        // Retrieves programs total value data and saves to summary dialog fields
        SimpleDateFormat dateFormat = appPreferences.getSetting_DatePattern().getDateFormat();
        ArrayList<LoyaltyProgram> programs = programDS.getAll(null, ItemField.EXPIRATION_DATE, false);
        programCount.setText(String.valueOf(programs.size()));

        BigDecimal totalProgramValue = BigDecimal.valueOf(0);
        int programNotificationCount = 0;
        boolean programNextExpireIsSet = false;
        for (LoyaltyProgram program : programs) {
            totalProgramValue = totalProgramValue.add(program.getTotalValue());

            // Counts programs with notifications
            NotificationStatus notificationStatus = program.getNotificationStatus();
            if (notificationStatus == NotificationStatus.ON){
                programNotificationCount++;
            }

            // Retrieves the next expiring monitored program
            Date expDate = program.getExpirationDate();
            boolean hasExpirationDate = program.hasExpirationDate() && expDate != null;
            if (!programNextExpireIsSet && notificationStatus != NotificationStatus.UNMONITORED && hasExpirationDate) {
                int points = program.getPoints();
                if (expDate.compareTo(today) > 0 && points > 0) {
                    TextView programNextExpire = v.findViewById(R.id.programNextExpireField);
                    programNextExpire.setText(dateFormat.format(program.getExpirationDate()));
                    programNextExpireIsSet = true;
                }
            }
        }
        programValue.setText(currency.formatValue(totalProgramValue));
        programNotifications.setText(String.valueOf(programNotificationCount));

        ArrayList<CreditCard> cardList = cardDS.getAll(null, ItemField.AF_DATE, false, false);
        cardCount.setText(String.valueOf(cardList.size()));

        BigDecimal totalAF = BigDecimal.valueOf(0);
        int cardNotificationCount = 0;
        boolean cardNextAfDateIsSet = false;
        for (CreditCard card : cardList) {
            // Counts programs with notifications
            NotificationStatus notificationStatus = card.getNotificationStatus();
            if (notificationStatus == NotificationStatus.ON){
                cardNotificationCount++;
            }

            if (card.getStatus() == CardStatus.OPEN) {
                // Sums total annual fees of all open cards
                totalAF = totalAF.add(card.getAnnualFee());

                // Retrieves the next annual fee due
                Date annualFeeDate = card.getAfDate();
                boolean hasAnnualFee = card.hasAnnualFee() && annualFeeDate != null;
                if (!cardNextAfDateIsSet && card.getNotificationStatus() != NotificationStatus.UNMONITORED && hasAnnualFee) {
                    if (annualFeeDate.compareTo(today) > 0) {
                        TextView cardNextAF = v.findViewById(R.id.cardNextAFField);
                        cardNextAF.setText(dateFormat.format(card.getAfDate()));
                        cardNextAfDateIsSet = true;
                    }
                }
            }
        }
        cardAF.setText(currency.formatValue(totalAF));
        cardNotifications.setText(String.valueOf(cardNotificationCount));

        // Closes dialog when "Close" button is clicked
        Button closeButton2 = v.findViewById(R.id.closeButton);
        closeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diag.dismiss();
            }
        });

        // Displays dialog
        diag.show();

        // Dims background while dialog is active
        diag.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    // Opens Program/Card Limit Popup
    public void showLimitPopup(String limitTitle, String limitText) {
        // Gets dialog layout
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_upgrade, null);

        // Creates dialog
        final AlertDialog diag = new AlertDialog.Builder(this).setView(v).create();

        // Sets title in dialog toolbar on top
        Toolbar toolBar1 = v.findViewById(R.id.toolbar);
        toolBar1.setTitle(limitTitle);

        // Sets text in dialog
        TextView mainText = v.findViewById(R.id.text);
        mainText.setText(limitText);

        // Opens PurchaseProActivity when "Upgrade" button is clicked
        Button upgradeButton = v.findViewById(R.id.upgradeButton);
        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diag.dismiss();
                Intent intent = new Intent(MainActivity.this, PurchaseProActivity.class);
                startActivity(intent);
            }
        });

        // Closes dialog when "Close" button is clicked
        Button closeButton = v.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        setTitle(getAppName());
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

    private void addAllCardsAndPrograms(boolean createPrograms, boolean createCards) {
        UserDataSource userDS = UserDataSource.getInstance(this);
        userDS.deleteAll();
        User user = userDS.create("Test User", "Test Notes");
        if (createPrograms) {
            programDS.deleteAll();
            for (String pType : programDS.getAvailableTypes(false)) {
                for (String pName : programDS.getAvailablePrograms(pType, false, false)) {
                    Integer refID = programDS.getProgramRefId(pType, pName);
                    Double pointValue = Math.random() * 100000;
                    programDS.create(refID, user, "ABC123", pointValue.intValue(), new Date(), "");
                }
            }
        }
        if (createCards) {
            cardDS.deleteAll();
            Calendar openDate = Calendar.getInstance();
            for (String cBank : cardDS.getAvailableBanks(null, false)) {
                for (String cName : cardDS.getAvailableCards(null, cBank, false)) {
                    Integer refID = cardDS.getCardRefId(cBank, cName);
                    Calendar afDate = openDate;
                    afDate.add(YEAR, 1);
                    cardDS.create(refID, user, CardStatus.OPEN, new BigDecimal("0.0"), openDate.getTime(), afDate.getTime(), null, "");
                    openDate.add(DAY, -5);
                }
            }
        }
    }
}