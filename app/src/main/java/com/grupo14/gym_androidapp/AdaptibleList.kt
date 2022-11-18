package com.grupo14.gym_androidapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun <T> AdaptibleList(
    items: List<T>,
    maxItemWidth: Int = 500,
    itemPaddingDp: Int = 10,
    addLoadingIndicator: Boolean = false,
    content: @Composable (item: T) -> Unit
) {
    val currentWidth = getCurrentMaxWidth()
    val columnCount = (currentWidth + maxItemWidth - 1) / maxItemWidth
    val maxRowCount = (items.size + columnCount - 1) / columnCount

    val itemWidth = (currentWidth - columnCount * itemPaddingDp) / columnCount

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(itemPaddingDp.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        state = rememberLazyListState()
    ) {
        items(maxRowCount) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(itemPaddingDp.dp),
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
                }
            }
        }

        if (addLoadingIndicator) {
            item {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.secondaryVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
fun AdaptibleSimpleList(
    itemCount: Int,
    maxItemWidth: Int = 500,
    itemPaddingDp: Int = 10,
    content: @Composable (index: Int) -> Unit
) {
    val currentWidth = getCurrentMaxWidth()
    val columnCount = (currentWidth + maxItemWidth - 1) / maxItemWidth
    val maxRowCount = (itemCount + columnCount - 1) / columnCount

    val itemWidth = (currentWidth - columnCount * itemPaddingDp) / columnCount

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(itemPaddingDp.dp),
        // contentPadding = PaddingValues(top = 0.dp)
    ) {
        (0 until maxRowCount).forEach { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(itemPaddingDp.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val startIndex = rowIndex * columnCount
                val endIndex = Math.min(startIndex + columnCount, itemCount) - 1
                (startIndex..endIndex).forEach { index ->
                    Box(
                        modifier = Modifier.width(itemWidth.dp)
                    ) {
                        content(index)
                    }
                }
            }

            Divider(color = Color.DarkGray)
        }
    }
}