package com.heavyobj.notaku;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import info.debatty.java.stringsimilarity.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private static final String URL = "https://kuristina.herokuapp.com/anime/Kronosfear.json";
    private static final String DB = "http://s3.ap-south-1.amazonaws.com/notakuserver/anime.json";
    private JSONObject DBObject;
    private ArrayList<String> today_name;
    private ArrayList<String> today_ep;
    private ArrayList<String> today_time;
    private String epiair;

    private List<Card> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();
        today_name = new ArrayList<>();
        today_ep = new ArrayList<>();
        today_time = new ArrayList<>();
        loadRecyclerViewData();
    }

    private void loadRecyclerViewData()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading list...");
        progressDialog.show();
        StringRequest DBRequest = new StringRequest(Request.Method.GET, DB, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    DBObject = new JSONObject(response);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }
                });

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                try
                {
                    if(progressDialog != null) {
                        if(progressDialog.isShowing()) { //check if dialog is showing.

                            //get the Context object that was used to great the dialog
                            Context context = ((ContextWrapper)progressDialog.getContext()).getBaseContext();

                            //if the Context used here was an activity AND it hasn't been finished or destroyed
                            //then dismiss it
                            if(context instanceof Activity) {
                                if(!((Activity)context).isFinishing() && !((Activity)context).isDestroyed())
                                    progressDialog.dismiss();
                            } else //if the Context used wasnt an Activity, then dismiss it too
                                progressDialog.dismiss();
                        }
                    }

                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject jsonArray = jsonObject.getJSONObject("myanimelist");
                    JSONArray anime = jsonArray.getJSONArray("anime");
                    JSONArray today_list = DBObject.getJSONArray("anime");
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
                    DateTimeZone dz = DateTimeZone.forID("Asia/Tokyo");
                    String tzid = dz.getShortName(DateTimeUtils.currentTimeMillis());
                    Log.d("tag", tzid);
                    for (int i = 0; i < today_list.length(); i++)
                    {
                        formatter = formatter.withZone(dz);
                        JSONObject x = today_list.getJSONObject(i);
                        today_name.add(x.getString("show_title"));
                        today_ep.add(x.getString("ep_no"));
                        DateTime jptime = new DateTime (formatter.parseDateTime(x.getString("show_time")));
                        Log.d("tag", formatter.print(jptime));
                        formatter = formatter.withZone(DateTimeZone.getDefault());
                        Log.d("tag", formatter.getZone().toString());
                        Log.d("tag", x.getString("show_title") + formatter.print(jptime));

                    }
                    for (int i = 0; i < anime.length(); i++)
                    {
                        JSONObject o = anime.getJSONObject(i);
                        epiair = "Not airing today";
                        QGram dig = new QGram(2);
                        if (Integer.parseInt(o.getString("my_status")) == 1 && Integer.parseInt(o.getString("series_status")) == 1)
                        {
                            for (int j = 0; j < today_name.size(); j++)
                            {
                                Float sim = (float) dig.distance(today_name.get(j), o.getString("series_title"));
                                Log.d("tag", o.getString("series_title") + ' ' + today_name.get(j) + ' ' + sim.toString());
                                if (sim < 20.0)
                                    epiair = "Airing today at " + today_time.get(j);
                            }
                            Log.d("tag", o.getString("series_title") + ' ' + epiair);
                            Card listItem = new Card(o.getString("series_title"), epiair);
                            listItems.add(listItem);
                        }
                    }
                    adapter = new Adapter(listItems, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(DBRequest);
        requestQueue.add(stringRequest);
    }
}
