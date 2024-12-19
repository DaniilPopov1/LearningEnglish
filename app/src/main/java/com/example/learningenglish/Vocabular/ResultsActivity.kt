package com.example.learningenglish.Vocabular

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
        setContentView(R.layout.activity_results2)

        val correctAnswers = intent.getIntExtra("correctAnswers", 0)
        val totalAnswers = intent.getIntExtra("totalAnswers", 0)
        val percentage = (correctAnswers / totalAnswers.toFloat()) * 100

        findViewById<TextView>(R.id.resultText).text = "Правильных ответов: $correctAnswers/$totalAnswers\nПроцент выполнения: %.2f".format(percentage)

        findViewById<Button>(R.id.finishButton).setOnClickListener {
            // Переход на экран с уроками
            val intent = Intent(this, ActivityVocabular::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Завершает текущую активити
        }
    }
}
