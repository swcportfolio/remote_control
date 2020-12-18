package com.remote.remotecontrol.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.remote.remotecontrol.Logger;
import com.remote.remotecontrol.R;
import com.remote.remotecontrol.activity.add.AddDeviceActivity;
import com.remote.remotecontrol.activity.add.SearchDeviceActivity;
import com.remote.remotecontrol.activity.ble.BluetoothLeService;
import com.remote.remotecontrol.activity.ble.GattAttributes;
import com.remote.remotecontrol.activity.ble.LeSttActivity;
import com.remote.remotecontrol.activity.info.DeviceInfoActivity;
import com.remote.remotecontrol.retrofit.RetrofitClient;
import com.remote.remotecontrol.retrofit.UeiRcModel;
import com.remote.remotecontrol.retrofit.UserUeiRC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.remote.remotecontrol.R.drawable.bluetooth_view;
import static com.remote.remotecontrol.R.drawable.bluetooth_view_on;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private ImageView IV_add, IV_le_stt;
    private LinearLayout btn_main_1, btn_main_2, btn_main_3, btn_main_4;

    private UserUeiRC userUeiRC = new UserUeiRC();
    private RetrofitClient client = new RetrofitClient();
    private Logger logger = new Logger();

    private Set<BluetoothDevice> mDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mRemoteDevice;
    private BluetoothLeService mBluetoothLeService;

    public static final String URL = "https://rcl.ueiquickset.com";
    private String ueiRcURL = URL + "/quicksetlite.svc/";
    private String mDeviceName,mDeviceAddress ;

    private static final int REQUEST_ENABLE_BT = 1 ;
    private int mPairedDeviceCount;
    private static boolean mConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ImageView btn
        IV_add = findViewById(R.id.IV_add);
        IV_le_stt = findViewById(R.id.IV_le_stt);

        //LinearLayout btn
        btn_main_1 = findViewById(R.id.btn_main_1);
        btn_main_2 = findViewById(R.id.btn_main_2);
        btn_main_3 = findViewById(R.id.btn_main_3);
        btn_main_4 = findViewById(R.id.btn_main_4);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter()); //Broadcast Receiver 등록
        getUeiRc();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_main_1.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DeviceInfoActivity.class);
            intent.putExtra("image", "tv");
            startActivity(intent);

        });

        btn_main_2.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                    intent.putExtra("image", "fan");
                    startActivity(intent);
                }
        );

        btn_main_3.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DeviceInfoActivity.class);
            intent.putExtra("image", "air");
            startActivity(intent);
        });

        btn_main_4.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AddDeviceActivity.class));
        });
        IV_add.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AddDeviceActivity.class));
        });

        //bluetooth, stt 화면 이동
        IV_le_stt.setOnClickListener(view -> checkBluetooth());
        IV_le_stt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(MainActivity.this, LeSttActivity.class));
                return true;
            }
        });
    }

    private void getUeiRc() {

        client.getClient(ueiRcURL).getUeiRc("application/json", userUeiRC).enqueue(new Callback<UeiRcModel>() {
            @Override
            public void onResponse(Call<UeiRcModel> call, Response<UeiRcModel> response) {
                logger.LoggerStart(TAG, "getUeiRc()");
                Log.d(TAG, "##REST## getUeiRc Success network");
                String ueiRc = response.body().getUeiRc();
                Log.d(TAG, "##REST## ueiRc : " + ueiRc);

                SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ueiRc", ueiRc);
                editor.commit();
                Log.d(TAG, "##REST## ueiRc editor.commit : " + ueiRc);
                logger.LoggerEnd(TAG, "getUeiRc()");
            }

            @Override
            public void onFailure(Call<UeiRcModel> call, Throwable t) {
                Log.d(TAG, "##REST##getUeiRc False network" + t.toString());
            }
        });

    }
    /**
     * &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& bluetooth connect &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
     */
    private void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){
            Log.d(TAG,"Bluetooth no available");
        }else{
            // 장치가 블루투스 지원하는 경우
            if (!mBluetoothAdapter.isEnabled()) {
                // 블루투스를 지원하지만 비활성 상태인 경우
                // 블루투스를 활성 상태로 바꾸기 위해 사용자 동의 요첨
                Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                // 블루투스를 지원하며 활성 상태인 경우
                // 페어링된 기기 목록을 보여주고 연결할 장치를 선택.
                selectDevice();
            }
        }
    }
    private void selectDevice() {
        // 페어링되었던 기기 목록 획득
        mDevices = mBluetoothAdapter.getBondedDevices();
        //페어링되었던 기기 갯수
        mPairedDeviceCount = mDevices.size();
        //Alertdialog 생성(activity에는 context입력)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("리모컨 선택");

        // 페어링 된 블루투스 장치의 이름 목록 작성
        final List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }

        if(listItems.size() == 0){
            //no bonded device => searching
            Log.d("Bluetooth", "No bonded device");
            // scroll_ble.fullScroll(View.FOCUS_DOWN);
        }else{
            Log.d("Bluetooth", "Find bonded device");

            //scroll_ble.fullScroll(View.FOCUS_DOWN);
            // 취소 항목 추가
            listItems.add("취소");

            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                //각 아이템의 click에 따른 listener를 설정
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Dialog dialog_ = (Dialog) dialog;
                    // 취소
                    if (which == listItems.size()-1) {
                        Toast.makeText(dialog_.getContext(), "Choose cancel", Toast.LENGTH_SHORT).show();
                    } else {
                        String  selectedDeviceName =items[which].toString();
                        mRemoteDevice  = getDeviceFromBondedList(selectedDeviceName);
                        mDeviceName    = mRemoteDevice.getName();
                        mDeviceAddress = mRemoteDevice.getAddress();
                        Log.d(TAG,"Device Name : "+mRemoteDevice.getName()+"   DeviceAddress : "+mRemoteDevice.getAddress());
                        //scroll_ble.fullScroll(View.FOCUS_DOWN);
                        DeviceControl();
                    }
                }
            });

            builder.setCancelable(false);    // 뒤로 가기 버튼 사용 금지
            AlertDialog alert = builder.create();
            alert.show();   //alert 시작
        }
    }
    private void DeviceControl() {
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        boolean isBind = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE); //bindService->Service를 실행하고 결과를 Activity의 UI에 반영해주는 기능, mServiceConnection함수에서 연결 요청결과 값을 받는다.
        //scroll_ble.fullScroll(View.FOCUS_DOWN);
        //gattServiceIntent 기반으로 Service를 실행시키고 요청 하게된다.
        //세번째 인자는 바인딩 옵션을 설정하는 flags를 설정한다.

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothLeService != null) {
                    final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                    Log.d(TAG, "Connect request result=" + result);
                }else{
                    Log.d(TAG, "mBluetoothLeService null");
                }
            }
        },1000);
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() { //서비스가 연결되었을때, 안되었을때 관리
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) { //연결되었을 경우
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            boolean connect = mBluetoothLeService.connect(mDeviceAddress);// 장치 연결
            Log.d(TAG,"connect : "+connect);
            //scroll_ble.fullScroll(View.FOCUS_DOWN);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) { // 연결이 안되었을 경우
            mBluetoothLeService = null;
            Toast.makeText(getApplicationContext(),"연결 실패 다시 시도바랍니다.",Toast.LENGTH_SHORT).show();
        }
    };

    private BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;
        mDevices = mBluetoothAdapter.getBondedDevices();
        //pair 목록에서 해당 이름을 갖는 기기 검색, 찾으면 해당 device 출력
        for (BluetoothDevice device : mDevices) {
            if (name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    // 서비스에서 발생한 다양한 이벤트를 처리
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() { //Broadcast Receiver는 BluetoothLeService로부터 연결상태와 데이터들을 받아오는 역활을 한다.
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // ACTION_GATT_CONNECTED : GATT 서버에 연결되었습니다.
                mConnected = true;
                //updateConnectionState(R.string.connected);
               // invalidateOptionsMenu();
                Log.d(TAG,"GATT service GATT 서버");
               // TV_ble_log.append("[Bluetooth] :  GATT 서버 연결\n");
               // scroll_ble.fullScroll(View.FOCUS_DOWN);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { // ACTION_GATT_DISCONNECTED : GATT 서버에서 연결이 끊어졌습니다.
                mConnected = false;
                //updateConnectionState(R.string.disconnected);
               //invalidateOptionsMenu();
                Log.d(TAG,"GATT service 연결이 끊어졌습니다.");
                Toast.makeText(getApplicationContext(),"블루투스 사전 연결을 확인 바랍니다.",Toast.LENGTH_LONG).show();
              //  TV_ble_log.append("[Bluetooth] :  GATT 서버 끊김\n");
               // scroll_ble.fullScroll(View.FOCUS_DOWN);
                  unbindService(mServiceConnection); //서비스 해제 한다.
               // clearUI();
                mUnregisterReceiver();
                IV_le_stt.setBackground(getDrawable(bluetooth_view));

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {// ACTION_GATT_SERVICES_DISCOVERED : GATT 서비스를 발견했습니다.
                // Show all the supported services and characteristics on the user interface.
               // TV_ble_log.append("[Bluetooth] :  GATT 서버 발견\n");
                //scroll_ble.fullScroll(View.FOCUS_DOWN);
               // displayGattServices(mBluetoothLeService.getSupportedGattServices());
                IV_le_stt.setBackground(getDrawable(bluetooth_view_on));
                Toast.makeText(getApplicationContext(),"연결 성공",Toast.LENGTH_SHORT).show();
                Log.d(TAG,"GATTService ####리모컨과 연결이 완료되었습니다.####");

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { /** ACTION_DATA_AVAILABLE : 기기에서 수신 된 데이터입니다. 이것은 읽기의 결과 일 수 있습니다.**/
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
               // TV_ble_log.append("[Bluetooth] :  GATT 서버 데이터 수신\n");
               // scroll_ble.fullScroll(View.FOCUS_DOWN);
                String resultData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
               // TV_stt_log.append("STT 결과:"+resultData+"\n");
               // scroll_stt.fullScroll(View.FOCUS_DOWN);


            }
        }
    };

    // 지원되는 GATT 서비스 / 특성을 통해 반복하는 방법을 보여줍니다.
    //이 샘플에서는 ExpandableListView에 바인딩 된 데이터 구조를 채 웁니다.
    // UI
/*    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();

        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            Log.d("GATTService / ","displayGattServices::"+uuid);
            TV_ble_log.append("displayGattServices::"+uuid+"\n");

            currentServiceData.put(
                    LIST_NAME, GattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, GattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);

    }*/

    private void displayData(String data) {
        if (data != null) {
            Log.d(TAG,"GATTService receive Data");
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    /**
     * receiver 해체 함수
     */
    private void mUnregisterReceiver() {
        try {
            Log.d("GATT_SERVICES", "mUnregisterReceiver");
            getApplicationContext().unregisterReceiver(mGattUpdateReceiver); //Receiver 해제

        } catch (IllegalArgumentException e){

        } catch (Exception e) {

        }finally {

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        //GATT sever connect check
        if(isMobileServiceRunning()){
            IV_le_stt.setBackground(getDrawable(bluetooth_view_on));
            Log.d(TAG,"bluetooth_view_on");
        }else{
            IV_le_stt.setBackground(getDrawable(bluetooth_view));
            Log.d(TAG,"bluetooth_view_off");
        }
    }

    public boolean isMobileServiceRunning() {
        try{
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo rsi : am.getRunningServices(Integer.MAX_VALUE)) {
                if (BluetoothLeService.class.getName().equals(rsi.service.getClassName()))
                    return true;
            }
        }catch (Exception e){
            Log.d("msg","isMoblicServiceRunning error");
        }
        return false;
    }
}