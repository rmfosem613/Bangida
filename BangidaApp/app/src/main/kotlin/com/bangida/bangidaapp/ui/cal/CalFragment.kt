package com.bangida.bangidaapp.ui.cal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangida.bangidaapp.databinding.FragmentCalBinding

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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        calViewModel =
            ViewModelProvider(this).get(CalViewModel::class.java)

        _binding = FragmentCalBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textCal
        calViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}