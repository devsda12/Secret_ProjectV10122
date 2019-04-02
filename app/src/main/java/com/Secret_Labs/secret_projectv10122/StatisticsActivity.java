package com.Secret_Labs.secret_projectv10122;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
    BarChart barChart2;
    float Logins1 = 0;
    float Logins2 = 0;
    float Logins3 = 0;
    float Logins4 = 0;
    float Logins5 = 0;
    float Logins6 = 0;
    float Logins7 = 0;

    float messages1 = 0;
    float messages2 = 0;
    float messages3 = 0;
    float messages4 = 0;
    float messages5 = 0;
    float messages6 = 0;
    float messages7 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        barChart = (BarChart) findViewById(R.id.Barchart01);
        barChart2 = (BarChart) findViewById(R.id.Barchart02);
        requestData();
    }

    public void onRequestBindData(){
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, Logins1));
        barEntries.add(new BarEntry(1, Logins2));
        barEntries.add(new BarEntry(2, Logins3));
        barEntries.add(new BarEntry(3, Logins4));
        barEntries.add(new BarEntry(4, Logins5));
        barEntries.add(new BarEntry(5, Logins6));
        barEntries.add(new BarEntry(6, Logins7));

        Log.d("StatisticsNotice", "Now bar entries array filled");

        BarDataSet set = new BarDataSet(barEntries, "Logins");
        BarData data = new BarData(set);
        data.setBarWidth(0.9f);
        barChart.setData(data);
        barChart.invalidate();


        //messages graph
        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        barEntries2.add(new BarEntry(0, messages1));
        barEntries2.add(new BarEntry(1, messages2));
        barEntries2.add(new BarEntry(2, messages3));
        barEntries2.add(new BarEntry(3, messages4));
        barEntries2.add(new BarEntry(4, messages5));
        barEntries2.add(new BarEntry(5, messages6));
        barEntries2.add(new BarEntry(6, messages7));

        Log.d("StatisticsNotice", "Now bar entries array filled");

        BarDataSet set2 = new BarDataSet(barEntries2, "messages");
        BarData data2 = new BarData(set2);
        data.setBarWidth(0.9f);
        barChart2.setData(data2);
        barChart2.invalidate();

    }

    public void requestData(){
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", 0);
        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            Log.d("StatisticsError", "The api connection is false so could not send do anything");
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
            Log.d("StatisticsError", "Json error occured on packing");
            Log.d("StatisticsError", e.toString());
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

                    Logins1 = BigDecimal.valueOf(Integer.parseInt(Monday.getString("logins"))).floatValue();
                    Logins2 = BigDecimal.valueOf(Integer.parseInt(Tuesday.getString("logins"))).floatValue();
                    Logins3 = BigDecimal.valueOf(Integer.parseInt(Wednesday.getString("logins"))).floatValue();
                    Logins4 = BigDecimal.valueOf(Integer.parseInt(Thursday.getString("logins"))).floatValue();
                    Logins5 = BigDecimal.valueOf(Integer.parseInt(Friday.getString("logins"))).floatValue();
                    Logins6 = BigDecimal.valueOf(Integer.parseInt(Saturday.getString("logins"))).floatValue();
                    Logins7 = BigDecimal.valueOf(Integer.parseInt(Sunday.getString("logins"))).floatValue();
                    Log.d("StatisticsNotice", "Value of tuesday logins: " + Float.toString(Logins2));

                    messages1 = BigDecimal.valueOf(Integer.parseInt(Monday.getString("messages"))).floatValue();
                    messages2 = BigDecimal.valueOf(Integer.parseInt(Tuesday.getString("messages"))).floatValue();
                    messages3 = BigDecimal.valueOf(Integer.parseInt(Wednesday.getString("messages"))).floatValue();
                    messages4 = BigDecimal.valueOf(Integer.parseInt(Thursday.getString("messages"))).floatValue();
                    messages5 = BigDecimal.valueOf(Integer.parseInt(Friday.getString("messages"))).floatValue();
                    messages6 = BigDecimal.valueOf(Integer.parseInt(Saturday.getString("messages"))).floatValue();
                    messages7 = BigDecimal.valueOf(Integer.parseInt(Sunday.getString("messages"))).floatValue();
                    Log.d("StatisticsNotice", "Value of tuesday messages: " + Float.toString(messages2));

                    onRequestBindData();

                } catch (JSONException e) {
                    Log.d("StatisticsError", "Json error occured on packing");
                    Log.d("StatisticsError", e.toString());
                }
                //zet in variabele voor grafiek

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("StatisticsError", "The server returned an error on the statistics update");
            }
        });

        Volley.newRequestQueue(this).add(tempTokenUpdateRequest);
    }
}
