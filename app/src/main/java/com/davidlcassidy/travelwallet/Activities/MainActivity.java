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
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.BaseActivities.BaseActivity_Main;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.NumberPattern;
import com.davidlcassidy.travelwallet.Fragments.CardListFragment;
import com.davidlcassidy.travelwallet.Fragments.NotificationsListFragment;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Fragments.ProgramListFragment;
import com.davidlcassidy.travelwallet.R;
import com.davidlcassidy.travelwallet.Classes.NotificationTimerService;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;

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

    private UserPreferences userPreferences;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDisplayHomeEnabled(false);
		fab = (FloatingActionButton) findViewById(R.id.fabPlus);
        fab.hide();
        
		// Starts Notification Timer Service
        startService(new Intent(this, NotificationTimerService.class));

		// Attaches fragments to main activity
        viewPager = (ViewPager) findViewById(R.id.mainView);
        setupViewPagerAdapter(viewPager);

        // Creates tab layout for navigation between fragments
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Opens summary dialog if configured in user settings
        userPreferences = UserPreferences.getInstance(this);
        if (userPreferences.getInitialSummary()){
            menuSummaryClicked();
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
                                Intent intent = new Intent(MainActivity.this, ProgramAddEditActivity.class);
                                intent.putExtra("PROGRAM_ID", String.valueOf(-1));
                                startActivity(intent);
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
                                Intent intent = new Intent(MainActivity.this, CardAddEditActivity.class);
                                intent.putExtra("CARD_ID", String.valueOf(-1));
                                startActivity(intent);
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

    // Runs when about button is clicked
    @Override
    public void menuAboutClicked() {
        // Gets dialog layout
        LayoutInflater inflater1 = (LayoutInflater) this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v1 = inflater1.inflate(R.layout.dialog_about, null);

        // Creates dialog
        final AlertDialog diag1 = new AlertDialog.Builder(this).setView(v1).create();

        // Sets title in dialog toolbar on top
        Toolbar toolBar1 = (Toolbar) v1.findViewById(R.id.toolbar);
        toolBar1.setTitle("App Info");

        // Sets text in dialog
        TextView mainText = (TextView) v1.findViewById(R.id.text);
        String text =
                "Travel Wallet v1.2\n\n" +
                        "I hope you are able to find some value out of this app. If you have any feature requests, " +
                        "bug reports, or any other feedback, please feel free to shoot me an email. " +
                        "I love hearing back about my projects! \n\n" +
                        "Email:  travelwallet@davidlcassidy.com\nWebsite:  www.DavidLCassidy.com";
        mainText.setText(text);
        Linkify.addLinks(mainText, Linkify.ALL);

        // Runs with "Close" button is clicked
        Button closeButton = (Button) v1.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dialog is destroyed
                diag1.dismiss();
            }
        });

        // Displays dialog
        diag1.show();

        // Dims background while dialog is active
        diag1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    // Runs when summary button is clicked
    @Override
    public void menuSummaryClicked() {
        NumberPattern numberPattern = NumberPattern.COMMADOT;
        Currency currency = userPreferences.getCurrency();

        ProgramDataSource programDS = ProgramDataSource.getInstance(this);
        CardDataSource cardDS = CardDataSource.getInstance(this);

        // Gets dialog layout
        LayoutInflater inflater2 = (LayoutInflater) this.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View v2 = inflater2.inflate(R.layout.dialog_summary, null);

        // Creates dialog
        final AlertDialog diag2 = new AlertDialog.Builder(this).setView(v2).create();

        // Gets summary layout fields
        TextView programCount = (TextView) v2.findViewById(R.id.programCountField);
        TextView programNotifications = (TextView) v2.findViewById(R.id.programNotificationsField);
        TextView programValue = (TextView) v2.findViewById(R.id.programValueField);
        TextView cardCount = (TextView) v2.findViewById(R.id.cardCountField);
        TextView cardNotifications = (TextView) v2.findViewById(R.id.cardNotificationsField);
        TextView cardAF = (TextView) v2.findViewById(R.id.cardAFField);

        // Sets title in dialog toolbar on top
        Toolbar toolBar2 = (Toolbar) v2.findViewById(R.id.toolbar);
        toolBar2.setTitle("Summary");

        // Retrieves program summary data and saves to dialog fields
        SimpleDateFormat dateFormat = userPreferences.getDatePattern().getDateFormat();
        programCount.setText(String.valueOf(programDS.getAll(null).size()));
        programNotifications.setText(String.valueOf(programDS.getProgramsWithNotifications().size()));
        programValue.setText(currency.numToString(programDS.getAllProgramsValue(), numberPattern));
        LoyaltyProgram program = programDS.getNextExpire();
        if (program != null){
            Date expirationDate = program.getExpirationDate();
            if (expirationDate != null){
                TextView  programNextExpire = (TextView) v2.findViewById(R.id.programNextExpireField);
                programNextExpire.setText(dateFormat.format(expirationDate));
            }
        }

        // Retrieves card summary data and saves to dialog fields
        cardCount.setText(String.valueOf(cardDS.getAll(null).size()));
        cardNotifications.setText(String.valueOf(cardDS.getCardsWithNotifications().size()));
        cardAF.setText(currency.numToString(cardDS.getAllCardsAnnualFees(), numberPattern));
        CreditCard card = cardDS.getNextAF();
        if (card != null){
            Date afDate = card.getAfDate();
            if (afDate != null){
                TextView  programNextExpire = (TextView) v2.findViewById(R.id.cardNextAFField);
                programNextExpire.setText(dateFormat.format(afDate));
            }
        }

        // Runs with "Close" button is clicked
        Button closeButton2 = (Button) v2.findViewById(R.id.closeButton);
        closeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dialog is destroyed
                diag2.dismiss();
            }
        });

        // Displays dialog
        diag2.show();

        // Dims background while dialog is active
        diag2.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    // Runs when settings button is clicked
    @Override
    public void menuSettingsClicked() {
        // Opens Settings Activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}