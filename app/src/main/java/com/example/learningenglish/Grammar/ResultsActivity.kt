package com.example.learningenglish.Grammar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.learningenglish.Grammar.ActivityGrammar
import com.example.learningenglish.R
import com.google.firebase.database.FirebaseDatabase

class ResultsActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val correctAnswers = intent.getIntExtra("correctAnswers", 0)
        val totalAnswers = intent.getIntExtra("totalAnswers", 0)
        val percentage = (correctAnswers / totalAnswers.toFloat()) * 100

        findViewById<TextView>(R.id.resultText).text = "Правильных ответов: $correctAnswers/$totalAnswers\nПроцент выполнения: %.2f".format(percentage)

        val idLes = intent.getIntExtra("lessonID", 0)
        val sharedPreferences = getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val userUID = sharedPreferences.getString("userUID", null)

        val result = when (percentage) {
            in 0.00..59.00 -> "Провалено"
            in 60.00..70.00 -> "Удовлетворительно"
            in 71.00..84.00 -> "Хорошо"
            in 85.00..100.00 -> "Отлично"
            else -> "Неизвестно"
        }

        val database = FirebaseDatabase.getInstance().reference

        if (userUID != null) {
            val progressRef = database.child("progress").child(userUID).child("grammar").child(idLes.toString())

            val progressData = mapOf(
                "lessonID" to idLes,
                "result" to result,
                "percentage" to percentage
            )

            progressRef.setValue(progressData)
                .addOnSuccessListener {
                    Log.d("Firebase", "Запись успешно добавлена/обновлена")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Ошибка добавления записи", e)
                }
        } else {
            Log.e("Firebase", "Ошибка: userUID равен null")
        }

        findViewById<Button>(R.id.finishButton).setOnClickListener {
            val intent = Intent(this, ActivityGrammar::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
