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

import java.util.HashMap;

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

    public static String UUID_SERVICE       = "ab5e0001-5a21-4f05-bc7d-af01f617b664"; //Service
    public static String UUID_URINE_ANALYZER = "0783b03e-8535-b5a0-7140-a304d2495cba"; //Write UUID
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"; //클라이언트 특성구성*/

    static {

        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service"); // 심박수 서비스 장치 정보 서비스
        // Sample Characteristics.
        //attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");// 심박수 측정
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");//제조업체 이름 문자열
    }

    public static String lookup(String uuid, String defaultName) { //조회 함수
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
