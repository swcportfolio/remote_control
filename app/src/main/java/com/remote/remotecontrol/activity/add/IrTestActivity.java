package com.remote.remotecontrol.activity.add;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.remote.remotecontrol.R;
import com.remote.remotecontrol.activity.MainActivity;
import com.remote.remotecontrol.retrofit.NextKeyModel;
import com.remote.remotecontrol.retrofit.Nextkey;
import com.remote.remotecontrol.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IrTestActivity extends AppCompatActivity {
    private final String TAG = IrTestActivity.class.getCanonicalName();

    private ImageView IV_test;
    private Intent intent;

    private RetrofitClient retrofitClient = new RetrofitClient();

    private String BASE_URL = MainActivity.URL+"/quicksetlite.svc/";  //URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ir_test);

        IV_test = findViewById(R.id.IV_test);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IV_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextKey();
            }
        });
    }

    private void getNextKey() {
       intent = getIntent();
       String brandName    = intent.getStringExtra("brandName");
       String devTypeCodes = intent.getStringExtra("devTypeCodes");
       String devGroupId   = intent.getStringExtra("devGroupId");

        SharedPreferences sharedPreferences  = getSharedPreferences("data",MODE_PRIVATE);
        String ueiRc = sharedPreferences.getString("ueiRc",null);
        String inputXML =  "<osm b=\""+brandName+"\" dtypes=\""+devTypeCodes+"\" dgroupId=\""+devGroupId+"\" region=\"593\"/>";

        Nextkey nextkey = new Nextkey();
        nextkey.setUeiRc(ueiRc);
        nextkey.setInputXML(inputXML);
        retrofitClient.getClient(BASE_URL).getTestKey("application/json", nextkey).enqueue(new Callback<NextKeyModel>() {
            @Override
            public void onResponse(Call<NextKeyModel> call, Response<NextKeyModel> response) {
                Log.d(TAG ,"getBrand Success network");
                Log.d(TAG ,"testCode :: "+response.body().getTestCode()+" / testCodeData :: "+response.body().getTestCodeData());
            }

            @Override
            public void onFailure(Call<NextKeyModel> call, Throwable t) {
                Log.d(TAG ,"getBrand False network");
            }
        });





    }
}