package com.ioh.cekimei;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ioh.cekimei.API.APIGetTokenMyRetail;
import com.ioh.cekimei.API.APILogin;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    CardView cardView2;
    EditText editUsername, editPIN, editNewPIN;
    TextView textForgotPassword;
    Button btnLogin;
    boolean state_change_pin = false;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    RequestQueue requestQueue;
    Response.Listener<String> listener;
    Response.ErrorListener errorListener;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        requestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        setProgressDialog("Login","Validasi Akun");

        cardView2 = (CardView)findViewById(R.id.cardView2);
        editUsername = (EditText) findViewById(R.id.editUsername);
        editPIN = (EditText) findViewById(R.id.editPIN);
        editNewPIN = (EditText) findViewById(R.id.editNewPIN);
        textForgotPassword = (TextView) findViewById(R.id.textForgotPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state_change_pin){
                    cardView2.setVisibility(View.GONE);
                    state_change_pin = false;
                    editPIN.setHint("PIN");
                    btnLogin.setText("MASUK");
                    textForgotPassword.setText("Lupa PIN?");
                    textForgotPassword.setVisibility(View.VISIBLE);
                }else {
                    cardView2.setVisibility(View.VISIBLE);
                    state_change_pin = true;
                    editPIN.setHint("PIN Standar");
                    btnLogin.setText("UBAH PIN & MASUK");
                    textForgotPassword.setText("Batal Ubah PIN");
                    textForgotPassword.setVisibility(View.VISIBLE);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state_change_pin){
                    LoginToApps(editUsername.getText().toString(), editPIN.getText().toString(), editNewPIN.getText().toString());
                    progressDialog.show();
                }else{
                    LoginToApps(editUsername.getText().toString(), editPIN.getText().toString(), "0");
                    progressDialog.show();
                }
            }
        });

        String access_token = sharedPreferences.getString("access_token", "");
        if (!access_token.equals("")){
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish();
        }

        editor.putString("imei_result","");
        editor.commit();
    }

    private void LoginToApps(String username, String password, String new_password){
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    String error_message = jsonResponse.getString("error_message");
                    if(success){
                        boolean need_to_change = jsonResponse.getBoolean("need_to_change");
                        if (need_to_change){
                            progressDialog.cancel();
                            state_change_pin = true;
                            cardView2.setVisibility(View.VISIBLE);
                            btnLogin.setText("UBAH KE PIN BARU & MASUK");
                            textForgotPassword.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"PIN Standar terdeteksi, mohon masukan PIN baru Anda",Toast.LENGTH_LONG).show();

                            btnLogin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(editNewPIN.getText().toString().equals("")){
                                        Toast.makeText(Login.this, "PIN Baru Kosong. Mohon dapat mengubah PIN terlebih dahulu.", Toast.LENGTH_LONG).show();
                                    }else if(editNewPIN.getText().length() < 6){
                                        Toast.makeText(Login.this, "PIN Baru kurang dari 6 digit", Toast.LENGTH_LONG).show();
                                    }else{
                                        LoginToApps(editUsername.getText().toString(), editPIN.getText().toString(), editNewPIN.getText().toString());
                                        progressDialog.show();
                                    }
                                }
                            });
                        }else{
                            progressDialog.cancel();
                            String data_username = jsonResponse.getString("data_username");
                            String data_name = jsonResponse.getString("data_name");
                            String data_title = jsonResponse.getString("data_title");
                            String data_region = jsonResponse.getString("data_region");
                            String data_area = jsonResponse.getString("data_area");
                            String data_sales_area = jsonResponse.getString("data_sales_area");
                            String data_cluster = jsonResponse.getString("data_cluster");
                            String data_micro_cluster = jsonResponse.getString("data_micro_cluster");
                            String data_fitur_site_info = jsonResponse.getString("data_fitur_site_info");

                            String encrypt_username = jsonResponse.getString("encrypt_username");
                            String encrypt_password = jsonResponse.getString("encrypt_password");
                            String token = jsonResponse.getString("token");

                            String username = jsonResponse.getString("username");
                            String password = jsonResponse.getString("password");
                            String client_id = jsonResponse.getString("client_id");
                            String client_secret = jsonResponse.getString("client_secret");
                            String grant_type = jsonResponse.getString("grant_type");

                            editor.putString("encrypt_username",encrypt_username);
                            editor.putString("encrypt_password",encrypt_password);
                            editor.putString("token",token);

                            editor.putString("data_username",data_username);
                            editor.putString("data_name",data_name);
                            editor.putString("data_title",data_title);
                            editor.putString("data_region",data_region);
                            editor.putString("data_area",data_area);
                            editor.putString("data_sales_area",data_sales_area);
                            editor.putString("data_cluster",data_cluster);
                            editor.putString("data_micro_cluster",data_micro_cluster);
                            editor.putString("data_fitur_site_info",data_fitur_site_info);

                            //Toast.makeText(getApplicationContext(), outlet_id, Toast.LENGTH_SHORT).show();

                            editor.putString("username",username);
                            editor.putString("password",password);
                            editor.putString("client_id",client_id);
                            editor.putString("client_secret",client_secret);
                            editor.putString("grant_type",grant_type);
                            editor.commit();

                            getOAuthMyRetail(grant_type, client_id, client_secret, username, password);
                            progressDialog.show();
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),error_message,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, ""+error, Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        };
        APILogin apiLogin = new APILogin(getApplicationContext(), username, password, new_password, listener, errorListener);
        requestQueue.add(apiLogin);
    }

    private void getOAuthMyRetail(String grant_type, String client_id, String client_secret,
                                  String username, String password){
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String token_type = jsonResponse.getString("token_type");
                    String expires_in = jsonResponse.getString("expires_in");
                    String access_token = jsonResponse.getString("access_token");
                    String refresh_token = jsonResponse.getString("refresh_token");

                    editor.putString("token_type",token_type);
                    editor.putString("expires_in",expires_in);
                    editor.putString("access_token",access_token);
                    editor.putString("refresh_token",refresh_token);
                    editor.commit();

                    progressDialog.cancel();
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, ""+error, Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        };

        APIGetTokenMyRetail apiGetTokenMyRetail = new APIGetTokenMyRetail(getApplicationContext(),
                grant_type, client_id, client_secret, username, password, listener, errorListener);
        requestQueue.add(apiGetTokenMyRetail);
        //Toast.makeText(getApplicationContext(),""+requestQueue.toString(),Toast.LENGTH_SHORT).show();
    }

    private void setProgressDialog(String title, String message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
    }
}