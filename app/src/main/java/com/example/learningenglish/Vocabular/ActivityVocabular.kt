package com.example.learningenglish.Vocabular

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningenglish.R
import com.example.learningenglish.dataClasses.Lessons
import com.google.firebase.database.FirebaseDatabase

class ActivityVocabular : AppCompatActivity() {

    val lessonList = mutableListOf<Lessons>()

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vocabular)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.hh)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val database = FirebaseDatabase.getInstance()
        val lessonsRef = database.getReference("lessonsV")

        lessonsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    // Перебираем всех пользователей
                    for (lessonsSnapshot in snapshot.children) {
                        val LessonId = lessonsSnapshot.key?.toInt() ?: continue
                        val Title = lessonsSnapshot.child("Title").getValue(String::class.java) ?: lessonsSnapshot.child("Title").value?.toString() ?: "Unknown"
                        val Theme = lessonsSnapshot.child("Theme").getValue(String::class.java) ?: lessonsSnapshot.child("Theme").value?.toString() ?: "Unknown"
                        val Theory = lessonsSnapshot.child("Theory").getValue(String::class.java) ?: lessonsSnapshot.child("Theory").value?.toString() ?: "Unknown"

                        val lesson = Lessons(LessonId = LessonId,
                            Title = Title,
                            Theme = Theme,
                            Theory = Theory)
                        lessonList.add(lesson)
                    }
                    (mRecyclerView.adapter as RVAdapter).notifyDataSetChanged()
                    lessonList.forEach { user ->
                        println("ID: ${user.LessonId}, Name: ${user.Theme}, Email: ${user.Title}")
                    }
                }
            }
        }
        mRecyclerView = findViewById(R.id.my_recycler_view)
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = LinearLayoutManager(this.applicationContext)
        mRecyclerView.layoutManager = mLayoutManager

        val mAdapter = RVAdapter(lessonList)
        mRecyclerView.adapter = mAdapter
    }
}