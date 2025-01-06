package com.example.learningenglish.Vocabular

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.learningenglish.MainActivity
import com.example.learningenglish.R

class ActivityVocabularTh : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vocabular_th)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val nameLes = intent.getStringExtra("lessonTheme")
        val idLes = intent.getIntExtra("lessonID",0)
        val th = intent.getStringExtra("theoryLesson")

        val nameLesson = findViewById<TextView>(R.id.tvLessonTitle)
        nameLesson.text = nameLes

        val Theory = findViewById<TextView>(R.id.tvLessonContent)
        Theory.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(th, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(th)
        }

        val butEx = findViewById<Button>(R.id.btnStartExercise)
        butEx.setOnClickListener{
            val intent = Intent(this, ActivityVocabularExer::class.java).apply {
                putExtra("lessonID",idLes)
            }
            startActivity(intent)
        }

        val butBack = findViewById<ImageButton>(R.id.btnBack)
        butBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}