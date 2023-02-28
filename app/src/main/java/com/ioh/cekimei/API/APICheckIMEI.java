package com.ioh.cekimei.API;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ioh.cekimei.R;

import java.util.HashMap;
import java.util.Map;

public class APICheckIMEI extends StringRequest {

    private SharedPreferences sharedPreferences;
    private Map<String, String> params_body,params_header;

    public APICheckIMEI(Context context, String auth, String imei,
                   Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, context.getResources().getString(R.string.url_api_check_imei), listener, errorListener);
        params_body = new HashMap<>();
        params_header = new HashMap<>();

        sharedPreferences = context.getSharedPreferences("UserData", MODE_PRIVATE);
        String data_username = sharedPreferences.getString("data_username", "");
        String data_name = sharedPreferences.getString("data_name", "");
        String data_region = sharedPreferences.getString("data_region", "");
        String data_area = sharedPreferences.getString("data_area", "");
        String data_cluster = sharedPreferences.getString("data_cluster", "");
        String data_micro_cluster = sharedPreferences.getString("data_micro_cluster", "");

        String encrypt_username = sharedPreferences.getString("encrypt_username", "");
        String encrypt_password = sharedPreferences.getString("encrypt_password", "");

        params_header.put("Authorization","Bearer "+auth);
        params_body.put("imei", imei);

        //Toast.makeText(context, outlet_id, Toast.LENGTH_SHORT).show();

        params_body.put("username", encrypt_username);
        params_body.put("password", encrypt_password);

        params_body.put("data_username", data_username);
        params_body.put("data_name", data_name);
        params_body.put("data_region", data_region);
        params_body.put("data_area", data_area);
        params_body.put("data_cluster", data_cluster);
        params_body.put("data_micro_cluster", data_micro_cluster);

        String device = "Manf. : " + Build.MANUFACTURER + " | Model : " + Build.MODEL + " | OS : " + Build.VERSION.RELEASE;
        String version = context.getResources().getString(R.string.app_ver);
        params_body.put("device", device);
        params_body.put("version", version);
        params_body.put("activity", "Cek IMEI (Ask MyRetail)");
    }

    public APICheckIMEI(Context context, String auth, String imei, String status, String result,
                        String device_model, String msisdn) {
        super(Method.POST, context.getResources().getString(R.string.url_check_imei_log), null, null);
        params_body = new HashMap<>();
        params_header = new HashMap<>();

        String device = "Manf. : " + Build.MANUFACTURER + " | Model : " + Build.MODEL + " | OS : " + Build.VERSION.RELEASE;
        String version = context.getResources().getString(R.string.app_ver);
        params_body.put("device", device);
        params_body.put("version", version);
        params_body.put("activity", "Cek IMEI (Record Log)");

        sharedPreferences = context.getSharedPreferences("UserData", MODE_PRIVATE);
        String data_username = sharedPreferences.getString("data_username", "");
        String data_name = sharedPreferences.getString("data_name", "");
        String data_region = sharedPreferences.getString("data_region", "");
        String data_area = sharedPreferences.getString("data_area", "");
        String data_cluster = sharedPreferences.getString("data_cluster", "");
        String data_micro_cluster = sharedPreferences.getString("data_micro_cluster", "");

        String encrypt_username = sharedPreferences.getString("encrypt_username", "");
        String encrypt_password = sharedPreferences.getString("encrypt_password", "");

        //Toast.makeText(context, outlet_id, Toast.LENGTH_SHORT).show();

        params_body.put("username", encrypt_username);
        params_body.put("password", encrypt_password);

        params_body.put("authorization","Bearer "+auth);
        params_body.put("imei", imei);
        params_body.put("status", status);
        params_body.put("result", result);
        params_body.put("data_username", data_username);
        params_body.put("data_name", data_name);
        params_body.put("data_region", data_region);
        params_body.put("data_area", data_area);
        params_body.put("data_cluster", data_cluster);
        params_body.put("data_micro_cluster", data_micro_cluster);

        params_body.put("data_device_model", device_model);
        params_body.put("data_msisdn", msisdn);
    }

    public Map<String, String> getParams(){
        return params_body;
    }
    public Map<String, String> getHeader(){
        return params_body;
    }

}
