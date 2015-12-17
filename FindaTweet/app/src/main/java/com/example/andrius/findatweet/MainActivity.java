package com.example.andrius.findatweet;

import android.app.ActionBar;
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
import android.widget.SearchView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener   ///, ActionBar.OnNavigationListener
{


            // action bar
         ///   private ActionBar actionBar;

            // Title navigation Spinner data
           /// private ArrayList<SpinnerNavItem> navSpinner;

            // Navigation adapter
           //// private TitleNavigationAdapter adapter1;





    public Button button;
    private SuggestionsDatabase database;
    private SearchView searchView;
    private SharedPreferences preferences;






            @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

            ///    actionBar = getActionBar();

                // Hide the action bar title
               /// actionBar.setDisplayShowTitleEnabled(false);

                // Enabling Spinner dropdown navigation
               //// actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

                // Spinner title navigation data
               //// navSpinner = new ArrayList<SpinnerNavItem>();
                ////navSpinner.add(new SpinnerNavItem("USA", R.drawable.usa));
               //// navSpinner
                      ///  .add(new SpinnerNavItem("Sweden", R.drawable.sweden));
               ///// navSpinner.add(new SpinnerNavItem("Brasil", R.drawable.brazil));
               //// navSpinner.add(new SpinnerNavItem("Sweden", R.drawable.sweden));

                // title drop down adapter
               //// adapter1 = new TitleNavigationAdapter(getApplicationContext(),
                     ///   navSpinner);

                // assigning the spinner navigation
               //// actionBar.setListNavigationCallbacks(adapter1, this);

                // Changing the action bar icon
                // actionBar.setIcon(R.drawable.ico_actionbar);



    database = new SuggestionsDatabase(this);
        searchView = (SearchView) findViewById(R.id.searchView1);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);






                AutoCompleteTextView search_text = (AutoCompleteTextView) searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        search_text.setThreshold(1);









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
                int indexColumnSuggestion = cursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);

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
    }}


  ///  @Override
   /// public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        // Action to be taken after selecting a spinner item
      ///  return false;




