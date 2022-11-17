package com.grupo14.gym_androidapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> AdaptibleList(
    items: List<T>,
    content: @Composable (item: T) -> Unit
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        // contentPadding = PaddingValues(top = 0.dp),
        state = rememberLazyListState(),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(items) { item -> content(item) }
    }
}