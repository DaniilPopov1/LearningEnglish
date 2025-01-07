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

        findViewById<TextView>(R.id.resultText).text = ("Правильных ответов: $correctAnswers/$totalAnswers\n" +
                "Процент выполнения: $percentage\n" + "Оценка: $result")

        val database = FirebaseDatabase.getInstance().reference

        if (userUID != null) {
            val progressRef = database.child("progress").child(userUID).child("grammar").child(idLes.toString())

            progressRef.get().addOnSuccessListener { snapshot ->
                val existingResult = snapshot.child("result").value as? String

                // Определение порядка приоритета оценок
                val priorities = mapOf(
                    "Провалено" to 1,
                    "Удовлетворительно" to 2,
                    "Хорошо" to 3,
                    "Отлично" to 4
                )

                // Сравнение текущей и новой оценки
                val newResult = if (existingResult != null) {
                    val currentPriority = priorities[existingResult] ?: 0
                    val newPriority = priorities[result] ?: 0
                    if (newPriority > currentPriority) result else existingResult
                } else {
                    result // Если записи ещё нет, сохраняем новую
                }

                // Данные для сохранения
                val progressData = mapOf(
                    "lessonID" to idLes,
                    "result" to newResult,
                    "percentage" to percentage
                )

                // Сохранение данных в базу
                progressRef.setValue(progressData)
                    .addOnSuccessListener {
                        Log.d("Firebase", "Запись успешно добавлена/обновлена")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Ошибка добавления записи", e)
                    }
            }.addOnFailureListener { e ->
                Log.e("Firebase", "Ошибка чтения данных", e)
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
