package com.dusty.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.dusty.util.toPriceString

@Composable
fun PriceTag(
    price: Double,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$${price.toPriceString()}",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}
