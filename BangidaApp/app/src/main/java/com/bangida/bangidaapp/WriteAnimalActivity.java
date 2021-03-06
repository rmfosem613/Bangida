package com.bangida.bangidaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton add_task_btn;
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
                //???????????? ????????????
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // Number Picker ??????
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
                    Log.i("??????", birth);
                } else {
                    Toast.makeText(WriteAnimalActivity.this, "????????? ??????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // ???????????? ?????? ????????????
    private void addTask(String petname, String breed, String birth, String etc) {
        String url = "https://bangidaapp.herokuapp.com/api/animal";

        HashMap<String, String> body = new HashMap<>();
        body.put("petname", petname);
        body.put("breed", breed);
        body.put("birth", birth);
        body.put("etc", etc);

        // POST ???????????? ???????????? ??????
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(body), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // success??? ??????????????????, boolean?????? true | false ?????? ??????.
                    if(response.getBoolean("success")) {

                        Toast.makeText(WriteAnimalActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(WriteAnimalActivity.this, MainActivity.class));

                    }
                } catch (JSONException e) { // ?????? : ???????????? ????????? ???????????? ??????
                    e.printStackTrace(); // getMessage, toSting??? ????????? ???????????? ??????.
                }
            }
        }, new Response.ErrorListener() {
            // Volley??? ??????????????? ?????? ??????????????? ??? ??????, ??????????????? ??? ????????? ?????? HTTP ?????????????????????.
            @Override
            // Volley ????????? ????????? ?????? ?????? ?????????
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                // instanceof : ??????????????? ??????
                if(error instanceof ServerError && response != null) {
                    try {
                        // ????????? String??? ????????? ??????.
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                        JSONObject obj = new JSONObject(res);
                        // ???????????? ????????? ????????? ?????????
                        Toast.makeText(WriteAnimalActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException | UnsupportedEncodingException je){ // ?????? ??????
                        je.printStackTrace(); // return??? ??????.
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // ????????? ????????? ??? Headers ??????.
                // key : Content-Type, value : application/json
                // Authorization??? value ?????? ????????? ????????? ?????? token
                headers.put("Connect-Type", "application/json");
                headers.put("Authorization", token);

                // success, animal(_id, petname, breed, birth, etc, user)?????? return
                return headers;
            }
        };

        // ????????? ????????? ???????????? ?????? ??? ?????????
        // socket ?????? ???????????? 30???
        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);

        // Volley??? requestQueue??? ??????
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // ???????????? ??????.
        requestQueue.add(jsonObjectRequest);
    }
}