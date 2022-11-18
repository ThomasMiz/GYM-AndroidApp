package com.grupo14.gym_androidapp

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.grupo14.gym_androidapp.api.models.Gender
import com.grupo14.gym_androidapp.navigation.myLittlePony
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

fun NavHostController.navigateAndReplaceStartRoute(route: String) {
    popBackStack(graph.startDestinationId, true)
    graph.setStartDestination(route)
    navigate(route)
}

fun SanitizeAndShit(s: String?): String {
    return s?.filter { c ->
        (c in 'a'..'z') || (c in 'A'..'Z') || (c in '0'..'9') || (c == '_') || (c == '.') || (c == ' ')
    }?.trim() ?: ""
}

fun getErrorStringIdForHttpCode(code: Int?): Int {
    return when (code) {
        400 -> R.string.serverCommunicationError
        500 -> R.string.serverInternalError
        else -> R.string.serverUnknownError
    }
}

@Composable
fun getCurrentMaxWidth(): Int {
    val configuration = LocalConfiguration.current
    var width = configuration.screenWidthDp
    if (myLittlePony?.uiState?.currentScreen?.showBottomAppBar == true && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        width -= 80
    return width
}