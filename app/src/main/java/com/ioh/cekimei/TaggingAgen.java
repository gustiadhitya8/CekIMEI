package com.ioh.cekimei;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ioh.cekimei.API.APICheckIMEI;
import com.ioh.cekimei.API.APITaggingAgen;
import com.ioh.cekimei.API.APIUserValidationCheck;

import org.json.JSONException;
import org.json.JSONObject;

public class TaggingAgen extends AppCompatActivity {

    EditText editNamaAgent, editMSISDNAgen, editEmailAgen;
    Button btnSubmitAgen, btnRiwayatAgen;
    ImageView btnBack;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    RequestQueue requestQueue;
    Response.Listener<String> listener;
    Response.ErrorListener errorListener;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagging_agen);

        progressDialog = new ProgressDialog(this);
        setProgressDialog("Loading","Mengirim Data");

        editNamaAgent = (EditText)findViewById(R.id.editNamaAgent);
        editMSISDNAgen = (EditText)findViewById(R.id.editMSISDNAgen);
        editEmailAgen = (EditText)findViewById(R.id.editEmailAgen);

        btnSubmitAgen = (Button) findViewById(R.id.btnSubmitAgen);
        btnRiwayatAgen = (Button) findViewById(R.id.btnRiwayatAgen);

        btnRiwayatAgen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TaggingAgenRiwayat.class);
                startActivity(intent);
            }
        });

        btnBack = (ImageView) findViewById(R.id.btnBack);

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        UserValidationCheck();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSubmitAgen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String access_token = sharedPreferences.getString("access_token", "");
                String dsf_username = sharedPreferences.getString("data_username","");
                String agen_msisdn = editMSISDNAgen.getText().toString();
                String agen_nama = editNamaAgent.getText().toString();
                String agen_email = editEmailAgen.getText().toString();

                if (agen_msisdn.isEmpty() || agen_nama.isEmpty() || agen_email.isEmpty()){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(TaggingAgen.this);
                    builder1.setTitle("Error");
                    builder1.setMessage("Mohon lengkapi formulir");
                    builder1.setPositiveButton("Ok",null);
                    builder1.show();
                }else {
                    setTaggingAgen(dsf_username, agen_msisdn, agen_nama, agen_email);
                    progressDialog.show();
                }
            }
        });

    }


    private void setTaggingAgen(String dsf_username, String agen_msisdn, String agen_nama, String agen_email){
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean sukses = jsonResponse.getBoolean("sukses");
                    if(sukses){
                        Toast.makeText(getApplicationContext(),"Submit Sukses",Toast.LENGTH_LONG).show();
                    }else{
                        String error_message = jsonResponse.getString("error_message");
                        Toast.makeText(getApplicationContext(),error_message,Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.cancel();
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
                Toast.makeText(TaggingAgen.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        };
        APITaggingAgen check_Imei = new APITaggingAgen(this,
                dsf_username, agen_msisdn, agen_nama, agen_email, listener, errorListener);
        requestQueue.add(check_Imei);
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