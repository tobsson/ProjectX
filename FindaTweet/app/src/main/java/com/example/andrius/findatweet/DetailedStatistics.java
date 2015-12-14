package com.example.andrius.findatweet;

/**
 * Created by Christosgialias on 2015-12-04.
 */
import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

public class DetailedStatistics extends FragmentActivity {

    private BarChart mChart;
    //private SeekBar mSeekBarX, mSeekBarY;
    //private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);

        //tvX = (TextView) findViewById(R.id.tvXMax);
        //tvY = (TextView) findViewById(R.id.tvYMax);

        //mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        //mSeekBarX.setOnSeekBarChangeListener(this);

        //mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
        //mSeekBarY.setOnSeekBarChangeListener(this);

        mChart = (BarChart) findViewById(R.id.chart1);
        //mChart.setOnChartValueSelectedListener(this);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(100);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(false);

        // change the position of the y-labels
        YAxis yLabels = mChart.getAxisLeft();
        //yLabels.setValueFormatter(new MyYAxisValueFormatter());
        mChart.getAxisRight().setEnabled(false);

        XAxis xLabels = mChart.getXAxis();
        xLabels.setPosition(XAxisPosition.TOP);

        // mChart.setDrawXLabels(false);
        // mChart.setDrawYLabels(false);

        // setting data
        //mSeekBarX.setProgress(12);
        //mSeekBarY.setProgress(100);

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.BELOW_CHART_RIGHT);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        // mChart.setDrawLegend(false);
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 1; i < 8; i++) {
            xVals.add("Day " + i);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < 7; i++) {
            float range = (100 - 0)+1;
            float val1 = (float) (int) (Math.random() * range) + 0;
            range = (range - val1 - 0);
            float val2 = (float) (int) (Math.random() * range) + 0;
            float val3 = range - val2;


            yVals1.add(new BarEntry(new float[] { val1, val2, val3 }, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Tweeter's Opinion");
        set1.setColors(getColors());
        set1.setStackLabels(new String[] { "Positive", "Neutral", "Negative" });

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        //data.setValueFormatter(new MyValueFormatter());

        mChart.setData(data);
        mChart.invalidate();
    }





    private int[] getColors() {

        int stacksize = 3;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < stacksize; i++) {
            colors[i] = ColorTemplate.VORDIPLOM_COLORS[i];
        }

        return colors;
    }
}


