package com.ioh.cekimei;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ioh.cekimei.API.APIRiwayatCek;
import com.ioh.cekimei.API.APITaggingAgenReport;
import com.ioh.cekimei.API.APIUserValidationCheck;
import com.ioh.cekimei.RecyclerClass.AdapterData;
import com.ioh.cekimei.RecyclerClass.AdapterDataTaggingAgen;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaggingAgenRiwayat extends AppCompatActivity {

    String data_date_filter = "Hari Ini";

    //GlobalVariable
    ArrayList listData, filtered_listData;
    LinearLayoutManager linearLayoutManager;
    AdapterDataTaggingAgen adapterData;

    //Componenet
    ProgressDialog progressDialog;
    RadioGroup radiogroupDateRange;
    RadioButton radioHariIni, radioKemarin, radioMingguIni, radioBulanIni;
    EditText editMSISDNAgen;
    RecyclerView recyclerHistory;
    ImageView btnBack;
    SwipeRefreshLayout SwipeRefresh;

    //Listener for API
    RequestQueue requestQueue;
    Response.Listener<String> listener;
    Response.ErrorListener errorListener;

    //Cache
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AlertDialog.Builder builder;

    @Override
    protected void onResume() {
        UserValidationCheck();
        RiwayatTaggingAgen(data_date_filter);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagging_agen_riwayat);

        //SetUp ProgressDialog
        progressDialog = new ProgressDialog(this);
        setProgressDialog("Loading","Memeriksa IMEI");

        //Identify Component Id
        SwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.SwipeRefresh);
        radiogroupDateRange = (RadioGroup)findViewById(R.id.radiogroupDateRange);
        radioHariIni = (RadioButton)findViewById(R.id.radioHariIni);
        radioKemarin = (RadioButton)findViewById(R.id.radioKemarin);
        radioMingguIni = (RadioButton)findViewById(R.id.radioMingguIni);
        radioBulanIni = (RadioButton)findViewById(R.id.radioBulanIni);
        radioHariIni.setChecked(true);

        btnBack = (ImageView) findViewById(R.id.btnBack);
        editMSISDNAgen = (EditText)findViewById(R.id.editMSISDNAgen);
        recyclerHistory = (RecyclerView)findViewById(R.id.recylerHistory);

        //Set Up Volley to Call API
        requestQueue = Volley.newRequestQueue(this);

        //Set Up Cache
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        radiogroupDateRange.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.radioHariIni:
                        data_date_filter = "Hari Ini";
                        RiwayatTaggingAgen(data_date_filter);
                        break;
                    case R.id.radioKemarin:
                        data_date_filter = "Kemarin";
                        RiwayatTaggingAgen(data_date_filter);
                        break;
                    case R.id.radioMingguIni:
                        data_date_filter = "Minggu Ini";
                        RiwayatTaggingAgen(data_date_filter);
                        break;
                    case R.id.radioBulanIni:
                        data_date_filter = "Bulan Ini";
                        RiwayatTaggingAgen(data_date_filter);
                        break;
                }
            }
        });

        SwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                SwipeRefresh.setRefreshing(false);
            }
        });
    }

    private void setProgressDialog(String title, String message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
    }

    private void RiwayatTaggingAgen(String date_filter){
        listData = new ArrayList<>();
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
                            String agen_msisdn = jsonResponse.getString("agen_msisdn" + i);
                            String agen_nama = jsonResponse.getString("agen_nama" + i);
                            String agen_email = jsonResponse.getString("agen_email" + i);
                            String status_agen = jsonResponse.getString("status_agen" + i);
                            String register = jsonResponse.getString("register" + i);
                            String result = jsonResponse.getString("result" + i);
                            listData.add(agen_msisdn+"~"+agen_nama+"~"+agen_email+"~"+status_agen+"~"+register+"~"+result);
                        }

                        linearLayoutManager = new LinearLayoutManager(TaggingAgenRiwayat.this,
                                LinearLayoutManager.VERTICAL, false);
                        adapterData = new AdapterDataTaggingAgen(TaggingAgenRiwayat.this, listData);
                        recyclerHistory.setLayoutManager(linearLayoutManager);
                        recyclerHistory.setAdapter(adapterData);
                        adapterData.notifyDataSetChanged();

                        editMSISDNAgen.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                            @Override
                            public void afterTextChanged(Editable editable) {
                                filter_list(editable.toString());
                            }
                        });

                        progressDialog.cancel();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(TaggingAgenRiwayat.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(TaggingAgenRiwayat.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        };
        APITaggingAgenReport apiRiwayatCek = new APITaggingAgenReport(this, date_filter, listener, errorListener);
        requestQueue.add(apiRiwayatCek);
        UserValidationCheck();
    }

    private void filter_list(String text) {
        filtered_listData = new ArrayList<>();
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(0).toString().toLowerCase().contains(text.toLowerCase())) {
                filtered_listData.add(listData.get(0));
            }
        }
        adapterData.filterList(filtered_listData);
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
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(getApplicationContext(), ""+error, Toast.LENGTH_SHORT).show();
            }
        };
        APIUserValidationCheck apiUserValidationCheck = new APIUserValidationCheck(this, listener, errorListener);
        requestQueue.add(apiUserValidationCheck);
    }

}