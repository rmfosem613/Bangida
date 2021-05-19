package com.bangida.bangidaapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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
import com.bangida.bangidaapp.interfaces.RecyclerViewClickListener;
import com.bangida.bangidaapp.model.AnimalModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// 처음에는 수정, 삭제 버튼이 안보였다가 roomitem을 누르면 반응하게 해주기 위해 recyclerviewclicklister
public class RoomFragment extends Fragment implements RecyclerViewClickListener {

    RecyclerView recyclerView;
    TextView empty_tv;
    ProgressBar progressBar;
    RoomListAdapter roomListAdapter;
    ArrayList<AnimalModel> arrayList;

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

    // 동물 정보 수정을 위한 dialog 생성
    public void showUpdateDialog(final  String  id, String petname, String breed, String etc)  {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_layout, null);

        final EditText petname_field = alertLayout.findViewById(R.id.petname);
        final EditText breed_field = alertLayout.findViewById(R.id.breed);
        final EditText etc_field = alertLayout.findViewById(R.id.etc);

        petname_field.setText(petname);
        breed_field.setText(breed);
        etc_field.setText(etc);

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(alertLayout)
                .setTitle("동물 정보 수정하기")
                .setPositiveButton("수정", null)
                .setNegativeButton("취소", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog)alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String petname = petname_field.getText().toString();
                        String breed = breed_field.getText().toString();
                        String etc = etc_field.getText().toString();

                        updateTask(id, petname, breed, etc);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    // 동물 정보 삭제
    public void showDeleteDialog(final String id, final  int position) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("동물 정보를 삭제하시겠습니까?")
                .setPositiveButton("네", null)
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTasks();
                    }
                })
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog)alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAnimal(id, position);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    // put 방식
    // 동물 정보 수정을 위해 서버랑 통신
    private void updateTask(String id, String petname, String breed, String etc) {
        // 게시글의 id를 추가해서 url을 보내줌 (판별하기 위해)
        String url = "https://bangidaapp.herokuapp.com/api/animal/"+id;
        HashMap<String, String> body = new HashMap<>();
        body.put("petname", petname);
        body.put("breed", breed);
        body.put("etc", etc);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(body),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")) {
                                getTasks();
                                Toast.makeText(getActivity(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    // delete 방식
    // 동물 정보 삭제를 위해 서버와 통신
    private void deleteAnimal(String id, int position) {
        String url = "https://bangidaapp.herokuapp.com/api/animal/"+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")) {
                        Toast.makeText(getActivity(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                        arrayList.remove(position);
                        roomListAdapter.notifyItemRemoved(position);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
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

                            AnimalModel animalModel = new AnimalModel(
                                    jsonObject.getString("_id"),
                                    jsonObject.getString("petname"),
                                    jsonObject.getString("breed"),
                                    jsonObject.getString("etc")
                                    // 방 인원수 세는 거 추가하기
                            );

                            arrayList.add(animalModel);
                        }

                       roomListAdapter = new RoomListAdapter(getActivity(), arrayList, RoomFragment.this);
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

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getActivity(), "Position "+ position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(int position) {
        showUpdateDialog(arrayList.get(position).getId(), arrayList.get(position).getPetname(), arrayList.get(position).getBreed(), arrayList.get(position).getEtc());
        Toast.makeText(getActivity(), "Position "+ position, Toast.LENGTH_SHORT).show();
    }

    // 수정을 위해 postion(배열에서 위치한 index값), id(room id), petname, breed, etc 값 넘겨줌
    @Override
    public void onEditButtonClick(int position) {
        showUpdateDialog(arrayList.get(position).getId(), arrayList.get(position).getPetname(), arrayList.get(position).getBreed(), arrayList.get(position).getEtc());
    }

    // 삭제를 위해 adapter에 postion, id 값 넘겨줌
    @Override
    public void onDeleteButtonClick(int position) {
        showDeleteDialog(arrayList.get(position).getId(), position);
    }
}