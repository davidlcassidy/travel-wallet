package com.davidlcassidy.travelwallet.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Activities.CardDetailActivity;
import com.davidlcassidy.travelwallet.Adapters.CardListAdapter;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.R;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;

import java.util.ArrayList;

/*
CardListFragment is the third and final fragment within MainActivity that displays a
summary of the user added credit cards. It is primarily composed of a listview
utilizing the CardListAdapter. It also contains a floating "add" button which creates
CardAddEditActivity to allow new cards to be added. Finally, there a textview that
is only visible to the user when there are no cards (empty listview), directing users to
add new cards with the "add" button.
 */

public class CardListFragment extends Fragment {

    private View view;
    private Context context;
    private Activity activity;

    private UserPreferences userPreferences;
    private CardDataSource cardDS;
    private ArrayList<CreditCard> cardList;

    private TextView emptyListText;
    private ListView lv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list, container, false);
        context = view.getContext();
        activity= getActivity();

        userPreferences = UserPreferences.getInstance(context);
        cardDS = CardDataSource.getInstance(activity);

		// Sets the text used when credit card list is empty
        emptyListText = (TextView) view.findViewById(R.id.emptyListText);
        emptyListText.setText("Click the + button below to add a credit card.");

		// Sets credit card click listener
        lv = (ListView) view.findViewById(R.id.fragmentList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			// Opens CardDetail Activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cardID = cardList.get(position).getId().toString();
                Intent intent = new Intent(context, CardDetailActivity.class);
                intent.putExtra("CARD_ID", cardID);
                startActivity(intent);
            }
        });

        return view;
    }

    public void onResume() {
        super.onResume();

		// Gets all credit cards sorted by field defined in user preferences
        ItemField sortField = userPreferences.getCardSortField();
        cardList = cardDS.getAll(sortField);

		// Hides list and shows empty list text if there are no credit cards
        if (cardList.size() == 0){
            emptyListText.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);

        } else {

			// Add credit cards to list view
            emptyListText.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);

			// Sets adaptor to list view
            CardListAdapter adapter = new CardListAdapter(activity, cardList);
            lv.setAdapter(adapter);
        }

    }
}