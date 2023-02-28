package com.ioh.cekimei;

import static android.graphics.Bitmap.createBitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ioh.cekimei.API.APIGetSiteInfo;
import com.ioh.cekimei.API.APIGetSiteListData;
import com.ioh.cekimei.databinding.ActivitySiteMapsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SiteMaps extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnMyLocationButtonClickListener{

    private GoogleMap mMap;
    private ActivitySiteMapsBinding binding;

    private ImageButton SetMyArea, GetMySite;
    private AutoCompleteTextView mSearch;
    private String my_role = "";
    private String My_Area_Name = "", My_Area_Level = "DSF";
    private Location location;
    private String myCI = "", myLAC = "";
    private double myLongitude, myLatitude;
    private TextView MyTeksArea,MySiteName;
    private ImageView CloseMaps;

    private HashMap<String, Polyline> hashPolyLine = new HashMap<>();
    private HashMap<String, Double> hashLongitude = new HashMap<>();
    private HashMap<String, Double> hashLatitude = new HashMap<>();
    private HashMap<String, Marker> hashSite = new HashMap<>();
    private HashMap<String, String> hashSiteValue = new HashMap<>();
    private ArrayList<String> idSite = new ArrayList<>();

    private ArrayList<String> mSite = new ArrayList<>();
    private ArrayList<Double> mSiteLong = new ArrayList<>();
    private ArrayList<Double> mSiteLat = new ArrayList<>();

    private ArrayList<String> SearchMaterial = new ArrayList<>();
    private ArrayList<Double> LongitudeMaterial = new ArrayList<>();
    private ArrayList<Double> LatitudeMaterial = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private AlertDialog.Builder builder;
    private RequestQueue requestQueue;
    private Response.Listener<String> listener;
    private Response.ErrorListener errorListener;
    private ProgressDialog progressDialog;

    private void setProgressDialog(String title, String message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySiteMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        progressDialog = new ProgressDialog(this);
        setProgressDialog("Get Data From Server","Loading...");

        requestQueue = Volley.newRequestQueue(this);
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Cannot get the data!" + error.getMessage(),Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        };

        CloseMaps = (ImageView)findViewById(R.id.CloseMaps);
        mSearch = (AutoCompleteTextView)findViewById(R.id.mSearch);
        SetMyArea = (ImageButton) findViewById(R.id.SetMyArea);
        MyTeksArea = (TextView)findViewById(R.id.MyTeksArea);

        GetMySite = (ImageButton) findViewById(R.id.GetMySite);
        MySiteName = (TextView)findViewById(R.id.MySiteName);
        GetMySite.setEnabled(false);

        String data_username = sharedPreferences.getString("data_username","");
        MyTeksArea.setText(data_username);
        getLacCI();
        getMyLongLat();
        getSiteData(data_username);
        getNDBData(myLAC, myCI);

        GetMySite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLacCI();
                getMyLongLat();
                getNDBData(myLAC, myCI);
            }
        });

        CloseMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        mMap.setOnMyLocationButtonClickListener(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(mMap.MAP_TYPE_HYBRID);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                getMyLongLat();
                if(myLatitude != 0 && myLongitude != 0 &&
                        (Double)myLatitude != null && (Double)myLongitude != null){
                    LatLng mLatling = new LatLng(myLatitude, myLongitude);
                    float zoomLevel = 12.0f; //This goes up to 21
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatling, zoomLevel));
                }
            }
        }, 2000);
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return null;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        View v = setInfoWindowsContent(marker);
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                v.setBackgroundColor(Color.DKGRAY);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                v.setBackgroundColor(Color.WHITE);
                break;
        }
        return v;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        goto_mylocation();
        return true;
    }

    private void removeSiteMarker(){
        GetMySite.setEnabled(false);
        //MySiteName.setText("My Site");
        for(int a = 0; a < mSite.size(); a++){ SearchMaterial.remove(mSite.get(a)); }
        for(int a = 0; a < mSiteLong.size(); a++){LongitudeMaterial.remove(mSiteLong.get(a)); }
        for(int a = 0; a < mSiteLat.size(); a++){ LatitudeMaterial.remove(mSiteLat.get(a)); }

        for(int a = 0; a < idSite.size(); a++){
            Marker markerX = hashSite.get(idSite.get(a));
            markerX.remove();
            hashSite.remove(idSite.get(a));
        }
        hashSite.clear();
        hashSiteValue.clear();
        idSite.clear();
        mSite.clear();
        mSiteLong.clear();
        mSiteLat.clear();
        Polyline polylineX = hashPolyLine.get("MySiteLine");
        if(polylineX != null) {polylineX.remove();}
        getAutoText();
    }

    private void getSiteData(String dsf_username){
        removeSiteMarker();
        listener = getSiteListener();
        APIGetSiteListData mRequest = new APIGetSiteListData(this, dsf_username, listener, errorListener);
        int socketTimeout = 60000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        mRequest.setRetryPolicy(policy);
        requestQueue.add(mRequest);
        progressDialog.show();
    }

    private void getNDBData(String myLAC, String myCI){
        listener = getNDBListener();
        APIGetSiteInfo mRequest = new APIGetSiteInfo(this, myLAC, myCI, listener, errorListener);
        int socketTimeout = 60000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        mRequest.setRetryPolicy(policy);
        requestQueue.add(mRequest);
    }

    private Response.Listener<String> getSiteListener(){
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int count = jsonResponse.getInt("count");
                    for(int n = 1; n <= count; n++){
                        String site_id = jsonResponse.getString("site_id"+n);
                        String site_name = jsonResponse.getString("site_name"+n);

                        Double longitude = jsonResponse.getDouble("longitude"+n);
                        Double latitude = jsonResponse.getDouble("latitude"+n);

                        String dsf_1 = jsonResponse.getString("dsf_1"+n);
                        String dsf_2 = jsonResponse.getString("dsf_2"+n);

                        LatLng mLatling = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions0 = new MarkerOptions().title("Site").snippet(site_id).position(mLatling);
                        Bitmap icon = createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_site_check_imei));
                        markerOptions0.icon(BitmapDescriptorFactory.fromBitmap(setMarkerBitmapSize(icon)));
                        Marker marker = mMap.addMarker(markerOptions0);

                        GetMySite.setEnabled(true);
                        idSite.add(site_id);
                        mSite.add(site_name);
                        mSiteLong.add(longitude);
                        mSiteLat.add(latitude);

                        SearchMaterial.add(site_name);
                        LongitudeMaterial.add(longitude);
                        LatitudeMaterial.add(latitude);

                        hashSite.put(site_id, marker);
                        hashLongitude.put(marker.getId() + "latitude", latitude);
                        hashLatitude.put(marker.getId() + "longitude", longitude);

                        hashSiteValue.put(marker.getId()+"site_id", site_id);
                        hashSiteValue.put(marker.getId()+"site_name", site_name);
                        hashSiteValue.put(marker.getId()+"dsf_1", dsf_1);
                        hashSiteValue.put(marker.getId()+"dsf_2", dsf_2);

                        //goto_mylocation();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatling));
                        float zoomLevel = 10.0f; //This goes up to 21
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatling, zoomLevel));
                    }
                    getAutoText();
                    progressDialog.cancel();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //String nXX = "Cannot get the data!";
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            }
        };
        return listener;
    }

    private Response.Listener<String> getNDBListener(){
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean LacCI_success = jsonResponse.getBoolean("LacCI_success");
                    if (LacCI_success){
                        String LacCI_site_id = jsonResponse.getString("LacCI_site_id");
                        progressDialog.cancel();
                    }

                    boolean SiteId_success = jsonResponse.getBoolean("SiteId_success");
                    if (SiteId_success){
                        Polyline polylineX = hashPolyLine.get("MySiteLine");
                        if (polylineX != null) { polylineX.remove(); }
                        Integer count = jsonResponse.getInt("count");
                        for (int a = 1; a <= count; a++) {
                            String site_id = jsonResponse.getString("site_id");
                            String site_name = jsonResponse.getString("site_name");
                            Double longitude = jsonResponse.getDouble("longitude");
                            Double latitude = jsonResponse.getDouble("latitude");
                            LatLng site = new LatLng(latitude, longitude);
                            MySiteName.setText(site_name);
                            if (myLatitude != 0 && myLongitude != 0 &&
                                    (Double) myLatitude != null && (Double) myLongitude != null) {
                                LatLng myLoc = new LatLng(myLatitude, myLongitude);
                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .add(site)
                                        .add(myLoc)
                                        .color(getResources().getColor(R.color.yellow_light));
                                Polyline polyline = mMap.addPolyline(polylineOptions);
                                hashPolyLine.put("MySiteLine", polyline);
                                MySiteName.setText(site_name);
                            }
                        }
                        progressDialog.cancel();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    MySiteName.setText("My Site");
                    progressDialog.cancel();
                }
            }
        };
        return listener;
    }

    private void getAutoText(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, SearchMaterial);
        mSearch.setAdapter(arrayAdapter);
        mSearch.setThreshold(1);
        mSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                int pos = 0;
                for (int X = 0; X < SearchMaterial.size(); i++) {
                    if (SearchMaterial.get(i).equals(selection)) {
                        pos = i;
                        break;
                    }
                }
                LatLng site = new LatLng(LatitudeMaterial.get(pos), LongitudeMaterial.get(pos));
                float zoomLevel = 15.0f;
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        site, zoomLevel);
                mMap.animateCamera(location);
                hideSoftKeyboard();
                mSearch.setText("");
            }
        });
    }

    private void getLacCI(){
        TelephonyManager telephony = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
            if (location != null) {
                myLAC =  String.valueOf(location.getLac());
                myCI = String.valueOf(location.getCid());
            }
        }
    }

    private void getMyLongLat(){
        if (mMap != null){
            location = mMap.getMyLocation();
            if(location != null) {
                myLongitude = location.getLongitude();
                myLatitude = location.getLatitude();
            }else{
                myLongitude = 0;
                myLatitude = 0;
            }
        }
    }

    private void goto_mylocation(){
        getMyLongLat();
        if(myLatitude != 0 && myLongitude != 0 &&
                (Double)myLatitude != null && (Double)myLongitude != null){
            LatLng mLatling = new LatLng(myLatitude, myLongitude);
            float zoomLevel = 14.0f; //This goes up to 21
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatling, zoomLevel));
        }
    }

    private View infoWindowView(Marker marker, String Kondisi){
        View v = getLayoutInflater().inflate(R.layout.item_site_info_window, null);

        TextView textSiteId = (TextView) v.findViewById(R.id.textSiteId);
        TextView textSiteName = (TextView) v.findViewById(R.id.textSiteName);
        TextView textDSF1 = (TextView) v.findViewById(R.id.textDSF1);
        TextView textDSF2 = (TextView) v.findViewById(R.id.textDSF2);

        String site_id = hashSiteValue.get(marker.getId() + "site_id");
        String site_name = hashSiteValue.get(marker.getId() + "site_name");
        String dsf_1 = hashSiteValue.get(marker.getId() + "dsf_1");
        String dsf_2 = hashSiteValue.get(marker.getId() + "dsf_2");

        textSiteId.setText(site_id);
        textSiteName.setText(site_name);
        textDSF1.setText(dsf_1);
        textDSF2.setText(dsf_2);

        return v;
    }

    private View setInfoWindowsContent(Marker marker){
        View v = null;
        v = infoWindowView(marker, "");
        return v;
    }

    private void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private Bitmap setMarkerBitmapSize(Bitmap b) {
        int height = 100;
        int width = 100;
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }
}