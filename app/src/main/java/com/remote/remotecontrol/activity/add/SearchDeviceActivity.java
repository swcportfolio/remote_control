package com.remote.remotecontrol.activity.add;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.remote.remotecontrol.R;
import com.remote.remotecontrol.activity.MainActivity;
import com.remote.remotecontrol.retrofit.BrandModel;
import com.remote.remotecontrol.retrofit.Brands;
import com.remote.remotecontrol.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDeviceActivity extends AppCompatActivity {
    private final String TAG = SearchDeviceActivity.class.getSimpleName();

    private LinearLayout btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9;

    private String ueiRcURL = MainActivity.URL+"/quicksetlite.svc/";

    private RetrofitClient client = new RetrofitClient();
    private Brands brands = new Brands();

    private  String region = "593"; // 한국

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_6 = findViewById(R.id.btn_6);
        btn_7 = findViewById(R.id.btn_7);
        btn_8 = findViewById(R.id.btn_8);
        btn_9 = findViewById(R.id.btn_9);


        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true);   // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_back);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //get devTypeCodes
        Intent intent = getIntent();
        String devTypeCodes = intent.getStringExtra("devTypeCodes");

        //get ueiRc
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        String ueiRc = sharedPreferences.getString("ueiRc",null);

        brands.setUeiRc(ueiRc);
        brands.setRegion(region);
        brands.setDeviceTypes(devTypeCodes);

        client.getClient(ueiRcURL).getBrand("application/json",brands).enqueue(new Callback<BrandModel>() {
            @Override
            public void onResponse(Call<BrandModel> call, Response<BrandModel> response) {
                Log.d(TAG ,"getUeiRc Success network");

                List<String> arrayList = response.body().getBrands();
                for(String i:arrayList){
                    Log.d(TAG,"getBrands : "+i);

                }
            }

            @Override
            public void onFailure(Call<BrandModel> call, Throwable t) {
                Log.d(TAG ,"getUeiRc False network");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_1.setOnClickListener(view-> startActivity(new Intent(getApplicationContext(), AddDeviceInfoActivity.class)));
        btn_2.setOnClickListener(view-> startActivity(new Intent(getApplicationContext(), AddDeviceInfoActivity.class)));
        btn_3.setOnClickListener(view-> startActivity(new Intent(getApplicationContext(), AddDeviceInfoActivity.class)));
        btn_4.setOnClickListener(view-> startActivity(new Intent(getApplicationContext(), AddDeviceInfoActivity.class)));
        btn_5.setOnClickListener(view-> startActivity(new Intent(getApplicationContext(), AddDeviceInfoActivity.class)));
        btn_6.setOnClickListener(view-> startActivity(new Intent(getApplicationContext(), AddDeviceInfoActivity.class)));
        btn_7.setOnClickListener(view-> startActivity(new Intent(getApplicationContext(), AddDeviceInfoActivity.class)));
        btn_8.setOnClickListener(view-> startActivity(new Intent(getApplicationContext(), AddDeviceInfoActivity.class)));
        btn_9.setOnClickListener(view-> startActivity(new Intent(getApplicationContext(), AddDeviceInfoActivity.class)));

    }

    /**
     * Toolbar의 back키 눌렀을 때 동작
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}