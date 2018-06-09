package com.davidlcassidy.travelwallet.Fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

    private View view;
    private Context context;
    private Activity activity;

    private UserPreferences userPreferences;
    private ProgramDataSource programDS;
    private ArrayList<LoyaltyProgram> programList;

    private TextView emptyListText;
    private ListView lv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list, container, false);
        context = view.getContext();
        activity= getActivity();

        userPreferences = UserPreferences.getInstance(context);
        programDS = ProgramDataSource.getInstance(activity);

		// Sets the text used when loyalty program list is empty
        emptyListText = (TextView) view.findViewById(R.id.emptyListText);
        emptyListText.setText("Click the + button below to add a loyalty program.");

		// Sets loyalty program click listener
        lv = (ListView) view.findViewById(R.id.fragmentList);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			// Opens ProgramDetail Activity
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String programID = programList.get(position).getId().toString();
                Intent intent = new Intent(context, ProgramDetailActivity.class);
                intent.putExtra("PROGRAM_ID", programID);
                startActivity(intent);
            }
        });

        return view;
    }

    public void onResume() {
        super.onResume();

		// Gets all loyalty programs sorted by field defined in user preferences
        ItemField sortField = userPreferences.getProgramSortField();
        programList = programDS.getAll(sortField);

		// Hides list and shows empty list text if there are no loyalty programs
        if (programList.size() == 0){
            emptyListText.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);

        } else {

			// Add loyalty programs to list view
            emptyListText.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);

			// Sets adaptor to list view
            ProgramListAdapter adapter = new ProgramListAdapter(activity, programList);
            lv.setAdapter(adapter);
        }

    }
}