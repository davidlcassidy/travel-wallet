package com.davidlcassidy.travelwallet.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.Activities.CardDetailActivity;
import com.davidlcassidy.travelwallet.Activities.MainActivity;
import com.davidlcassidy.travelwallet.Adapters.CardListAdapter;
import com.davidlcassidy.travelwallet.Adapters.FilterSpinnerAdapter;
import com.davidlcassidy.travelwallet.Classes.CreditCard;
import com.davidlcassidy.travelwallet.Database.CardDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.CardStatus;
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

    private Activity activity;
    private UserPreferences userPreferences;
    private CardDataSource cardDS;
    private ArrayList<CreditCard> fullCardList, filteredCardList;
    private TextView emptyListText;
    private ListView lv;
    private Spinner filter1, filter2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        activity= getActivity();

        userPreferences = UserPreferences.getInstance(getContext());
        cardDS = CardDataSource.getInstance(getContext());

		// Sets the text used when credit card list is empty
        emptyListText = (TextView) view.findViewById(R.id.emptyListText);
        emptyListText.setText("Click the + button below to add a credit card.");

		// Sets credit card click listener
        lv = (ListView) view.findViewById(R.id.fragmentList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			// Opens CardDetail Activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cardID = filteredCardList.get(position).getId().toString();
                Intent intent = new Intent(getContext(), CardDetailActivity.class);
                intent.putExtra("CARD_ID", cardID);
                startActivity(intent);
            }
        });

        filter1 = (Spinner) view.findViewById(R.id.spinner1);
        filter2 = (Spinner) view.findViewById(R.id.spinner2);
        setFilters();

        return view;
    }

    public void onResume() {
        super.onResume();

		// Gets all credit cards sorted by field defined in user preferences
        ItemField sortField = userPreferences.getSetting_CardSortField();
        fullCardList = cardDS.getAll(sortField,false);
        filterCards();

		// Hides list and shows empty list text if there are no credit cards
        if (fullCardList.size() == 0){
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

    private void setFilters(){

        // Creates card status filter with values
        ArrayList<String> cardStatuses = new ArrayList<String>();
        cardStatuses.add("Filter Off");
        cardStatuses.add(CardStatus.OPEN.getName());
        cardStatuses.add(CardStatus.CLOSED.getName());
        FilterSpinnerAdapter cardStatusSpinnerAdapter =new FilterSpinnerAdapter(activity,cardStatuses);
        filter1.setAdapter(cardStatusSpinnerAdapter);
        filter1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userPreferences.setFilter_CardStatus(parent.getItemAtPosition(position).toString());
                onResume();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Sets card status filter to value in user preferences
        String filter1value = userPreferences.getFilter_CardStatus();
        int filter1Position= cardStatuses.indexOf(filter1value);
        if (filter1Position == -1){
            filter1.setSelection(0);
        } else {
            filter1.setSelection(filter1Position);
        }

        // Creates card af filter with values
        ArrayList<String> cardAF = new ArrayList<String>();
        cardAF.add("Filter Off");
        cardAF.add("Annual Fee");
        cardAF.add("No Annual Fee");
        FilterSpinnerAdapter afStatusSpinnerAdapter =new FilterSpinnerAdapter(activity,cardAF);
        filter2.setAdapter(afStatusSpinnerAdapter);
        filter2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userPreferences.setFilter_CardAF(parent.getItemAtPosition(position).toString());
                onResume();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Sets card af filter to value in user preferences
        String filter2value = userPreferences.getFilter_CardAF();
        int filter2Position= cardAF.indexOf(filter2value);
        if (filter2Position == -1){
            filter2.setSelection(0);
        } else {
            filter2.setSelection(filter2Position);
        }
    }

    private void filterCards(){
        filteredCardList = new ArrayList<CreditCard>();

        String filter1value = userPreferences.getFilter_CardStatus();
        String filter2value = userPreferences.getFilter_CardAF();
        for (CreditCard card : fullCardList) {
            if (filter1value.equals("Open")) {
                if (card.getStatus() != CardStatus.OPEN) {
                    continue;
                }
            } else if (filter1value.equals("Closed")) {
                if (card.getStatus() != CardStatus.CLOSED) {
                    continue;
                }
            }
            if (filter2value.equals("Annual Fee")) {
                if (!card.hasAnnualFee()) {
                    continue;
                }
            } else if (filter2value.equals("No Annual Fee")) {
                if (card.hasAnnualFee()) {
                    continue;
                }
            }
            filteredCardList.add(card);
        }
    }
}