package com.bangida.bangidaapp.ui.cal

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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

// url = "https://bangidaapp.herokuapp.com/api/calendar";
// post 방식으로 일정 작성
// get 방식으로 일정 불러오기
// put 방식으로 수정
// delete 방식으로 삭제

/* post 요청 json 구성
{
    "cdate":"string 날짜 정보",
    "sche":"기차랑 놀기",
    "pcheck": false
}
pcheck는 boolean 데이터로 기본이 false. 별도로 입력하지 않은 상태.
cdate, cplan만 입력해도 데이터가 저장됨. check 하게 되면 pcheck를 true로 바꿔주는 코드를 추가하면 됨.
*/

/* post 응답 json 구성
{
    "success": true,
    "plan": {
        "pcheck": false,
        "_id": "60a0d844675e25001522409c",
        "cdate": "날짜",
        "sche": "계획",
        "animals": "60a0ce57f7ecfd3510cc0e47",
        "__v": 0
    },
    "msg": "Successfully created"
}
success는 저장되었는지 여부에 대한 boolean 값.
하나의 plan 구성 안에는 pcheck, _id(plan에 대한), cdate, sche, animals(선택한 동물의 id값) 으로 구성되어 있음.
msg는 무시해도 됨. Toast로 테스트할 때 사용
id, animals는 자동 생성이니까 신경쓸 필요 없음.
cdate, sche는 string.
*/

/* get 요청 방식
RoomFragment.java 78번째 줄부터 참고하면 좋을듯.
json은 필요없음. (RoomFragment.java 86번째 코드, null 값임.)
RoomFragment.java 149번째 줄처럼 통신할 때 header정보로
key, value : "Connect-Type", "application/json"
key, value : "Authorization", token
값을 넘겨주어야 함.
이때 token은 서버에서 넘겨줄거야(아마...? 이걸 이용하는 건 될지 안될지 몰라서 좀더 알아보고 알려줄게)

MainActivity2.kt 가보면 동물 버튼 누르고 cal fragment로 넘어올때 동물마다 id 값을 받아오는데 그거 이용해서 구현할 수 있다면
그걸 이용해도 괜찮을 거 같아.

*/

/* get 응답 json 구성
{
    "success": true,
    "count": 2,
    "plans": [
        {
            "pcheck": false,
            "_id": "60a0ebcbe21b4a56bc68cc66",
            "cdate": "날짜2",
            "sche": "계획2",
            "animals": "60a0ce57f7ecfd3510cc0e47",
            "__v": 0
        },
        {
            "pcheck": false,
            "_id": "60a0ec0c8cc7ce09cca55a9a",
            "cdate": "날짜",
            "sche": "계획",
            "animals": "60a0ce57f7ecfd3510cc0e47",
            "__v": 0
        }
    ],
    "msg": "Successfully fetched"
}
plans안에 {}하나가 한 일정으로 위에 json은 일정이 2개 들어간거야.
get으로 불러오기 하면 token(동물 방에 따라) plan들이 출력되는데 이걸 날짜별로 분류하는 작업이 필요해.
 */

class CalFragment : Fragment() {

    private lateinit var calViewModel: CalViewModel
    private var _binding: FragmentCalBinding? = null

    var todoItem: String = ""
    var cdate: String = ""
    var sharedPreferenceClass: SharedPreferenceClass? = null
    var token: String = ""

    var calListAdapter: CalendarAdapter? = null
    var arrayList: ArrayList<CalListModel>? = null

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

        binding.addBtn.visibility = View.INVISIBLE
        binding.TextView.visibility = View.INVISIBLE
        binding.view.visibility = View.INVISIBLE
        binding.view2.visibility = View.INVISIBLE

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
            var edit: EditText? = alert.findViewById<EditText>(R.id.editText)

            todoItem = edit?.text.toString()
            saveTodo(todoItem)

            //tv1.text = "${edit1?.text}"
            //tv1.append("${edit2?.text}")
        }

        builder?.setPositiveButton("완료", com_listener)
        builder?.setNegativeButton("취소", null)

        builder?.show()

    }

    fun checkedDay(cYear: Int, cMonth: Int, cDay: Int){
        binding.addBtn.visibility = View.VISIBLE
        binding.TextView.visibility = View.VISIBLE
        binding.view.visibility = View.VISIBLE
        binding.view2.visibility = View.VISIBLE
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
            override fun getHeaders(): Map<String, String>? {
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
    private fun getTodo(){
        arrayList = ArrayList()
        var url = "https://bangidaapp.herokuapp.com/api/calendar"


        // Get 방식으로 데이터를 요청
        val jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try{
                    // success는 변수이름이고, boolean값인 true | false 값을 가짐.
                    if(response.getBoolean("success")){
                        val jsonArray :JSONArray = response.getJSONArray("plans")

                        Log.d("태그", "gettodo")
                        // 사용자가 작성한 캘린더 정보 출력하기
                        for (index in 0 until jsonArray.length()){
                            var jsonObject :JSONObject = jsonArray.getJSONObject(index)

                            var calListModel = CalListModel(
                                jsonObject.getString("cdate"),
                                jsonObject.getString("sche"),
                                jsonObject.getBoolean("pcheck")
                            )
                            if(calListModel.cdate==cdate){
                                arrayList?.add(calListModel)
                            }
                        }
                        Log.d("태그", "arraylist: "+arrayList)

                        calListAdapter = CalendarAdapter(arrayList!!)
                        binding.calrecycler.adapter = calListAdapter
                    }
                } catch (e: JSONException){  // 예외 : 정상적인 처리를 벗어나는 경우
                    e.printStackTrace() // getMessage, toString과 다르게 리턴값이 없음.
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
            override fun getHeaders(): Map<String, String>? {
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

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}