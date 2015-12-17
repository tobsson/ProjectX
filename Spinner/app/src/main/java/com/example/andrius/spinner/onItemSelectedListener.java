package com.example.andrius.spinner;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by andrius on 2015-12-08.
 */
public  class onItemSelectedListener implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent,
                               View view, int pos, long id) {
        Toast.makeText(parent.getContext(), "Searching for tweets in " +
                parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
    }

    public void onNothingSelected(AdapterView parent) {
        // Do nothing.
    }
}