package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetailsActivity extends AppCompatActivity {

    @BindView(R.id.stock_details_chart)
    LineChart chartView;
    @BindView(R.id.stock_details_name)
    TextView textName;
    @BindView(R.id.stock_details_exchange)
    TextView textExchange;
    @BindView(R.id.stock_details_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_activity_stock_details);
        }

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

                String stockSymbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
                String history = cursor.getString(Contract.Quote.POSITION_HISTORY);
                String stockName = cursor.getString(Contract.Quote.POSITION_NAME);
                String stockExchange = cursor.getString(Contract.Quote.POSITION_STOCK_EXCHANGE);

                cursor.close();

                // Setup Actionbar Title
                if (actionBar != null) {
                    actionBar.setTitle(stockSymbol);
                }

                // Setup other text
                textName.setText(stockName);
                textExchange.setText(String.format(getString(R.string.stock_exchange), stockExchange));

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
        final HistoryData historyData = new HistoryData(historyString);

        LineDataSet dataSet = new LineDataSet(historyData.getChartEntries(), "Stock Value");

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return historyData.getLabelForValue(value);
            }
        };

        XAxis xAxis = chartView.getXAxis();
        xAxis.setValueFormatter(formatter);
        xAxis.setGranularity(1f);

        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(true);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setColor(Color.WHITE);
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.WHITE);

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
        Toast.makeText(this, R.string.stock_details_empty_message, Toast.LENGTH_SHORT).show();
        finish();
    }

    class HistoryData {

        Map<Float, String> labelMap;
        Map<Float, Float> valueMap;

        HistoryData(String historyString) {

            labelMap = new TreeMap<>();
            valueMap = new LinkedHashMap<>();

            SimpleDateFormat format = new SimpleDateFormat("yyyyMM", Locale.getDefault());
            SimpleDateFormat labelFormat = new SimpleDateFormat("MMM, yy", Locale.getDefault());

            Map<String, List<Float>> data = new TreeMap<>();
            String[] historyData = historyString.split("\n");

            for (String s : historyData) {
                String[] dat = s.split(",");
                Date millis = new Date(Long.parseLong(dat[0]));
                String key = format.format(millis);
                if (!data.containsKey(key))
                    data.put(key, new ArrayList<Float>());

                float stockVal = Float.parseFloat(dat[1]);
                data.get(key).add(stockVal);
            }

            int i = 0;
            for (String key : data.keySet()) {
                List<Float> floats = data.get(key);
                float avg = 0;
                for (Float f : floats) {
                    avg += f;
                }
                if (floats.size() != 0)
                    avg /= floats.size();

                try {
                    Date date = format.parse(key);
                    String monthLabel = labelFormat.format(date);
                    labelMap.put((float) i, monthLabel);
                    valueMap.put((float) i, avg);
                    i++;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

        List<Entry> getChartEntries() {
            List<Entry> entries = new ArrayList<>();
            for (Float key : valueMap.keySet()) {
                entries.add(new Entry(key, valueMap.get(key)));
            }

            return entries;
        }

        String getLabelForValue(float xValue) {
            if (labelMap.containsKey(xValue))
                return labelMap.get(xValue);
            return "";
        }
    }
}
