package com.bangida.bangidaapp.ui.acbook

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bangida.bangidaapp.AcAdapter
import com.bangida.bangidaapp.AclAdapter
import com.bangida.bangidaapp.R
import com.bangida.bangidaapp.UtilsService.SharedPreferenceClass
import com.bangida.bangidaapp.databinding.FragmentAcbookBinding
import com.bangida.bangidaapp.model.AccModel
import com.bangida.bangidaapp.model.AclModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class AcbookFragment : Fragment() {

    private lateinit var acbookViewModel: AcbookViewModel
    private var _binding: FragmentAcbookBinding? = null

    var acCost: String = ""
    var acItem: String = ""
    var acDate: String = ""
    var sharedPreferenceClass: SharedPreferenceClass? = null
    var token: String = ""
    var arrayList: ArrayList<AccModel>? = null
    var acAdapter: AcAdapter? = null
    var totalP:Int = 0

    var checkItem:String = ""
    var carrayList: ArrayList<AclModel>? = null
    var aclAdapter: AclAdapter? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        acbookViewModel =
            ViewModelProvider(this).get(AcbookViewModel::class.java)

        _binding = FragmentAcbookBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferenceClass = SharedPreferenceClass(requireActivity())
        token = sharedPreferenceClass!!.getValue_string("token")
        getAc()
        getAl()

        binding.tvTotalnum.setText(totalP.toString())

        binding.addAc.setOnClickListener {
            showItemAddPopup()
        }

        binding.addAclist.setOnClickListener {
            showClistAddPopup()
        }

        binding.acrecycler.layoutManager = LinearLayoutManager(requireActivity())
        binding.aclistrecycler.layoutManager = GridLayoutManager(requireActivity(), 2)
        return root
    }

    private fun showItemAddPopup() {

        val builder = getActivity()?.let { AlertDialog.Builder(it) }
        builder?.setTitle("항목 입력")

        val v1 = layoutInflater.inflate(R.layout.ac_dialog, null)
        builder?.setView(v1)

        // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
        var com_listener = DialogInterface.OnClickListener { p0, p1 ->
            var alert = p0 as AlertDialog
            var edit: EditText? = alert.findViewById(R.id.et_date)
            var edit2: EditText? = alert.findViewById(R.id.et_item)
            var edit3: EditText? = alert.findViewById(R.id.et_cost)

            acDate = edit?.text.toString()
            acCost = edit3?.text.toString()
            acItem = edit2?.text.toString()
            saveAcItem(acDate, acItem, acCost)
        }

        builder?.setPositiveButton("완료", com_listener)
        builder?.setNegativeButton("취소", null)

        builder?.show()
    }

    private fun showItemUDPopup(id: String?, position: Int, date: String, item: String, cost: String) {
        val builder = getActivity()?.let { AlertDialog.Builder(it) }
        builder?.setTitle("항목 수정")
        val v1 = layoutInflater.inflate(R.layout.ac_dialog, null)

        val edit:EditText = v1.findViewById(R.id.et_date)
        val edit2:EditText = v1.findViewById(R.id.et_item)
        val edit3:EditText = v1.findViewById(R.id.et_cost)

        edit.setText(date)
        edit2.setText(item)
        edit3.setText(cost)

        builder?.setView(v1)


        // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
        var com_listener = DialogInterface.OnClickListener { p0, p1 ->
            var alert = p0 as AlertDialog
            var edit: EditText? = alert.findViewById(R.id.et_date)
            var edit2: EditText? = alert.findViewById(R.id.et_item)
            var edit3: EditText? = alert.findViewById(R.id.et_cost)

            acDate = edit?.text.toString()
            acCost = edit3?.text.toString()
            acItem = edit2?.text.toString()

            updateAcItem(id, acDate, acItem, acCost)
        }

        var del_listener = DialogInterface.OnClickListener { p0, p1 ->
            var alert = p0 as AlertDialog
            deleteAcItem(id, position)
        }

        builder?.setPositiveButton("수정", com_listener)
        builder?.setNegativeButton("삭제", del_listener)

        builder?.show()
    }

    private fun showClistAddPopup() {

        val builder = getActivity()?.let { AlertDialog.Builder(it) }
        builder?.setTitle("항목 입력")

        val v1 = layoutInflater.inflate(R.layout.add_dialog, null)
        builder?.setView(v1)

        // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
        var com_listener = DialogInterface.OnClickListener { p0, p1 ->
            var alert = p0 as AlertDialog
            var edit: EditText? = alert.findViewById(R.id.editText)

            checkItem = edit?.text.toString()
            savaAclistItem(checkItem)
        }

        builder?.setPositiveButton("완료", com_listener)
        builder?.setNegativeButton("취소", null)

        builder?.show()
    }

    private fun showClistUDPopup(id: String?, position: Int, alcon: String) {
        val builder = getActivity()?.let { AlertDialog.Builder(it) }
        builder?.setTitle("항목 수정")
        val v1 = layoutInflater.inflate(R.layout.add_dialog, null)

        val edit:EditText = v1.findViewById(R.id.editText)
        edit.setText(alcon)
        builder?.setView(v1)

        // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
        var com_listener = DialogInterface.OnClickListener { p0, p1 ->
            var alert = p0 as AlertDialog
            var edit: EditText? = alert.findViewById(R.id.editText)

            checkItem = edit?.text.toString()
            updateAl(id, checkItem)
        }

        var del_listener = DialogInterface.OnClickListener { p0, p1 ->
            var alert = p0 as AlertDialog
            deleteAl(id, position)
        }

        builder?.setPositiveButton("수정", com_listener)
        builder?.setNegativeButton("삭제", del_listener)

        builder?.show()
    }

    private fun deleteAcItem(id: String?, position: Int) {
        val url = "https://bangidaapp.herokuapp.com/api/account/"+id
        var jsonObjectRequest = object: JsonObjectRequest(Request.Method.DELETE, url, null,
            { response ->
                try{
                    if(response.getBoolean("success")){
                        Toast.makeText(requireActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        arrayList?.removeAt(position)
                        acAdapter?.notifyItemRemoved(position)
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

    private fun deleteAl(id: String?, position: Int) {
        val url = "https://bangidaapp.herokuapp.com/api/accountlist/"+id
        var jsonObjectRequest = object: JsonObjectRequest(Request.Method.DELETE, url, null,
            { response ->
                try{
                    if(response.getBoolean("success")){
                        Toast.makeText(requireActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        carrayList?.removeAt(position)
                        aclAdapter?.notifyItemRemoved(position)
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

    private fun saveAcItem(acDate: String, acItem: String, acCost: String) {
        val url = "https://bangidaapp.herokuapp.com/api/account"

        var body = HashMap<String, String>()
        body.put("acdate", acDate)
        body.put("accontent", acItem)
        body.put("acprice", acCost)

        save(url, body)
    }

    private fun savaAclistItem(checkItem: String) {
        val url = "https://bangidaapp.herokuapp.com/api/accountlist"

        var body = HashMap<String, String>()
        body.put("alcontent", checkItem)

        save(url, body)
    }

    private fun save(url: String, body: HashMap<String, *>) {
        var jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.POST, url, JSONObject(body as Map<*, *>),
            { response ->
                try{
                    if(response.getBoolean("success")){
                        getAl()
                        getAc()
                        Toast.makeText(requireActivity(), "Added Successfully", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException){  // 예외 : 정상적인 처리를 벗어나는 경우
                    e.printStackTrace() // getMessage, toString과 다르게 리턴값이 없음.
                }
            },
            { error ->
                Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show()
                val response = error.networkResponse
                if (error is ServerError && response != null){
                    try {
                        var res: String = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
                        var obj = JSONObject(res)
                        Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show()
                    }catch (je: UnsupportedEncodingException){
                        je.printStackTrace()
                    }
                }
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

    private fun getAc() {
        arrayList = ArrayList()

        val url = "https://bangidaapp.herokuapp.com/api/account"

        // Get 방식으로 데이터를 요청
        var jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try{
                    if(response.getBoolean("success")){
                        val jsonArray : JSONArray = response.getJSONArray("contents")

                        for (index in 0 until jsonArray.length()){
                            var jsonObject :JSONObject = jsonArray.getJSONObject(index)

                            var accModel = AccModel(
                                jsonObject.getString("_id"),
                                jsonObject.getString("acprice"),
                                jsonObject.getString("acdate"),
                                jsonObject.getString("accontent")
                            )
                            arrayList?.add(accModel)
                        }
                        acAdapter = AcAdapter(requireActivity(), arrayList!!)
                        binding.acrecycler.adapter = acAdapter

                        // 리사이클러뷰 아이템 클릭 이벤트
                        acAdapter!!.setItemClickListener(object : AcAdapter.ItemClickListener{
                            override fun onClick(view: View, position: Int) {
                                showItemUDPopup(arrayList!!.get(position).getId(), position, arrayList!!.get(position).getAcdate(),
                                    arrayList!!.get(position).getAccontent(), arrayList!!.get(position).getAcprice())
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
                if (error is ServerError && response != null){
                    try {
                        var res: String = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
                        var obj = JSONObject(res)
                        Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show()
                    }catch (je: UnsupportedEncodingException){
                        je.printStackTrace()
                    }
                }
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

    private fun getAl() {
        carrayList = ArrayList()

        val url = "https://bangidaapp.herokuapp.com/api/accountlist"

        // Get 방식으로 데이터를 요청
        var jsonObjectRequest = object: JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try{
                    if(response.getBoolean("success")){
                        val jsonArray : JSONArray = response.getJSONArray("contents")

                        for (index in 0 until jsonArray.length()){
                            var jsonObject :JSONObject = jsonArray.getJSONObject(index)

                            var aclModel = AclModel(
                                jsonObject.getString("_id"),
                                jsonObject.getString("alcontent"),
                                jsonObject.getBoolean("alcheck")
                            )
                            carrayList?.add(aclModel)
                        }
                        aclAdapter = AclAdapter(requireActivity(), carrayList!!)
                        binding.aclistrecycler.adapter = aclAdapter

                        // 리사이클러뷰 아이템 클릭 이벤트
                        aclAdapter!!.setItemClickListener(object : AclAdapter.ItemClickListener{
                            override fun onClick(view: View, position: Int) {
                                showClistUDPopup(carrayList!!.get(position).getId(), position, carrayList!!.get(position).getAlcontent())
                            }
                        })

                        // 리사이클러뷰 체크박스 클릭 이벤트
                        aclAdapter!!.setCheckClickListener(object : AclAdapter.CheckClickListener{
                            override fun onClick(view: View, position: Int, alcheck: Boolean) {
                                updateCheck(carrayList!!.get(position).getId(), alcheck)
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
                if (error is ServerError && response != null){
                    try {
                        var res: String = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
                        var obj = JSONObject(res)
                        Toast.makeText(activity, obj.getString("msg"), Toast.LENGTH_SHORT).show()
                    }catch (je: UnsupportedEncodingException){
                        je.printStackTrace()
                    }
                }
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

    private fun updateCheck(id: String?, alcheck: Boolean) {
        val url = "https://bangidaapp.herokuapp.com/api/accountlist/"+id

        var body = HashMap<String, Boolean>()
        body.put("alcheck", alcheck)

        updatef(url, body)
    }

    private fun updateAcItem(id: String?, acDate: String, acItem: String, acCost: String) {
        val url = "https://bangidaapp.herokuapp.com/api/account/"+id

        var body = HashMap<String, String>()
        body.put("acdate", acDate)
        body.put("accontent", acItem)
        body.put("acprice", acCost)

        updatef(url, body)
    }

    private fun updateAl(id: String?, checkItem: String) {
        val url = "https://bangidaapp.herokuapp.com/api/accountlist/"+id

        var body = HashMap<String, String>()
        body.put("alcontent", checkItem)

        updatef(url, body)
    }

    private fun updatef(url: String, body: HashMap<String, *>) {
        // PUT방식으로 수정
        var jsonObjectRequest = object: JsonObjectRequest(Request.Method.PUT, url, JSONObject(body as Map<*, *>),
            { response ->
                try{
                    if(response.getBoolean("success")){
                        getAl()
                        getAc()
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