package com.dusty.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dusty.data.model.ListingCondition
import com.dusty.presentation.theme.*

@Composable
fun ConditionBadge(
    condition: ListingCondition,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (condition) {
        ListingCondition.MINT -> ForestGreen to Color.White
        ListingCondition.EXCELLENT -> SageGreen to Color.White
        ListingCondition.GOOD -> AntiqueGold to DarkEspresso
        ListingCondition.FAIR -> LightWarmGray to DarkEspresso
        ListingCondition.POOR -> WarmGray to Color.White
        ListingCondition.FOR_PARTS -> RustRed to Color.White
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        color = backgroundColor.copy(alpha = 0.9f),
        shadowElevation = 1.dp
    ) {
        Text(
            text = condition.displayName,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
