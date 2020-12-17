package com.remote.remotecontrol.activity.info;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.remote.remotecontrol.R;

public class DeviceInfoActivity extends AppCompatActivity {
    LinearLayout btn_modify;
    private TextView txt_brand,txt_type,txt_name;
    private ImageView IV_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_appliance_info);
        btn_modify = findViewById(R.id.btn_modify);

        txt_brand = findViewById(R.id.txt_brand);
        txt_type = findViewById(R.id.txt_type);
        txt_name = findViewById(R.id.txt_name);

        IV_image = findViewById(R.id.IV_image);


        Intent intent = getIntent();
        String imageName = intent.getStringExtra("image");
        if(imageName.equals("tv")){
            IV_image.setImageDrawable(getDrawable(R.drawable.ic_tv));
            txt_brand.setText("SAMSUNG");
            txt_type.setText("TV");
            txt_name.setText("거실 TV");

        }else if(imageName.equals("fan")){
            IV_image.setImageDrawable(getDrawable(R.drawable.ic_fan));
            txt_brand.setText("다이슨");
            txt_type.setText("선풍기");
            txt_name.setText("안방 선풍기");

        }else if(imageName.equals("air")){
            IV_image.setImageDrawable(getDrawable(R.drawable.ic_aircon));
            txt_brand.setText("LG");
            txt_type.setText("에어컨");
            txt_name.setText("거실 에어컨");
        }else{
            Toast.makeText(getApplicationContext(),"image Name null",Toast.LENGTH_LONG).show();
        }
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true);   // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_back);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_modify.setOnClickListener(v -> startActivity(new Intent(DeviceInfoActivity.this, DeviceEditInfoActivity.class)));
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