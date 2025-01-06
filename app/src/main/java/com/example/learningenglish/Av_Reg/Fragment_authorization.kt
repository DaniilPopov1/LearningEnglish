package com.example.learningenglish.Av_Reg

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.learningenglish.MainActivity
import com.example.learningenglish.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


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
        val text2 = view.findViewById<TextView>(R.id.textViewForgetPass)

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
                        if (currentUser!=null && currentUser.isEmailVerified){
                            val userId = currentUser?.uid

                            // Сохранение в SharedPreferences
                            val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("userUID", userId)
                            editor.apply()

                            // Переход в MainActivity
                            val intent = Intent(view.context, MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(inflater.context,
                                "Ваш email не подтверждён. Проверьте свою почту",
                                Toast.LENGTH_LONG
                            ).show()
                        }

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

        text2.setOnClickListener {
            val builder = AlertDialog.Builder(inflater.context)
            val view = layoutInflater.inflate(R.layout.dialog_fogot,null)
            val userEmail = view.findViewById<EditText>(R.id.emailBox)
            builder.setView(view)
            val dialog=builder.create()
            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                compareEmail(userEmail)
                dialog.dismiss()
            }
            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }
            if(dialog.window!=null){
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser!=null && auth.currentUser!!.isEmailVerified){
            val intent = Intent(layoutInflater.context,MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun compareEmail(email:EditText) {
        if (email.text.toString().isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Введите email",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            Toast.makeText(
                requireContext(),
                "Некорректный email",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val emailToCheck = email.text.toString()
        val db = FirebaseDatabase.getInstance()
        val lessonsRef = db.getReference("users")

        lessonsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    // Перебираем всех пользователей
                    for (lessonsSnapshot in snapshot.children) {
                        val em = lessonsSnapshot.child("email").getValue(String::class.java) ?: lessonsSnapshot.child("email").value?.toString() ?: "Unknown"
                        if (em == emailToCheck) {
                            // Если email найден, отправляем письмо
                            auth.sendPasswordResetEmail(emailToCheck)
                                .addOnCompleteListener { resetTask ->
                                    if (resetTask.isSuccessful) {
                                        Toast.makeText(
                                            requireContext(),
                                            "Проверьте свою почту для сброса пароля",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Ошибка при отправке письма",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        } else {
                            // Email не найден в базе
                            Toast.makeText(
                                requireContext(),
                                "Пользователь с таким email не найден",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}