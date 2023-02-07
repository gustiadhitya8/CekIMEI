package com.ioh.checkimei.API;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ioh.checkimei.R;

import java.util.HashMap;
import java.util.Map;

public class APIPencapaian extends StringRequest {

    private SharedPreferences sharedPreferences;
    private Map<String, String> params;

    public APIPencapaian(Context context,
                         Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, context.getResources().getString(R.string.url_riwayat_cek), listener, errorListener);
        params = new HashMap<>();

        String device = "Manf. : " + Build.MANUFACTURER + " | Model : " + Build.MODEL + " | OS : " + Build.VERSION.RELEASE;
        String version = context.getResources().getString(R.string.app_ver);
        sharedPreferences = context.getSharedPreferences("UserData", MODE_PRIVATE);
        String encrypt_username = sharedPreferences.getString("encrypt_username", "");

        params.put("username", encrypt_username);
        params.put("device", device);
        params.put("version", version);
    }
    public Map<String, String> getParams(){
        return params;
    }
}
