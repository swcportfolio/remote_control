package com.remote.remotecontrol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.remote.remotecontrol.R;
import com.remote.remotecontrol.activity.add.AddDeviceActivity;
import com.remote.remotecontrol.activity.add.SearchDeviceActivity;
import com.remote.remotecontrol.activity.info.DeviceInfoActivity;
import com.remote.remotecontrol.retrofit.RetrofitClient;
import com.remote.remotecontrol.retrofit.UeiRcModel;
import com.remote.remotecontrol.retrofit.UserUeiRC;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private ImageView IV_add;
    private LinearLayout btn_1,btn_2,btn_3,btn_4;

    private UserUeiRC  userUeiRC = new UserUeiRC();
    private RetrofitClient client = new RetrofitClient();

    public static final String URL = "https://rcl.ueiquickset.com";
    private String ueiRcURL = URL+"/quicksetlite.svc/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ImageView btn
        IV_add = findViewById(R.id.IV_add);

        //LinearLayout btn
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);

        getUeiRc();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_1.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DeviceInfoActivity.class)));

        btn_2.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DeviceInfoActivity.class)));

        btn_3.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DeviceInfoActivity.class)));

        btn_4.setOnClickListener(view -> {
        startActivity(new Intent(MainActivity.this, AddDeviceActivity.class ));
        });
        IV_add.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AddDeviceActivity.class ));
        });
    }

    private void getUeiRc() {

        client.getClient(ueiRcURL).getUeiRc("application/json", userUeiRC).enqueue(new Callback<UeiRcModel>() {
            @Override
            public void onResponse(Call<UeiRcModel> call, Response<UeiRcModel> response) {
                Log.d(TAG ,"getUeiRc Success network");
                String ueiRc = response.body().getUeiRc();
                Log.d(TAG,"ueiRc : "+ueiRc);

                SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                SharedPreferences.Editor editor  = sharedPreferences.edit();
                editor.putString("ueiRc",ueiRc);
                editor.commit();
                Log.d(TAG,"ueiRc editor.commit : "+ueiRc);

            }

            @Override
            public void onFailure(Call<UeiRcModel> call, Throwable t) {
                Log.d(TAG ,"getUeiRc False network");
            }
        });

    }
}