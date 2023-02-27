package com.ioh.checkimei;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ioh.checkimei.API.APIPencapaian;
import com.ioh.checkimei.API.APIUserValidationCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentDSFPortal extends Fragment {

    Context context;
    ArrayList<String> pencapianQSC, pencapaianNewIMEI;

    //Componenet
    ProgressDialog progressDialog;
    SwipeRefreshLayout SwipeRefresh;
    RadioGroup radiogroupDateRange;
    RadioButton radioQ1, radioQ2, radioQ3, radioQ4;
    LinearLayout cardTitleQSC, cardTargetQSC;
    TextView
            month1DateQSC, month2DateQSC, month3DateQSC,
            month1TargetQSC, month2TargetQSC, month3TargetQSC,
            month1ActualQSC, month2ActualQSC, month3ActualQSC,
            month1AchQSC, month2AchQSC, month3AchQSC,
            month1GapQSC, month2GapQSC, month3GapQSC;
    TextView
            month1DateNewIMEI, month2DateNewIMEI, month3DateNewIMEI,
            month1TargetNewIMEI, month2TargetNewIMEI, month3TargetNewIMEI,
            month1ActualNewIMEI, month2ActualNewIMEI, month3ActualNewIMEI,
            month1AchNewIMEI, month2AchNewIMEI, month3AchNewIMEI,
            month1GapNewIMEI, month2GapNewIMEI, month3GapNewIMEI;

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
        super.onResume();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dsf_portal, container, false);
        context = getActivity();

        //SetUp ProgressDialog
        progressDialog = new ProgressDialog(context);
        setProgressDialog("Loading","Memeriksa IMEI");

        //Identify Component Id
        SwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.SwipeRefresh);
        //Identify Component Id
        SwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.SwipeRefresh);
        radiogroupDateRange = (RadioGroup)v.findViewById(R.id.radiogroupDateRange);
        radioQ1 = (RadioButton)v.findViewById(R.id.radioQ1);
        radioQ2 = (RadioButton)v.findViewById(R.id.radioQ2);
        radioQ3 = (RadioButton)v.findViewById(R.id.radioQ3);
        radioQ4 = (RadioButton)v.findViewById(R.id.radioQ4);
        radioQ1.setChecked(true);

        cardTitleQSC = (LinearLayout)v.findViewById(R.id.cardTitleQSC);
        cardTargetQSC = (LinearLayout)v.findViewById(R.id.cardTargetQSC);

        month1DateQSC = (TextView)v.findViewById(R.id.month1DateQSC);
        month2DateQSC = (TextView)v.findViewById(R.id.month2DateQSC);
        month3DateQSC = (TextView)v.findViewById(R.id.month3DateQSC);

        month1TargetQSC = (TextView)v.findViewById(R.id.month1TargetQSC);
        month2TargetQSC = (TextView)v.findViewById(R.id.month2TargetQSC);
        month3TargetQSC = (TextView)v.findViewById(R.id.month3TargetQSC);

        month1ActualQSC = (TextView)v.findViewById(R.id.month1ActualQSC);
        month2ActualQSC = (TextView)v.findViewById(R.id.month2ActualQSC);
        month3ActualQSC = (TextView)v.findViewById(R.id.month3ActualQSC);

        month1AchQSC = (TextView)v.findViewById(R.id.month1AchQSC);
        month2AchQSC = (TextView)v.findViewById(R.id.month2AchQSC);
        month3AchQSC = (TextView)v.findViewById(R.id.month3AchQSC);

        month1GapQSC = (TextView)v.findViewById(R.id.month1GapQSC);
        month2GapQSC = (TextView)v.findViewById(R.id.month2GapQSC);
        month3GapQSC = (TextView)v.findViewById(R.id.month3GapQSC);

        month1DateNewIMEI = (TextView)v.findViewById(R.id.month1DateNewIMEI);
        month2DateNewIMEI = (TextView)v.findViewById(R.id.month2DateNewIMEI);
        month3DateNewIMEI = (TextView)v.findViewById(R.id.month3DateNewIMEI);

        month1TargetNewIMEI = (TextView)v.findViewById(R.id.month1TargetNewIMEI);
        month2TargetNewIMEI = (TextView)v.findViewById(R.id.month2TargetNewIMEI);
        month3TargetNewIMEI = (TextView)v.findViewById(R.id.month3TargetNewIMEI);

        month1ActualNewIMEI = (TextView)v.findViewById(R.id.month1ActualNewIMEI);
        month2ActualNewIMEI = (TextView)v.findViewById(R.id.month2ActualNewIMEI);
        month3ActualNewIMEI = (TextView)v.findViewById(R.id.month3ActualNewIMEI);

        month1AchNewIMEI = (TextView)v.findViewById(R.id.month1AchNewIMEI);
        month2AchNewIMEI = (TextView)v.findViewById(R.id.month2AchNewIMEI);
        month3AchNewIMEI = (TextView)v.findViewById(R.id.month3AchNewIMEI);

        month1GapNewIMEI = (TextView)v.findViewById(R.id.month1GapNewIMEI);
        month2GapNewIMEI = (TextView)v.findViewById(R.id.month2GapNewIMEI);
        month3GapNewIMEI = (TextView)v.findViewById(R.id.month3GapNewIMEI);

        pencapianQSC = new ArrayList<>();
        pencapaianNewIMEI = new ArrayList<>();

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

        return v;
    }

    private void DSFAchievement(){
        progressDialog.show();
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                pencapianQSC.clear();
                pencapaianNewIMEI.clear();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean status = jsonResponse.getBoolean("status");
                    if(status) {
                        Integer count = jsonResponse.getInt("count_dsf_ach");
                        for (int i = 1; i <= count; i++) {
                            String month = jsonResponse.getString("month" + i);
                            String date = jsonResponse.getString("date" + i);

                            String target_qsc = jsonResponse.getString("target_qsc" + i);
                            String actual_qsc = jsonResponse.getString("actual_qsc" + i);
                            String ach_qsc = jsonResponse.getString("ach_qsc" + i);
                            String gap_qsc = jsonResponse.getString("gap_qsc" + i);

                            String target_new_imei = jsonResponse.getString("target_new_imei" + i);
                            String actual_new_imei = jsonResponse.getString("actual_new_imei" + i);
                            String ach_new_imei = jsonResponse.getString("ach_new_imei" + i);
                            String gap_new_imei = jsonResponse.getString("gap_new_imei" + i);

                            pencapianQSC.add(month+"|"+date+"|"+target_qsc+"|"+actual_qsc+"|"+ach_qsc+"|"+gap_qsc);
                            pencapaianNewIMEI.add(month+"|"+date+"|"+target_new_imei+"|"+actual_new_imei+"|"+ach_new_imei+"|"+gap_new_imei);

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
        APIPencapaian apiPencapaian = new APIPencapaian(context, listener, errorListener);
        requestQueue.add(apiPencapaian);
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