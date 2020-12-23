package com.remote.remotecontrol.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {


    private List<String> deviceType = new ArrayList<>();
    private List<String> brand = new ArrayList<>();
    private List<String> groupId = new ArrayList<>();
    private List<String> nickName = new ArrayList<>();

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE deviceInfo (devTypeCodes TEXT, brandName TEXT, devGroupId TEXT, nickName TEXT);");
    }

    //DB업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void insert(String devTypeCodes, String brandName, String devGroupId,String nickName){
        //읽고 쓰기가 가능하게 DB열기
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO deviceInfo VALUES('" + devTypeCodes + "', '" + brandName + "', '" + devGroupId + "', '" + nickName + "');");

    }
    public void update(String nickName,String change){
        //입력한 항목과 일치하는 행의 별칭정보 수정
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE deviceInfo SET nickName ='"+change+"'WHERE nickName='" + nickName + "';");
        db.close();
    }
    public void delete(String nickName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM deviceInfo WHERE nickName='"+nickName+"';");
        db.close();
    }
    public String  getResult(){
        //nickName 만 추출
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        //DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT nickName FROM deviceInfo",null);
        while(cursor.moveToNext()){
            result += cursor.getString(0)
                    + "\n";
        }
        return result;
    }
    public String  getInfo(int position){
        //nickName 만 추출
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        int count = 0;
        //DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM deviceInfo",null);
        while(cursor.moveToNext()){
            if(position == count){
                result += cursor.getString(0)
                        + "/"
                        + cursor.getString(1)
                        + "/"
                        + cursor.getString(2)
                        + "/"
                        + cursor.getString(3);
                Log.d("TAG","cursorExecution");
            }
                count++;


        }
        return result;
    }

    public String  getGroupId(){

        //nickName 만 추출
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        //DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT devGroupId FROM deviceInfo",null);
        while(cursor.moveToNext()){
            result += cursor.getString(0)
                    + "\n";
        }
        return result;
    }



}
