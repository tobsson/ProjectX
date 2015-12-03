package com.example.andrius.searchfunction;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class MainActivity extends SearchActivity {
    @Override
    ListAdapter makeMeAnAdapter(Intent intent) {
        return(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items));
    }
}