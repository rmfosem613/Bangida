package com.bangida.bangidaapp.ui.acbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangida.bangidaapp.databinding.FragmentAcbookBinding

class AcbookFragment : Fragment() {

    private lateinit var acbookViewModel: AcbookViewModel
    private var _binding: FragmentAcbookBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        acbookViewModel =
            ViewModelProvider(this).get(AcbookViewModel::class.java)

        _binding = FragmentAcbookBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}