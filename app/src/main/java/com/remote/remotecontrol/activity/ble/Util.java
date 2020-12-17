package com.remote.remotecontrol.activity.ble;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Util {
    // private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static int count = 1;
    public static int dataLength = 134;
    public static ArrayList<Byte> alldata = new ArrayList<>();
    public static int bp = 128;

    public static void Initilization(){
        alldata.clear();
    }
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static String byteArrayToHex(byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        for (final byte b : bytes) {
            if (!sb.toString().equalsIgnoreCase("[")) {
                sb.append("");
            }
            sb.append(String.format("%02x", b & 0xff));
        }
        sb.append("]");

        return sb.toString();
    }

    public static byte[] getByte(byte[] bytes) {

        // 헤더 6byte 제거
        byte[] pcmData = new byte[128];
        int j =0;

        for(int i = 6 ;i<bytes.length;i++){
          //  pcmData[j] = bytes[i];
            alldata.add(pcmData[j]= bytes[i]);
            j++;
        }

        return pcmData;
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] data = new byte[s.length() / 2];

        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }
    public static ArrayList<Byte> getAllData (){
        return alldata;
    }
    public static byte[] arrToByte(List<Byte> arr){
        byte [] toByte = new byte[arr.size()];
        int  i = 0;

       for(Byte b : arr){
           toByte[i] = b;
           i++;
       }
        return toByte;
    }
}
