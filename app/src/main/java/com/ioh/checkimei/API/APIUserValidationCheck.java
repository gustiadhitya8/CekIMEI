package com.ioh.checkimei.API;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ioh.checkimei.Extra.SHA1Encrypt;
import com.ioh.checkimei.R;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class APIUserValidationCheck extends StringRequest {

    private SharedPreferences sharedPreferences;
    private Map<String, String> params;

    public APIUserValidationCheck(Context context, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, context.getResources().getString(R.string.url_user_validation), listener, errorListener);
        params = new HashMap<>();

        sharedPreferences = context.getSharedPreferences("UserData", MODE_PRIVATE);
        String encrypt_username = sharedPreferences.getString("encrypt_username", "");
        String encrypt_password = sharedPreferences.getString("encrypt_password", "");
        String token = sharedPreferences.getString("token", "");
        params.put("encrypt_username", encrypt_username);
        params.put("encrypt_password", encrypt_password);
        params.put("token", token);

        //Toast.makeText(context, token, Toast.LENGTH_SHORT).show();

    }
    public Map<String, String> getParams(){
        return params;
    }
}
