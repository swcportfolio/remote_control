package com.remote.remotecontrol.activity.ble;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
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
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.remote.remotecontrol.R;
import com.remote.remotecontrol.retrofit.Convert;
import com.remote.remotecontrol.retrofit.ConvertIO;
import com.remote.remotecontrol.retrofit.RetrofitClient;
import com.remote.remotecontrol.retrofit.RetrofitInterface;
import com.remote.remotecontrol.retrofit.STTModel;
import com.remote.remotecontrol.retrofit.SpeechModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class LeSttActivity extends AppCompatActivity {

    private final String TAG = LeSttActivity.class.getSimpleName();

    private Button btn_ble_search,btn_ble_connect,btn_data;
    private TextView device_address,device_name,mConnectionState,mDataField, TV_ble_log,TV_stt_log;
    private ScrollView scroll_ble,scroll_all,scroll_stt;

    private ImageView btn_stt;
    private Set<BluetoothDevice> mDevices;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mRemoteDevice;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private ExpandableListView mGattServicesList;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private String mDeviceName,mDeviceAddress ;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private final String clientId     = "629fhvdo3y";                                    // Application Client ID";
    private final String clientSecret = "4h6uYJg24jBte1fEqvv13SGs9WVYmrFBb9yHFmTq";     // Application Client Secret";
    private String json = null;
    private String result;

    private boolean mConnected = false;
    private String URL ="http://106.251.70.71:50010/ws/file/";

    private static final int REQUEST_ENABLE_BT = 1 ;
    int mPairedDeviceCount;
    private int SPEECH_TO_TEXT =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_stt);

        //Speech to text버튼
        //btn_stt = findViewById(R.id.btn_stt);

        //ScrollView
        scroll_ble = findViewById(R.id.scroll_ble);
        scroll_all = findViewById(R.id.scroll_all);
        scroll_stt = findViewById(R.id.scroll_stt);

        // Sets up UI references.
        TV_ble_log = findViewById(R.id.TV_ble_log);
        TV_stt_log = findViewById(R.id.TV_stt_log);

        device_address = findViewById(R.id.device_address);
        device_name = findViewById(R.id.device_name);

        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress); // 주소
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state); //연결 상태 표시
        mDataField = (TextView) findViewById(R.id.data_value);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter()); //Broadcast Receiver 등록

        /*  getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);//액션바의 앱 아이콘 옆에 화살표를 만들어 전의 액티비티로 돌아가는 기능
        */

        //Button
        btn_ble_search = findViewById(R.id.btn_ble_search);
       // btn_ble_connect = findViewById(R.id.btn_ble_connect);
        //btn_data = findViewById(R.id.btn_data);
        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true);   // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_back);

        //BLE LOG 이중 스크롤 이벤트 처리
        scroll_ble.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    scroll_all.requestDisallowInterceptTouchEvent(false);
                }
                else {
                    scroll_all.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });

       /* //STT LOG 이중 스크롤 이벤트 처리
        scroll_stt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    scroll_all.requestDisallowInterceptTouchEvent(false);
                }
                else {
                    scroll_all.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_ble_search.setOnClickListener(v -> checkBluetooth());

        //stt 버튼
      /*  btn_stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TV_stt_log.append("STT: mp3 파일 전송 시작\n");
                scroll_stt.fullScroll(View.FOCUS_DOWN);
                SpeechToText();
            }
        });*/

      /*  btn_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
    }

    private void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null){
            Log.d(TAG,"Bluetooth no available");
            TV_ble_log.append("[Bluetooth] : no available..\n");
            scroll_ble.fullScroll(View.FOCUS_DOWN);
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
        builder.setTitle("Bonded Device");
        TV_ble_log.append("[Bluetooth] : Bonded Device..\n");
        scroll_ble.fullScroll(View.FOCUS_DOWN);

        // 페어링 된 블루투스 장치의 이름 목록 작성
        final List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }

        if(listItems.size() == 0){
            //no bonded device => searching
            Log.d("Bluetooth", "No bonded device");
            TV_ble_log.append("[Bluetooth] : No bonded device\n");
           // scroll_ble.fullScroll(View.FOCUS_DOWN);
        }else{
            Log.d("Bluetooth", "Find bonded device");
            TV_ble_log.append("[Bluetooth] : Find bonded device\n");

            //scroll_ble.fullScroll(View.FOCUS_DOWN);
            // 취소 항목 추가
            listItems.add("Cancel");

            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                //각 아이템의 click에 따른 listener를 설정
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Dialog dialog_ = (Dialog) dialog;
                    // 취소
                    if (which == listItems.size()-1) {
                        Toast.makeText(dialog_.getContext(), "Choose cancel", Toast.LENGTH_SHORT).show();
                        TV_ble_log.append("[Bluetooth] : Choose cancel\n");
                        scroll_ble.fullScroll(View.FOCUS_DOWN);
                    } else {
                        String  selectedDeviceName =items[which].toString();
                        mRemoteDevice  = getDeviceFromBondedList(selectedDeviceName);
                        mDeviceName    = mRemoteDevice.getName();
                        mDeviceAddress = mRemoteDevice.getAddress();
                        Log.d(TAG,"Device Name : "+mRemoteDevice.getName()+"   DeviceAddress : "+mRemoteDevice.getAddress());
                        TV_ble_log.append("[Bluetooth] :Device Name :"+mDeviceName+"+DeviceAddress : +"+mDeviceAddress+"\n");
                        //scroll_ble.fullScroll(View.FOCUS_DOWN);
                        device_name.setText(mDeviceName);
                        device_address.setText(mDeviceAddress);
                        DeviceControl();
                    }
                }
            });

            builder.setCancelable(false);    // 뒤로 가기 버튼 사용 금지
            AlertDialog alert = builder.create();
            alert.show();   //alert 시작
        }
    }


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



    private void DeviceControl() {
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        boolean isBind = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE); //bindService->Service를 실행하고 결과를 Activity의 UI에 반영해주는 기능, mServiceConnection함수에서 연결 요청결과 값을 받는다.
        TV_ble_log.append("[Bluetooth] : bindService "+isBind+"\n");
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
                        scroll_ble.fullScroll(View.FOCUS_DOWN);
                    }else{
                        Log.d(TAG, "mBluetoothLeService null");
                        scroll_ble.fullScroll(View.FOCUS_DOWN);
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
                TV_ble_log.append("[Bluetooth] : Unable to initialize Bluetooth\n");
                scroll_ble.fullScroll(View.FOCUS_DOWN);
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            boolean connect = mBluetoothLeService.connect(mDeviceAddress);// 장치 연결
            Log.d(TAG,"connect : "+connect);
            TV_ble_log.append("[Bluetooth] : BluetoothLeService : "+connect+"\n");
            //scroll_ble.fullScroll(View.FOCUS_DOWN);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) { // 연결이 안되었을 경우
            mBluetoothLeService = null;
            TV_ble_log.append("[Bluetooth] : BluetoothLeService : false\n");
            scroll_ble.fullScroll(View.FOCUS_DOWN);
        }
    };
    // 서비스에서 발생한 다양한 이벤트를 처리
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() { //Broadcast Receiver는 BluetoothLeService로부터 연결상태와 데이터들을 받아오는 역활을 한다.
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) { // ACTION_GATT_CONNECTED : GATT 서버에 연결되었습니다.
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
                Log.d(TAG,"GATT service GATT 서버");
                TV_ble_log.append("[Bluetooth] :  GATT 서버 연결\n");
                scroll_ble.fullScroll(View.FOCUS_DOWN);

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) { // ACTION_GATT_DISCONNECTED : GATT 서버에서 연결이 끊어졌습니다.
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                Log.d(TAG,"GATT service 연결이 끊어졌습니다.");
                TV_ble_log.append("[Bluetooth] :  GATT 서버 끊김\n");
                scroll_ble.fullScroll(View.FOCUS_DOWN);
             //   unbindService(mServiceConnection); //서비스 해제 한다.
                clearUI();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {// ACTION_GATT_SERVICES_DISCOVERED : GATT 서비스를 발견했습니다.
                // Show all the supported services and characteristics on the user interface.
                TV_ble_log.append("[Bluetooth] :  GATT 서버 발견\n");
                 scroll_ble.fullScroll(View.FOCUS_DOWN);
                displayGattServices(mBluetoothLeService.getSupportedGattServices());

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { /** ACTION_DATA_AVAILABLE : 기기에서 수신 된 데이터입니다. 이것은 읽기의 결과 일 수 있습니다.**/
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                TV_ble_log.append("[Bluetooth] :  GATT 서버 데이터 수신\n");
                scroll_ble.fullScroll(View.FOCUS_DOWN);
                String resultData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                TV_stt_log.append("STT 결과:"+resultData+"\n");
                scroll_stt.fullScroll(View.FOCUS_DOWN);


            }
        }
    };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }
    // 주어진 GATT 특성이 선택되면 지원되는 기능을 확인
    // '읽기'및 '알림'기능을 보여줍니다. 보다
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html 전체
    // 지원되는 특징의 목록입니다.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {

                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);

                        final int charaProp = characteristic.getProperties();

             /*           Log.d("GATTService / ","UUID::"+characteristic.getUuid());
                        Log.d("GATTService / ","Value::"+characteristic.getValue());
                        Log.d("GATTService / ","Descriptor::"+characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")));
                        Log.d("GATTService / ","Properties::"+characteristic.getProperties());*/

                        TV_ble_log.append("UUID::"+characteristic.getUuid()+"\n");
                        TV_ble_log.append("Value::"+characteristic.getValue()+"\n");
                        TV_ble_log.append("Descriptor::"+characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))+"\n");
                        TV_ble_log.append("Properties::"+characteristic.getProperties()+"\n");


                       /* byte data2[] = hexStringToByteArray("2554530a");

                        Log.d(TAG, "디버깅 데이터 = " + mBluetoothLeService.writeCharacteristic(characteristic, data2));
                        Log.d(TAG, "디버깅 WriteCharacteristic");*/
                        Log.d(TAG, "readCharacteristic Execution: " + characteristic);

                        /*mBluetoothLeService.readCharacteristic(characteristic);
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                        }*/
                        return true;
                    }
                    return false;
                }
            };
    // 지원되는 GATT 서비스 / 특성을 통해 반복하는 방법을 보여줍니다.
    //이 샘플에서는 ExpandableListView에 바인딩 된 데이터 구조를 채 웁니다.
    // UI
    private void displayGattServices(List<BluetoothGattService> gattServices) {
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
        TV_ble_log.append("####리모컨과 연결이 완료되었습니다.####");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUnregisterReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     /*   try {

            mBluetoothLeService = null;
        } catch (IllegalArgumentException e){

        } catch (Exception e) {

        }finally {

        }*/

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
}