package com.example.learningenglish.Vocabular

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.learningenglish.R
import com.example.learningenglish.dataClasses.Lessons
import com.google.firebase.database.FirebaseDatabase


class RVAdapter(val myDataset: MutableList<Lessons>, val userUID: String): RecyclerView.Adapter<RVHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_item,
            parent,
            false
        )
        return RVHolder(view)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    override fun onBindViewHolder(holder: RVHolder, position: Int) {
        holder.bind(myDataset[position], userUID)
    }
}

class RVHolder(private val view: View): RecyclerView.ViewHolder(view){
    fun bind(lesson: Lessons, userUID: String){
        val textView = view.findViewById<TextView>(R.id.my_text_view)
        val textView1 = view.findViewById<TextView>(R.id.lesson_description)
        val textView2 = view.findViewById<TextView>(R.id.lesson_status_value)
        textView.text = lesson.Title
        textView1.text = lesson.Theme

        val idLes = lesson.LessonId
        val uUID = userUID

        if (uUID != null) {
            val progressRef = FirebaseDatabase.getInstance().reference
                .child("progress")
                .child(uUID)
                .child("vocabular")
                .child(idLes.toString())

            progressRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    if (snapshot.exists()) {
                        val result = snapshot.child("result").getValue(String::class.java)
                        textView2.text = result ?: "Неизвестный статус"
                    } else {
                        textView2.text = "Не пройден"
                    }
                }
            }
        }

        view.setOnClickListener{
            Log.i("Click", lesson.Title.toString())
            val intent = Intent(view.context, ActivityVocabularTh::class.java).apply {
                putExtra("lessonID",lesson.LessonId)
                putExtra("lessonTheme",lesson.Theme)
                putExtra("theoryLesson",lesson.Theory)
            }
            view.context.startActivity(intent)
        }
    }
}
