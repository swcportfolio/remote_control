package com.remote.remotecontrol.activity.info;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.remote.remotecontrol.R;
import com.remote.remotecontrol.TypeCoversion;
import com.remote.remotecontrol.activity.MainActivity;
import com.remote.remotecontrol.activity.add.AddDeviceInfoActivity;
import com.remote.remotecontrol.database.DBHelper;
import com.remote.remotecontrol.gridview.SingerItem;

public class DeviceInfoActivity extends AppCompatActivity {
    private final String TAG = DeviceInfoActivity.class.getSimpleName();

    private  LinearLayout btn_modify,btn_delete;
    private TextView txt_brand,txt_type,txt_name;
    private ImageView IV_image;

    private TypeCoversion coversion = new TypeCoversion();
    private DBHelper dbHelper;

    private String[] parsing;
    private int position;
    private int REQUEST_CODE =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_appliance_info);

        Intent intent = getIntent();
        position = intent.getIntExtra("position",-1);


        btn_modify = findViewById(R.id.btn_modify);
        btn_delete = findViewById(R.id.btn_delete);

        txt_brand = findViewById(R.id.txt_brand);
        txt_type = findViewById(R.id.txt_type);
        txt_name = findViewById(R.id.txt_name);

        IV_image = findViewById(R.id.IV_image);

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
        if(position != -1){
            dbHelper = new DBHelper(getApplicationContext(), "remote.db", null, 1);
            String resultData = dbHelper.getInfo(position);
            Log.d(TAG,"resultData:"+resultData);
            if(!resultData.isEmpty()){
                parsing = resultData.split("/");

                txt_type.setText(coversion.devGroupIdConversion(parsing[2]));
                txt_brand.setText(parsing[1]);
                IV_image.setImageResource(coversion.imageConversion(parsing[2]));
                txt_name.setText(parsing[3]);
            }
        }else{
            Toast.makeText(getApplicationContext(), "오류", Toast.LENGTH_SHORT).show();
        }
    }
    private void typeConversion(String type) {
        switch (type){
            case "T":txt_type.setText("TV");break;
            case "N":txt_type.setText("OTT");break;
            case "C":txt_type.setText("STB");break;
            case "A":txt_type.setText("Audio");break;
            case "H":txt_type.setText("Lighting");break;
            case "Z":txt_type.setText("Air Conditioner");break;

            //("4","Projector","T")); // 삭제
            //("6","FAN","H"));// 삭제
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_modify.setOnClickListener(v ->{
            startActivity(new Intent(DeviceInfoActivity.this, DeviceEditInfoActivity.class));
            Intent intent = new Intent(getApplicationContext(),DeviceEditInfoActivity.class);
            intent.putExtra("deviceType",parsing[0]);
            intent.putExtra("brand",parsing[1]);
            intent.putExtra("groupId",parsing[2]);
            intent.putExtra("nickName",parsing[3]);
            startActivityForResult(intent,REQUEST_CODE);
        });

        btn_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
            builder.setTitle("삭제");
            builder.setMessage("등록된 가전을 삭제 하시겠습니까?");

            builder.setPositiveButton("확인", (dialogInterface, i) -> {

                infoDelete();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            });
            builder.setNegativeButton("취소", (dialogInterface, i) -> {
                Log.d(TAG, "취소버튼");
            });
            builder.show();
        });
    }

    private void infoDelete() {
        final DBHelper dbHelper = new DBHelper(getApplicationContext(), "remote.db", null, 1);
        dbHelper.delete(parsing[3]); // nickName 으로 삭제
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(),"수정완료",Toast.LENGTH_LONG).show();
            }
        }
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