package com.Secret_Labs.secret_projectv10122;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    BarChart barChart;
    float Value1 = 0;
    float Value2 = 0;
    float Value3 = 0;
    float Value4 = 0;
    float Value5 = 0;
    float Value6 = 0;
    float Value7 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        requestData();

        barChart = (BarChart) findViewById(R.id.Barchart01);
    }

    public void onRequestBindData(){
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
        final JSONArray tempTokenUpdateArr = new JSONArray();
        try {
            tempTokenUpdateObj.put("device_Id", mainPrefs.getString("device_Id", "0"));
            tempTokenUpdateObj.put("acc_Id", mainPrefs.getString("activeAccId", "none"));
            tempTokenUpdateArr.put(tempTokenUpdateObj);
        } catch (JSONException e) {
            Log.d("ServiceError", "Json error occured on packing");
            return;
        }

        JsonArrayRequest tempTokenUpdateRequest = new JsonArrayRequest(Request.Method.POST, "http://54.36.98.223:5000/sapp_requestStats", tempTokenUpdateArr, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //Data staat in response
                //respons.getString/boolean/int
                try {
                    JSONObject Monday = response.getJSONObject(0);
                    JSONObject Tuesday = response.getJSONObject(1);
                    JSONObject Wednesday = response.getJSONObject(2);
                    JSONObject Thursday = response.getJSONObject(3);
                    JSONObject Friday = response.getJSONObject(4);
                    JSONObject Saturday = response.getJSONObject(5);
                    JSONObject Sunday = response.getJSONObject(6);

                    Value1 = BigDecimal.valueOf(Monday.getDouble("logins")).floatValue();
                    Value2 = BigDecimal.valueOf(Tuesday.getDouble("logins")).floatValue();
                    Value3 = BigDecimal.valueOf(Wednesday.getDouble("logins")).floatValue();
                    Value4 = BigDecimal.valueOf(Thursday.getDouble("logins")).floatValue();
                    Value5 = BigDecimal.valueOf(Friday.getDouble("logins")).floatValue();
                    Value6 = BigDecimal.valueOf(Saturday.getDouble("logins")).floatValue();
                    Value7 = BigDecimal.valueOf(Sunday.getDouble("logins")).floatValue();
                    onRequestBindData();

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
