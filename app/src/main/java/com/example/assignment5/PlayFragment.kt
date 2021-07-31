package com.example.assignment5

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.assignment5.databinding.FragmentPlayBinding
import java.text.DecimalFormat

class PlayFragment : Fragment() {
    private lateinit var binding: FragmentPlayBinding
    private var name: String? = null
    private var email: String? = null
    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            name = bundle.getString("name")
            email = bundle.getString("email")
            id = bundle.getLong("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayBinding.inflate(inflater, container, false)

        binding.playNameTv.text = if (name == null) "#$id" else name
        binding.playMessageTv.text = if (email == null) "큰 거 한방 터져라" else email

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val intent = Intent(activity, GameActivity::class.java)
            startActivity(intent)
        }
    }

    fun makeDecimal(amount: Int) : String {
        val value = if (amount < 0) -amount else amount
        val formatter = DecimalFormat("###,###,###")
        return formatter.format(value)
    }
}