package com.example.andrius.findatweet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener   ,  android.support.v7.app.ActionBar.OnNavigationListener {


            // action bar
         private android.support.v7.app.ActionBar actionBar;

            // Title navigation Spinner data
         private ArrayList<SpinnerNavItem> navSpinner;

            // Navigation adapter
         private TitleNavigationAdapter adapter;



    private SuggestionsDatabase database;
    private SearchView searchView;
    private SharedPreferences preferences;


    public static final String REQUEST_TAG = "MainVolleyActivity";
    private RequestQueue mQueue;
    //private TextView tweetView;
    private String neutral;
    private String negative;
    private String positive;
    private String user1;
    private String user2;
    private String user3;
    private String tweet1;
    private String tweet2;
    private String tweet3;
    private float longtitude;
    private float latitude;
    private String city;
    private int index;






            @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                AdView mAdView = (AdView) findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);



/*

                getActionBar().
                getActionBar().setCustomView(R.layout.custom_actionbar);
                ActionBar.LayoutParams p = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                p.gravity = Gravity.CENTER;

*/

                actionBar = getSupportActionBar();

            // Hide the action bar title
            actionBar.setDisplayShowTitleEnabled(false);

            // Enabling Spinner dropdown navigation
            actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_LIST);
            //Spinner title navigation data
            navSpinner = new ArrayList<SpinnerNavItem>();
                navSpinner.add(new SpinnerNavItem("Global", R.drawable.globe_white));
            navSpinner.add(new SpinnerNavItem("New York", R.drawable.usa));
            navSpinner.add(new SpinnerNavItem("Stockholm", R.drawable.sweden));
            navSpinner.add(new SpinnerNavItem("Rio De Jeneiro", R.drawable.brazil));
            navSpinner.add(new SpinnerNavItem("Gothenburg", R.drawable.sweden));
            navSpinner.add(new SpinnerNavItem("Helsinki", R.drawable.finland));
            navSpinner.add(new SpinnerNavItem("Copenhagen", R.drawable.denmark));
            navSpinner.add(new SpinnerNavItem("Paris", R.drawable.france));
            navSpinner.add(new SpinnerNavItem("Amsterdam", R.drawable.netherlands));
            navSpinner.add(new SpinnerNavItem("London", R.drawable.england));
            navSpinner.add(new SpinnerNavItem("Berlin", R.drawable.germany));


            // title drop down adapter
            adapter = new TitleNavigationAdapter(getApplicationContext(), navSpinner);

            // assigning the spinner navigation
            actionBar.setListNavigationCallbacks(adapter, this);

                // Changing the action bar icon
                // actionBar.setIcon(R.drawable.ico_actionbar);

           database = new SuggestionsDatabase(this);
           searchView = (SearchView) findViewById(R.id.searchView1);
           searchView.setOnQueryTextListener(this);
                searchView.setSubmitButtonEnabled(true);
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
        // Inflate the menu; this adds items to the action bar if it is present.
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_main, menu);

            return super.onCreateOptionsMenu(menu);
            }



         @Override
            public boolean onOptionsItemSelected(MenuItem item) {

            return super.onOptionsItemSelected(item);

         }

            @Override
            public boolean onSuggestionSelect(int position) {

                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
            //When user will tap on suggested word ,onSuggestionClick(int position) will be called for that.
            // We get the SQLiteCursor object from the SearchView's
            // adapter (SuggestionSimpleCursorAdapter)
            // and get the Suggestion text from it, set the suggestion text in SearchView object


            SQLiteCursor cursor = (SQLiteCursor) searchView.getSuggestionsAdapter().getItem(position);
            int indexColumnSuggestion = cursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);
             searchView.setQuery(cursor.getString(indexColumnSuggestion), false);

                return true;
            }







            @Override
            public boolean onQueryTextSubmit(String query) {
            //When user taps the search or enter, the onQueryTextSubmit() will
            // be triggered and then the search keyword will be saved in Android Sqlite database

                //Hides on-screen keyboard after search term i submitted
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                //Loading dialog window when searching tweets
                final ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please be patient ...", "Analyzing tweets ...", true);
                ringProgressDialog.setCancelable(false);
                ringProgressDialog.setIndeterminate(true);


                index = actionBar.getSelectedNavigationIndex();

                Log.d("LOOOOG", "INDEX"+ index);
                longtitude = navSpinner.get(index).getLongtitude();
                latitude = navSpinner.get(index).getLatitude();
                Log.d("LOOOOG", "longtitude"+ longtitude);


                    mQueue = new RequestQueue(new DiskBasedCache(getApplicationContext().getCacheDir(), 10 * 1024 * 1024), new BasicNetwork(new HurlStack()));
                    mQueue.start();
                    final String keyword = searchView.getQuery().toString().replaceAll(" ", "_").toLowerCase();
                    String url = "";
                    if (index == 0){
                        url = "http://83.248.73.168:8080/findtweets?query="+keyword;
                        Log.d("LOOOOG url", "URL " + url);
                    } else {
                        url = "http://83.248.73.168:8080/findtweets?query="+keyword + "&loc="+ latitude +","+longtitude+",20km";
                        Log.d("LOOOOG url", "URL " + url);}

                    final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {


                            try {

                                //Create the bundle
                                Bundle bundle = new Bundle();

                                neutral = ((JSONObject) response).getString
                                        ("neutral").toString();

                                negative =  ((JSONObject) response).getString
                                        ("negative").toString();
                                positive =  ((JSONObject) response).getString
                                        ("positive").toString();

                                //Make sure third tweet is present
                                if(((JSONObject) response).has("11")) {
                                tweet1 =  ((JSONObject) response).getString
                                        ("11").toString();
                                    //And add to bundle
                                    bundle.putString("tweet1", tweet1);
                                }
                                //Make sure second tweet is present
                                if(((JSONObject) response).has("12")) {
                                tweet2 =  ((JSONObject) response).getString
                                        ("12").toString();
                                    //And add to bundle
                                    bundle.putString("tweet2", tweet2);}


                                tweet3 =  ((JSONObject) response).getString
                                        ("13").toString();

                                //Make sure third tweet user is present
                                if(((JSONObject) response).has("1")) {
                                user1 =  ((JSONObject) response).getString
                                        ("1").toString();
                                    //And add to bundle
                                    bundle.putString("user1", user1);
                                }
                                //Make sure second tweet user is present
                                if(((JSONObject) response).has("2")) {
                                user2 =  ((JSONObject) response).getString
                                        ("2").toString();
                                    //And add to bundle
                                    bundle.putString("user2", user2);
                                }

                                user3 =  ((JSONObject) response).getString
                                        ("3").toString();

                                //tweetView.setText(neutral+" "+positive + " "+ negative);
                                Log.d("log", "that worked1 " + neutral +" "+positive + " "+ negative + " NEW" + tweet1 +  " NEW" + tweet2 +  " NEW"+ tweet3);

                                Intent i = new Intent(MainActivity.this, ResultsActivity.class);



                                //Add remaining data from json to bundle
                                bundle.putString("neutral", neutral);
                                bundle.putString("negative", negative);
                                bundle.putString("positive", positive);
                                bundle.putString("user3", user3);
                                bundle.putString("tweet3", tweet3);
                                bundle.putString("keyword", keyword);
                                bundle.putInt("index", index);

                                //Add the bundle to the intent
                                i.putExtras(bundle);

                                ringProgressDialog.dismiss();

                                //Fire that second activity
                                startActivity(i);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            ringProgressDialog.dismiss();

                            //tweetView.setText(error.getMessage());
                            Toast.makeText(MainActivity.this, "No Results found. Please search another term, or check your internet connection",
                                    Toast.LENGTH_LONG).show();
                            Log.d("log", "error");
                        }
                    });
                    jsonRequest.setTag(REQUEST_TAG);
                    int socketTimeout = 7000;//5 seconds - change to what you want
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    jsonRequest.setRetryPolicy(policy);
                    mQueue.add(jsonRequest);
                    Log.d("log", "request added to the queue");


                long result = database.insertSuggestion(query);
                return result != -1;
            }



            @Override
            public boolean onQueryTextChange(String newText) {
                //If the user writes a string for example "Hel" or "H" in SearchView the onQueryTextChange()
                // will be called and then search for this keyword  will be performed in Android SQLiteDatabase (SuggestionDatabase).
                // If "Hel" or "H" matches "Hello" , displays the results of
                // query by setting the returned Cursor in SuggestionSimpleCursorAdapter
                // and then set this adapter in SearchView.

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
                }}





//Actionbar navigation item select listener
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
//Action to be taken after selecting a spinner item
        return false;
    }
}

