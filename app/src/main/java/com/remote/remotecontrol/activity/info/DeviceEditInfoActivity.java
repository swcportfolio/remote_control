package com.remote.remotecontrol.activity.info;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.remote.remotecontrol.R;
import com.remote.remotecontrol.TypeCoversion;
import com.remote.remotecontrol.activity.MainActivity;
import com.remote.remotecontrol.database.DBHelper;

public class DeviceEditInfoActivity extends AppCompatActivity {
    private final String TAG = DeviceEditInfoActivity.class.getSimpleName();

    private TextView txt_brand,txt_type;
    private EditText edit_nickName;
    private String brand,deviceType,nickName,groupId;
    private LinearLayout btn_edit;
    private ImageView image_edit;

    private TypeCoversion coversion = new TypeCoversion();
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_appliance_info_modify);

        //image
        image_edit = findViewById(R.id.image_edit);

        //button
        btn_edit = findViewById(R.id.btn_edit);

        //text
        txt_brand    = findViewById(R.id.txt_brand);
        txt_type     = findViewById(R.id.txt_type);
        edit_nickName = findViewById(R.id.edit_nickName);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true);   // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_back);

        Intent intent = getIntent();
        brand = intent.getStringExtra("brand");
        deviceType  = intent.getStringExtra("deviceType");
        nickName = intent.getStringExtra("nickName");
        groupId = intent.getStringExtra("groupId");

        txt_brand.setText(brand);
        txt_type.setText(coversion.devGroupIdConversion(groupId));
        edit_nickName.setText(nickName);
        image_edit.setImageResource(coversion.imageConversion(groupId));
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_edit.setOnClickListener(view->{

               AlertDialog.Builder builder = new AlertDialog.Builder(DeviceEditInfoActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
               builder.setTitle("수정");
               builder.setMessage("별칭을 수정하시겠습니까?");

               builder.setPositiveButton("확인", (dialogInterface, i) -> {

                   infoUpdate();
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

    private void infoUpdate() {
        String changeNickName = edit_nickName.getText().toString();
        if(changeNickName != null) {
            dbHelper = new DBHelper(getApplicationContext(), "remote.db", null, 1);
            dbHelper.update(nickName, changeNickName);
        }else{
            Log.d(TAG,"DB update False");
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