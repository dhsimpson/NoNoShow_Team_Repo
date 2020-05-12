package com.example.nonoshow.ui.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.nonoshow.MyApplication
import com.example.nonoshow.R

class SignUpFragment : Fragment() {

    private lateinit var toolsViewModel: SignUpViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.isLogined = false
        val signUp : Button = getView()!!.findViewById(R.id.button_signup)

        signUp.setOnClickListener{
            MyApplication.isLogined = false
            it.findNavController().navigate(R.id.nav_signIn)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        toolsViewModel =
            ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_sign_up, container, false)
        return root
    }
}