package com.example.learningenglish.Listening


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learningenglish.R
import com.example.learningenglish.dataClasses.Exercise
import kotlin.math.E

class FillInTheBlankAdapter(
    val myDataset: MutableList<Exercise>,
    val userUID: String,
    private val listener: TrueFalseListener
): RecyclerView.Adapter<FillInTheBlankHolder>() {

    val userAnswers = mutableMapOf<Int, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FillInTheBlankHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fill_in_the_blank_item,
            parent,
            false
        )
        return FillInTheBlankHolder(view)
    }

    interface TrueFalseListener {
        fun onAnswerSelected1(correctCount: Int, totalQuestions: Int)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    override fun onBindViewHolder(holder: FillInTheBlankHolder, position: Int) {
        holder.bind(myDataset[position], userUID) { isCorrect ->
            userAnswers[position] = isCorrect
            val correctCount = userAnswers.values.count { it }
            listener.onAnswerSelected1(correctCount,myDataset.size)
        }
    }
}

class FillInTheBlankHolder(private val view: View): RecyclerView.ViewHolder(view){
    fun bind(Exercise: Exercise,userUID: String,onAnswerChecked: (Boolean) -> Unit) {
        val TVB = view.findViewById<TextView>(R.id.part_before_blank)
        val TVA = view.findViewById<TextView>(R.id.part_after_blank)
        val spinner = view.findViewById<Spinner>(R.id.fill_in_blank)

        TVB.text = Exercise.Question1
        TVA.text = Exercise.Question2

        val options = mutableListOf("Выберите ответ").apply{addAll(listOf(Exercise.AnswerOptions1,
            Exercise.AnswerOptions2,
            Exercise.AnswerOptions3,
            Exercise.AnswerOptions4).filterNotNull())
        }


        val correctAnswer = Exercise.CorrectAnswer

        val spinnerAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            options
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.setSelection(0)

        spinner.setOnItemSelectedListener(object :android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0){
                    onAnswerChecked(false)
                }
                else {
                    val selectedAnswer = options[position]
                    val isCorrect = selectedAnswer == correctAnswer
                    onAnswerChecked(isCorrect)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })
    }
}
