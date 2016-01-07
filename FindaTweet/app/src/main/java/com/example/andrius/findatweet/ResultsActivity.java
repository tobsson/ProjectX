package com.example.andrius.findatweet;

/**
 * Created by andrius on 2015-12-17.
 */

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.EditText;
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

public class ResultsActivity extends FragmentActivity implements View.OnKeyListener {

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
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_piechart);

        tweetsView = (TextView)findViewById(R.id.tweetView);
        searchField = (EditText)findViewById(R.id.newSearchField);
        bundle = getIntent().getExtras();
        neutral = Integer.parseInt(bundle.getString("neutral"));
        positive = Integer.parseInt(bundle.getString("positive"));
        negative = Integer.parseInt(bundle.getString("negative"));
        tweet1 = bundle.getString("tweet1");
        tweet2 = bundle.getString("tweet2");
        tweet3 = bundle.getString("tweet3");
        user1 = bundle.getString("user1");
        user2 = bundle.getString("user2");
        user3 = bundle.getString("user3");
        keyword = bundle.getString("keyword");

        yData = new float[]{ positive, neutral, negative};
        //tweetsView = (TextView)findViewById(R.id.tweetView);
        tweetsView.setText(user1 + "\n" + tweet1 + "\n\n" + user2 + "\n" + tweet2 + "\n\n" +user3 + "\n" + tweet3);

        //tvX = (TextView) findViewById(R.id.tvXMax);
        //tvY = (TextView) findViewById(R.id.tvYMax);

        // mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        //mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);

        // mSeekBarY.setProgress(10);

        // mSeekBarX.setOnSeekBarChangeListener(this);
        // mSeekBarY.setOnSeekBarChangeListener(this);
        //chartBtn = (Button) findViewById(R.id.chartBtn);
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

        mChart.setCenterText("TreeTalk");

        setData(3, 100);

        mChart.animateY(2000, Easing.EasingOption.EaseOutBack);
        // distChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.PIECHART_CENTER);
        l.setXEntrySpace(15f);
        l.setYEntrySpace(10f);


        /*View.OnClickListener buttonListener = new View.OnClickListener() {
            boolean clicked = false;
            @Override
            public void onClick(View v) {
                Intent i = new Intent (ResultsActivity.this, DetailedStatistics.class);
                startActivity(i);
            }
        };
        chartBtn.setOnClickListener(buttonListener);*/

    }

    /*public void onButtonClick(View v) {
        //open SearchResultsActivity when search button is clicked
        if (v.getId() == R.id.timelineBtn) {
            Intent i = new Intent(ResultsActivity.this, DetailedStatistics.class);
            startActivity(i);
        }
    }*/





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


    public void onButtonClick(View v) {



        if (v.getId() == R.id.newSearchBtn) {

            mQueue = new RequestQueue(new DiskBasedCache(getApplicationContext().getCacheDir(), 10 * 1024 * 1024), new BasicNetwork(new HurlStack()));
            mQueue.start();
            final String keyword = searchField.getText().toString().replaceAll(" ", "_").toLowerCase();
            String url = "http://83.248.73.168:8080/findtweets?query="+keyword;
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
                        Log.d("log", "that shit worked1 " + neutral +" "+positive + " "+ negative + " NEW" + tweet1 +  " NEW" + tweet2 +  " MEW"+ tweet3);

                        yData = new float[]{ positive, neutral, negative};
                        tweetsView.setText(user1 + "\n" + tweet1 + "\n\n" + user2 + "\n" + tweet2 + "\n\n" + user3 + "\n" + tweet3);
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
                        mChart.setCenterText("TreeTalk");
                        mChart.getLegend().setEnabled(false);
                        mChart.invalidate();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //tweetView.setText(error.getMessage());
                    Toast.makeText(ResultsActivity.this, "No Results found. Please search another term.",
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
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }


    //
    // PieData data = distChart.getDataOriginal();
    //
    // if (data != null) {
    //
    // PieDataSet set = data.getDataSet();
    //
    // if (set != null) {
    //
    // Entry e = set.getEntryForXIndex(set.getEntryCount() - 1);
    //
    // data.removeEntry(e, 0);
    // // or remove by index
    // // mData.removeEntry(xIndex, dataSetIndex);
    //
    // distChart.notifyDataSetChanged();
    // distChart.invalidate();
    // }
    // }
    // }
    //
    // private void addEntry() {
    //
    // PieData data = distChart.getDataOriginal();
    //
    // if (data != null) {
    //
    // PieDataSet set = data.getDataSet();
    // // set.addEntry(...);
    //
    // data.addEntry(new Entry((float) (Math.random() * 25) + 20f,
    // set.getEntryCount()), 0);
    //
    // // let the chart know it's data has changed
    // distChart.notifyDataSetChanged();
    //
    // // redraw the chart
    // distChart.invalidate();
    // }
    // }
}