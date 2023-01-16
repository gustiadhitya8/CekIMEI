package com.ioh.checkimei.API;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ioh.checkimei.Extra.SHA1Encrypt;
import com.ioh.checkimei.R;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class APILogin extends StringRequest {

    private SharedPreferences sharedPreferences;
    private Map<String, String> params;

    public APILogin(Context context, String username, String password, String new_password,
                    Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, context.getResources().getString(R.string.url_loginl), listener, errorListener);
        params = new HashMap<>();

        params.put("new_password", new_password);
        String device = "Manf. : " + Build.MANUFACTURER + " | Model : " + Build.MODEL + " | OS : " + Build.VERSION.RELEASE;
        String version = context.getResources().getString(R.string.app_ver);
        params.put("device", device);
        params.put("version", version);

        SHA1Encrypt encrypt = new SHA1Encrypt();
        try {
            params.put("username", encrypt.SHA1(username.toUpperCase()));
            params.put("password", encrypt.SHA1(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public Map<String, String> getParams(){
        return params;
    }
}
