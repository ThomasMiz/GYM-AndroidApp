package com.grupo14.gym_androidapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize

@Composable
fun MyDropDownMenu(
    elements: List<String>,
    selectedText: String,
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean = true,
    onValueChanged: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val suggestions = elements

    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Column(
        Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { onValueChanged(it) },
            modifier = modifier
                .onGloballyPositioned { coordinates ->
                    textfieldSize = coordinates.size.toSize()
                },
            label = { Text(text = label, color = Color.Black) },
            placeholder = { Text(text = label, color = Color.Black) },
            singleLine = true,
            trailingIcon = {
                if (enabled) Icon(icon, null, Modifier.clickable { expanded = !expanded })
            },
            shape = RoundedCornerShape(50),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            readOnly = true,
            enabled = enabled
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textfieldSize.width.toDp() })
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onValueChanged(label)
                }) {
                    Text(text = label)
                }
            }
        }
    }
}