package com.ioh.checkimei;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.ioh.checkimei.API.APICheckIMEI;
import com.ioh.checkimei.API.APIRiwayatCek;
import com.ioh.checkimei.API.APIUserValidationCheck;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentRiwayatCek extends Fragment {

    Context context;
    String data_date_filter = "Hari Ini";

    //Componenet
    ProgressDialog progressDialog;
    SwipeRefreshLayout SwipeRefresh;
    RadioGroup radiogroupDateRange;
    RadioButton radioHariIni, radioKemarin, radioMingguIni, radioBulanIni;

    //Listener for API
    RequestQueue requestQueue;
    Response.Listener<String> listener;
    Response.ErrorListener errorListener;

    //Cache
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onResume() {
        UserValidationCheck();
        RiwayatCekImei(data_date_filter);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_riwayat_cek, container, false);
        context = getActivity();

        //SetUp ProgressDialog
        progressDialog = new ProgressDialog(context);
        setProgressDialog("Loading","Memeriksa IMEI");

        //Identify Component Id
        SwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.SwipeRefresh);
        radiogroupDateRange = (RadioGroup)v.findViewById(R.id.radiogroupDateRange);
        radioHariIni = (RadioButton)v.findViewById(R.id.radioHariIni);
        radioKemarin = (RadioButton)v.findViewById(R.id.radioKemarin);
        radioMingguIni = (RadioButton)v.findViewById(R.id.radioMingguIni);
        radioBulanIni = (RadioButton)v.findViewById(R.id.radioBulanIni);
        radioHariIni.setChecked(true);

        //Set Up Volley to Call API
        requestQueue = Volley.newRequestQueue(context);

        //Set Up Cache
        sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //On Swipe Refresh Activity
        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                SwipeRefresh.setRefreshing(false);
            }
        });

        radiogroupDateRange.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.radioHariIni:
                        data_date_filter = "Hari Ini";
                        RiwayatCekImei(data_date_filter);
                        break;
                    case R.id.radioKemarin:
                        data_date_filter = "Kemarin";
                        RiwayatCekImei(data_date_filter);
                        break;
                    case R.id.radioMingguIni:
                        data_date_filter = "Minggu Ini";
                        RiwayatCekImei(data_date_filter);
                        break;
                    case R.id.radioBulanIni:
                        data_date_filter = "Bulan Ini";
                        RiwayatCekImei(data_date_filter);
                        break;
                }
            }
        });

        return v;
    }

    private void RiwayatCekImei(String date_filter){
        progressDialog.show();
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean status = jsonResponse.getBoolean("status");
                    if(status) {
                        Integer count = jsonResponse.getInt("count_history");
                        for (int i = 1; i <= count; i++) {
                            String date = jsonResponse.getString("data_date" + i);
                            String time = jsonResponse.getString("data_time" + i);
                            String imei = jsonResponse.getString("data_imei" + i);
                            String device_model = jsonResponse.getString("data_device_model" + i);
                            String msisdn = jsonResponse.getString("data_msisdn" + i);
                            String result = jsonResponse.getString("data_result" + i);

                            //Put your array to save this data
                            //Put IMEI as key to search feature
                            //Put your recycler view with this data

                        }
                        progressDialog.cancel();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(context, ""+error, Toast.LENGTH_SHORT).show();
            }
        };
        APIRiwayatCek apiRiwayatCek = new APIRiwayatCek(context, date_filter, listener, errorListener);
        requestQueue.add(apiRiwayatCek);
        UserValidationCheck();
    }

    private void setProgressDialog(String title, String message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
    }

    private void UserValidationCheck(){
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean status = jsonResponse.getBoolean("status");
                    if(!status){
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(context, Login.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(context, ""+error, Toast.LENGTH_SHORT).show();
            }
        };
        APIUserValidationCheck apiUserValidationCheck = new APIUserValidationCheck(context, listener, errorListener);
        requestQueue.add(apiUserValidationCheck);
    }

}