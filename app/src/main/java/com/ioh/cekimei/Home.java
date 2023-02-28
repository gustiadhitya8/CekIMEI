package com.ioh.cekimei;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class Home extends AppCompatActivity {

    //Indetify Component
    BottomNavigationView bottomNavigationMenuView;
    int myContainer = R.id.container;

    //Call the Fragment
    FragmentCekImei fragmentCekImei = new FragmentCekImei();
    FragmentRiwayatCek fragmentRiwayatCek = new FragmentRiwayatCek();
    FragmentSiteInfo fragmentDSFPortal = new FragmentSiteInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Ask Camera Permission
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();

        //Identify Component With ID
        bottomNavigationMenuView = findViewById(R.id.BottomNavigationMenu);
        replaceContainer(myContainer, fragmentCekImei);

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String data_fitur_site_info = sharedPreferences.getString("data_fitur_site_info", "");

        if (!data_fitur_site_info.equals("1")){
            bottomNavigationMenuView.getMenu().removeItem(R.id.pencapaian_qsc_imei);
        }

        bottomNavigationMenuView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.cek_imei:{
                        replaceContainer(myContainer, fragmentCekImei);
                        return true;}
                    case R.id.riwayat_cek_imei:
                        replaceContainer(myContainer, fragmentRiwayatCek);
                        return true;
                    case R.id.pencapaian_qsc_imei:
                        replaceContainer(myContainer, fragmentDSFPortal);
                        return true;
                }
                return false;
            }
        });

    }

    private void replaceContainer(int nContainer, Fragment nFragment){
        getSupportFragmentManager().beginTransaction().replace(nContainer, nFragment).commit();
    }
}