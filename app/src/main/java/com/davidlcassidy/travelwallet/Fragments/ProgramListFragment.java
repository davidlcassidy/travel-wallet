/*
 * Travel Wallet Android App
 * Copyright (C) 2018 David L Cassidy. All rights reserved.
 * Last modified 7/25/19 10:48 PM
 */

package com.davidlcassidy.travelwallet.Fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Adapters.FilterSpinnerAdapter;
import com.davidlcassidy.travelwallet.Adapters.ProgramListAdapter;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.Owner;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;
import com.davidlcassidy.travelwallet.Database.OwnerDataSource;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Activities.ProgramDetailActivity;
import com.davidlcassidy.travelwallet.R;

import java.util.ArrayList;
import java.util.Arrays;

/*
ProgramListFragment is the second fragment within MainActivity that displays a summary
of the user added loyalty programs. It is primarily composed of a listview utilizing
the ProgramListAdapter. It also contains two spinners for filtering over the listview
and a floating "add" button which creates ProgramAddEditActivity to allow new programs
to be added. Finally, there a textview that is only visible to the user when there are
no programs (empty listview), directing users to add new programs with the "add" button.
 */

public class ProgramListFragment extends Fragment {

    private Activity activity;
    private UserPreferences userPreferences;
    private ProgramDataSource programDS;
    private OwnerDataSource userDS;
    private ArrayList<LoyaltyProgram> fullProgramList, filteredProgramList;
    private TextView emptyListText;
    private ListView lv;
    private LinearLayout filterLayout;
    private Spinner filter1, filter2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        activity = getActivity();

        userPreferences = UserPreferences.getInstance(getContext());
        userPreferences.setProgramFiltersUpdateRequired(true);

        programDS = ProgramDataSource.getInstance(getContext());
        userDS = OwnerDataSource.getInstance(getContext());

		// Sets the text used when loyalty program list is empty
        emptyListText = (TextView) view.findViewById(R.id.emptyListText);
        emptyListText.setText("Click the + button below to add a loyalty program.");

		// Sets loyalty program click listener
        lv = (ListView) view.findViewById(R.id.fragmentList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			// Opens ProgramDetail Activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer programID = filteredProgramList.get(position).getId();
                Intent intent = new Intent(getContext(), ProgramDetailActivity.class);
                intent.putExtra("PROGRAM_ID", programID);
                startActivity(intent);
            }
        });

        filterLayout = (LinearLayout) view.findViewById(R.id.filterLayout);
        filter1 = (Spinner) view.findViewById(R.id.spinner1);
        filter2 = (Spinner) view.findViewById(R.id.spinner2);
        setFilters(false);

        return view;
    }

    public void onResume() {
        super.onResume();

		// Gets all loyalty programs sorted by field defined in user preferences
        ItemField sortField = userPreferences.getCustom_ProgramSortField();
        fullProgramList = programDS.getAll(null, sortField, false);

        if (userPreferences.getProgramFiltersUpdateRequired() == true) {
            if (userPreferences.getCustom_ProgramFilters() == true) {
                filterLayout.setVisibility(LinearLayout.VISIBLE);
                setFilters(true);
                filterPrograms();
            } else {
                filterLayout.setVisibility(LinearLayout.GONE);
                filteredProgramList = fullProgramList;
            }
            userPreferences.setProgramFiltersUpdateRequired(false);
        }

		// Hides list and shows empty list text if there are no loyalty programs
        if (fullProgramList.size() == 0){
            emptyListText.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            filter1.setVisibility(View.GONE);
            filter2.setVisibility(View.GONE);

        } else {

			// Add loyalty programs to list view
            emptyListText.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
            filter1.setVisibility(View.VISIBLE);
            filter2.setVisibility(View.VISIBLE);

			// Sets adaptor to list view
            ProgramListAdapter adapter = new ProgramListAdapter(activity, filteredProgramList);
            lv.setAdapter(adapter);
        }

    }

    private void setFilters(boolean onlySetOwnerFilter){

        // Creates program owner filter with values
        ArrayList<String> owners = userDS.getAllNames();
        if (owners.size() == 0) {
            owners.add(0, "No Owners Added");
            filter1.setEnabled(false);
            filter1.setClickable(false);
        } else {
            owners.add(0, "All Owners");
            filter1.setEnabled(true);
            filter1.setClickable(true);
        }
        FilterSpinnerAdapter ownersSpinnerAdapter = new FilterSpinnerAdapter(activity, owners);
        filter1.setAdapter(ownersSpinnerAdapter);
        filter1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userPreferences.setFilter_ProgramOwner(parent.getItemAtPosition(position).toString());
                filterPrograms();
                onResume();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Sets program owner filter to value in user preferences
        String filter1value = userPreferences.getFilter_ProgramOwner();
        int filter1Position = owners.indexOf(filter1value);
        if (filter1Position == -1) {
            filter1.setSelection(0);
        } else {
            filter1.setSelection(filter1Position);
        }

        if (!onlySetOwnerFilter) {

            // Creates program type filter with values
            ArrayList<String> programTypes = programDS.getAvailableTypes(false);
            programTypes.add(0, "All Types");
            FilterSpinnerAdapter programTypeSpinnerAdapter = new FilterSpinnerAdapter(activity, programTypes);
            filter2.setAdapter(programTypeSpinnerAdapter);
            filter2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    userPreferences.setFilter_ProgramType(parent.getItemAtPosition(position).toString());
                    filterPrograms();
                    onResume();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            // Sets program type filter to value in user preferences
            String filter2value = userPreferences.getFilter_ProgramType();
            int filter2Position = programTypes.indexOf(filter2value);
            if (filter2Position == -1) {
                filter2.setSelection(0);
            } else {
                filter2.setSelection(filter2Position);
            }
        }
    }

    private void filterPrograms(){
        filteredProgramList = new ArrayList<LoyaltyProgram>();

        String filter1value = userPreferences.getFilter_ProgramOwner();
        String filter2value = userPreferences.getFilter_ProgramType();
        for (LoyaltyProgram program : fullProgramList) {
            if (!Arrays.asList("All Owners", "No Owners Added").contains(filter1value)) {
                Owner owner = program.getOwner();
                if (owner == null || !owner.getName().equals(filter1value)) {
                    continue;
                }
            }
            if (!filter2value.equals("All Types")) {
                if (!program.getType().equals(filter2value)) {
                    continue;
                }
            }
            filteredProgramList.add(program);
        }
    }
}