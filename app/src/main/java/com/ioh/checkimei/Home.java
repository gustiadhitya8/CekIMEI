package com.ioh.checkimei;

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
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class Home extends AppCompatActivity {

    //Indetify Component
    BottomNavigationView bottomNavigationMenuView;
    int myContainer = R.id.container;

    //Call the Fragment
    FragmentCekImei fragmentCekImei = new FragmentCekImei();
    FragmentRiwayatCek fragmentRiwayatCek = new FragmentRiwayatCek();
    FragmentPencapaian fragmentPencapaian = new FragmentPencapaian();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Ask Camera Permission
        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

        //Identify Component With ID
        bottomNavigationMenuView = findViewById(R.id.BottomNavigationMenu);
        replaceContainer(myContainer, fragmentCekImei);

        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String data_fitur_pencapaian = sharedPreferences.getString("data_fitur_pencapaian", "");

        if (!data_fitur_pencapaian.equals("1")){
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
                        replaceContainer(myContainer, fragmentPencapaian);
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