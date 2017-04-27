package com.heavyobj.notaku;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private static final String URL = "https://kuristina.herokuapp.com/anime/Kronosfear.json";
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

        loadRecyclerViewData();
    }

    private void loadRecyclerViewData()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading list...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                try
                {
                    progressDialog.dismiss();
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject jsonArray = jsonObject.getJSONObject("myanimelist");
                    JSONArray anime = jsonArray.getJSONArray("anime");
                    for (int i = 0; i < anime.length(); i++)
                    {
                        JSONObject o = anime.getJSONObject(i);
                        epiair = "Total no. of Episodes: " + o.getString("series_episodes");
                        Card listItem = new Card(o.getString("series_title"), epiair);
                        if (Integer.parseInt(o.getString("my_status")) == 1 && Integer.parseInt(o.getString("series_status")) == 1)
                            listItems.add(listItem);
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
        requestQueue.add(stringRequest);
    }
}
