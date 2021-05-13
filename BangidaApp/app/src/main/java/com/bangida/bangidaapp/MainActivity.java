package com.bangida.bangidaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bangida.bangidaapp.UtilsService.SharedPreferenceClass;

public class MainActivity extends AppCompatActivity {

    Button logout;
    SharedPreferenceClass sharedPreferenceClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.logout);
        sharedPreferenceClass = new SharedPreferenceClass(this);

        // 로그아웃 버튼 클릭시 token을 삭제하고, LoginActiivity로 이동
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferenceClass.clear();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}