/*
 * Copyright (C) 2025-2026 AxionOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.axion.compose.preferences

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt

@Composable
fun CustomSeekBar(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int,
    modifier: Modifier = Modifier,
    summary: String = "",
    defaultValue: Int = min,
    enabled: Boolean = true,
    formatValue: ((Int) -> String)? = null,
    position: PreferencePosition = LocalPreferencePosition.current,
) {
    val clamped = value.coerceIn(min, max)
    SliderPreference(
        title = title,
        summary = summary,
        value = clamped.toFloat(),
        onValueChange = { onValueChange(it.roundToInt().coerceIn(min, max)) },
        onValueChangeFinished = {},
        valueRange = min.toFloat()..max.toFloat(),
        displayValue = formatValue?.invoke(clamped) ?: clamped.toString(),
        modifier = modifier,
        enabled = enabled,
        position = position,
        onReset = { onValueChange(defaultValue) },
    )
}
