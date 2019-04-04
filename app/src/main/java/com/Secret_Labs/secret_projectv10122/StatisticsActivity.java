package com.Secret_Labs.secret_projectv10122;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    BarChart barChartLoginsCurrent;
    BarChart barChartMessagesCurrent;
    BarChart barChartLoginsOld;
    BarChart barChartMessagesOld;
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

    float Logins1Old = 0;
    float Logins2Old = 0;
    float Logins3Old = 0;
    float Logins4Old = 0;
    float Logins5Old = 0;
    float Logins6Old = 0;
    float Logins7Old = 0;

    float messages1Old = 0;
    float messages2Old = 0;
    float messages3Old = 0;
    float messages4Old = 0;
    float messages5Old = 0;
    float messages6Old = 0;
    float messages7Old = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //Setting the custom toolbar for the activity
        Toolbar newConvSelToolbar = (Toolbar) findViewById(R.id.statisticsToolbar);
        newConvSelToolbar.setTitle(R.string.toolbar_title_statistics);
        setSupportActionBar(newConvSelToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        barChartLoginsCurrent = (BarChart) findViewById(R.id.BarchartLoginsCurrent);
        barChartMessagesCurrent = (BarChart) findViewById(R.id.BarchartMessagesCurrent);
        barChartLoginsOld = (BarChart) findViewById(R.id.BarchartLoginsOld);
        barChartMessagesOld = (BarChart) findViewById(R.id.BarchartMessagesOld);
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

        final ArrayList<String> xLabel = new ArrayList<>();
        xLabel.add("Monday");
        xLabel.add("Tuesday");
        xLabel.add("Wednesday");
        xLabel.add("Thursday");
        xLabel.add("Friday");
        xLabel.add("Saturday");
        xLabel.add("Sunday");

        XAxis xAxis = barChartLoginsCurrent.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int)value);
            }
        });

        YAxis yAxisLeft = barChartLoginsCurrent.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxisRight = barChartLoginsCurrent.getAxis(YAxis.AxisDependency.RIGHT);
        yAxisLeft.setAxisMinValue(0);
        yAxisRight.setAxisMinValue(0);

        BarDataSet set = new BarDataSet(barEntries, "Logins");
        set.setColor(getResources().getColor(R.color.colorPrimary));
        BarData data = new BarData(set);
        data.setBarWidth(0.9f);

        barChartLoginsCurrent.setData(data);
        barChartLoginsCurrent.setScaleEnabled(false);
        barChartLoginsCurrent.getDescription().setEnabled(false);
        barChartLoginsCurrent.invalidate();


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

        XAxis xAxis2 = barChartMessagesCurrent.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setDrawGridLines(false);
        xAxis2.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int)value);
            }
        });

        YAxis yAxisLeft2 = barChartMessagesCurrent.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxisRight2 = barChartMessagesCurrent.getAxis(YAxis.AxisDependency.RIGHT);
        yAxisLeft2.setAxisMinValue(0);
        yAxisRight2.setAxisMinValue(0);

        BarDataSet set2 = new BarDataSet(barEntries2, "Messages");
        set2.setColor(getResources().getColor(R.color.colorPrimary));
        BarData data2 = new BarData(set2);
        data2.setBarWidth(0.9f);

        barChartMessagesCurrent.setData(data2);
        barChartMessagesCurrent.setScaleEnabled(false);
        barChartMessagesCurrent.getDescription().setEnabled(false);
        barChartMessagesCurrent.invalidate();


        // old messages graph
        ArrayList<BarEntry> barEntries4 = new ArrayList<>();
        barEntries4.add(new BarEntry(0, messages1Old));
        barEntries4.add(new BarEntry(1, messages2Old));
        barEntries4.add(new BarEntry(2, messages3Old));
        barEntries4.add(new BarEntry(3, messages4Old));
        barEntries4.add(new BarEntry(4, messages5Old));
        barEntries4.add(new BarEntry(5, messages6Old));
        barEntries4.add(new BarEntry(6, messages7Old));

        Log.d("StatisticsNotice", "Now bar entries array filled");

        XAxis xAxis4 = barChartMessagesOld.getXAxis();
        xAxis4.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis4.setDrawGridLines(false);
        xAxis4.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int)value);
            }
        });

        YAxis yAxisLeft4 = barChartMessagesOld.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxisRight4 = barChartMessagesOld.getAxis(YAxis.AxisDependency.RIGHT);
        yAxisLeft4.setAxisMinValue(0);
        yAxisRight4.setAxisMinValue(0);

        BarDataSet set4 = new BarDataSet(barEntries4, "Messages");
        set4.setColor(getResources().getColor(R.color.colorPrimaryDark));
        BarData data4 = new BarData(set4);
        data4.setBarWidth(0.9f);

        barChartMessagesOld.setData(data4);
        barChartMessagesOld.setScaleEnabled(false);
        barChartMessagesOld.getDescription().setEnabled(false);
        barChartMessagesOld.invalidate();


        // old logins graph
        ArrayList<BarEntry> barEntries3 = new ArrayList<>();
        barEntries3.add(new BarEntry(0, Logins1Old));
        barEntries3.add(new BarEntry(1, Logins2Old));
        barEntries3.add(new BarEntry(2, Logins3Old));
        barEntries3.add(new BarEntry(3, Logins4Old));
        barEntries3.add(new BarEntry(4, Logins5Old));
        barEntries3.add(new BarEntry(5, Logins6Old));
        barEntries3.add(new BarEntry(6, Logins7Old));

        Log.d("StatisticsNotice", "Now bar entries array filled");

        XAxis xAxis3 = barChartLoginsOld.getXAxis();
        xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis3.setDrawGridLines(false);
        xAxis3.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                return xLabel.get((int)value);
            }
        });

        YAxis yAxisLeft3 = barChartLoginsOld.getAxis(YAxis.AxisDependency.LEFT);
        YAxis yAxisRight3 = barChartLoginsOld.getAxis(YAxis.AxisDependency.RIGHT);
        yAxisLeft3.setAxisMinValue(0);
        yAxisRight3.setAxisMinValue(0);

        BarDataSet set3 = new BarDataSet(barEntries3, "Logins");
        set3.setColor(getResources().getColor(R.color.colorPrimaryDark));
        BarData data3 = new BarData(set3);
        data3.setBarWidth(0.9f);

        barChartLoginsOld.setData(data3);
        barChartLoginsOld.setScaleEnabled(false);
        barChartLoginsOld.getDescription().setEnabled(false);
        barChartLoginsOld.invalidate();

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

                    JSONObject MondayOld = response.getJSONObject(7);
                    JSONObject TuesdayOld = response.getJSONObject(8);
                    JSONObject WednesdayOld = response.getJSONObject(9);
                    JSONObject ThursdayOld = response.getJSONObject(10);
                    JSONObject FridayOld = response.getJSONObject(11);
                    JSONObject SaturdayOld = response.getJSONObject(12);
                    JSONObject SundayOld = response.getJSONObject(13);

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

                    Logins1Old = BigDecimal.valueOf(Integer.parseInt(MondayOld.getString("logins"))).floatValue();
                    Logins2Old = BigDecimal.valueOf(Integer.parseInt(TuesdayOld.getString("logins"))).floatValue();
                    Logins3Old = BigDecimal.valueOf(Integer.parseInt(WednesdayOld.getString("logins"))).floatValue();
                    Logins4Old = BigDecimal.valueOf(Integer.parseInt(ThursdayOld.getString("logins"))).floatValue();
                    Logins5Old = BigDecimal.valueOf(Integer.parseInt(FridayOld.getString("logins"))).floatValue();
                    Logins6Old = BigDecimal.valueOf(Integer.parseInt(SaturdayOld.getString("logins"))).floatValue();
                    Logins7Old = BigDecimal.valueOf(Integer.parseInt(SundayOld.getString("logins"))).floatValue();
                    Log.d("StatisticsNotice", "Value of tuesday logins: " + Float.toString(Logins2Old));

                    messages1Old = BigDecimal.valueOf(Integer.parseInt(MondayOld.getString("messages"))).floatValue();
                    messages2Old = BigDecimal.valueOf(Integer.parseInt(TuesdayOld.getString("messages"))).floatValue();
                    messages3Old = BigDecimal.valueOf(Integer.parseInt(WednesdayOld.getString("messages"))).floatValue();
                    messages4Old = BigDecimal.valueOf(Integer.parseInt(ThursdayOld.getString("messages"))).floatValue();
                    messages5Old = BigDecimal.valueOf(Integer.parseInt(FridayOld.getString("messages"))).floatValue();
                    messages6Old = BigDecimal.valueOf(Integer.parseInt(SaturdayOld.getString("messages"))).floatValue();
                    messages7Old = BigDecimal.valueOf(Integer.parseInt(SundayOld.getString("messages"))).floatValue();
                    Log.d("StatisticsNotice", "Value of tuesday messages: " + Float.toString(messages2Old));

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

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
