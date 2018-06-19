package com.davidlcassidy.travelwallet.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.Classes.LoyaltyProgram;
import com.davidlcassidy.travelwallet.EnumTypes.Currency;
import com.davidlcassidy.travelwallet.EnumTypes.NumberPattern;
import com.davidlcassidy.travelwallet.EnumTypes.ItemField;
import com.davidlcassidy.travelwallet.R;
import com.davidlcassidy.travelwallet.Classes.UserPreferences;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
ProgramListFragment is the second fragment within MainActivity. It is where a summary of
all user added loyalty programs is displayed to the user. It is primarily composed of a
listview utilizing the ProgramListAdapter. There is also a floating "add" button allowing
the user to add new loyalty programs and a textview directing users to the "add" button
that is only visible when there are no loyalty programs (empty listview).
 */

public class ProgramListAdapter extends ArrayAdapter<LoyaltyProgram> {

    private UserPreferences userPreferences;

    public ProgramListAdapter(Context context, List<LoyaltyProgram> programs) {
        super(context, 0, programs);
        userPreferences = UserPreferences.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_main, parent, false);
        }

        final ItemField primaryField = userPreferences.getSetting_ProgramPrimaryField();
        DecimalFormat numberFormat = NumberPattern.COMMADOT.getNumberFormat();
        SimpleDateFormat dateFormat = userPreferences.getSetting_DatePattern().getDateFormat();

		// Gets the item at this position
        LoyaltyProgram program = getItem(position);

		// Gets adapter fields
        ImageView logo = (ImageView) convertView.findViewById(R.id.logo);
        TextView programField = (TextView) convertView.findViewById(R.id.firstField);
        TextView messageField = (TextView) convertView.findViewById(R.id.secondField);

		// Gets logo resource from name
        Context context = logo.getContext();
        int logoNum = context.getResources().getIdentifier(program.getLogoIcon(), "drawable", context.getPackageName());
        logo.setImageResource(logoNum);

		// Sets field values, based on user preferences
        programField.setText(program.getName());
        switch (primaryField.getName()) {
            case "Account Number":
                messageField.setText(program.getAccountNumber());
                break;
            case "Points":
                messageField.setText(numberFormat.format(program.getPoints()));
                break;
            case "Value":
                Currency currency = userPreferences.getSetting_Currency();
                BigDecimal programValue = program.getTotalValue();
                String programValueString = currency.numToString(programValue, NumberPattern.COMMADOT);
                messageField.setText(programValueString);
                break;
            case "Expiration Date":
                Date lastActivityDate = program.getLastActivityDate();
                Date expirationDate = program.getExpirationDate();
                String expirationOverride = program.getExpirationOverride();
                String message = null;
				// Uses expiration override value if there is no expiration date
                if (program.hasExpirationDate() == false && expirationOverride != null){
                    message = expirationOverride;
                } else if (expirationDate != null && lastActivityDate != null) {
                    message = dateFormat.format(expirationDate);
                }
                messageField.setText(message);
                break;
        }
        return convertView;
    }
}