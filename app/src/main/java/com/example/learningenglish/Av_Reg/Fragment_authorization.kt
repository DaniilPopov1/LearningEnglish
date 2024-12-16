package com.example.learningenglish.Av_Reg

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.learningenglish.MainActivity
import com.example.learningenglish.R
import com.google.firebase.auth.FirebaseAuth

class fragment_authorization : Fragment() {

    private lateinit var auth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.authorization_fragment, container, false)
        val ET_mail = view.findViewById<EditText>(R.id.editTextEmailAddress)
        val ET_pass = view.findViewById<EditText>(R.id.editTextPassword)
        val Button = view.findViewById<Button>(R.id.button)
        val text1 = view.findViewById<TextView>(R.id.textViewReg)

        text1.setOnClickListener {
            findNavController().navigate(R.id.navigation_registration)
        }

        auth = FirebaseAuth.getInstance()
        Button.setOnClickListener {
            val email = ET_mail.text.toString()
            val pass = ET_pass.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                if (email.isEmpty()) {
                    ET_mail.error = "Введите email"
                }
                if (pass.isEmpty()) {
                    ET_pass.error = "Введите пароль"
                }
            } else {
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val currentUser = auth.currentUser
                        val userId = currentUser?.uid

                        // Сохранение в SharedPreferences
                        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("userUID", userId)
                        editor.apply()

                        // Переход в MainActivity
                        val intent = Intent(view.context, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            inflater.context,
                            "Неверный логин или пароль",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        return view
    }
}