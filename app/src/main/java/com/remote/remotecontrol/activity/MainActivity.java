package com.remote.remotecontrol.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.remote.remotecontrol.Logger;
import com.remote.remotecontrol.R;
import com.remote.remotecontrol.activity.add.AddDeviceActivity;
import com.remote.remotecontrol.activity.add.SearchDeviceActivity;
import com.remote.remotecontrol.activity.ble.LeSttActivity;
import com.remote.remotecontrol.activity.info.DeviceInfoActivity;
import com.remote.remotecontrol.retrofit.RetrofitClient;
import com.remote.remotecontrol.retrofit.UeiRcModel;
import com.remote.remotecontrol.retrofit.UserUeiRC;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    byte data2[] = hexStringToByteArray("2554530a");
    private final String TAG = MainActivity.class.getSimpleName();

    private ImageView IV_add,IV_le_stt;
    private LinearLayout btn_main_1,btn_main_2,btn_main_3,btn_main_4;

    private UserUeiRC  userUeiRC = new UserUeiRC();
    private RetrofitClient client = new RetrofitClient();
    private Logger logger = new Logger();

    public static final String URL = "https://rcl.ueiquickset.com";
    private String ueiRcURL = URL+"/quicksetlite.svc/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ImageView btn
        IV_add    = findViewById(R.id.IV_add);
        IV_le_stt = findViewById(R.id.IV_le_stt);

        //LinearLayout btn
        btn_main_1 = findViewById(R.id.btn_main_1);
        btn_main_2 = findViewById(R.id.btn_main_2);
        btn_main_3 = findViewById(R.id.btn_main_3);
        btn_main_4 = findViewById(R.id.btn_main_4);

        getUeiRc();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_main_1.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DeviceInfoActivity.class)));

        btn_main_2.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DeviceInfoActivity.class)));

        btn_main_3.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DeviceInfoActivity.class)));

        btn_main_4.setOnClickListener(view -> {
        startActivity(new Intent(MainActivity.this, AddDeviceActivity.class ));
        });
        IV_add.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AddDeviceActivity.class ));
        });

        //bluetooth, stt 화면 이동
        IV_le_stt.setOnClickListener(view ->startActivity(new Intent(MainActivity.this, LeSttActivity.class)));

    }

    private void getUeiRc() {

        client.getClient(ueiRcURL).getUeiRc("application/json", userUeiRC).enqueue(new Callback<UeiRcModel>() {
            @Override
            public void onResponse(Call<UeiRcModel> call, Response<UeiRcModel> response) {
                logger.LoggerStart(TAG,"getUeiRc()");
                Log.d(TAG ,"##REST## getUeiRc Success network");
                String ueiRc = response.body().getUeiRc();
                Log.d(TAG,"##REST## ueiRc : "+ueiRc);

                SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                SharedPreferences.Editor editor  = sharedPreferences.edit();
                editor.putString("REST ueiRc",ueiRc);
                editor.commit();
                Log.d(TAG,"##REST## ueiRc editor.commit : "+ueiRc);
                logger.LoggerEnd(TAG,"getUeiRc()");
            }
            @Override
            public void onFailure(Call<UeiRcModel> call, Throwable t) {
                Log.d(TAG ,"##REST##getUeiRc False network"+t.toString());
            }
        });

    }

    public byte[] hexStringToByteArray(String s) {
        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));

        }
        for(int j =0;j<data.length;j++){
            Log.d("data::",""+data[j]);
        }
        return data;
    }
}