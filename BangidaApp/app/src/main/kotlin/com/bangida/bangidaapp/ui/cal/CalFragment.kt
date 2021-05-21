package com.bangida.bangidaapp.ui.cal

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bangida.bangidaapp.CalendarAdapter
import com.bangida.bangidaapp.R
import com.bangida.bangidaapp.UtilsService.SharedPreferenceClass
import com.bangida.bangidaapp.databinding.FragmentCalBinding
import com.bangida.bangidaapp.model.CalListModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

//git history "Create Calendar Remark" 참고

class CalFragment : Fragment() {

    private lateinit var calViewModel: CalViewModel
    private var _binding: FragmentCalBinding? = null

    var todoItem: String = ""
    var cdate: String = ""
    var sharedPreferenceClass: SharedPreferenceClass? = null
    var token: String = ""
    var arrayList: ArrayList<CalListModel>? = null
    var calListAdapter: CalendarAdapter? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        calViewModel =
            ViewModelProvider(this).get(CalViewModel::class.java)

        _binding = FragmentCalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferenceClass = SharedPreferenceClass(requireActivity())
        token = sharedPreferenceClass!!.getValue_string("token")

        val cal  = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR).toString()
        val month = (cal.get(Calendar.MONTH)+1).toString()
        val day = cal.get(Calendar.DATE).toString()
        cdate = ""+year+"-"+month+""+"-"+day+""
        getTodo()

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth -> // 달력 날짜가 선택되면
            checkedDay(year, month, dayOfMonth) // checkedDay 메소드 호출
        }

        binding.addBtn.setOnClickListener { view ->
            showSettingPopup()
        }

        binding.calrecycler.layoutManager = LinearLayoutManager(requireActivity())
        return root
    }

    private fun showSettingPopup() {

        val builder = getActivity()?.let { AlertDialog.Builder(it) }
        builder?.setTitle("할 일 입력")

        val v1 = layoutInflater.inflate(R.layout.add_dialog, null)
        builder?.setView(v1)

        // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
        var com_listener = DialogInterface.OnClickListener { p0, p1 ->
            var alert = p0 as AlertDialog
            var edit: EditText? = alert.findViewById(R.id.editText)

            todoItem = edit?.text.toString()
            saveTodo(todoItem)
        }

        builder?.setPositiveButton("완료", com_listener)
        builder?.setNegativeButton("취소", null)

        builder?.show()

    }

    private fun showUDPopup(id:String, position: Int, sche:String) {

        val builder = getActivity()?.let { AlertDialog.Builder(it) }
        builder?.setTitle("할 일 수정")
        val v1 = layoutInflater.inflate(R.layout.add_dialog, null)

        val edit: EditText = v1.findViewById(R.id.editText)
        edit.setText(sche)
        builder?.setView(v1)

        // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
        var com_listener = DialogInterface.OnClickListener { p0, p1 ->
            var alert = p0 as AlertDialog
            var edit: EditText? = alert.findViewById(R.id.editText)

            todoItem = edit?.text.toString()
            updateTodo(id, todoItem)
        }

        var del_listener = DialogInterface.OnClickListener { p0, p1 ->
            var alert = p0 as AlertDialog
            deleteTodo(id, position)
        }

        builder?.setPositiveButton("수정", com_listener)
        builder?.setNegativeButton("삭제", del_listener)

        builder?.show()
    }


    fun checkedDay(cYear: Int, cMonth: Int, cDay: Int){
        cdate = ""+cYear+"-"+(cMonth+1)+""+"-"+cDay+""
        Toast.makeText(activity, cdate, Toast.LENGTH_SHORT).show()
        try {
            getTodo()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    private fun saveTodo(todoItem: String) {
        val url = "https://bangidaapp.herokuapp.com/api/calendar"

        var body = HashMap<String, String>()
        body.put("cdate", cdate)
        body.put("sche", todoItem)

        var jsonObjectRequest = object: JsonObjectRequest(Request.Method.POST, url, JSONObject(body as Map<*, *>),
            { response ->
                try{
                    // success는 변수이름이고, boolean값인 true | false 값을 가짐.
                    if(response.getBoolean("success")){
                        Toast.makeText(requireActivity(), "Added Successfully", Toast.LENGTH_SHORT).show()
                        getTodo()
                    }
                } catch (e: JSONException){  // 예외 : 정상적인 처리를 벗어나는 경우
                    e.printStackTrace() // getMessage, toString과 다르게 리턴값이 없음.
                }
            },
            { error ->
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show()
                val response = error.networkResponse
                // instanceof : 객체타입을 확인
                if (error is ServerError && response != null){
                    try {
                        // 긁어온 String을 서버로 보냄.  HttpHeaderParser.parseCharset(response.headers, "utf-8")
                        var res: String = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
                        var obj = JSONObject(res)
                        // 로그인 정보가 맞지 않으면 "User not exists..." 메시지를 띄움.
                        Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show()
                    }catch (je: UnsupportedEncodingException){
                        je.printStackTrace()
                    }
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                var headers = HashMap<String, String>()
                // 서버에 요청할 때 Headers 정보.
                // key : Content-Type, value : application/json
                // Authorization의 value 값은 사용자 정보가 담긴 token
                headers.put("Connect-Type", "application/json")
                headers.put("Authorization", token)

                // success, calendar(pcheck, _id, cdate, sche, animals)값을 return
                return headers
            }
        }

        // 서버의 응답이 오랫동안 없을 때 재시도
        // socket 통신 제한시간 30초
        val socketTime = 3000
        var policy: RetryPolicy = DefaultRetryPolicy(
            socketTime,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.retryPolicy = policy

        // Volley를 requestQueue에 대입
        var requestQueue = Volley.newRequestQueue(requireActivity())
        // 데이터를 파싱.
        requestQueue.add<JSONObject>(jsonObjectRequest)
    }

    @SuppressLint("WrongConstant")
    fun getTodo(){
        arrayList = ArrayList()
        val url = "https://bangidaapp.herokuapp.com/api/calendar"

        // Get 방식으로 데이터를 요청
        var jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try{
                    if(response.getBoolean("success")){
                        val jsonArray :JSONArray = response.getJSONArray("plans")

                        // 사용자가 작성한 캘린더 정보 출력하기
                        for (index in 0 until jsonArray.length()){
                            var jsonObject :JSONObject = jsonArray.getJSONObject(index)

                            var calListModel = CalListModel(
                                jsonObject.getString("_id"),
                                jsonObject.getString("cdate"),
                                jsonObject.getString("sche"),
                                jsonObject.getBoolean("pcheck")
                            )
                            if(calListModel.cdate==cdate){
                                arrayList?.add(calListModel)
                            }
                        }

                        calListAdapter = CalendarAdapter(requireActivity(), arrayList!!)
                        binding.calrecycler.adapter = calListAdapter

                        // 리사이클러뷰 아이템 클릭 이벤트
                        calListAdapter!!.setItemClickListener(object :CalendarAdapter.ItemClickListener{
                            override fun onClick(view: View, position: Int) {
                                showUDPopup(arrayList!!.get(position).getId(), position, arrayList!!.get(position).getSche())
                            }
                        })

                        // 리사이클러뷰 체크박스 클릭 이벤트
                        calListAdapter!!.setCheckClickListener(object :CalendarAdapter.CheckClickListener{
                            override fun onClick(view: View, position: Int, pcheck: Boolean) {
                                updateCheck(arrayList!!.get(position).getId(), pcheck)
                            }
                        })

                    }
                } catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show()
                val response = error.networkResponse
                // instanceof : 객체타입을 확인
                if (error is ServerError && response != null){
                    try {
                        // 긁어온 String을 서버로 보냄.  HttpHeaderParser.parseCharset(response.headers, "utf-8")
                        var res: String = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
                        var obj = JSONObject(res)
                        // 로그인 정보가 맞지 않으면 "User not exists..." 메시지를 띄움.
                        Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show()
                    }catch (je: UnsupportedEncodingException){
                        je.printStackTrace()
                    }
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                var headers = HashMap<String, String>()
                // 서버에 요청할 때 Headers 정보.
                // key : Content-Type, value : application/json
                // Authorization의 value 값은 사용자 정보가 담긴 token
                headers.put("Connect-Type", "application/json")
                headers.put("Authorization", token)

                // success, calendar(pcheck, _id, cdate, sche, animals)값을 return
                return headers
            }
        }

        val socketTime = 3000
        var policy: RetryPolicy = DefaultRetryPolicy(
            socketTime,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.retryPolicy = policy

        var requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add<JSONObject>(jsonObjectRequest)

    }

    @SuppressLint("WrongConstant")
    private fun updateTodo(id: String, uSche: String){
        val url = "https://bangidaapp.herokuapp.com/api/calendar/"+id

        var body = HashMap<String, String>()
        body.put("sche", uSche)

        updatef(url, body)
    }

    @SuppressLint("WrongConstant")
    private fun updateCheck(id: String, pCheck: Boolean){
        val url = "https://bangidaapp.herokuapp.com/api/calendar/"+id

        var body = HashMap<String, Boolean>()
        body.put("pcheck", pCheck)

        updatef(url, body)
    }

    private fun updatef(url: String, body: HashMap<String, *>) {
        // PUT방식으로 수정
        var jsonObjectRequest = object: JsonObjectRequest(Request.Method.PUT, url, JSONObject(body as Map<*, *>),
            { response ->
                try{
                    if(response.getBoolean("success")){
                        getTodo()
                        Toast.makeText(requireActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                var headers = HashMap<String, String>()
                headers.put("Connect-Type", "application/json")
                headers.put("Authorization", token)
                return headers
            }
        }

        val socketTime = 3000
        var policy: RetryPolicy = DefaultRetryPolicy(
            socketTime,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.retryPolicy = policy

        var requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add<JSONObject>(jsonObjectRequest)
    }


    @SuppressLint("WrongConstant")
    private fun deleteTodo(id: String, position:Int){
        val url = "https://bangidaapp.herokuapp.com/api/calendar/"+id
        var jsonObjectRequest = object: JsonObjectRequest(Request.Method.DELETE, url, null,
            { response ->
                try{
                    if(response.getBoolean("success")){
                        Toast.makeText(requireActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        arrayList?.removeAt(position)
                        calListAdapter?.notifyItemRemoved(position)
                    }
                } catch (e: JSONException){
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                var headers = HashMap<String, String>()
                headers.put("Connect-Type", "application/json")
                headers.put("Authorization", token)
                return headers
            }
        }

        val socketTime = 3000
        var policy: RetryPolicy = DefaultRetryPolicy(
            socketTime,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.retryPolicy = policy

        var requestQueue = Volley.newRequestQueue(requireActivity())
        requestQueue.add<JSONObject>(jsonObjectRequest)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}