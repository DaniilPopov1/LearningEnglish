package com.example.learningenglish.Grammar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.learningenglish.Grammar.ActivityGrammar
import com.example.learningenglish.R

class ResultsActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val correctAnswers = intent.getIntExtra("correctAnswers", 0)
        val totalAnswers = intent.getIntExtra("totalAnswers", 0)
        val percentage = (correctAnswers / totalAnswers.toFloat()) * 100

        findViewById<TextView>(R.id.resultText).text = "Правильных ответов: $correctAnswers/$totalAnswers\nПроцент выполнения: %.2f".format(percentage)

        findViewById<Button>(R.id.finishButton).setOnClickListener {
            // Переход на экран с уроками
            val intent = Intent(this, ActivityGrammar::class.java)
            startActivity(intent)
            finish()
        }
    }
}
