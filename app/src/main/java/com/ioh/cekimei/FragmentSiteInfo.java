package com.ioh.cekimei;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ioh.cekimei.API.APIGetSiteInfo;
import com.ioh.cekimei.API.APIUserValidationCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentSiteInfo extends Fragment {

    Context context;
    ArrayList<String> pencapianQSC, pencapaianNewIMEI;

    //Global Variable
    String myLAC = "", myCI = "";
    String gmapShortUrl = "https://maps.google.com/?q=";

    //Componenet
    ProgressDialog progressDialog;
    SwipeRefreshLayout SwipeRefresh;
    TextView textLacCi, textSiteId, textSiteName,
            textMicroCluster, textCluster, textDSF1, textDSF2;
    Button btnOpenSiteAttachGmaps, btnTaggingAgenMOBO;

    //Listener for API
    RequestQueue requestQueue;
    Response.Listener<String> listener;
    Response.ErrorListener errorListener;

    //Cache
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AlertDialog.Builder builder;
    ImageView btnLogout;

    @Override
    public void onResume() {
        UserValidationCheck();
        textSiteId.setText("");
        textSiteName.setText("");
        textMicroCluster.setText("");
        textCluster.setText("");
        textDSF1.setText("");
        textDSF2.setText("");
        getPhoneLacCi();
        super.onResume();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_site_info, container, false);
        context = getActivity();

        //SetUp ProgressDialog
        progressDialog = new ProgressDialog(context);
        setProgressDialog("Loading","Memeriksa IMEI");

        //Identify Component Id
        SwipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.SwipeRefresh);
        textLacCi = (TextView) v.findViewById(R.id.textLacCi);
        textSiteId = (TextView) v.findViewById(R.id.textSiteId);
        textSiteName = (TextView) v.findViewById(R.id.textSiteName);
        textMicroCluster = (TextView) v.findViewById(R.id.textMicroCluster);
        textCluster = (TextView) v.findViewById(R.id.textCluster);
        textDSF1 = (TextView) v.findViewById(R.id.textDSF1);
        textDSF2 = (TextView) v.findViewById(R.id.textDSF2);
        btnOpenSiteAttachGmaps = (Button) v.findViewById(R.id.btnOpenSiteAttachGmaps);
        btnTaggingAgenMOBO = (Button) v.findViewById(R.id.btnTaggingAgenMOBO);

        //Setup ArrayList
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

        //Get Phone LacCi
        getPhoneLacCi();

        String data_title = sharedPreferences.getString("data_title","");
        Intent i = new Intent(getContext(), SiteMaps.class);
        btnOpenSiteAttachGmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data_title.equals("DSF")) {
                    startActivity(i);
                }else{
                    Toast.makeText(context, "Fitur Ini Hanya untuk DSF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogout = (ImageView) v.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Logout(context, editor, builder, getActivity());
            }
        });

        btnTaggingAgenMOBO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaggingAgen.class);
                startActivity(intent);
            }
        });

        return v;
    }

    private void getPhoneLacCi(){
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
            if (location != null) {
                myLAC =  String.valueOf(location.getLac());
                myCI = String.valueOf(location.getCid());
                textLacCi.setText(myLAC+" "+myCI);
                getSiteInformation();
            }
        }
    }

    private void getSiteInformation(){
        progressDialog.show();
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                pencapianQSC.clear();
                pencapaianNewIMEI.clear();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Boolean LacCI_success = jsonResponse.getBoolean("LacCI_success");
                    if(LacCI_success) {
                        Boolean SiteId_success = jsonResponse.getBoolean("SiteId_success");
                        if(SiteId_success){
                            String site_id = jsonResponse.getString("site_id");
                            String site_name = jsonResponse.getString("site_name");
                            textSiteId.setText(site_id);
                            textSiteName.setText(site_name);

                            Double longitude = jsonResponse.getDouble("longitude");
                            Double latitude = jsonResponse.getDouble("latitude");
                            /*
                            String url = gmapShortUrl+latitude+","+longitude;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            btnOpenSiteAttachGmaps.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(i);
                                }
                            });
                            */

                            String micro_cluster = jsonResponse.getString("micro_cluster");
                            String cluster = jsonResponse.getString("cluster");
                            textMicroCluster.setText(micro_cluster);
                            textCluster.setText(cluster);

                            String dsf_1 = jsonResponse.getString("dsf_1");
                            String dsf_2 = jsonResponse.getString("dsf_2");
                            textDSF1.setText(dsf_1);
                            textDSF2.setText(dsf_2);
                        }else{
                            Toast.makeText(context, "Tidak menemukan data Site", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, "Tidak menemukan data LAC CI", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.cancel();
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
        APIGetSiteInfo apiGetSiteInfo = new APIGetSiteInfo(context, myLAC, myCI, listener, errorListener);
        requestQueue.add(apiGetSiteInfo);
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