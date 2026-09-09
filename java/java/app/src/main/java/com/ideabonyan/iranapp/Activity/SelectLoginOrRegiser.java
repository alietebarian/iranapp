package com.ideabonyan.iranapp.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.ideabonyan.iranapp.R;

import java.util.ArrayList;
import java.util.List;

public class SelectLoginOrRegiser extends AppCompatActivity {

    LinearLayout lin_register, lin_login;
    int REQUEST_ID_MULTIPLE_PERMISSIONS = 1001;
    boolean canExitNow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_login_or_regiser);
        checkAndRequestPermissions();
        holder();
        onclick();
    }

    private void onclick() {
        lin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectLoginOrRegiser.this, LoginActivity.class));
            }
        });
        lin_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectLoginOrRegiser.this, SignUpActivity.class));

            }
        });
    }

    private void holder() {
        lin_login = findViewById(R.id.lin_login);
        lin_register = findViewById(R.id.lin_register);
    }

    private boolean checkAndRequestPermissions() {

        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int locSvc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int task = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_TASKS);
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int read_external_storeg = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int RECEIVE_SMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (locSvc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (task != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.GET_TASKS);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read_external_storeg != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (RECEIVE_SMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new
                    String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        ///////////////////

        if (canExitNow) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            System.exit(1);
        } else {
            canExitNow = true;

            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.splashScreen),
                    "آیا مایل به خروج از برنامه هستید؟", Snackbar.LENGTH_SHORT);
            mySnackbar.setAction("خروج", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    System.exit(1);
                }
            });
            mySnackbar.setActionTextColor(Color.YELLOW);
            mySnackbar.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    canExitNow = false;
                }
            }, 1200);
        }
    }

}
