package com.remote.remotecontrol.activity.add;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.remote.remotecontrol.R;

public class AddDeviceActivity extends AppCompatActivity {
    private LinearLayout btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_6 = findViewById(R.id.btn_6);
        btn_7 = findViewById(R.id.btn_7);
        btn_8 = findViewById(R.id.btn_8);

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

        btn_1.setOnClickListener(view-> deviceType(0,"TV","T"));
        btn_2.setOnClickListener(view-> deviceType(1,"OTT","N"));
        btn_3.setOnClickListener(view-> deviceType(2,"STB","C"));
        btn_4.setOnClickListener(view-> deviceType(3,"Audio","A"));
        btn_5.setOnClickListener(view-> deviceType(4,"Projector","T"));
        btn_6.setOnClickListener(view-> deviceType(5,"Lighting","H"));
        btn_7.setOnClickListener(view-> deviceType(6,"FAN","H"));
        btn_8.setOnClickListener(view-> deviceType(7,"Air Conditioner","Z"));
    }

    private void deviceType(int groupIds, String groups, String devTypeCodes) {
        Intent  intent = new Intent(getApplicationContext(),SearchDeviceActivity.class);
        intent.putExtra("groupIds",groupIds);
        intent.putExtra("groups",groups);
        intent.putExtra("devTypeCodes",devTypeCodes);
        startActivity(intent);
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