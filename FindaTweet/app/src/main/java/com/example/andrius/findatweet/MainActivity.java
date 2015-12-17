package com.example.andrius.findatweet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener {
    public Button button;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);


        database = new SuggestionsDatabase(this);
        searchView = (SearchView) findViewById(R.id.searchView1);
        //tweetView = (TextView) findViewById(R.id.tweetView);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSuggestionListener(this);

        final AutoCompleteTextView search_text = (AutoCompleteTextView) searchView.findViewById(searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        search_text.setThreshold(1);


        // Get the shared preferences
        preferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Check if onboarding_complete is false
        if (!preferences.getBoolean("onboarding_complete", false)) {
            // Start the onboarding Activity
            Intent onboarding = new Intent(this, Onboarding.class);
            startActivity(onboarding);

            // Close the main Activity
            finish();
            return;
        }

       /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQueue = CustomVolleyRequestQueue.getInstance(getApplicationContext())
                        .getRequestQueue();
                String url = "http://mysafeinfo.com/api/data?list=englishmonarchs&format=json";
                final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        tweetView.setText("Response is: " + response);
                        try {
                            tweetView.setText(tweetView.getText() + "\n\n" + ((JSONObject) response).getString
                                    ("nm"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tweetView.setText(error.getMessage());
                        Log.d("log", "error");
                    }
                });
                jsonRequest.setTag(REQUEST_TAG);
                mQueue.add(jsonRequest);


            }

        });
*/

    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
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
        if (cursor.getCount() != 0) {
            String[] columns = new String[]{SuggestionsDatabase.FIELD_SUGGESTION};
            int[] columnTextId = new int[]{android.R.id.text1};

            SuggestionSimpleCursorAdapter simple = new SuggestionSimpleCursorAdapter(getBaseContext(),
                    android.R.layout.simple_list_item_1, cursor,
                    columns, columnTextId
                    , 0);

            searchView.setSuggestionsAdapter(simple);
            return true;
        } else {
            return false;
        }
    }


    public void onButtonClick(View v) {
        //open SearchResultsActivity when search button is clicked


        if (v.getId() == R.id.btSearch) {
            mQueue = new RequestQueue(new DiskBasedCache(getApplicationContext().getCacheDir(), 10 * 1024 * 1024), new BasicNetwork(new HurlStack()));
            mQueue.start();
            final String keyword = searchView.getQuery().toString().replaceAll(" ", "_").toLowerCase();
            String url = "http://83.248.73.168:8080/findtweets?query="+keyword;
            final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {


                    try {
                        neutral = ((JSONObject) response).getString
                                ("neutral").toString();

                        negative =  ((JSONObject) response).getString
                                ("negative").toString();
                        positive =  ((JSONObject) response).getString
                                ("positive").toString();
                        tweet1 =  ((JSONObject) response).getString
                                ("11").toString();
                        tweet2 =  ((JSONObject) response).getString
                                ("12").toString();
                        tweet3 =  ((JSONObject) response).getString
                                ("13").toString();
                        user1 =  ((JSONObject) response).getString
                                ("1").toString();
                        user2 =  ((JSONObject) response).getString
                                ("2").toString();
                        user3 =  ((JSONObject) response).getString
                                ("3").toString();
                        //tweetView.setText(neutral+" "+positive + " "+ negative);
                        Log.d("log", "that shit worked1 " + neutral +" "+positive + " "+ negative + " NEW" + tweet1 +  " NEW" + tweet2 +  " NEW"+ tweet3);

                        Intent i = new Intent(MainActivity.this, ResultsActivity.class);

                        //Create the bundle
                        Bundle bundle = new Bundle();

                        //Add your data to bundle
                        bundle.putString("neutral", neutral);
                        bundle.putString("negative", negative);
                        bundle.putString("positive", positive);
                        bundle.putString("user1", user1);
                        bundle.putString("user2", user2);
                        bundle.putString("user3", user3);
                        bundle.putString("tweet1", tweet1);
                        bundle.putString("tweet2", tweet2);
                        bundle.putString("tweet3", tweet3);
                        bundle.putString("keyword", keyword);

                        //Add the bundle to the intent
                        i.putExtras(bundle);

                        //Fire that second activity
                        startActivity(i);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //tweetView.setText(error.getMessage());
                    Toast.makeText(MainActivity.this, "No Results found. Please search another term.",
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
        }
    }




}

/*mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQueue = CustomVolleyRequestQueue.getInstance(getApplicationContext())
                        .getRequestQueue();
                String url = "http://mysafeinfo.com/api/data?list=englishmonarchs&format=json";
                final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        mTextView.setText("Response is: " + response);
                        try {
                            mTextView.setText(mTextView.getText() + "\n\n" + ((JSONObject) response).getString
                                    ("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText(error.getMessage());
                        Log.d("log", "error");
                    }
                });
                jsonRequest.setTag(REQUEST_TAG);
                mQueue.add(jsonRequest);


            }

        });*/
