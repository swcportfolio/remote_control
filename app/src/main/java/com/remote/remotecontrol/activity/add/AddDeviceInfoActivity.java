package com.remote.remotecontrol.activity.add;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.remote.remotecontrol.R;
import com.remote.remotecontrol.activity.MainActivity;

public class AddDeviceInfoActivity extends AppCompatActivity {
    LinearLayout btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_info);

        btn_add = findViewById(R.id.btn_add);
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
        btn_add.setOnClickListener(view -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddDeviceInfoActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    builder.setTitle("가전 등록");
                    builder.setMessage("정상적으로 가전이 등록되었습니다.");
                    builder.setPositiveButton("예", (dialogInterface, i) -> {
                        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                        SharedPreferences.Editor editor     = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(AddDeviceInfoActivity.this, MainActivity.class); // 로그인 화면으로 이동
                        startActivity(intent);
                        finish();
                    });
                    builder.show();
        }
        );
    }

    /**
     * Toolbar의 back키 눌렀을 때 동작
     *
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