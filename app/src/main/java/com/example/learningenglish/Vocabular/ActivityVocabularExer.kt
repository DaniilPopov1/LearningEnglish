package com.example.learningenglish.Vocabular

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

class ActivityVocabularExer : AppCompatActivity() {

    val exerciseList = mutableListOf<Exercise>()

    private var currentExerciseIndex = 0
    private var correctAnswersCount = 0
    private var totalAnswersCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vocabular_exer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val userUID = sharedPreferences.getString("userUID", null)

        val idLes = intent.getIntExtra("lessonID", 0)
        val database = FirebaseDatabase.getInstance()
        val exerciseRef = database.getReference("ExerciseV")
        exerciseRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    for (exerciseSnapshot in snapshot.children) {
                        val ExerciseId = exerciseSnapshot.key?.toInt() ?: continue
                        val LessonsId =
                            exerciseSnapshot.child("LessonsID").getValue(Int::class.java) ?: 0
                        val Question1 =
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
                            Question1,
                            "",
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

            if (exerciseList.isNotEmpty()) {
                displayExercise(exerciseList[currentExerciseIndex])
            } else {
                Toast.makeText(this, "Нет упражнений для этого урока", Toast.LENGTH_SHORT).show()
            }
        }

        val checkButton = findViewById<Button>(R.id.checkButton)
        val nextQuestionButton = findViewById<Button>(R.id.nextQuestionButton)

        checkButton.setOnClickListener {
            val selectedId = findViewById<RadioGroup>(R.id.answersRadioGroup).checkedRadioButtonId
            if (selectedId != -1) {
                checkAnswer(userUID)
            } else {
                Toast.makeText(this, "Выберите ответ", Toast.LENGTH_SHORT).show()
            }
        }

        nextQuestionButton.setOnClickListener {
            if (checkButton.isEnabled == true){
                Toast.makeText(this, "Выберите и проверьте ответ", Toast.LENGTH_SHORT).show()
            }else{
                loadNextQuestion(idLes)
            }
        }
    }

    private fun displayExercise(exercise: Exercise) {
        val exerciseNumber = currentExerciseIndex + 1

        findViewById<TextView>(R.id.exerciseNumber).text = "Упражнение $exerciseNumber"
        findViewById<TextView>(R.id.questionText).text = exercise.Question1
        findViewById<RadioButton>(R.id.answerOption1).text = exercise.AnswerOptions1
        findViewById<RadioButton>(R.id.answerOption2).text = exercise.AnswerOptions2
        findViewById<RadioButton>(R.id.answerOption3).text = exercise.AnswerOptions3
        findViewById<RadioButton>(R.id.answerOption4).text = exercise.AnswerOptions4
    }

    private fun checkAnswer(userUID: String?) {
        val selectedAnswer = findViewById<RadioGroup>(R.id.answersRadioGroup)
            .checkedRadioButtonId
        val correctAnswer = exerciseList[currentExerciseIndex].CorrectAnswer

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

        findViewById<Button>(R.id.checkButton).isEnabled = false

        findViewById<Button>(R.id.nextQuestionButton).isEnabled = true
    }

    private fun loadNextQuestion(idLes: Int?) {
        if (currentExerciseIndex < exerciseList.size - 1) {
            currentExerciseIndex++
            displayExercise(exerciseList[currentExerciseIndex])

            findViewById<Button>(R.id.checkButton).isEnabled = true
            findViewById<RadioGroup>(R.id.answersRadioGroup).clearCheck()
        } else {
            val intent = Intent(this, ResultsActivity::class.java)
            intent.putExtra("correctAnswers", correctAnswersCount)
            intent.putExtra("totalAnswers", totalAnswersCount)
            intent.putExtra("lessonID",idLes)
            startActivity(intent)
            finish()
        }
    }

}