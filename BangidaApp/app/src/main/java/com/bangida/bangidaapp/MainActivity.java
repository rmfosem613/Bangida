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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    TextView username_test;

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

        // 로그인한 정보 폰에 저장
        sharedPreferenceClass = new SharedPreferenceClass(this);

        // MainActivity
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        username_test = (TextView) findViewById(R.id.username_test);

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
        
        // 옆에서 서랍처럼 메뉴가 나타나게 하는 함수
        initDrawer();

        getMenu();

    }
    // menu에 user 정보 가져오기
    private void getMenu() {
        // userInfoList = new ArrayList<>();
        String url = "https://bangidaapp.herokuapp.com/api/bangida/auth";

        // Get 방식으로 데이터를 요청
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // 서버 응답값 id, username, email 값 받아옴.
                    // success는 변수이름이고, boolean값인 true | false 값을 가짐.
                    if(response.getBoolean("success")) {
                        // 배열에서 가지고 오는 것
                        //JSONObject jsonObject = response.getJSONArray("user").getJSONObject(0);

                        String username = response.getString("username");
                        // username 받아오는 거 확인함
                        Log.i("username",username);

                        username_test.setText(username);

                        // username과 email을 받아오지 못함
                        Toast.makeText(MainActivity.this, username, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) { // 예외 : 정상적인 처리를 벗어나는 경우
                    e.printStackTrace(); // getMessage, toSting과 다르게 리턴값이 없음.
                    }
                }
            }, new Response.ErrorListener() {
                // Volley는 안드로이드 앱의 네트워킹을 더 쉽고, 무엇보다도 더 빠르게 하는 HTTP 라이브러리이다.
                @Override
                // Volley 사용시 에러에 대한 응답 클래스
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    NetworkResponse response_ob = error.networkResponse;
                    // instanceof : 객체타입을 확인
                    if(error instanceof ServerError && response_ob != null) {
                        try {
                            // 긁어온 String을 서버로 보냄.
                            String res = new String(response_ob.data, HttpHeaderParser.parseCharset(response_ob.headers, "utf-8"));

                            JSONObject obj = new JSONObject(res);

                        } catch (JSONException | UnsupportedEncodingException je){ // 예외 경우
                            je.printStackTrace(); // return값 없음.
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    // 서버에 요청할 때 Headers 정보.
                    // "key": value
                    headers.put("Authorization", token);

                    // id, username, email 값을 리턴
                    return headers;
                }
            };

            // 서버의 응답이 오랫동안 없을 때 재시도
            // socket 통신 제한시간 30초
            int socketTime = 3000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsonObjectRequest.setRetryPolicy(policy);

            // Volley를 requestQueue에 대입
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            // 데이터를 파싱.
            requestQueue.add(jsonObjectRequest);
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
