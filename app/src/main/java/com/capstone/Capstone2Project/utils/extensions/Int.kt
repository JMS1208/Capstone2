package com.capstone.Capstone2Project.utils.extensions

import android.content.res.Resources
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


internal fun Int.toDp(density: Density): Dp {
    return (this * density.density + 0.5f).toInt().dp
}
