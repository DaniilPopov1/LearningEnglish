package com.example.learningenglish.Grammar

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.learningenglish.R
import com.example.learningenglish.dataClsses.Exercise
import com.google.firebase.database.FirebaseDatabase

class ActivityGrammExer : AppCompatActivity() {

    val exerciseList = mutableListOf<Exercise>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gramm_exer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val database = FirebaseDatabase.getInstance()
        val exerciseRef = database.getReference("Exercise")
        exerciseRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    // Перебираем всех пользователей
                    for (exerciseSnapshot in snapshot.children) {
                        val ExerciseId = exerciseSnapshot.key?.toInt() ?: continue
                        val LessonsId = exerciseSnapshot.child("LessonsID").getValue(Int::class.java) ?: 0
                        val Question = exerciseSnapshot.child("Question").getValue(String::class.java) ?: "Unknown"
                        val AnswerOptions1 = exerciseSnapshot.child("AnswerOptions1").getValue(String::class.java) ?: "Unknown"
                        val AnswerOptions2 = exerciseSnapshot.child("AnswerOptions2").getValue(String::class.java) ?: "Unknown"
                        val AnswerOptions3 = exerciseSnapshot.child("AnswerOptions3").getValue(String::class.java) ?: "Unknown"
                        val AnswerOptions4 = exerciseSnapshot.child("AnswerOptions4").getValue(String::class.java) ?: "Unknown"
                        val CorrectAnswer = exerciseSnapshot.child("CorrectAnswer").getValue(String::class.java) ?: "Unknown"
                        // Создаём объект User и добавляем в список
                        val exercise = Exercise(ExerciseId = ExerciseId,
                            LessonsId = LessonsId,
                            Question = Question,
                            AnswerOptions1 = AnswerOptions1,
                            AnswerOptions2 = AnswerOptions2,
                            AnswerOptions3 = AnswerOptions3,
                            AnswerOptions4 = AnswerOptions4,
                            CorrectAnswer = CorrectAnswer)
                        exerciseList.add(exercise)
                    }
                }
            }
        }

        val exr = findViewById<TextView>(R.id.questionText)



    }
}