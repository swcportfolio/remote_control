package com.remote.remotecontrol.activity.add;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.remote.remotecontrol.R;
import com.remote.remotecontrol.TypeCoversion;
import com.remote.remotecontrol.activity.MainActivity;
import com.remote.remotecontrol.database.DBHelper;

public class AddDeviceInfoActivity extends AppCompatActivity {
    private static final String TAG = AddDeviceInfoActivity.class.getSimpleName();

    private LinearLayout btn_add;
    private TextView txt_info_type,txt_info_brand;
    private EditText edit_info_name;
    private ImageView ImV_info_image;
    private String devTypeCodes,devGroupId,brandName;

    private TypeCoversion coversion = new TypeCoversion();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_info);

        btn_add = findViewById(R.id.btn_add);

        //TextView
        txt_info_brand = findViewById(R.id.txt_info_brand);
        txt_info_type = findViewById(R.id.txt_info_type);
        edit_info_name = findViewById(R.id.edit_info_name);

        //Image
        ImV_info_image = findViewById(R.id.ImV_info_image);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true);   // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_back);


        //TextView set
        Intent intent = getIntent();
        brandName = intent.getStringExtra("brandName");
        devTypeCodes = intent.getStringExtra("devTypeCodes");
        devGroupId = intent.getStringExtra("devGroupId");

        if(brandName.isEmpty()||brandName == null||devTypeCodes.isEmpty()||devTypeCodes == null){
            Log.e(TAG,"Text info NULL");
        }else{
            txt_info_brand.setText(brandName);
            txt_info_type.setText(coversion.devGroupIdConversion(devGroupId));
            ImV_info_image.setImageResource(coversion.imageConversion(devGroupId));
        }


    }
/*  btn_1.setOnClickListener(view-> deviceType("0","TV","T"));
        btn_2.setOnClickListener(view-> deviceType("1","OTT","N"));
        btn_3.setOnClickListener(view-> deviceType("2","STB","C"));
        btn_4.setOnClickListener(view-> deviceType("3","Audio","A"));
        btn_5.setOnClickListener(view-> deviceType("4","Projector","T"));
        btn_6.setOnClickListener(view-> deviceType("5","Lighting","H"));
        btn_7.setOnClickListener(view-> deviceType("6","FAN","H"));
        btn_8.setOnClickListener(view-> deviceType("7","Air Conditioner","Z"));*/
/*    private void deviceTypeConversion( String devGroupId) {

        switch (devGroupId){
            case "0" :txt_info_type.setText("TV");break;
            case "1" :txt_info_type.setText("OTT");break;
            case "2" :txt_info_type.setText("STB");break;
            case "3" :txt_info_type.setText("Audio");break;
            case "4" :txt_info_type.setText("Projector");break;
            case "5" :txt_info_type.setText("Lighting");break;
            case "6" :txt_info_type.setText("FAN");break;
            case "7" :txt_info_type.setText("Air Conditioner");break;
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        btn_add.setOnClickListener(view -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddDeviceInfoActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    builder.setTitle("가전 등록");
                    builder.setMessage("정상적으로 가전이 등록되었습니다.");
                    builder.setPositiveButton("확인", (dialogInterface, i) -> {
                        /*SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                        SharedPreferences.Editor editor     = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();*/
                        infoInsert();
                        Intent intent = new Intent(AddDeviceInfoActivity.this, MainActivity.class);
                        startActivity(intent);

                    });
                    builder.show();
        }
        );
    }

    private void infoInsert() {
        String nickName = edit_info_name.getText().toString();
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "remote.db", null, 1);
        dbHelper.insert(devTypeCodes, brandName, devGroupId,nickName);

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