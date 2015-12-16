package com.example.andrius.findatweet;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener
        {


    public Button button;
    private SuggestionsDatabase database;
    private SearchView searchView;
    private SharedPreferences preferences;

            HashMap<String, List<String>> countriesHashMap;
            List<String> countriesHashMapKeys;
            ExpandableListView expandableListView;
            ListCustomAdapter adapter;




            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new SuggestionsDatabase(this);
        searchView = (SearchView) findViewById(R.id.searchView1);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);






                AutoCompleteTextView search_text = (AutoCompleteTextView) searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        search_text.setThreshold(1);
///Code for expandableListView
          expandableListView = (ExpandableListView) findViewById(R.id.expandableList);
          countriesHashMap = CountryDataProvider.getDataHashMap();
          countriesHashMapKeys = new ArrayList<String>(countriesHashMap.keySet());

          adapter = new ListCustomAdapter(this, countriesHashMap, countriesHashMapKeys);
          expandableListView.setAdapter(adapter);

           expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int groupPosition) {
                        Toast.makeText(MainActivity.this,
                                countriesHashMapKeys.get(groupPosition)
                                        + " expanded", Toast.LENGTH_SHORT).show();
                    }
                });

           expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                    @Override
                    public void onGroupCollapse(int groupPosition) {
                        Toast.makeText(MainActivity.this, countriesHashMapKeys.get(groupPosition) + " collapsed", Toast.LENGTH_SHORT).show();
                    }
                });

           expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView expandableListView, View clickedView, int groupPosition, int childPosition, long id) {
                        Toast.makeText(MainActivity.this, "Selected " + countriesHashMap.get(countriesHashMapKeys.get(groupPosition)).get(childPosition)
                                + " from " + countriesHashMapKeys.get(groupPosition), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });








        // Get the shared preferences
        preferences =  getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Check if onboarding_complete is false
        if(!preferences.getBoolean("onboarding_complete",false)) {
            // Start the onboarding Activity
            Intent onboarding = new Intent(this, Onboarding.class);
            startActivity(onboarding);

            // Close the main Activity
            finish();
            return;
        }
    }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_main, menu);




                return super.onCreateOptionsMenu(menu);
            }



        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.


           // int id = item.getItemId();
           // if (id == R.id.action_location) {
               // startActivity(new Intent(getApplicationContext(),SearchResultsActivity.class));
                //return true;
           // }
            return super.onOptionsItemSelected(item);
        }

            @Override
            public boolean onSuggestionSelect(int position) {

                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                SQLiteCursor cursor = (SQLiteCursor) searchView.getSuggestionsAdapter().getItem(position);
                int indexColumnSuggestion = cursor.getColumnIndex( SuggestionsDatabase.FIELD_SUGGESTION);

                searchView.setQuery(cursor.getString(indexColumnSuggestion), false);




                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                long result = database.insertSuggestion(query);
                return result != -1;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                Cursor cursor = database.getSuggestions(newText);
                if(cursor.getCount() != 0)
                {
                    String[] columns = new String[] {SuggestionsDatabase.FIELD_SUGGESTION };
                    int[] columnTextId = new int[] { android.R.id.text1};

                    SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getBaseContext(),
                            android.R.layout.simple_list_item_1, cursor,
                            columns , columnTextId
                            , 0);

             searchView.setSuggestionsAdapter(simple);
                    return true;
                }
                else
                {
                    return false;
                }
            }
            public void onButtonClick(View v){
        //open SearchResultsActivity when search button is clicked

        if (v.getId() == R.id.btSearch){
            Intent i = new Intent (MainActivity.this, SearchResultsActivity.class);
            startActivity(i);
        }
    }


        }

