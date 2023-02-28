package com.ioh.cekimei;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

public class Logout {

    public Logout(Context context, SharedPreferences.Editor editor, AlertDialog.Builder builder, Activity activity){
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Logout")
                .setMessage("Logout dari aplikasi?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(context, Login.class);
                        context.startActivity(intent);
                        activity.finish();
                    }
                })
                .setNegativeButton("Tidak",null)
                .create()
                .show();
    }

}
