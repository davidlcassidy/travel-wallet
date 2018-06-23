package com.davidlcassidy.travelwallet.Fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.davidlcassidy.travelwallet.Activities.MainActivity;
import com.davidlcassidy.travelwallet.Adapters.FilterSpinnerAdapter;
import com.davidlcassidy.travelwallet.Adapters.ProgramListAdapter;
import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.Database.ProgramDataSource;
import com.davidlcassidy.travelwallet.Activities.ProgramDetailActivity;
import com.davidlcassidy.travelwallet.R;

import java.util.ArrayList;

/*
ProgramListFragment is the second fragment within MainActivity that displays a summary
of the user added loyalty programs. It is primarily composed of a listview utilizing
the ProgramListAdapter. It also contains a floating "add" button which creates
ProgramAddEditActivity to allow new programs to be added. Finally, there a textview
that is only visible to the user when there are no programs (empty listview),
directing users to add new programs with the "add" button.
 */

public class ProgramListFragment extends Fragment {

    private Activity activity;
    private UserPreferences userPreferences;
    private ProgramDataSource programDS;
    private ArrayList<LoyaltyProgram> fullProgramList, filteredProgramList;
    private TextView emptyListText;
    private ListView lv;
    private Spinner filter1, filter2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        activity = getActivity();

        userPreferences = UserPreferences.getInstance(getContext());
        programDS = ProgramDataSource.getInstance(getContext());

		// Sets the text used when loyalty program list is empty
        emptyListText = (TextView) view.findViewById(R.id.emptyListText);
        emptyListText.setText("Click the + button below to add a loyalty program.");

		// Sets loyalty program click listener
        lv = (ListView) view.findViewById(R.id.fragmentList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			// Opens ProgramDetail Activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String programID = filteredProgramList.get(position).getId().toString();
                Intent intent = new Intent(getContext(), ProgramDetailActivity.class);
                intent.putExtra("PROGRAM_ID", programID);
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

		// Gets all loyalty programs sorted by field defined in user preferences
        ItemField sortField = userPreferences.getSetting_ProgramSortField();
        fullProgramList = programDS.getAll(sortField);
        filterPrograms();

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

    private void setFilters(){

        // Creates program type filter with values
        ArrayList<String> programTypes = programDS.getAvailableTypes(false);
        programTypes.add(0,"Filter Off");
        FilterSpinnerAdapter programTypeSpinnerAdapter =new FilterSpinnerAdapter(activity,programTypes);
        filter1.setAdapter(programTypeSpinnerAdapter);
        filter1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userPreferences.setFilter_ProgramType(parent.getItemAtPosition(position).toString());
                onResume();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Sets program type filter to value in user preferences
        String filter1value = userPreferences.getFilter_ProgramType();
        int filter1Position= programTypes.indexOf(filter1value);
        if (filter1Position == -1){
            filter1.setSelection(0);
        } else {
            filter1.setSelection(filter1Position);
        }

        // Creates program points filter with values
        ArrayList<String> programPoints = new ArrayList<String>();
        programPoints.add("Filter Off");
        programPoints.add("Points = 0");
        programPoints.add("Points > 0");
        FilterSpinnerAdapter programPointsSpinnerAdapter =new FilterSpinnerAdapter(activity,programPoints);
        filter2.setAdapter(programPointsSpinnerAdapter);
        filter2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userPreferences.setFilter_ProgramPoints(parent.getItemAtPosition(position).toString());
                onResume();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Sets program points filter to value in user preferences
        String filter2value = userPreferences.getFilter_ProgramPoints();
        int filter2Position= programPoints.indexOf(filter2value);
        if (filter2Position == -1){
            filter2.setSelection(0);
        } else {
            filter2.setSelection(filter2Position);
        }
    }

    private void filterPrograms(){
        filteredProgramList = new ArrayList<LoyaltyProgram>();

        String filter1value = userPreferences.getFilter_ProgramType();
        String filter2value = userPreferences.getFilter_ProgramPoints();
        for (LoyaltyProgram program : fullProgramList) {
            if (!filter1value.equals("Filter Off")) {
                if (!program.getType().equals(filter1value)) {
                    continue;
                }
            }
            if (filter2value.equals("Points = 0")) {
                if (program.getPoints() != 0) {
                    continue;
                }
            } else if (filter2value.equals("Points > 0")) {
                if (program.getPoints() == 0) {
                    continue;
                }
            }
            filteredProgramList.add(program);
        }
    }
}