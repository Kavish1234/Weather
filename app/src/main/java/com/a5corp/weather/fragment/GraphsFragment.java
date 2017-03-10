package com.a5corp.weather.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.a5corp.weather.R;
import com.a5corp.weather.internet.FetchWeather;
import com.a5corp.weather.preferences.Preferences;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class GraphsFragment extends Fragment {

    View rootView;
    Handler handler;
    LineChart chart;
    List<Entry> temperatures;
    LineDataSet dataSet;
    List<Entry> entries = new ArrayList<Entry>(10);
    FetchWeather fw;
    Preferences pf;

    public GraphsFragment() {
        // Required empty public constructor
        handler = new Handler();
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fw = new FetchWeather(getContext());
        pf = new Preferences(getContext());
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_graphs, container, false);
        Log.i("Loaded" , "Fragment");
        chart = (LineChart) rootView.findViewById(R.id.chart);
        temperatures = new ArrayList<>();
        function();
        return rootView;
    }

    public void function() {
        getTemperatures();
        loadChart();
    }

    public void getTemperatures() {
        Bundle bundle = this.getArguments();
        JSONObject str;
        JSONArray list;
        if (bundle != null) {
            try {
                str = new JSONObject(bundle.getString("json", null));
                list = str.getJSONArray("list");
                for (int i = 0; i < 10; ++i) {
                    long day = list.getJSONObject(i).getLong("dt");
                    long temp = list.getJSONObject(i).getJSONObject("temp").getLong("day");
                    entries.add(new Entry(day , temp));
                    Log.i("Added" , "Entry");
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
                Log.i("Caught" , "JSON Ex");
            }
        }
        else
            Log.e("Null" , "Bundle");
    }

    public void loadChart() {
        dataSet = new LineDataSet(entries, "Temperature"); // add entries to dataset
        dataSet.setColor(Color.parseColor("#FF0000"));
        dataSet.setValueTextColor(Color.parseColor("#FFFFFF"));
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        Description desc = new Description();
        desc.setText("Temperature, " + getString(R.string.c));
        chart.setDescription(desc);
        chart.setBackgroundColor(Color.parseColor("#FFFFFF"));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.enableAxisLineDashedLine(2f , 1f , 2f);

        chart.invalidate();
    }
}
