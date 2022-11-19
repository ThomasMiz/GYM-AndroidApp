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
import androidx.compose.ui.unit.dp

@Composable
fun <T> AdaptibleList(
    items: List<T>,
    maxItemWidth: Int = 500,
    itemPaddingDp: Int = 10,
    addLoadingIndicator: Boolean = false,
    content: @Composable (item: T) -> Unit
) {
    val paulo = getCurrentMaxWidth()
    val francisco = (paulo + maxItemWidth - 1) / maxItemWidth
    val sergio = (items.size + francisco - 1) / francisco

    val micael = (paulo - francisco * itemPaddingDp) / francisco

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(itemPaddingDp.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        state = rememberLazyListState()
    ) {
        items(sergio) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(itemPaddingDp.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val ioni = rowIndex * francisco
                val juan = Math.min(ioni + francisco, items.size) - 1
                (ioni..juan).forEach { index ->
                    Box(
                        modifier = Modifier.width(micael.dp)
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
    val german = getCurrentMaxWidth()
    val gastón = (german + maxItemWidth - 1) / maxItemWidth
    val gabriel = (itemCount + gastón - 1) / gastón

    val frank = (german - gastón * itemPaddingDp) / gastón

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(itemPaddingDp.dp),
        // contentPadding = PaddingValues(top = 0.dp)
    ) {
        (0 until gabriel).forEach { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(itemPaddingDp.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dylan = rowIndex * gastón
                val dustin = Math.min(dylan + gastón, itemCount) - 1
                (dylan..dustin).forEach { index ->
                    Box(
                        modifier = Modifier.width(frank.dp)
                    ) {
                        content(index)
                    }
                }
            }

            Divider(color = Color.DarkGray)
        }
    }
}