/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.remote.remotecontrol.activity.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_SERVICE                 =  UUID.fromString(GattAttributes.UUID_SERVICE);
    public final static UUID UUID_TX                      =  UUID.fromString(GattAttributes.UUID_TX);
    public final static UUID UUID_RX                      =  UUID.fromString(GattAttributes.UUID_RX);
    public final static UUID UUID_CTL                     =  UUID.fromString(GattAttributes.UUID_CTL);
    public final static UUID CLIENT_CHARACTERISTIC_CONFIG =  UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG); //Descriptor


    private StringBuffer stb = new StringBuffer();
    private String buffer;

    private int offset = 5;
    //App 이 갖는 GATT 이벤트에 대한 콜백 메서드를 구현합니다.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {//연결 상태가 바뀌면 호출됨
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        /**
         * allback invoked when the list of remote services, characteristics and descriptors
         * setCharacteristicNotification:: Read/Write/Notification 권한 설정
          */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) { //BLE 장치에서 GATT 서비스들이 발견되면 호출된다.
            if (status == BluetoothGatt.GATT_SUCCESS) { //gatt 서비스들을 이용가능한지 확인 할 수 있다.
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

                BluetoothGattService openService        = gatt.getService(UUID_SERVICE);
                BluetoothGattCharacteristic openTxChar  = openService.getCharacteristic(UUID.fromString("ab5e0004-5a21-4f05-bc7d-af01f617b664")); // 데이터 수신 UUID
                boolean notiResult = gatt.setCharacteristicNotification(openTxChar, true); // Notification 활성화 및 데이터 receive ::
                //boolean readResult = mBluetoothGatt.readCharacteristic(openTxChar);
               // Log.d("GATTService", "Characteristic Read : " + readResult);
                Log.d("GATTService", "Characteristic Noti : " + notiResult);

                BluetoothGattDescriptor descriptor = openTxChar.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG); //데이터 수신 UUID 에 특성을 설명하는 메타데이타
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); // write했다가 read 할 떄 데이터를 읽기 위한 작업 그래서 descriptor가 필요
                boolean success = mBluetoothGatt.writeDescriptor(descriptor); //변경된 UUID descriptor // 설명된 데이타를 Gatt서버에 write한다.
                Log.d("GATTService","writeDescriptor:"+success);

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,int status) { ////characteristic를 읽는데 성공하면 호출
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        /**
         * BLE 장치간 데이터 전송 (사전에 Notification으로 권한 설정을 해주어야한다.)
         * @param gatt
         * @param characteristic
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {//특성의 값이 바뀔 때
            Log.d("GATTService / ", "onCharacteristicChanged::" + characteristic.getValue());
            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);

            handler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {

                    if(characteristic.getUuid().equals(UUID_CTL)){
                        Log.d("GATTService ", "Characteristic Noti UUID : " + characteristic.getUuid());
                        Log.d("GATTService ", "Characteristic Noti Value (Byte) : " + Util.byteArrayToHex(characteristic.getValue()));
                        Log.d("GATTService ", "Characteristic Noti Value (String) : " + new String(characteristic.getValue(), StandardCharsets.UTF_8));
                        byte [] searchData = characteristic.getValue();
                        String firstData   = String.valueOf(searchData[0]);

                        if (firstData.equals("8")){ //Master >> searchData
                            Log.d("GATTService ", "getSearchData" );

                            BluetoothGattService openService     = gatt.getService(UUID_SERVICE);
                            BluetoothGattCharacteristic openMic  = openService.getCharacteristic(UUID_TX);

                            byte data[] = Util.hexStringToByteArray("0C00000000000000000000000000000000000000"); // open Mic
                            Log.d("GATTService ", "Mic_open : " + Util.byteArrayToHex(data));
                            Log.d(TAG, "GATTService " + writeCharacteristic(openMic, data));
                        }else if(firstData.equals("4")) {
                            Log.d("GATTService ", "Audio_start" );

                            BluetoothGattService openService        = gatt.getService(UUID_SERVICE);
                            BluetoothGattCharacteristic openTxChar  = openService.getCharacteristic(UUID_RX); // 데이터 수신 UUID
                            boolean notiResult = gatt.setCharacteristicNotification(openTxChar, true); // Notification 활성화 및 데이터 receive ::
                            //boolean readResult = mBluetoothGatt.readCharacteristic(openTxChar);
                            // Log.d("GATTService", "Characteristic Read : " + readResult);
                            Log.d("GATTService", "Characteristic Noti : " + notiResult);

                            BluetoothGattDescriptor descriptor = openTxChar.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG); //데이터 수신 UUID 에 특성을 설명하는 메타데이타
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); // write했다가 read 할 떄 데이터를 읽기 위한 작업 그래서 descriptor가 필요
                            boolean success = mBluetoothGatt.writeDescriptor(descriptor); //변경된 UUID descriptor // 설명된 데이타를 Gatt서버에 write한다.
                            Log.d("GATTService","writeDescriptor:"+success);
                        }
                        else if(firstData.equals("C")){
                            Log.d("TAG","GATTService: Mic_open_error ");
                        }else{
                            Log.d("TAG","GATTService: default");
                        }
                    }else  if(characteristic.getUuid().equals(UUID_RX)){
                        Log.d("GATTService ", "Audio_data" );

                        Log.d("GATTService ", "Characteristic Noti UUID : " + characteristic.getUuid());
                     Log.d("GATTService ", "Characteristic Noti Value (Byte) : " + Util.byteArrayToHex(characteristic.getValue()));
                      /*  byte[]  getData = Util.getByte(characteristic.getValue());
                        InputStream targetStream = new ByteArrayInputStream(getData);

                        DataOutputStream output = null;*/



                        /*String fileName = "test.txt";
                        File file = new File("/data/data/com.remote.remotecontrol/cache/"+fileName);
                        AudioFormat format = new AudioFormat(8000, 16, 1, true, false);
                        AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(
                                decodedData), format, decodedData.length), AudioFileFormat.Type.WAVE, outFile);
                        FileOutputStream fos = null;
                        try{
                            fos = new FileOutputStream(file);
                            fos.write(getData);
                            fos.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        //Log.d("GATTService ", "Characteristic getData = Util (Byte) : " + Util.byteArrayToHex(getData));

                       // Util.byteArrayToHex(ADPCMDecoder.decodeBlock(getData,1));
                    }

                /*    if(firstData.equals("8")){
                        Log.d("GATTService /", "success!" );
                    }*/

                    intent.putExtra(EXTRA_DATA, "onCharacteristicChanged Data OK...");
                    sendBroadcast(intent);

                    /*//openMic
                    BluetoothGattService openService        = gatt.getService(UUID_SERVICE);
                    BluetoothGattCharacteristic openMic  = openService.getCharacteristic(UUID.fromString("ab5e0002-5a21-4f05-bc7d-af01f617b664")); // 데이터 수신 UUID

                    byte data[] = Util.hexStringToByteArray("0C00000000000000000000000000000000000000");
                    Log.d(TAG, "GATT_SERVICES = " + writeCharacteristic(openMic, data));*/
                }

            });
        }


    };


    private void broadcastUpdate(final String action) { // 인자가 하나인 action DeviceControlActivity로 Message 날린다.
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) { //BLE 장치로부터 받아오 데이터를 DeviceControlActivity로 넘겨주기 위해 이 함수를 사용한다.
        final Intent intent = new Intent(action);

       /* // 심박수 측정 프로필에 대한 특수 처리입니다. 데이터 파싱은
        // 프로필 사양에 따라 수행 :
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) { // GATT를 사용하는 장치와 연결되었을 때 심박수를 받아오도록 설정되어 있다.
            int flag = characteristic.getProperties();
            int format = -1;

            if ((flag & 0x01) != 0) { //16진수인지 8진수인지 확인
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }

            final int heartRate = characteristic.getIntValue(format,1 );// 수집된 심박동데이터 로직

            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate)); //intent를 통해 심박동 데이터를 보내준다.

        } else {
            //다른 모든 프로필의 경우 HEX 형식의 데이터를 씁니다.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);*/
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    } //DeviceControlActivity에서 BindService로 서비스를 연결하면 onBind 함수가 호출

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) { //null 검사
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection."); //연결을 위해 기존 Gatt사용 시도
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback); //디바이스와 연결 시도
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;

        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) { //연결된BLE 장치의 특성을 읽어오라는 명령을 내린다.
        Log.d(TAG, "Call readCharacteristic");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     *  제공 특성에 대한 알림을 활성화하거나 비활성화
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, //BLE 장치가 데이터를 보낼 때를 기다려, 보내면 받아오도록 리스너를 설정한다.
                                              boolean enabled) {


        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);/*ENABLE_NOTIFICATION_VALUE*/
        //if (UUID_URINE_ANALYZER.equals(characteristic.getUuid())) {

            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG); //데이터 수신 UUID 에 특성을 설명하는 메타데이타
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); // write했다가 read 할 떄 데이터를 읽기 위한 작업 그래서 descriptor가 필요
            mBluetoothGatt.writeDescriptor(descriptor); //변경된 UUID descriptor // 설명된 데이타를 Gatt서버에 write한다.

            Log.d(TAG, "Service Notification Execution");
        //}

    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() { //BLE 장치에서 제공되는 서비스들을 받아올수 있도록 해준다.
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    /**
     * 연결된 디바이스에 데이터 쓰기
     * @param characteristic
     * @param data
     * @return
     */
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoohtAdapter not initialized");
            return false;
        }
        characteristic.setValue(data);
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }



}
