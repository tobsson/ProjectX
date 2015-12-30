package com.example.andrius.findatweet;

/**
 * Created by andrius on 2015-12-17.
 */


        import com.android.volley.AuthFailureError;
        import com.android.volley.NetworkResponse;
        import com.android.volley.Request;
        import com.android.volley.Response;
        import com.android.volley.RetryPolicy;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;
        import com.android.volley.toolbox.StringRequest;

        import org.json.JSONObject;

        import java.util.HashMap;
        import java.util.Map;

        import android.content.Context;
        import android.util.Log;

        import com.android.volley.Cache;
        import com.android.volley.Network;
        import com.android.volley.RequestQueue;
        import com.android.volley.toolbox.BasicNetwork;
        import com.android.volley.toolbox.DiskBasedCache;
        import com.android.volley.toolbox.HurlStack;

/**
 * Custom implementation of Volley Request Queue
 */
public class CustomVolleyRequestQueue {

    private static CustomVolleyRequestQueue mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    private CustomVolleyRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized CustomVolleyRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CustomVolleyRequestQueue(context);
            Log.d("log", "instance received");
        }
        Log.d("log", "instance returned");
        return mInstance;

    }

    public RequestQueue getRequestQueue() {
        Log.d("log", "getting request queue");
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 5 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
            Log.d("log", "request queue started");
        }
        return mRequestQueue;
    }
}