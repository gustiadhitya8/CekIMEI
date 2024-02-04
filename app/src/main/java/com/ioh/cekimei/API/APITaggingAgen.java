package com.ioh.cekimei.API;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ioh.cekimei.R;

import java.util.HashMap;
import java.util.Map;

public class APITaggingAgen extends StringRequest {

    private SharedPreferences sharedPreferences;
    private Map<String, String> params;

    public APITaggingAgen(Context context, String dsf_username, String agen_msisdn, String agen_nama, String agen_email,
            Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, context.getResources().getString(R.string.url_tagging_agen), listener, errorListener);
        params = new HashMap<>();

        String device = "Manf. : " + Build.MANUFACTURER + " | Model : " + Build.MODEL + " | OS : " + Build.VERSION.RELEASE;
        String version = context.getResources().getString(R.string.app_ver);
        sharedPreferences = context.getSharedPreferences("UserData", MODE_PRIVATE);
        String encrypt_username = sharedPreferences.getString("encrypt_username", "");

        params.put("encrypt_username", encrypt_username);
        params.put("dsf_username", dsf_username);
        params.put("device", device);
        params.put("version", version);
        params.put("activity", "Submit Tagging Agen");

        params.put("agen_msisdn", agen_msisdn);
        params.put("agen_nama", agen_nama);
        params.put("agen_email", agen_email);
    }
    public Map<String, String> getParams(){
        return params;
    }
}
