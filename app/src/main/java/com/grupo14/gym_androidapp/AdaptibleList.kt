package com.grupo14.gym_androidapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun <T> AdaptibleList(
    items: List<T>,
    content: @Composable (item: T) -> Unit
) {
    val maxItemWidth = 500

    val currentConfig = LocalConfiguration.current
    val columnCount = (currentConfig.screenWidthDp + maxItemWidth - 1) / maxItemWidth
    val maxRowCount = (items.size + columnCount - 1) / columnCount

    val paddingDp = 10
    val itemWidth = (currentConfig.screenWidthDp - columnCount * paddingDp) / columnCount

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(paddingDp.dp),
        // contentPadding = PaddingValues(top = 0.dp),
        state = rememberLazyListState()
    ) {
        items(maxRowCount) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(paddingDp.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val startIndex = rowIndex * columnCount
                val endIndex = Math.min(startIndex + columnCount, items.size) - 1
                (startIndex..endIndex).forEach { index ->
                    Box(
                        modifier = Modifier.width(itemWidth.dp)
                    ) {
                        content(items[index])
                    }
                    println("SCRWIDTH=${currentConfig.screenWidthDp} MAXWIDTH=$maxItemWidth COLCOUNT=$columnCount ITEMS=${items.size} MAXROWS=$maxRowCount ROWINDEX=$rowIndex STARTINDEX=$startIndex ENDINDEX=$endIndex INDEX=$index")
                }
            }
        }
    }
}