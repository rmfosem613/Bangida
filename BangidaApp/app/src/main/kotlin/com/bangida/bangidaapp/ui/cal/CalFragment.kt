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