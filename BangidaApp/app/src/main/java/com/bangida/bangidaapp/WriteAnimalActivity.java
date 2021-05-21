package com.bangida.bangidaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class WriteAnimalActivity extends AppCompatActivity {

    private EditText petname_ET,breed_ET, etc_ET;
    private Button add_task_btn;
    SharedPreferenceClass sharedPreferenceClass, animalsharedPreferenceClass;
    String token;

    ImageView back_btn;

    TextView textYear, textMonth, textDay;
    NumberPicker pickerYear, pickerMonth, pickerDay;


    private String petname, breed, birth, etc;

    String bYear, bMonth, bDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_animal);

        petname_ET = findViewById(R.id.petname_ET);
        breed_ET = findViewById(R.id.breed_ET);
        etc_ET = findViewById(R.id.etc_ET);
        back_btn = findViewById(R.id.back_room);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WriteAnimalActivity.this, MainActivity.class);
                //액티비티 스택제거
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // Number Picker 사용
        textYear = findViewById(R.id.textpicker_y);
        textMonth = findViewById(R.id.textpicker_m);
        textDay = findViewById(R.id.textpicker_d);
        pickerYear = findViewById(R.id.birth_y);
        pickerMonth = findViewById(R.id.birth_m);
        pickerDay = findViewById(R.id.birth_d);

        pickerYear.setMaxValue(2021);
        pickerYear.setMinValue(1971);
        pickerYear.setValue(2021);
        pickerYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int picked = newVal-1;
                textYear.setText(""+picked);
                bYear = textYear.getText().toString();
            }
        });

        pickerMonth.setMaxValue(12);
        pickerMonth.setMinValue(1);
        pickerMonth.setValue(5);
        pickerMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int picked = newVal-1;
                textMonth.setText(""+picked);
                bMonth = textMonth.getText().toString();
            }
        });

        pickerDay.setMaxValue(31);
        pickerDay.setMinValue(1);
        pickerDay.setValue(1);
        pickerDay.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int picked = newVal-1;
                textDay.setText(""+picked);
                bDay = textDay.getText().toString();
            }
        });

        add_task_btn = findViewById(R.id.add_task_btn);

        sharedPreferenceClass = new SharedPreferenceClass(this);
        token = sharedPreferenceClass.getValue_string("token");

        animalsharedPreferenceClass = new SharedPreferenceClass(this);

        add_task_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                petname = petname_ET.getText().toString();
                breed = breed_ET.getText().toString();
                etc = etc_ET.getText().toString();
                birth = bYear + "/" + bMonth + "/" + bDay;

                if (!TextUtils.isEmpty(petname)) {
                    addTask(petname, breed, birth, etc);
                    Log.i("생일", birth);
                } else {
                    Toast.makeText(WriteAnimalActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 반려동물 정보 추가하기
    private void addTask(String petname, String breed, String birth, String etc) {
        String url = "https://bangidaapp.herokuapp.com/api/animal";

        HashMap<String, String> body = new HashMap<>();
        body.put("petname", petname);
        body.put("breed", breed);
        body.put("birth", birth);
        body.put("etc", etc);

        // POST 방식으로 데이터를 요청
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(body), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // success는 변수이름이고, boolean값인 true | false 값을 가짐.
                    if(response.getBoolean("success")) {

                        Toast.makeText(WriteAnimalActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(WriteAnimalActivity.this, MainActivity.class));

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
                NetworkResponse response = error.networkResponse;
                // instanceof : 객체타입을 확인
                if(error instanceof ServerError && response != null) {
                    try {
                        // 긁어온 String을 서버로 보냄.
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                        JSONObject obj = new JSONObject(res);
                        // 저장되지 않으면 띄우는 메시지
                        Toast.makeText(WriteAnimalActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                // key : Content-Type, value : application/json
                // Authorization의 value 값은 사용자 정보가 담긴 token
                headers.put("Connect-Type", "application/json");
                headers.put("Authorization", token);

                // success, animal(_id, petname, breed, birth, etc, user)값을 return
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
}