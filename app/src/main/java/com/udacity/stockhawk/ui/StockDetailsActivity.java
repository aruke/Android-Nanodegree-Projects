package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetailsActivity extends AppCompatActivity {

    @BindView(R.id.stock_details_chart)
    LineChart chartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);

        Uri data = getIntent().getData();
        if (data != null) {

            Cursor cursor = getContentResolver().query(
                    data,
                    Contract.Quote.QUOTE_COLUMNS,
                    null,
                    null,
                    null);

            if (cursor != null) {
                cursor.moveToFirst();

                String stockName = cursor.getString(Contract.Quote.POSITION_SYMBOL);
                String history = cursor.getString(Contract.Quote.POSITION_HISTORY);

                cursor.close();

                // Setup Actionbar Title
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(stockName);
                }

                // Setup graph chart
                setupChartView(history);

            } else {
                // Cursor null
                fallback();
            }
        } else {
            // No date found, so fallback
            fallback();
        }
    }

    private void setupChartView(String historyString) {
        HistoryData historyData = new HistoryData(historyString);

        LineDataSet dataSet = new LineDataSet(historyData.getChartEntries(), "Stock Value");

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            SimpleDateFormat format = new SimpleDateFormat("dd, mmm yy", Locale.getDefault());

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return format.format(new Date((long) value));
            }
        };

        XAxis xAxis = chartView.getXAxis();
        xAxis.setValueFormatter(formatter);
        xAxis.setGranularity(1000f);

        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(true);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setColor(Color.WHITE);

        // View setup
        chartView.setData(new LineData(dataSet));
        chartView.setScaleYEnabled(true);
        chartView.setNoDataTextColor(Color.WHITE);

        // no description text
        chartView.getDescription().setEnabled(false);
        // enable touch gestures
        chartView.setTouchEnabled(true);
        chartView.setDragDecelerationFrictionCoef(0.9f);
        // enable scaling and dragging
        chartView.setDragEnabled(true);
        chartView.setScaleEnabled(true);
        chartView.setDrawGridBackground(false);
        chartView.setHighlightPerDragEnabled(false);

        chartView.setViewPortOffsets(0f, 0f, 0f, 0f);

        chartView.invalidate();
    }

    private void fallback() {
        // If error during activity then close the activity
        Toast.makeText(this, "Error : Can't display details", Toast.LENGTH_SHORT).show();
        finish();
    }

    class HistoryData {

        List<Item> history;

        HistoryData(String historyString) {
            history = new ArrayList<>();
            String[] historyData = historyString.split("\n");
            int i = 1;
            for (String s : historyData) {
                String[] dat = s.split(",");
                Date millis = new Date(Long.parseLong(dat[0]));
                float stockVal = Float.parseFloat(dat[1]);

                SimpleDateFormat format = new SimpleDateFormat("MMM", Locale.getDefault());

                Item item = new Item(format.format(millis), stockVal, i++);
                history.add(item);
            }
        }

        class Item {
            String label;
            float stock;
            int week;

            Item(String label, float stock, int week) {
                this.label = label;
                this.stock = stock;
                this.week = week;
            }
        }

        List<Entry> getChartEntries() {
            List<Entry> entries = new ArrayList<>();
            for (Item item : history) {
                entries.add(new Entry(item.week, item.stock));
            }
            return entries;
        }
    }
}
