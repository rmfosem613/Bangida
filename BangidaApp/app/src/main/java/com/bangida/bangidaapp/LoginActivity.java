package com.bangida.bangidaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.bangida.bangidaapp.UtilsService.UtilService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private ImageButton loginBtn, registerBtn;
    private EditText email_ET, pw_ET;
    ProgressBar progressBar;
    private String email, password;
    
    // 사용자 편의를 위해 사용
    UtilService utilService;
    // 로그인 정보를 폰의 저장공간에 저장
    SharedPreferenceClass sharedPreferenceClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginBtn);
        email_ET = findViewById(R.id.email_ET);
        pw_ET = findViewById(R.id.pw_ET);
        progressBar = findViewById(R.id.progress_bar);
        registerBtn = findViewById(R.id.registerBtn);
        utilService = new UtilService();

        sharedPreferenceClass = new SharedPreferenceClass(this);

        // 회원가입 창으로 이동
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼을 누르면 키보드가 사라지고, 이메일과 비밀번호 텍스트를 긁어와 서버로 보냄.
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilService.hideKeyboard(v, LoginActivity.this);
                email = email_ET.getText().toString();
                password = pw_ET.getText().toString();

                // 176번째 코드
                // EditText가 확인되면(true) 사용자 확인 진행
                if(validate(v)) {
                    loginUser(v);
                }
            }
        });
    }

    // 사용자 확인
    private void loginUser(View view) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        // 디비에서 email, password 항목에 대한 값을 json형식으로 확인
        params.put("email",email);
        params.put("password",password);

        // 로그인을 확인하는 주소
        String apiKey = "https://bangidaapp.herokuapp.com/api/bangida/auth/login";

        // POST 방식으로 데이터를 요청
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                apiKey, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // 서버 응답값 success, msg, toast, user(_id, username, email, password)
                    // success는 변수이름이고, boolean값인 true | false 값을 가짐.
                    if(response.getBoolean("success")) {

                        // token정보 : success, token, user(_id, username, email)값
                        String token = response.getString("token");
                        // String username = response.getString("username");

                        // token 정보를 폰에 저장
                        sharedPreferenceClass.setValue_string("token", token);
                        Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();

                        // 로그인 되면 MainActivity로 넘어감.
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    // MainActivity로 넘어가면 progressBar 사라짐.
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) { // 예외 : 정상적인 처리를 벗어나는 경우
                    e.printStackTrace(); // getMessage, toSting과 다르게 리턴값이 없음.
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            // Volley는 안드로이드 앱의 네트워킹을 더 쉽고, 무엇보다도 더 빠르게 하는 HTTP 라이브러리이다.
            @Override
            // Volley 사용시 에러에 대한 응답 클래스
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                // instanceof : 객체타입을 확인
                if(error instanceof ServerError && response != null) {
                    try {
                        // 긁어온 String을 서버로 보냄.
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                        JSONObject obj = new JSONObject(res);
                        // 로그인 정보가 맞지 않으면 "User not exists..." 메시지를 띄움.
                        Toast.makeText(LoginActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException | UnsupportedEncodingException je){ // 예외 경우
                        je.printStackTrace(); // return값 없음.
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // 서버에 요청할 때 Headers 정보.
                // key : Content-Type, value : application/json
                headers.put("Connect-Type", "application/json");

                // success, msg, token, user(_id, username, email, password)값을 return
                return params;
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

    // EditText 확인
    // 시간이 되면 email 표준을 잘 지켰는지에 대한 코드를 추가하면 좋을듯
    public boolean validate(View view) {
        boolean isValid;
        
            // email, password 값을 toString으로 긁어올 수 있다면 로그인 진행
            if(!TextUtils.isEmpty(email)) {
                if(!TextUtils.isEmpty(password)) {
                    isValid = true;
                } else {
                    // 텍스트를 긁어올게 없다면 msg를 보이게함.
                    utilService.showSnackBar(view, "please enter password...");
                    isValid = false;
                }
            } else {
                utilService.showSnackBar(view, "please enter email...");
                isValid = false;
            }
        return isValid;
    }

    // 자동 로그인
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences todo_pref = getSharedPreferences("user_todo", MODE_PRIVATE);
        if(todo_pref.contains("token")) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}