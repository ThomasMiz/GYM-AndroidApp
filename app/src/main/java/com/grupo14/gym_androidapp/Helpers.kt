package com.grupo14.gym_androidapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.grupo14.gym_androidapp.api.models.Gender
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


fun ConvertDateToLocalDate(date: Date): LocalDate {
    return Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun ConvertLocalDateToDate(localDate: LocalDate): Date {
    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
}

@Composable
fun createGendersList(): MutableList<String> {
    var genderOptionsList = mutableListOf("", "", "")
    genderOptionsList[Gender.MALE.ordinal] = stringResource(R.string.male)
    genderOptionsList[Gender.FEMALE.ordinal] = stringResource(R.string.female)
    genderOptionsList[Gender.OTHER.ordinal] = stringResource(R.string.other)
    return genderOptionsList
}

fun formatDate(date: Date): String {
    // TODO: Localization somehow idk
    val instant = Instant.ofEpochMilli(date.time).atZone(ZoneId.systemDefault())
    return "${instant.dayOfMonth}/${instant.month.value}/${instant.year}";
}

fun formatDate(date: LocalDate): String {
    return formatDate(ConvertLocalDateToDate(date))
}