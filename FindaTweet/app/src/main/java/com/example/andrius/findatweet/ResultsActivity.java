package com.example.andrius.findatweet;

/**
 * Created by andrius on 2015-12-17.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity implements View.OnKeyListener, SearchView.OnQueryTextListener,
        SearchView.OnSuggestionListener,  android.support.v7.app.ActionBar.OnNavigationListener   {

    // action bar
    private android.support.v7.app.ActionBar actionBar;

    // Title navigation Spinner data
    private ArrayList<SpinnerNavItem> navSpinner;

    // Navigation adapter
    private TitleNavigationAdapter adapter;

    private PieChart mChart;
    //private SeekBar mSeekBarX, mSeekBarY;
    //private TextView tvX, tvY;
    public static final String REQUEST_TAG = "ResultsActivity";
    private float[] yData;
    private String[] xData = { "Positive", "Neutral", "Negative" };
    private Button chartBtn;
    private TextView tweetsView;
    private RequestQueue mQueue;
    private Typeface tf;
    private EditText searchField;

    private int neutral;
    private int negative;
    private int positive;
    private String tweet1;
    private String tweet2;
    private String tweet3;
    private String user1;
    private String user2;
    private String user3;
    private String keyword;
    private String randomTweets;
    private String randomTweets2;
    private int locIndex;
    Bundle bundle;

    //Stores values for location search
    private float longtitude;
    private float latitude;

    //Variable sotring the index of the navSpinner
    private int index;



    private SuggestionsDatabase database;
    private SearchView searchView;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE,
        // WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_piechart);


        database = new SuggestionsDatabase(this);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnSuggestionListener(this);


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


        tweetsView = (TextView) findViewById(R.id.tweetView);

        bundle = getIntent().getExtras();
        neutral = Integer.parseInt(bundle.getString("neutral"));
        positive = Integer.parseInt(bundle.getString("positive"));
        negative = Integer.parseInt(bundle.getString("negative"));

        //Check if user for tweet is present
        if(this.getIntent().getExtras().containsKey("user1")){
        user1 = bundle.getString("user1");
        randomTweets += user1 + "\n";
        }

        //Check if tweet is present
        if(this.getIntent().getExtras().containsKey("tweet1")){
        tweet1 = bundle.getString("tweet1");
            randomTweets += tweet1 + "\n\n";
        }

        //Check if user for tweet is present
        if(this.getIntent().getExtras().containsKey("user2")){
            user2 = bundle.getString("user2");
            randomTweets += user2 + "\n";
        }

        //Check if tweet is present
        if(this.getIntent().getExtras().containsKey("tweet2")) {
            tweet2 = bundle.getString("tweet2");
            randomTweets += tweet2 + "\n\n";
        }


        user3 = bundle.getString("user3");
        randomTweets+= user3 +"\n";
        tweet3 = bundle.getString("tweet3");
        randomTweets+= tweet3;

        keyword = bundle.getString("keyword");
        locIndex = bundle.getInt("index");
        actionBar.setSelectedNavigationItem(locIndex);

        yData = new float[]{positive, neutral, negative};
        //tweetsView = (TextView)findViewById(R.id.tweetView);

        //Set the randomtweets stored in the String "randomTweets" in textView
        tweetsView.setText(randomTweets);
        randomTweets="";

        //tweetsView.setText(user1 + "\n" + tweet1 + "\n\n" + user2 + "\n" + tweet2 + "\n\n" + user3 + "\n" + tweet3);


        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");

        mChart.setDragDecelerationFrictionCoef(0.150f);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);

        mChart.setTransparentCircleColor(Color.WHITE);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);

        // distChart.setUnit(" €");
        // distChart.setDrawUnitsInChart(true);

        // add a selection listener
        //mChart.setOnChartValueSelectedListener(this);

        mChart.setCenterText("What do people think about " + keyword + "?");

        setData(1, 100);

        mChart.animateY(2000, Easing.EasingOption.EaseOutBack);
        // distChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.PIECHART_CENTER);
        l.setXEntrySpace(15f);
        l.setYEntrySpace(10f);


    }





    private void setData(int count, float range) {

        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 0; i < yData.length; i++)
            yVals1.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        PieDataSet dataSet = new PieDataSet(yVals1, "Opinion Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);


        data.setValueTextSize(20f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tf);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");

        mChart.setDragDecelerationFrictionCoef(0.95f);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);

        mChart.setTransparentCircleColor(Color.WHITE);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);
        mChart.getLegend().setEnabled(false);
        mChart.invalidate();
        mChart.animateY(2000, Easing.EasingOption.EaseInOutQuad);
    }






    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        //Hides on-screen keyboard after search term i submitted
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);


        //Loading dialog window when searching tweets
        final ProgressDialog ringProgressDialog = ProgressDialog.show(ResultsActivity.this, "Please be patient ...", "Analyzing tweets ...", true);
        ringProgressDialog.setCancelable(false);
        ringProgressDialog.setIndeterminate(true);

        //Gets the index of actionbar selection
        index = actionBar.getSelectedNavigationIndex();

        Log.d("LOOOOG", "INDEX"+ index);

        //Getting the longtitude + latitude of selected item in navSpinner
        longtitude = navSpinner.get(index).getLongtitude();
        latitude = navSpinner.get(index).getLatitude();

        Log.d("LOOOOG", "longtitude"+ longtitude);

        mQueue = new RequestQueue(new DiskBasedCache(getApplicationContext().getCacheDir(), 10 * 1024 * 1024), new BasicNetwork(new HurlStack()));
        mQueue.start();
        final String keyword = searchView.getQuery().toString().replaceAll(" ", "_").toLowerCase();

        String url = "";
        //If index is 0 its a global search
        if (index == 0){
            url = "http://83.248.73.168:8080/findtweets?query="+keyword;
            Log.d("LOOOOG url", "URL " + url);
            //If index is other than 0 the location search query is used with long/lat parameters within the http request
        } else {
            url = "http://83.248.73.168:8080/findtweets?query="+keyword + "&loc="+ latitude +","+longtitude+",20km";
            Log.d("LOOOOG url", "URL " + url);}

        //String url = "http://83.248.73.168:8080/findtweets?query="+keyword;
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    neutral = Integer.parseInt(((JSONObject) response).getString
                            ("neutral").toString());

                    negative =  Integer.parseInt(((JSONObject) response).getString
                            ("negative").toString());
                    positive =  Integer.parseInt(((JSONObject) response).getString
                            ("positive").toString());

                    //Make sure tweet user is present
                    if(((JSONObject) response).has("1")) {
                        user1 =  ((JSONObject) response).getString
                                ("1").toString();
                        //And add to String
                        randomTweets2 += user1 + "\n";
                    }

                    //Make sure tweet is present
                    if(((JSONObject) response).has("11")) {
                        tweet1 =  ((JSONObject) response).getString
                                ("11").toString();
                        //And add to String
                        randomTweets2 += tweet1 +"\n\n";
                    }

                    //Make sure second tweet user is present
                    if(((JSONObject) response).has("2")) {
                        user2 =  ((JSONObject) response).getString
                                ("2").toString();
                        //And add to String
                        randomTweets2 += user2 +"\n";
                    }

                    //Make sure second tweet is present
                    if(((JSONObject) response).has("12")) {
                        tweet2 =  ((JSONObject) response).getString
                                ("12").toString();
                        //And add to String
                        randomTweets2 += tweet2 +"\n\n";
                    }

                    user3 =  ((JSONObject) response).getString
                            ("3").toString();
                    //And add to String
                    randomTweets2 += user3 +"\n";

                    tweet3 =  ((JSONObject) response).getString
                            ("13").toString();
                    //And add to String
                    randomTweets2 += tweet3;


                    //tweetView.setText(neutral+" "+positive + " "+ negative);
                    Log.d("log", "that shit worked1 " + neutral +" "+positive + " "+ negative + " NEW" + tweet1 +  " NEW" + tweet2 +  " MEW"+ tweet3);

                    yData = new float[]{ positive, neutral, negative};

                    tweetsView.setText(randomTweets2);
                    randomTweets2="";
                    //tweetsView.setText(user1 + "\n" + tweet1 + "\n\n" + user2 + "\n" + tweet2 + "\n\n" + user3 + "\n" + tweet3);
                    ArrayList<Entry> yVals1 = new ArrayList<Entry>();
                    for (int i = 0; i < yData.length; i++) {
                        yVals1.add(new Entry(yData[i], i));
                    }
                    PieDataSet dataSet = new PieDataSet(yVals1, "Opinion Results");
                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(5f);
                    ArrayList<String> xVals = new ArrayList<String>();

                    for (int i = 0; i < xData.length; i++)
                        xVals.add(xData[i]);
                    PieData data = new PieData(xVals, dataSet);
                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(5f);

                    // add a lot of colors

                    ArrayList<Integer> colors = new ArrayList<Integer>();

                    for (int c : ColorTemplate.COLORFUL_COLORS)
                        colors.add(c);

                    for (int c : ColorTemplate.JOYFUL_COLORS)
                        colors.add(c);

                    for (int c : ColorTemplate.COLORFUL_COLORS)
                        colors.add(c);

                    for (int c : ColorTemplate.LIBERTY_COLORS)
                        colors.add(c);

                    for (int c : ColorTemplate.PASTEL_COLORS)
                        colors.add(c);

                    colors.add(ColorTemplate.getHoloBlue());

                    dataSet.setColors(colors);


                    data.setValueTextSize(20f);
                    data.setValueTextColor(Color.WHITE);
                    data.setValueTypeface(tf);
                    mChart.setData(data);

                    // undo all highlights
                    mChart.highlightValues(null);
                    mChart.setCenterText("What do people think about " + keyword + "?");

                    mChart.getLegend().setEnabled(false);
                    mChart.invalidate();



                    //Remove the progress dialog window when method is completely finished
                    ringProgressDialog.dismiss();
                    searchView.clearFocus();
                    // Hide the onscreen keyboard
                    //InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                   // imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //tweetView.setText(error.getMessage());

                //Hide progress dialog
                ringProgressDialog.dismiss();

                Toast.makeText(ResultsActivity.this, "No Results found. Please search another term, or check your internet connection.",
                        Toast.LENGTH_LONG).show();
                Log.d("log", "error");
                Log.d("log", "error");
            }
        });
        jsonRequest.setTag(REQUEST_TAG);
        int socketTimeout = 5000;//5 seconds - change to what you want
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

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }



}