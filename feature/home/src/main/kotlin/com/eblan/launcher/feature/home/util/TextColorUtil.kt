/*
 *
 *   Copyright 2023 Einstein Blanco
 *
 *   Licensed under the GNU General Public License v3.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.gnu.org/licenses/gpl-3.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package com.eblan.launcher.feature.home.util

import androidx.compose.ui.graphics.Color
import com.eblan.launcher.domain.model.TextColor

internal fun getGridItemTextColor(
    systemTextColor: TextColor,
    systemCustomTextColor: Int,
    gridItemTextColor: TextColor,
    gridItemCustomTextColor: Int,
): Color = when (gridItemTextColor) {
    TextColor.System -> {
        getSystemTextColor(
            systemTextColor = systemTextColor,
            systemCustomTextColor = systemCustomTextColor,
        )
    }

    TextColor.Light -> {
        Color.White
    }

    TextColor.Dark -> {
        Color.Black
    }

    TextColor.Custom -> {
        Color(gridItemCustomTextColor)
    }
}

internal fun getSystemTextColor(
    systemTextColor: TextColor,
    systemCustomTextColor: Int,
): Color = when (systemTextColor) {
    TextColor.System -> {
        Color.Unspecified
    }

    TextColor.Light -> {
        Color.White
    }

    TextColor.Dark -> {
        Color.Black
    }

    TextColor.Custom -> {
        Color(systemCustomTextColor)
    }
}
