package com.example.learningenglish.Av_Reg

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.learningenglish.R
import com.example.learningenglish.dataClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class fragment_registration : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val emailPattern="[a-zA-Z0-9._]+@[a-z]+\\.+[a-z]+"

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.registration_fragment, container, false)
        val ET_log = view.findViewById<EditText>(R.id.editTextLogin)
        val ET_mail = view.findViewById<EditText>(R.id.editTextEmailAddress)
        val ET_pass = view.findViewById<EditText>(R.id.editTextPassword)
        val ET_pass_rep = view.findViewById<EditText>(R.id.editTextPasswordRep)
        val Button = view.findViewById<Button>(R.id.button)

        val login = ET_log.text.toString()
        val email = ET_mail.text.toString()
        val pass = ET_pass.text.toString()
        val repPass = ET_pass_rep.text.toString()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        Button.setOnClickListener {
            val login = ET_log.text.toString()
            val email = ET_mail.text.toString()
            val pass = ET_pass.text.toString()
            val repPass = ET_pass_rep.text.toString()
            if (login.isEmpty() || email.isEmpty() || pass.isEmpty() || repPass.isEmpty()) {
                if (login.isEmpty()){
                    ET_log.error="Введите логин"
                }
                if (email.isEmpty()){
                    ET_mail.error="Введите email"
                }
                if (pass.isEmpty()){
                    ET_pass.error="Введите пароль"
                }
                if (repPass.isEmpty()){
                    ET_pass_rep.error="Повторите пароль"
                }
            }
            else if (!email.matches(emailPattern.toRegex()) || pass.length < 8 || pass != repPass) {
                if (!email.matches(emailPattern.toRegex())) {
                    ET_mail.error = "Не корректный email"
                }
                if (pass.length < 8) {
                    ET_pass.error = "Пароль слишком короткий"
                }
                if (pass != repPass) {
                    ET_pass_rep.error = "Пароли не совпадают"
                }
            }
            else{
                auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                    if(it.isSuccessful){
                        val database=database.reference.child("users").child(auth.currentUser!!.uid)
                        val correctAnswer = 0
                        val wrongAnswer = 0
                        val users: Users = Users(login, email, auth.currentUser!!.uid, correctAnswer,wrongAnswer)
                        database.setValue(users).addOnCompleteListener{
                            if(it.isSuccessful){
                                Toast.makeText(
                                    inflater.context,
                                    "Вы успешно зарегистрированны",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            else{
                                Toast.makeText(
                                    inflater.context,
                                    "Что-то пошло не так, попробуйте снова",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    else{
                        Toast.makeText(
                            inflater.context,
                            "Пользователь с такими данными уже существует",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        return view
    }
}