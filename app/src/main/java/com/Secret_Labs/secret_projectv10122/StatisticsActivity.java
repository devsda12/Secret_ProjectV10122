package com.Secret_Labs.secret_projectv10122;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    BarChart barChart;
    float Value1;
    float Value2;
    float Value3;
    float Value4;
    float Value5;
    float Value6;
    float Value7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        barChart = (BarChart) findViewById(R.id.Barchart01);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(Value1, 0));
        barEntries.add(new BarEntry(Value2, 1));
        barEntries.add(new BarEntry(Value3, 2));
        barEntries.add(new BarEntry(Value4, 3));
        barEntries.add(new BarEntry(Value5, 4));
        barEntries.add(new BarEntry(Value6, 5));
        barEntries.add(new BarEntry(Value7, 6));

        BarDataSet set = new BarDataSet(barEntries, "Logins");
        BarData data = new BarData(set);
        data.setBarWidth(0.9f);
        barChart.setData(data);
        barChart.invalidate();
    }

    public void requestData(){
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", 0);
        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            Log.d("ServiceError", "The api connection is false so could not send do anything");
            return;
        }
        //Making request
        JSONObject tempTokenUpdateObj = new JSONObject();
        try {
            tempTokenUpdateObj.put("device_Id", mainPrefs.getString("device_Id", "0"));
            tempTokenUpdateObj.put("acc_Id", mainPrefs.getString("activeAccId", "none"));
        } catch (JSONException e) {
            Log.d("ServiceError", "Json error occured on packing");
            return;
        }

        JsonObjectRequest tempTokenUpdateRequest = new JsonObjectRequest(Request.Method.POST, "http://54.36.98.223:5000/sapp_requestStats", tempTokenUpdateObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Data staat in response
                //respons.getString/boolean/int
                try {
                    Value1 = BigDecimal.valueOf(response.getDouble("logins")).floatValue();
                } catch (JSONException e) {
                    Log.d("ServiceError", "Json error occured on packing");
                }
                //zet in variabele voor grafiek

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ServiceError", "The server returned an error on the statistics update");
            }
        });

        Volley.newRequestQueue(this).add(tempTokenUpdateRequest);
    }
}
