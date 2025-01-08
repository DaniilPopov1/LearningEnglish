package com.example.learningenglish.dataClasses

data class Exercise(
    val ExerciseId:Int?=null,
    val LessonsId:Int?=null,
    val Question1:String?=null,
    val Question2:String?=null,
    val AnswerOptions1:String?=null,
    val AnswerOptions2:String?=null,
    val AnswerOptions3:String?=null,
    val AnswerOptions4:String?=null,
    val CorrectAnswer:String?=null
)
