package com.heavyobj.notaku;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RetrieveDB extends IntentService
{
    private ArrayList<String> today_name;
    private ArrayList<String> today_ep;
    private ArrayList<String> today_time;
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.heavyobj.notaku.action.FOO";
    private static final String ACTION_BAZ = "com.heavyobj.notaku.action.BAZ";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.heavyobj.notaku.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.heavyobj.notaku.extra.PARAM2";
    private static final String DB = "http://s3.ap-south-1.amazonaws.com/notakuserver/anime.json";
    private JSONObject DBObject;

    public RetrieveDB()
    {
        super("RetrieveDB");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2)
    {
        Intent intent = new Intent(context, RetrieveDB.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2)
    {
        Intent intent = new Intent(context, RetrieveDB.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        final TinyDB tinydb = new TinyDB(this);
        today_name = new ArrayList<>();
        today_ep = new ArrayList<>();
        today_time = new ArrayList<>();
        StringRequest DBRequest = new StringRequest(Request.Method.GET, DB, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    DBObject = new JSONObject(response);
                    JSONArray today_list = DBObject.getJSONArray("anime");
                    DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
                    DateTimeZone dz = DateTimeZone.forID("Asia/Tokyo");
                    for (int i = 0; i < today_list.length(); i++)
                    {
                        formatter = formatter.withZone(dz);
                        JSONObject x = today_list.getJSONObject(i);
                        today_name.add(x.getString("show_title"));
                        today_ep.add(x.getString("ep_no"));
                        DateTime jptime = new DateTime (formatter.parseDateTime(x.getString("show_time")));
                        formatter = formatter.withZone(DateTimeZone.getDefault());
                        today_time.add(formatter.print(jptime));


                    }
                    tinydb.putListString("ShowTitles", today_name);
                    tinydb.putListString("ShowTimes", today_time);
                    tinydb.putListString("ShowEpisode", today_ep);

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

    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2)
    {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2)
    {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
