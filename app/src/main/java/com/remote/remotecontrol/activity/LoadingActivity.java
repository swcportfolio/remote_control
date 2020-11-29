package com.remote.remotecontrol.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.remote.remotecontrol.R;


/**
 *  앱 초기화작업 하는 로딩 클래스
 *
 * @author 박상원
 * @version 1.1
 * @see <pre>
 * << 개정이력(Modification Information) >>
 *
 *      수정일		   수정자              수정내용
 *  --------------   ---------    ---------------------------
 *   2020. 11. 23     박상원              최초 생성
 *
 * </pre>
 * @since 2020. 11.23
 */

public class LoadingActivity extends AppCompatActivity {


    /**
     * Application 첫번째 호출 함수, application 시작 로직 실행,초기화 작업
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_);


        startLoading();// 로딩 함수
    }

    /**
     * Application 회원여부확인 및 초기화 함수
     */
    private void startLoading() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            /**
             * handler 실행함수
             */
            @Override
            public void run() {
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);  //0.5초 delay
    }

}