package com.ioh.checkimei;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.ioh.checkimei.API.APICheckIMEI;
import com.ioh.checkimei.API.APIUserValidationCheck;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentCekImei extends Fragment {

    SwipeRefreshLayout SwipeRefresh;
    String data_username, data_name, data_region, data_area, data_cluster, data_micro_cluster;
    String token_type, expires_in, access_token, refresh_token;
    CodeScanner mCodeScanner;
    ImageView btnScanIMEI, btnLogout;
    CardView cardResult;
    Button btnCheckIMEI;
    EditText editIMEI;
    TextView textModel, textIMEI, textMSISDN, textStatus;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Context context;
    RequestQueue requestQueue;
    Response.Listener<String> listener;
    Response.ErrorListener errorListener;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cek_imei, container, false);
        context = getActivity();

        //SetUp ProgressDialog
        progressDialog = new ProgressDialog(context);
        setProgressDialog("Loading","Memeriksa IMEI");

        //Identify Component Id
        SwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.SwipeRefresh);
        editIMEI = (EditText)v.findViewById(R.id.editIMEI);
        btnCheckIMEI = (Button) v.findViewById(R.id.btnCheckIMEI);
        btnScanIMEI = (ImageView) v.findViewById(R.id.btnScanIMEI);
        btnLogout = (ImageView) v.findViewById(R.id.btnLogout);
        cardResult = (CardView)v.findViewById(R.id.cardResult);

        textModel = (TextView)v.findViewById(R.id.textModel);
        textIMEI = (TextView)v.findViewById(R.id.textIMEI);
        textMSISDN = (TextView)v.findViewById(R.id.textMSISDN);
        textStatus = (TextView)v.findViewById(R.id.textStatus);

        cardResult.setVisibility(View.GONE);

        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                SwipeRefresh.setRefreshing(false);
            }
        });

        btnScanIMEI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BarcodeScanner.class);
                startActivity(intent);
                editor.putString("imei_result","");
                editor.commit();
                editIMEI.setText("");
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(context);
                builder.setTitle("Logout")
                        .setMessage("Logout dari aplikasi?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                editor.clear();
                                editor.commit();
                                Intent intent = new Intent(context, Login.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("Tidak",null)
                        .create()
                        .show();
            }
        });

        requestQueue = Volley.newRequestQueue(context);
        sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        UserValidationCheck(false);
        data_username = sharedPreferences.getString("data_username", "");
        data_name = sharedPreferences.getString("data_name", "");
        data_region = sharedPreferences.getString("data_region", "");
        data_area = sharedPreferences.getString("data_area", "");
        data_cluster = sharedPreferences.getString("data_cluster", "");
        data_micro_cluster = sharedPreferences.getString("data_micro_cluster", "");

        token_type = sharedPreferences.getString("token_type", "");
        expires_in = sharedPreferences.getString("expires_in", "");
        access_token = sharedPreferences.getString("access_token", "");
        refresh_token = sharedPreferences.getString("refresh_token", "");

        String imei_result = sharedPreferences.getString("imei_result", "");
        editIMEI.setText(imei_result);

        btnCheckIMEI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editIMEI.getText().toString().matches("")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle("Error");
                    builder1.setMessage("Mohon masukan IMEI terlebih dahulu");
                    builder1.setPositiveButton("Ok",null);
                    builder1.show();

                }else {
                    checkIMEI(access_token, editIMEI.getText().toString());
                    progressDialog.show();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        String imei_result = sharedPreferences.getString("imei_result", "");
        editIMEI.setText(imei_result);
        UserValidationCheck(false);
    }

    private void checkIMEI(String auth, String check_imei){
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    String msisdn = jsonResponse.getString("msisdn");
                    String message = jsonResponse.getString("message");
                    if(!status.equals("ERROR")){
                        String imei = jsonResponse.getString("imei");
                        String model = jsonResponse.getString("model");
                        if(!imei.equals("") && !imei.equals(null)){
                            cardResult.setVisibility(View.VISIBLE);
                            textModel.setText(model);
                            textIMEI.setText(imei);
                            textMSISDN.setText(msisdn);
                            textStatus.setText(message);
                            editIMEI.setText("");
                        }
                    }
                    progressDialog.cancel();
                    APICheckIMEI my_api = new APICheckIMEI(context, auth, check_imei, "Success",status);
                    requestQueue.add(my_api);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                    APICheckIMEI my_api = new APICheckIMEI(context, auth, check_imei, e.getMessage(),"");
                    requestQueue.add(my_api);
                    progressDialog.cancel();
                }
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(context, ""+error, Toast.LENGTH_SHORT).show();
                APICheckIMEI my_api = new APICheckIMEI(context, auth, check_imei, error.getMessage(),"");
                requestQueue.add(my_api);
            }
        };
        APICheckIMEI check_Imei = new APICheckIMEI(context,
                auth, check_imei, listener, errorListener);
        requestQueue.add(check_Imei);
        UserValidationCheck(false);
    }

    private void setProgressDialog(String title, String message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
    }

    private void UserValidationCheck(Boolean show){
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean status = jsonResponse.getBoolean("status");
                    if(status){
                        if (show) Toast.makeText(context, "Hi "+data_name, Toast.LENGTH_SHORT).show();
                    }else{
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