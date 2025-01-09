package com.example.learningenglish.Listening

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningenglish.R
import com.example.learningenglish.dataClasses.Exercise
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.E

class ActivityListeningExer : AppCompatActivity(), TrueFalseAdapter.TrueFalseListener, FillInTheBlankAdapter.TrueFalseListener {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var playButton: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var audioTimer: TextView

    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper()) // Для обновления UI

    val exerciseList = mutableListOf<Exercise>()
    val exerciseList1 = mutableListOf<Exercise>()

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mRecyclerView1: RecyclerView
    private lateinit var mLayoutManager1: RecyclerView.LayoutManager

    private var correctAnswers = 0
    private var totalQuestions = 0
    private var correctAnswers1 = 0
    private var totalQuestions1 = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listening_exer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val idLes = intent.getIntExtra("lessonID", 0)

        val topicTitle: TextView = findViewById(R.id.topic_title)
        playButton = findViewById(R.id.play_button)
        seekBar = findViewById(R.id.audio_seekbar)
        audioTimer = findViewById(R.id.audio_timer)

        val topic = intent.getStringExtra("lessonTheme")
        topicTitle.text = topic


        val audioUrl = intent.getStringExtra("theoryLesson")
        if (audioUrl != null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl)
                prepare()
            }

            // Установка максимального значения SeekBar
            seekBar.max = mediaPlayer.duration

            // Настройка кнопки воспроизведения
            playButton.setOnClickListener {
                if (isPlaying) {
                    mediaPlayer.pause()
                    playButton.setImageResource(android.R.drawable.ic_media_play)
                } else {
                    mediaPlayer.start()
                    playButton.setImageResource(android.R.drawable.ic_media_pause)
                    startUpdatingSeekBar() // Начать обновление SeekBar и таймера
                }
                isPlaying = !isPlaying
            }

            // Обработка перемотки
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress)
                        updateTimer(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            mediaPlayer.setOnCompletionListener {
                isPlaying = false
                playButton.setImageResource(android.R.drawable.ic_media_play)
                seekBar.progress = 0
                updateTimer(0)
            }
        } else {
            topicTitle.text = "Ошибка загрузки аудио"
        }

        val sharedPreferences = getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val userUID = sharedPreferences.getString("userUID", null)

        val database = FirebaseDatabase.getInstance()
        val exerciseRef = database.getReference("ExerciseL")
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
                        val Type =
                            exerciseSnapshot.child("type").getValue(String::class.java)
                                ?: "Unknown"
                        val CorrectAnswer =
                            exerciseSnapshot.child("CorrectAnswer").getValue(String::class.java)
                                ?: "Unknown"

                        val exercise = Exercise(
                            ExerciseId,
                            LessonsId,
                            Question1,
                            "",
                            "",
                            "",
                            "",
                            "",
                            CorrectAnswer
                        )
                        if (exercise.LessonsId == idLes && Type == "t/f") {
                            exerciseList.add(exercise)
                        }
                    }
                    (mRecyclerView.adapter as TrueFalseAdapter).notifyDataSetChanged()
                }
                //exerciseList.shuffle()
            }

            if (!exerciseList.isNotEmpty()) {
                Toast.makeText(this, "Нет упражнений для этого урока", Toast.LENGTH_SHORT).show()
            }
        }

        mRecyclerView = findViewById(R.id.true_false_recycler_view)
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = LinearLayoutManager(this.applicationContext)
        mRecyclerView.layoutManager = mLayoutManager

        val mAdapter = TrueFalseAdapter(exerciseList, userUID!!,this)
        mRecyclerView.adapter = mAdapter

        totalQuestions = exerciseList.size

        val resBut = findViewById<Button>(R.id.show_results_button)
        resBut.setOnClickListener {
            showResults(idLes)
        }

        val database1 = FirebaseDatabase.getInstance()
        val exerciseRef1 = database1.getReference("ExerciseL")
        exerciseRef1.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot != null && snapshot.exists()) {
                    for (exerciseSnapshot in snapshot.children) {
                        val ExerciseId = exerciseSnapshot.key?.toInt() ?: continue
                        val LessonsId =
                            exerciseSnapshot.child("LessonsID").getValue(Int::class.java) ?: 0
                        val Question1 =
                            exerciseSnapshot.child("Question1").getValue(String::class.java)
                                ?: "Unknown"
                        val Question2 =
                            exerciseSnapshot.child("Question2").getValue(String::class.java)
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
                        val Type =
                            exerciseSnapshot.child("type").getValue(String::class.java)
                                ?: "Unknown"

                        val exercise = Exercise(
                            ExerciseId,
                            LessonsId,
                            Question1,
                            Question2,
                            AnswerOptions1,
                            AnswerOptions2,
                            AnswerOptions3,
                            AnswerOptions4,
                            CorrectAnswer
                        )
                        if (exercise.LessonsId == idLes && Type == "fill") {
                            exerciseList1.add(exercise)
                        }
                    }
                    (mRecyclerView1.adapter as FillInTheBlankAdapter).notifyDataSetChanged()
                }
                //exerciseList.shuffle()
            }

            if (!exerciseList1.isNotEmpty()) {
                Toast.makeText(this, "Нет упражнений для этого урока", Toast.LENGTH_SHORT).show()
            }
        }

        mRecyclerView1 = findViewById(R.id.fill_in_blank_recycler_view)
        mRecyclerView1.setHasFixedSize(true);
        mLayoutManager1 = LinearLayoutManager(this.applicationContext)
        mRecyclerView1.layoutManager = mLayoutManager1

        val mAdapter1 = FillInTheBlankAdapter(exerciseList1, userUID!!,this)
        mRecyclerView1.adapter = mAdapter1

        totalQuestions1 = exerciseList.size

    }

    override fun onAnswerSelected(correctCount: Int, totalQuestions: Int) {
        this.correctAnswers = correctCount
        this.totalQuestions = totalQuestions
    }

    override fun onAnswerSelected1(correctCount: Int, totalQuestions: Int) {
        this.correctAnswers1 = correctCount
        this.totalQuestions1 = totalQuestions
    }

    private fun showResults(idLes:Int) {
        if (exerciseList.size == (mRecyclerView.adapter as TrueFalseAdapter).userAnswers.size){
            val sharedPreferences = getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
            val userUID = sharedPreferences.getString("userUID", null)

            val database = FirebaseDatabase.getInstance()
            val corA = database.getReference("users").child(userUID.toString()).child("correctAnswer").get()
            corA.addOnSuccessListener { dataSnapshot ->
                var corrAns = dataSnapshot.getValue(Int::class.java)
                if (corrAns != null) {
                    corrAns = corrAns + correctAnswers+ correctAnswers1
                }
                database.getReference("users").child(userUID.toString()).child("correctAnswer").setValue(corrAns)
            }
            val wrongA = database.getReference("users").child(userUID.toString()).child("wrongAnswer").get()
            wrongA.addOnSuccessListener { dataSnapshot ->
                var wrongAns = dataSnapshot.getValue(Int::class.java)
                if (wrongAns != null) {
                    wrongAns = wrongAns + (totalQuestions+totalQuestions1 - correctAnswers -
                            correctAnswers1)
                }
                database.getReference("users").child(userUID.toString()).child("wrongAnswer").setValue(wrongAns)
            }

            val intent = Intent(this, ResultsActivity::class.java)
            intent.putExtra("correctAnswers", correctAnswers+correctAnswers1)
            intent.putExtra("totalAnswers", totalQuestions+totalQuestions1)
            intent.putExtra("lessonID",idLes)
            startActivity(intent)
            finish()
        }
        else{
            Toast.makeText(
                this,
                "Пожалуйста, ответьте на все вопросы перед завершением.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun startUpdatingSeekBar() {
        handler.post(object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    seekBar.progress = mediaPlayer.currentPosition
                    updateTimer(mediaPlayer.currentPosition)
                }
                handler.postDelayed(this, 500) // Обновлять каждые 500 мс
            }
        })
    }

    private fun updateTimer(milliseconds: Int) {
        val minutes = milliseconds / 1000 / 60
        val seconds = (milliseconds / 1000) % 60
        audioTimer.text = String.format("%d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        handler.removeCallbacksAndMessages(null)
    }
}

