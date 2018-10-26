package com.davidlcassidy.travelwallet.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.davidlcassidy.travelwallet.R;

import java.util.ArrayList;

public class SingleChoiceAdapter extends ArrayAdapter<String> {

    private Context context;
    private int selectedIndex = -1;
    private int numOfRows;
    private static String delimiter = "////";

    public SingleChoiceAdapter(Context context, ArrayList<String> data) {
        super(context, 0, data);
        this.context = context;

        // Check for delimiter to determine if second row is needed
        if (data.get(0).contains(delimiter)){
            numOfRows = 2;
        } else {
            numOfRows = 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder item = null;

        if (row == null) {

            // Inflate layout for appropriate number of rows
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            if (numOfRows == 1) {
                row = inflater.inflate(R.layout.listitem_singlechoice1line, parent, false);
            } else if (numOfRows == 2){
                row = inflater.inflate(R.layout.listitem_singlechoice2lines, parent, false);
            }

            // Gets adapter fields
            item = new ItemHolder();
            item.text1 = (TextView) row.findViewById(R.id.text1);
            if (numOfRows == 2){
                item.text2 = (TextView) row.findViewById(R.id.text2);
            }
            row.setTag(item);

        } else {
            item = (ItemHolder) row.getTag();
        }

        // Update radio button
        RadioButton rb = (RadioButton) row.findViewById(R.id.radiobutton);
        if(selectedIndex == position){
            rb.setChecked(true);
        }
        else{
            rb.setChecked(false);
        }

        // Sets field values
        if (numOfRows == 1) {
            String dataText = getItem(position);
            item.text1.setText(dataText);
        } else if (numOfRows == 2){
            String dataText = getItem(position);
            String[] splited = dataText.split(delimiter);
            item.text1.setText(splited[0]);
            item.text2.setText(splited[1]);
        }

        return row;
    }

    public void setSelectedIndex(int index){
        selectedIndex = index;
    }

    public static String getDelimiter(){
        return delimiter;
    }

    public static class ItemHolder {
        TextView text1;
        TextView text2;
    }
}