package com.bangida.bangidaapp.ui.share

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangida.bangidaapp.UtilsService.SharedPreferenceClass
import com.bangida.bangidaapp.databinding.FragmentShareBinding

class ShareFragment : Fragment() {

    private lateinit var shareViewModel: ShareViewModel
    private var _binding: FragmentShareBinding? = null

    var token: String = ""

    // This property is only valid between onCreateView and
    // onDestroyView.
    var sharedPreferenceClass: SharedPreferenceClass? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shareViewModel =
            ViewModelProvider(this).get(ShareViewModel::class.java)

        _binding = FragmentShareBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPreferenceClass = SharedPreferenceClass(requireActivity())
        token = sharedPreferenceClass!!.getValue_string("token")

        binding.ib.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT, "당신의 가족 혹은 지인과 함께 반려동물의 일정과 가계부를 손쉽게 관리해보세요. " +
                        "token : " + token
            )
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, "공유하기")
            startActivity(shareIntent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}