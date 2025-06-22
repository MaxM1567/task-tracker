package com.example.task_tracker.ui.screens.home

import java.time.LocalDate

data class Date(
    val day: Int,
    val month: String,
    val dayOfWeek: String
)

fun getFormattedDate(currentDate: LocalDate = LocalDate.now()): Date {
    val monthNames = listOf(
        "Января", "Февраля", "Марта", "Апреля", "Мая", "Июня",
        "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"
    )
    val dayNames = listOf(
        "Понедельник", "Вторник", "Среда",
        "Четверг", "Пятница", "Суббота", "Воскресенье"
    )

    return Date(
        day = currentDate.dayOfMonth,
        month = monthNames[currentDate.monthValue - 1],
        dayOfWeek = dayNames[currentDate.dayOfWeek.value - 1]
    )
}