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

    public static String UUID_SERVICE       = "ab5e0001-5a21-4f05-bc7d-af01f617b664"; // Service
    public static String UUID_TX            = "ab5e0002-5a21-4f05-bc7d-af01f617b664"; // Write
    public static String UUID_RX            = "ab5e0003-5a21-4f05-bc7d-af01f617b664"; // Read
    public static String UUID_CTL           = "ab5e0004-5a21-4f05-bc7d-af01f617b664"; // Control
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"; //클라이언트 특성구성

    static {

        attributes.put("ab5e0001-5a21-4f05-bc7d-af01f617b664", "ATV Voice Service");
        attributes.put("ab5e0002-5a21-4f05-bc7d-af01f617b664", "Write Characteristic");
        attributes.put("ab5e0003-5a21-4f05-bc7d-af01f617b664", "Read Characteristic");
        attributes.put("ab5e0004-5a21-4f05-bc7d-af01f617b664", "Control Characteristic");

        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
    }

    public static String lookup(String uuid, String defaultName) { //조회 함수
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
