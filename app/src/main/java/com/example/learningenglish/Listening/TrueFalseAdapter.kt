package com.example.learningenglish.Listening

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learningenglish.R
import com.example.learningenglish.dataClasses.Exercise
import com.example.learningenglish.dataClasses.Lessons
import com.google.firebase.database.FirebaseDatabase


class TrueFalseAdapter(
    val myDataset: MutableList<Exercise>,
    val userUID: String,
    private val listener: TrueFalseListener
): RecyclerView.Adapter<TrueFalseHolder>() {

    val userAnswers = mutableMapOf<Int, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrueFalseHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.true_false_item,
            parent,
            false
        )
        return TrueFalseHolder(view)
    }

    interface TrueFalseListener {
        fun onAnswerSelected(correctCount: Int, totalQuestions: Int)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    override fun onBindViewHolder(holder: TrueFalseHolder, position: Int) {
        holder.bind(myDataset[position], userUID) { isCorrect ->
            userAnswers[position] = isCorrect
            val correctCount = userAnswers.values.count { it }
            listener.onAnswerSelected(correctCount,myDataset.size)
        }
    }
}

class TrueFalseHolder(private val view: View): RecyclerView.ViewHolder(view){
    fun bind(Exercise: Exercise,userUID: String,onAnswerChecked: (Boolean) -> Unit) {
        val TB = view.findViewById<RadioButton>(R.id.true_button)
        val FB = view.findViewById<RadioButton>(R.id.false_button)
        val Q = view.findViewById<TextView>(R.id.true_false_sentence)
        Q.text = Exercise.Question1


        val correctAnswer = Exercise.CorrectAnswer.toString()

        val radioGroup = view.findViewById<RadioGroup>(R.id.true_false_group)

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
            val answerText = selectedRadioButton?.text.toString()
            Log.i("TrueFalseAdapter",  "Selected answer: $answerText")
            Log.i("TrueFalseAdapter",  "c answer: $correctAnswer")

            // Проверка правильности ответа
            val isCorrect = answerText == correctAnswer
            onAnswerChecked(isCorrect)
        }
    }
}
