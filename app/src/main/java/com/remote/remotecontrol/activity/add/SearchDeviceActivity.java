package com.remote.remotecontrol.activity.add;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.remote.remotecontrol.R;
import com.remote.remotecontrol.activity.MainActivity;
import com.remote.remotecontrol.listadapter.ListViewAdapter;
import com.remote.remotecontrol.listadapter.ListViewItem;
import com.remote.remotecontrol.retrofit.BrandModel;
import com.remote.remotecontrol.retrofit.Brands;
import com.remote.remotecontrol.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDeviceActivity extends AppCompatActivity {
    private final String TAG = SearchDeviceActivity.class.getSimpleName();

    private EditText edt_search;
    private ListView lv_brand;
    private TextView btn_search;

    private ListViewAdapter adapter; // 리스트 뷰 어뎁터

    private RetrofitClient client = new RetrofitClient();
    private Brands brands = new Brands();

    private  List<String> getBrand = new ArrayList<>();

    private boolean mLockListView       = false;  // 데이터 불러올때 중복값 체크
    private boolean lastItemVisibleFlag = false;  // 리스트 스쿠롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private int mFirstVisibleItem;     //리스트 현재 위치

    private String ueiRcURL = MainActivity.URL+"/quicksetlite.svc/";  // URL
    private String region   = "593"; // 한국
    private int  resultPageIndex = 0; // 검색 결과 인덱스
    private final int resultPageSize =  10;  // 리스트 개수
    private String devTypeCodes,devGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);

        btn_search = findViewById(R.id.btn_search);

        //검색 란
        edt_search = findViewById(R.id.edt_search);

        //리스트 뷰
        lv_brand = findViewById(R.id.lv_brand);
        adapter = new ListViewAdapter();
        lv_brand.setAdapter(adapter);

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
    }

    private void getListData() {

        //get devTypeCodes
        Intent intent = getIntent();
         devTypeCodes = intent.getStringExtra("devTypeCodes");
         devGroupId   = intent.getStringExtra("groupIds");
        String brandsStartWith = edt_search.getText().toString();
        Log.d(TAG,"brandsStartWith:"+brandsStartWith+"/devGroupId:"+devGroupId+"/devTypeCodes:"+devTypeCodes);

        //get ueiRc
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        String ueiRc = sharedPreferences.getString("ueiRc",null);

        brands.setUeiRc(ueiRc);
        brands.setRegion(region);
        brands.setDeviceTypes(devTypeCodes);
        brands.setDevGroupId(devGroupId);
        brands.setBrandsStartWith(brandsStartWith);
        brands.setResultPageIndex(resultPageIndex);
        brands.setResultPageSize(resultPageSize);
        Log.d(TAG,"ueiRc:"+ueiRc+"/region:"+region+"/devTypeCodes:"+devTypeCodes);
        Log.d(TAG,"devGroupId:"+devGroupId+"/brandsStartWith:"+brandsStartWith+"/resultPageSize:"+resultPageSize);
        client.getClient(ueiRcURL).getBrand("application/json",brands).enqueue(new Callback<BrandModel>() {
            @Override
            public void onResponse(Call<BrandModel> call, Response<BrandModel> response) {
                Log.d(TAG ,"getBrand Success network");
                Log.d(TAG ,"getBrand response getResultTotal"+response.body().getResultTotal());

                getBrand = response.body().getBrands();

                     /*for(String i:getBrand){
                    Log.d(TAG,"getBrands : " + i);
                }*/
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(getBrand != null){
                            AdapterAdd();
                        }else{
                            Toast.makeText(getApplicationContext(),"더이상 브랜드가 없습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }
                },500);

            }
            @Override
            public void onFailure(Call<BrandModel> call, Throwable t) {
                Log.d(TAG ,"getBrand False network");
                Log.d(TAG,"network false:"+t.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        lv_brand.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
                    getItem();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int visibleItemCount, int totalItemCount) {
                lastItemVisibleFlag = (totalItemCount > 0) && (i + visibleItemCount >= totalItemCount);
                mFirstVisibleItem = i;//상단 첫번째 리스트
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListData();
                InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.hideSoftInputFromWindow(btn_search.getWindowToken(), 0);
            }
        });

        lv_brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String brandName = ((ListViewItem)adapter.getItem(position)).getBrandName();

               Intent intent = new Intent(getApplicationContext(),IrTestActivity.class);
                intent.putExtra("brandName",brandName);
                intent.putExtra("devTypeCodes",devTypeCodes);
                intent.putExtra("devGroupId",devGroupId);
                startActivity(intent);
            }
        });


    }
    /**
     * 어뎁터 추가 함수
     */
    private void AdapterAdd() {
        for (int i = 0; i < getBrand.size(); i++) {
            adapter.addItem(getBrand.get(i));
        }
        adapter.notifyDataSetChanged();
        /*List_history.setSelection(mFirstVisibleItem);*/

        getBrand.clear();
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
    /**
     * 페이징 처리 후 데이터 가져오는 함수
     */
    private void getItem() {
        // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 mLockListView 를 true로 설정한다.
        mLockListView = true;
        resultPageIndex++;
        getListData();

        adapter.notifyDataSetChanged();
        mLockListView = false;


        // 1초 뒤 프로그레스바를 감추고 데이터를 갱신하고, 중복 로딩 체크하는 Lock을 했던 mLockListView변수를 풀어준다.
    }
}