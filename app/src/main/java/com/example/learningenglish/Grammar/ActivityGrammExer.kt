package com.example.learningenglish.Grammar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.learningenglish.R
import com.example.learningenglish.dataClasses.Exercise
import com.google.firebase.database.FirebaseDatabase

class ActivityGrammExer : AppCompatActivity() {

    val exerciseList = mutableListOf<Exercise>()

    private var currentExerciseIndex = 0 // Индекс текущего упражнения
    private var correctAnswersCount = 0 // Количество правильных ответов
    private var totalAnswersCount = 0 // Общее количество вопросов

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gramm_exer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val userUID = sharedPreferences.getString("userUID", null)

        // Загружаем данные упражнений
        val idLes = intent.getIntExtra("lessonID", 0)
        val database = FirebaseDatabase.getInstance()
        val exerciseRef = database.getReference("ExerciseG")
        exerciseRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    // Перебираем упражнения и добавляем в список
                    for (exerciseSnapshot in snapshot.children) {
                        val ExerciseId = exerciseSnapshot.key?.toInt() ?: continue
                        val LessonsId =
                            exerciseSnapshot.child("LessonsID").getValue(Int::class.java) ?: 0
                        val Question =
                            exerciseSnapshot.child("Question").getValue(String::class.java)
                                ?: "Unknown"
                        val AnswerOptions1 =
                            exerciseSnapshot.child("AnswerOptions1").getValue(String::class.java)
                                ?: "Unknown"
                        val AnswerOptions2 =
                            exerciseSnapshot.child("AnswerOptions2").getValue(String::class.java)
                                ?: "Unknown"
                        val AnswerOptions3 =
                            exerciseSnapshot.child("AnswerOptions3").getValue(String::class.java)
                                ?: "Unknown"
                        val AnswerOptions4 =
                            exerciseSnapshot.child("AnswerOptions4").getValue(String::class.java)
                                ?: "Unknown"
                        val CorrectAnswer =
                            exerciseSnapshot.child("CorrectAnswer").getValue(String::class.java)
                                ?: "Unknown"

                        val exercise = Exercise(
                            ExerciseId,
                            LessonsId,
                            Question,
                            AnswerOptions1,
                            AnswerOptions2,
                            AnswerOptions3,
                            AnswerOptions4,
                            CorrectAnswer
                        )
                        if (exercise.LessonsId == idLes) {
                            exerciseList.add(exercise)
                        }
                    }
                }
                exerciseList.shuffle()
            }

            // После загрузки данных, выводим первое упражнение
            if (exerciseList.isNotEmpty()) {
                displayExercise(exerciseList[currentExerciseIndex])
            } else {
                Toast.makeText(this, "Нет упражнений для этого урока", Toast.LENGTH_SHORT).show()
            }
        }

        val checkButton = findViewById<Button>(R.id.checkButton)
        val nextQuestionButton = findViewById<Button>(R.id.nextQuestionButton)

        // Слушатель для кнопки "Проверить"
        checkButton.setOnClickListener {
            checkAnswer(userUID)
        }

        // Кнопка "Следующий вопрос", скрыта до ответа
        nextQuestionButton.setOnClickListener {
            if (checkButton.isEnabled == true){
                Toast.makeText(this, "Выберите и проверьте ответ", Toast.LENGTH_SHORT).show()
            }else{
                loadNextQuestion()
            }
        }
    }

    private fun displayExercise(exercise: Exercise) {
        val exerciseNumber = currentExerciseIndex + 1
        // Заполнение UI данными из текущего упражнения
        findViewById<TextView>(R.id.exerciseNumber).text = "Упражнение $exerciseNumber"
        findViewById<TextView>(R.id.questionText).text = exercise.Question
        findViewById<RadioButton>(R.id.answerOption1).text = exercise.AnswerOptions1
        findViewById<RadioButton>(R.id.answerOption2).text = exercise.AnswerOptions2
        findViewById<RadioButton>(R.id.answerOption3).text = exercise.AnswerOptions3
        findViewById<RadioButton>(R.id.answerOption4).text = exercise.AnswerOptions4
    }

    private fun checkAnswer(userUID: String?) {
        val selectedAnswer = findViewById<RadioGroup>(R.id.answersRadioGroup)
            .checkedRadioButtonId
        val correctAnswer = exerciseList[currentExerciseIndex].CorrectAnswer

        // Проверка правильности ответа
        val answerText = findViewById<RadioButton>(selectedAnswer)?.text.toString()
        if (answerText == correctAnswer) {
            correctAnswersCount++
            Toast.makeText(this, "Правильный ответ!", Toast.LENGTH_SHORT).show()

            val database = FirebaseDatabase.getInstance()
            val corA = database.getReference("users").child(userUID.toString()).child("correctAnswer").get()
            corA.addOnSuccessListener { dataSnapshot ->
                var corrAns = dataSnapshot.getValue(Int::class.java)
                if (corrAns != null) {
                    corrAns = corrAns + 1
                }
                database.getReference("users").child(userUID.toString()).child("correctAnswer").setValue(corrAns)
            }
        } else {
            Toast.makeText(this, "Неправильный ответ.", Toast.LENGTH_SHORT).show()

            val database = FirebaseDatabase.getInstance()
            val wrongA = database.getReference("users").child(userUID.toString()).child("wrongAnswer").get()
            wrongA.addOnSuccessListener { dataSnapshot ->
                var wrongAns = dataSnapshot.getValue(Int::class.java)
                if (wrongAns != null) {
                    wrongAns = wrongAns + 1
                }
                database.getReference("users").child(userUID.toString()).child("wrongAnswer").setValue(wrongAns)
            }
        }

        totalAnswersCount++

        // Сделать кнопки проверки и смены ответа недоступными
        findViewById<Button>(R.id.checkButton).isEnabled = false
        findViewById<RadioGroup>(R.id.answersRadioGroup).isEnabled = false

        // Сделать кнопку "Следующий вопрос" доступной
        findViewById<Button>(R.id.nextQuestionButton).isEnabled = true
    }

    private fun loadNextQuestion() {
        if (currentExerciseIndex < exerciseList.size - 1) {
            currentExerciseIndex++
            displayExercise(exerciseList[currentExerciseIndex])

            // Сделать кнопки доступными для следующего вопроса
            findViewById<Button>(R.id.checkButton).isEnabled = true
            findViewById<RadioGroup>(R.id.answersRadioGroup).isEnabled = true
            findViewById<Button>(R.id.nextQuestionButton).isEnabled = false
        } else {
            // Переход к результатам, если вопросы закончились
            val intent = Intent(this, ResultsActivity::class.java)
            intent.putExtra("correctAnswers", correctAnswersCount)
            intent.putExtra("totalAnswers", totalAnswersCount)
            startActivity(intent)
            finish()
        }
    }

}