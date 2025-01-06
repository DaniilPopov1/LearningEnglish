package com.example.learningenglish

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.learningenglish.Grammar.ActivityGrammar
import com.google.firebase.database.FirebaseDatabase

class ActivityProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sharedPreferences = getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val userUID = sharedPreferences.getString("userUID", null)
        val loginTV = findViewById<TextView>(R.id.textViewLogin)
        val emailTV = findViewById<TextView>(R.id.textViewEmail)
        val corTV = findViewById<TextView>(R.id.textViewCorA)
        val wrongTV = findViewById<TextView>(R.id.textViewWrongA)
        val exitBut = findViewById<Button>(R.id.buttonLogout)

        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users").child(userUID.toString())
        userRef.get().addOnCompleteListener { dataTask ->
            if (dataTask.isSuccessful) {
                val userData = dataTask.result?.value as? Map<*, *>
                if (userData != null) {
                    val userName = userData["login"] as? String ?: "Unknown"
                    loginTV.setText(userName)
                    val userEmail = userData["email"] as? String ?: "Unknown"
                    emailTV.setText(userEmail)
                    val userCorA = userData["correctAnswer"]
                    corTV.setText(userCorA.toString())
                    val userWrongA = userData["wrongAnswer"]
                    wrongTV.setText(userWrongA.toString())

                }
            }
        }
        exitBut.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}