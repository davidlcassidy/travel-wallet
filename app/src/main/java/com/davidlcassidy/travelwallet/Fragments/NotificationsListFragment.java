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
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Activities.CardDetailActivity;
import com.davidlcassidy.travelwallet.Activities.ProgramDetailActivity;
import com.davidlcassidy.travelwallet.Adapters.NotificationListAdapter;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.Notification;
import com.davidlcassidy.travelwallet.Classes.User;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Database.UserDataSource;
import com.davidlcassidy.travelwallet.Enums.CardStatus;
import com.davidlcassidy.travelwallet.Enums.ItemType;
import com.davidlcassidy.travelwallet.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.icu.text.DateTimePatternGenerator.DAY;
import static java.util.Calendar.MONTH;

/*
NotificationsListFragment is the first fragment within MainActivity that displays a
list of loyalty program and credit card notifications. It is primarily composed of a
listview utilizing the NotificationListAdapter and a textview that is only visible to
the user when there are no notifications (empty listview).
 */

public class NotificationsListFragment extends Fragment {

    private Activity activity;
    private ProgramDataSource programDS;
    private CardDataSource cardDS;
    private List<Notification> notificationList;

    private TextView emptyListText;
    private ListView lv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        activity = getActivity();

        programDS = ProgramDataSource.getInstance(getContext());
        cardDS = CardDataSource.getInstance(getContext());
        notificationList = new ArrayList<>();

        // Hides filters
        LinearLayout filterLayout = view.findViewById(R.id.filterLayout);
        filterLayout.setVisibility(LinearLayout.GONE);

        // Set's text used when notification list is empty
        emptyListText = view.findViewById(R.id.emptyListText);
        emptyListText.setText("Yay!\n\nYou don't have any notifications.");

        // Sets notification click listener
        lv = view.findViewById(R.id.fragmentList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification selection = notificationList.get(position);
                ItemType selectionType = selection.getItemType();

                if (selectionType == ItemType.LOYALTY_PROGRAM) {
                    // If loyalty program notification is clicked, ProgramDetailActivity activity opens
                    Intent intent = new Intent(view.getContext(), ProgramDetailActivity.class);
                    intent.putExtra("PROGRAM_ID", selection.getId());
                    startActivity(intent);

                } else if (selectionType == ItemType.CREDIT_CARD) {
                    // If credit card notification is clicked, CardDetailActivity activity opens
                    Intent intent = new Intent(view.getContext(), CardDetailActivity.class);
                    intent.putExtra("CARD_ID", selection.getId());
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    public void onResume() {
        super.onResume();

        // Update all notifications
        notificationList.clear();
        programDS.updateProgramsNotifications();
        cardDS.updateCardsNotifications();

        // Hides list and shows empty list text if there are no notifications
        ArrayList<LoyaltyProgram> programsWithNotifications = programDS.getAll(null, null, true);
        ArrayList<CreditCard> cardsWithNotifications = cardDS.getAll(null, null, true, true);
        if (programsWithNotifications.size() + cardsWithNotifications.size() == 0) {
            emptyListText.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);

        } else {

            // Add notifications to list view
            emptyListText.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            for (LoyaltyProgram lp : programsWithNotifications) {
                notificationList.add(new Notification(lp));
            }
            for (CreditCard cc : cardsWithNotifications) {
                notificationList.add(new Notification(cc));
            }

            // Sorts notifications by event date
            Collections.sort(notificationList, new Comparator<Notification>() {
                @Override
                public int compare(Notification n1, Notification n2) {
                    Integer n1Id = n1.getId();
                    Integer n2Id = n2.getId();
                    String n1Header = n1.getHeader();
                    String n2Header = n2.getHeader();
                    Date n1Date = n1.getDate();
                    Date n2Date = n2.getDate();

                    Integer c = n1Date.compareTo(n2Date);
                    if (c == 0) {
                        c = n1Header.compareTo(n2Header);
                    }
                    if (c == 0) {
                        c = n1Id.compareTo(n2Id);
                    }
                    return c;
                }
            });

            // Sets adaptor to list view
            NotificationListAdapter adapter = new NotificationListAdapter(activity, notificationList);
            lv.setAdapter(adapter);
        }
    }

}