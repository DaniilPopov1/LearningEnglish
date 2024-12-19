package com.example.learningenglish.dataClasses

data class Users(
    val login:String?=null,
    val email:String?=null,
    val uid:String?=null,
    val correctAnswer:Int?=null,
    val wrongAnswer:Int?=null
)
