package com.andriiginting.muvi.home.ui.filter

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Chip(
    name: String = "Chip",
    isSelected: Boolean = false,
    onSelectionChanged: (String) -> Unit = {},
) {
    val (background, border, text) = if (isSelected) Triple(0xFF2446A9, 0xFFB1BCBE, 0xFFFFFFFFF)
    else Triple(0xFFFFFFFFF, 0xFFB1BCBE, 0xFF0000000)
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .border(width = 1.dp, color = Color(border), shape = RoundedCornerShape(80.dp)),
        shape = RoundedCornerShape(80.dp),
        color = Color(background),
    ) {
        Row(modifier = Modifier
            .toggleable(
                value = isSelected,
                onValueChange = {
                    onSelectionChanged(name)
                }
            )
            .padding(horizontal = 8.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.body2,
                color = Color(text),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}