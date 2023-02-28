package com.ioh.cekimei.API;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ioh.cekimei.R;

import java.util.HashMap;
import java.util.Map;

public class APIGetTokenMyRetail extends StringRequest {

    private SharedPreferences sharedPreferences;
    private Map<String, String> params;

    public APIGetTokenMyRetail(Context context,
                               String grant_type, String client_id, String client_secret,
                               String username, String password,
                               Response.Listener<String> listener,
                               Response.ErrorListener errorListener) {
        super(Method.POST, context.getResources().getString(R.string.url_token_myretail), listener, errorListener);
        params = new HashMap<>();

        params.put("grant_type", grant_type);
        params.put("client_id", client_id);
        params.put("client_secret", client_secret);
        params.put("username", username);
        params.put("password", password);

    }
    public Map<String, String> getParams(){
        return params;
    }
}
