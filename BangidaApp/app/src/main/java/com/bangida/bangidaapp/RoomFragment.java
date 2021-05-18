package com.bangida.bangidaapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.bangida.bangidaapp.Adapters.RoomListAdapter;
import com.bangida.bangidaapp.UtilsService.SharedPreferenceClass;
import com.bangida.bangidaapp.model.RoomListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomFragment extends Fragment {

    RecyclerView recyclerView;
    TextView empty_tv;
    ProgressBar progressBar;
    RoomListAdapter roomListAdapter;
    ArrayList<RoomListModel> arrayList;

    Button user_btn;


    SharedPreferenceClass sharedPreferenceClass;
    String token;

    public RoomFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        empty_tv = view.findViewById(R.id.empty_tv);
        progressBar = view.findViewById(R.id.progress_bar);

        sharedPreferenceClass = new SharedPreferenceClass(getContext());
        token = sharedPreferenceClass.getValue_string("token");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        getTasks();

        return view;
    }


    // 동물 정보 가져오기
    public void getTasks() {
        arrayList = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        String url = "https://bangidaapp.herokuapp.com/api/animal";

        // Get 방식으로 데이터를 요청
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // 서버 응답값 success, msg, toast, user(_id, username, email, password)
                    // success는 변수이름이고, boolean값인 true | false 값을 가짐.
                    if(response.getBoolean("success")) {
                        JSONArray jsonArray = response.getJSONArray("animals");

                        // 사용자가 작성한 동물 정보 출력하기
                       for(int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            RoomListModel roomListModel = new RoomListModel(
                                    jsonObject.getString("_id"),
                                    jsonObject.getString("petname")
                                    // 방 인원수 세는 거 추가하기
                            );

                            arrayList.add(roomListModel);
                        }

                       roomListAdapter = new RoomListAdapter(getActivity(), arrayList);
                       recyclerView.setAdapter(roomListAdapter);

                    }
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
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                NetworkResponse response = error.networkResponse;
                // instanceof : 객체타입을 확인
                if(error instanceof ServerError && response != null) {
                    try {
                        // 긁어온 String을 서버로 보냄.
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));

                        JSONObject obj = new JSONObject(res);
                        // 로그인 정보가 맞지 않으면 "User not exists..." 메시지를 띄움.
                        Toast.makeText(getActivity(), obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException | UnsupportedEncodingException je){ // 예외 경우
                        je.printStackTrace(); // return값 없음.
                        progressBar.setVisibility(View.GONE);
                    }
                }

                progressBar.setVisibility(View.GONE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // 서버에 요청할 때 Headers 정보.
                // key : Content-Type, value : application/json
                headers.put("Connect-Type", "application/json");
                headers.put("Authorization", token);

                // success, msg, token, user(_id, username, email, password)값을 return
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        // 데이터를 파싱.
        requestQueue.add(jsonObjectRequest);
    }
}