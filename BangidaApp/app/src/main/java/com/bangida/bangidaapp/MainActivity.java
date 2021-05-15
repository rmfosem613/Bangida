package com.bangida.bangidaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.bangida.bangidaapp.UtilsService.SharedPreferenceClass;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    SharedPreferenceClass sharedPreferenceClass;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 동물 정보 생성창으로 이동
        Button add_task_btn = (Button)findViewById(R.id.add_task_btn);
        add_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WriteAnimalActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 프래그먼트 테스트용 버튼
        Button testFrag = (Button)findViewById(R.id.test_fragment);
        testFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myintent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(myintent);
                finish();
            }
        });

        // 로그인한 정보 폰에 저장
        sharedPreferenceClass = new SharedPreferenceClass(this);

        // MainActivity
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

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
        
        // 옆에서 서랍처럼 메뉴가 나타나게 하는 함수
        initDrawer();

    }

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
            case R.id.action_finished_task:
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new FinishedTaskFragment()).commit();
                break;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@Nullable MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
