/*
 * Travel Wallet Android App
 * Copyright (C) 2021 David L Cassidy. All rights reserved.
 * Last modified 4/28/21 11:39 AM
 */

package com.davidlcassidy.travelwallet.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Activities.CardDetailActivity;
import com.davidlcassidy.travelwallet.Adapters.CardListAdapter;
import com.davidlcassidy.travelwallet.Adapters.FilterSpinnerAdapter;
import com.davidlcassidy.travelwallet.Classes.AppPreferences;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.UserDataSource;
import com.davidlcassidy.travelwallet.Enums.CardStatus;
import com.davidlcassidy.travelwallet.Enums.ItemField;
import com.davidlcassidy.travelwallet.R;

import java.util.ArrayList;
import java.util.Arrays;

/*
CardListFragment is the third and final fragment within MainActivity that displays a
summary of the user added credit cards. It is primarily composed of a listview
utilizing the CardListAdapter. It also contains two spinners for filtering over the
listview and a floating "add" button which creates CardAddEditActivity to allow new
cards to be added. Finally, there a textview that is only visible to the user when
there are no cards (empty listview), directing users to add new cards with the "add" button.
 */

public class CardListFragment extends Fragment {

    private Activity activity;
    private AppPreferences appPreferences;
    private CardDataSource cardDS;
    private UserDataSource userDS;
    private ArrayList<CreditCard> fullCardList, filteredCardList;
    private TextView emptyListText;
    private ListView lv;
    private LinearLayout filterLayout;
    private Spinner filter1, filter2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        activity = getActivity();

        appPreferences = AppPreferences.getInstance(getContext());
        appPreferences.setCardFiltersUpdateRequired(true);

        cardDS = CardDataSource.getInstance(getContext());
        userDS = UserDataSource.getInstance(getContext());

        // Sets the text used when credit card list is empty
        emptyListText = view.findViewById(R.id.emptyListText);
        emptyListText.setText("Click the + button below to add a credit card.");

        // Sets credit card click listener
        lv = view.findViewById(R.id.fragmentList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Opens CardDetail Activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer cardID = filteredCardList.get(position).getId();
                Intent intent = new Intent(getContext(), CardDetailActivity.class);
                intent.putExtra("CARD_ID", cardID);
                startActivity(intent);
            }
        });

        filterLayout = view.findViewById(R.id.filterLayout);
        filter1 = view.findViewById(R.id.spinner1);
        filter2 = view.findViewById(R.id.spinner2);
        setFilters(false);

        return view;
    }

    public void onResume() {
        super.onResume();

        // Gets all credit cards sorted by field defined in user preferences
        ItemField sortField = appPreferences.getCustom_CardSortField();
        fullCardList = cardDS.getAll(null, sortField, false, false);

        if (appPreferences.getCardFiltersUpdateRequired()) {
            if (appPreferences.getCustom_CardFilters()) {
                filterLayout.setVisibility(LinearLayout.VISIBLE);
                setFilters(true);
                filterCards();
            } else {
                filterLayout.setVisibility(LinearLayout.GONE);
                filteredCardList = fullCardList;
            }
            appPreferences.setCardFiltersUpdateRequired(false);
        }

        // Hides list and shows empty list text if there are no credit cards
        if (fullCardList.size() == 0) {
            emptyListText.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            filter1.setVisibility(View.GONE);
            filter2.setVisibility(View.GONE);

        } else {

            // Add credit cards to list view
            emptyListText.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            filter1.setVisibility(View.VISIBLE);
            filter2.setVisibility(View.VISIBLE);

            // Sets adaptor to list view
            CardListAdapter adapter = new CardListAdapter(activity, filteredCardList);
            lv.setAdapter(adapter);
        }
    }

    private void setFilters(boolean onlySetUserFilter) {

        // Creates card user filter with values
        ArrayList<String> users = userDS.getAllNames();
        if (users.size() == 0) {
            users.add(0, "No Users Added");
            filter1.setEnabled(false);
            filter1.setClickable(false);
        } else {
            users.add(0, "All Users");
            filter1.setEnabled(true);
            filter1.setClickable(true);
        }
        FilterSpinnerAdapter usersSpinnerAdapter = new FilterSpinnerAdapter(activity, users);
        filter1.setAdapter(usersSpinnerAdapter);
        filter1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appPreferences.setFilter_CardUser(parent.getItemAtPosition(position).toString());
                filterCards();
                onResume();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Sets card user filter to value in user preferences
        String filter1value = appPreferences.getFilter_CardUser();
        int filter1Position = users.indexOf(filter1value);
        if (filter1Position == -1) {
            filter1.setSelection(0);
        } else {
            filter1.setSelection(filter1Position);
        }

        if (!onlySetUserFilter) {

            // Creates card status filter with values
            ArrayList<String> cardStatuses = new ArrayList<>();
            cardStatuses.add("All Statuses");
            cardStatuses.add(CardStatus.OPEN.getName());
            cardStatuses.add(CardStatus.CLOSED.getName());
            FilterSpinnerAdapter cardStatusSpinnerAdapter = new FilterSpinnerAdapter(activity, cardStatuses);
            filter2.setAdapter(cardStatusSpinnerAdapter);
            filter2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    appPreferences.setFilter_CardStatus(parent.getItemAtPosition(position).toString());
                    filterCards();
                    onResume();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            // Sets card status filter to value in user preferences
            String filter2value = appPreferences.getFilter_CardStatus();
            int filter2Position = cardStatuses.indexOf(filter2value);
            if (filter2Position == -1) {
                filter2.setSelection(0);
            } else {
                filter2.setSelection(filter2Position);
            }
        }
    }

    private void filterCards() {
        filteredCardList = new ArrayList<>();

        String filter1value = appPreferences.getFilter_CardUser();
        String filter2value = appPreferences.getFilter_CardStatus();
        for (CreditCard card : fullCardList) {
            if (!Arrays.asList("All Users", "No Users Added").contains(filter1value)) {
                User user = card.getUser();
                if (user == null || !user.getName().equals(filter1value)) {
                    continue;
                }
            }
            if (filter2value.equals("Open")) {
                if (card.getStatus() != CardStatus.OPEN) {
                    continue;
                }
            } else if (filter2value.equals("Closed")) {
                if (card.getStatus() != CardStatus.CLOSED) {
                    continue;
                }
            }
            filteredCardList.add(card);
        }
    }
}