package com.example.learningenglish.dataClsses

data class Exercise(
    val ExerciseId:Int?=null,
    val LessonsId:Int?=null,
    val Question:String?=null,
    val AnswerOptions1:String?=null,
    val AnswerOptions2:String?=null,
    val AnswerOptions3:String?=null,
    val AnswerOptions4:String?=null,
    val CorrectAnswer:String?=null
)
