package com.bangida.bangidaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bangida.bangidaapp.UtilsService.SharedPreferenceClass;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    SharedPreferenceClass sharedPreferenceClass;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    // ViewPager mViewPager;

    // menu에 사용자 이름과 이메일 불러오기
    String token;

    ImageButton receive_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 동물 정보 생성창으로 이동
        ImageButton add_task_btn = (ImageButton)findViewById(R.id.add_task_btn);
        add_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteAnimalActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 로그인한 정보 폰에 저장
        sharedPreferenceClass = new SharedPreferenceClass(this);

        // MainActivity
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        receive_token = (ImageButton) findViewById(R.id.receive_token);

        token = sharedPreferenceClass.getValue_string("token");


        // error
//        setSupportActionBar(toolbar);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@Nullable MenuItem item) {
                setDrawerClick(item.getItemId());
                item.setChecked(true);

                drawerLayout.closeDrawers();
                return true;
            }
        });

        // 받은 토큰을 복사해 봐서 입력하기 위한 dialog 생성
        receive_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putShareTokenDialog();
            }
        });
        
        // 옆에서 서랍처럼 메뉴가 나타나게 하는 함수
        initDrawer();
    }

    // 초대받은 token을 입력하기 위한 dialog 함수
    private void putShareTokenDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_share_token, null);

        final EditText share_token_field = alertLayout.findViewById(R.id.share_token);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(alertLayout)
                .setTitle("초대받은 토큰을 입력해주세요")
                .setPositiveButton("입장", null)
                .setNegativeButton("취소", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog)alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String rtoken = share_token_field.getText().toString();

                        updateToken(rtoken);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();

    }

    // 입장 버튼을 누르면 초대받은 방 생성
    private void updateToken(String rtoken) {
        sharedPreferenceClass.clear();
        String token = rtoken;
        sharedPreferenceClass.setValue_string("token", token);
        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
        finish();
    }

    // 메뉴가 옆에서 서랍처럼 나오게 함
    private void initDrawer() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, new RoomFragment());
        ft.commit();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setDrawerClick(int itemId) {
        switch (itemId) {
            case R.id.action_room:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new RoomFragment()).commit();
                break;
            case R.id.action_logout:
                // 폰에 저장했던 로그인 정보 삭제
                sharedPreferenceClass.clear();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }
}
