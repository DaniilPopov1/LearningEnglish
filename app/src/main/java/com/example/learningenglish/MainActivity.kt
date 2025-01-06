package com.example.learningenglish

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.learningenglish.Av_Reg.ActivityRegAuth
import com.example.learningenglish.Grammar.ActivityGrammar
import com.example.learningenglish.Vocabular.ActivityVocabular
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sharedPreferences = getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val userUID = sharedPreferences.getString("userUID", null)

        val ButProf = findViewById<Button>(R.id.buttonProfile)
        ButProf.setOnClickListener{
            val intent = Intent(this, ActivityProfile::class.java)
            startActivity(intent)
        }

        val ButGramm = findViewById<Button>(R.id.buttonGrammarLessons)
        ButGramm.setOnClickListener{
            val intent = Intent(this, ActivityGrammar::class.java)
            startActivity(intent)
        }
        val ButVoc = findViewById<Button>(R.id.buttonLexicalLessons)
        ButVoc.setOnClickListener{
            val intent = Intent(this, ActivityVocabular::class.java)
            startActivity(intent)
        }
        val ButClose = findViewById<Button>(R.id.buttonClose)
        ButClose.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.signOut()

            val sharedPreferences = getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
            sharedPreferences.edit().remove("userUID").apply()

            val intent = Intent(this, ActivityRegAuth::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}