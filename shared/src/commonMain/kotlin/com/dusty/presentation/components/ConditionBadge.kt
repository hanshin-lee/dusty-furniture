package com.dusty.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dusty.data.model.ListingCondition
import com.dusty.presentation.theme.*

@Composable
fun ConditionBadge(
    condition: ListingCondition,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (condition) {
        ListingCondition.MINT -> ForestGreen to WarmWhite
        ListingCondition.EXCELLENT -> SageGreen to WarmWhite
        ListingCondition.GOOD -> AntiqueGold to DarkEspresso
        ListingCondition.FAIR -> LightWarmGray to DarkEspresso
        ListingCondition.POOR -> WarmGray to WarmWhite
        ListingCondition.FOR_PARTS -> RustRed to WarmWhite
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = backgroundColor
    ) {
        Text(
            text = condition.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
