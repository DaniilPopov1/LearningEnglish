package com.example.learningenglish.Grammar

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.learningenglish.R
import com.example.learningenglish.dataClsses.Lessons


class RVAdapter(val myDataset: MutableList<Lessons>): RecyclerView.Adapter<RVHolder>() {
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
        holder.bind(myDataset[position])
    }
}

class RVHolder(private val view: View): RecyclerView.ViewHolder(view){
    fun bind(lesson: Lessons){
        val textView = view.findViewById<TextView>(R.id.my_text_view)
        val textView1 = view.findViewById<TextView>(R.id.lesson_description)
        textView.text = lesson.Title
        textView1.text = lesson.Theme
        view.setOnClickListener{
            Log.i("Click", lesson.Title.toString())
            val intent = Intent(view.context, ActivityGrammTh::class.java).apply {
                putExtra("lessonID",lesson.LessonId)
                putExtra("lessonTheme",lesson.Theme)
            }
            view.context.startActivity(intent)
        }
    }
}
