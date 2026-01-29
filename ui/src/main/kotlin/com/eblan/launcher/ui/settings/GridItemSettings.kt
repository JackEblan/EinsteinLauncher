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
package com.eblan.launcher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.eblan.launcher.domain.model.GridItemSettings
import com.eblan.launcher.domain.model.HorizontalAlignment
import com.eblan.launcher.domain.model.TextColor
import com.eblan.launcher.domain.model.VerticalArrangement
import com.eblan.launcher.ui.dialog.ColorPickerDialog
import com.eblan.launcher.ui.dialog.RadioOptionsDialog
import com.eblan.launcher.ui.dialog.SingleTextFieldDialog
import com.eblan.launcher.ui.dialog.TextColorDialog

@Composable
fun GridItemSettings(
    modifier: Modifier = Modifier,
    gridItemSettings: GridItemSettings,
    onUpdateGridItemSettings: (GridItemSettings) -> Unit,
) {
    var showIconSizeDialog by remember { mutableStateOf(false) }

    var showTextColorDialog by remember { mutableStateOf(false) }

    var showTextSizeDialog by remember { mutableStateOf(false) }

    var showBackgroundColorDialog by remember { mutableStateOf(false) }

    var showPaddingDialog by remember { mutableStateOf(false) }

    var showCornerRadiusDialog by remember { mutableStateOf(false) }

    var showHorizontalAlignment by remember { mutableStateOf(false) }

    var showVerticalArrangement by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(15.dp),
            text = "Grid Item",
            style = MaterialTheme.typography.bodySmall,
        )

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
        ) {
            SettingsColumn(
                title = "Icon Size",
                subtitle = "${gridItemSettings.iconSize}",
                onClick = {
                    showIconSizeDialog = true
                },
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            TextColorSettingsRow(
                gridItemSettings = gridItemSettings,
                onClick = {
                    showTextColorDialog = true
                },
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            SettingsColumn(
                title = "Text Size",
                subtitle = "${gridItemSettings.textSize}",
                onClick = {
                    showTextSizeDialog = true
                },
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            CustomColorSettingsRow(
                title = "Background Color",
                customColor = gridItemSettings.customBackgroundColor,
                onClick = {
                    showBackgroundColorDialog = true
                },
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            SettingsColumn(
                title = "Padding",
                subtitle = "${gridItemSettings.padding}",
                onClick = {
                    showPaddingDialog = true
                },
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            SettingsColumn(
                title = "Corner Radius",
                subtitle = "${gridItemSettings.cornerRadius}",
                onClick = {
                    showCornerRadiusDialog = true
                },
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            SettingsSwitch(
                checked = gridItemSettings.showLabel,
                title = "Show Label",
                subtitle = "Show label",
                onCheckedChange = { showLabel ->
                    onUpdateGridItemSettings(gridItemSettings.copy(showLabel = showLabel))
                },
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            SettingsSwitch(
                checked = gridItemSettings.singleLineLabel,
                title = "Single Line Label",
                subtitle = "Show single line label",
                onCheckedChange = { singleLineLabel ->
                    onUpdateGridItemSettings(gridItemSettings.copy(singleLineLabel = singleLineLabel))
                },
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            SettingsColumn(
                title = "Horizontal Alignment",
                subtitle = gridItemSettings.horizontalAlignment.name.replace(
                    regex = Regex(pattern = "([a-z])([A-Z])"),
                    replacement = "$1 $2",
                ),
                onClick = {
                    showHorizontalAlignment = true
                },
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            SettingsColumn(
                title = "Vertical Arrangement",
                subtitle = gridItemSettings.verticalArrangement.name,
                onClick = {
                    showVerticalArrangement = true
                },
            )
        }
    }

    if (showIconSizeDialog) {
        var value by remember { mutableStateOf("${gridItemSettings.iconSize}") }

        var isError by remember { mutableStateOf(false) }

        SingleTextFieldDialog(
            title = "Icon Size",
            textFieldTitle = "Icon Size",
            value = value,
            isError = isError,
            keyboardType = KeyboardType.Number,
            onValueChange = {
                value = it
            },
            onDismissRequest = {
                showIconSizeDialog = false
            },
            onUpdateClick = {
                try {
                    onUpdateGridItemSettings(gridItemSettings.copy(iconSize = value.toInt()))

                    showIconSizeDialog = false
                } catch (_: NumberFormatException) {
                    isError = true
                }
            },
        )
    }

    if (showTextColorDialog) {
        TextColorDialog(
            title = "Text Color",
            textColor = gridItemSettings.textColor,
            customTextColor = gridItemSettings.customTextColor,
            onDismissRequest = {
                showTextColorDialog = false
            },
            onUpdateClick = { textColor, customTextColor ->
                onUpdateGridItemSettings(
                    gridItemSettings.copy(
                        textColor = textColor,
                        customTextColor = customTextColor,
                    ),
                )

                showTextColorDialog = false
            },
        )
    }

    if (showTextSizeDialog) {
        var value by remember { mutableStateOf("${gridItemSettings.textSize}") }

        var isError by remember { mutableStateOf(false) }

        SingleTextFieldDialog(
            title = "Text Size",
            textFieldTitle = "Text Size",
            value = value,
            isError = isError,
            keyboardType = KeyboardType.Number,
            onValueChange = {
                value = it
            },
            onDismissRequest = {
                showTextSizeDialog = false
            },
            onUpdateClick = {
                try {
                    onUpdateGridItemSettings(gridItemSettings.copy(textSize = value.toInt()))

                    showTextSizeDialog = false
                } catch (_: NumberFormatException) {
                    isError = true
                }
            },
        )
    }

    if (showBackgroundColorDialog) {
        ColorPickerDialog(
            customColor = gridItemSettings.customBackgroundColor,
            onDismissRequest = {
                showBackgroundColorDialog = false
            },
            onColorSelected = { newCustomColor ->
                onUpdateGridItemSettings(gridItemSettings.copy(customBackgroundColor = newCustomColor))

                showBackgroundColorDialog = false
            },
        )
    }

    if (showPaddingDialog) {
        var value by remember { mutableStateOf("${gridItemSettings.padding}") }

        var isError by remember { mutableStateOf(false) }

        SingleTextFieldDialog(
            title = "Padding",
            textFieldTitle = "Padding",
            value = value,
            isError = isError,
            keyboardType = KeyboardType.Number,
            onValueChange = {
                value = it
            },
            onDismissRequest = {
                showPaddingDialog = false
            },
            onUpdateClick = {
                try {
                    onUpdateGridItemSettings(gridItemSettings.copy(padding = value.toInt()))

                    showPaddingDialog = false
                } catch (_: NumberFormatException) {
                    isError = true
                }
            },
        )
    }

    if (showCornerRadiusDialog) {
        var value by remember { mutableStateOf("${gridItemSettings.cornerRadius}") }

        var isError by remember { mutableStateOf(false) }

        SingleTextFieldDialog(
            title = "Corner Radius",
            textFieldTitle = "Corner Radius",
            value = value,
            isError = isError,
            keyboardType = KeyboardType.Number,
            onValueChange = {
                value = it
            },
            onDismissRequest = {
                showCornerRadiusDialog = false
            },
            onUpdateClick = {
                try {
                    onUpdateGridItemSettings(gridItemSettings.copy(cornerRadius = value.toInt()))

                    showCornerRadiusDialog = false
                } catch (_: NumberFormatException) {
                    isError = true
                }
            },
        )
    }

    if (showHorizontalAlignment) {
        RadioOptionsDialog(
            title = "Horizontal Alignment",
            options = HorizontalAlignment.entries,
            selected = gridItemSettings.horizontalAlignment,
            label = {
                it.name.replace(
                    regex = Regex(pattern = "([a-z])([A-Z])"),
                    replacement = "$1 $2",
                )
            },
            onDismissRequest = {
                showHorizontalAlignment = false
            },
            onUpdateClick = { horizontalAlignment ->
                onUpdateGridItemSettings(gridItemSettings.copy(horizontalAlignment = horizontalAlignment))

                showHorizontalAlignment = false
            },
        )
    }

    if (showVerticalArrangement) {
        RadioOptionsDialog(
            title = "Vertical Arrangement",
            options = VerticalArrangement.entries,
            selected = gridItemSettings.verticalArrangement,
            label = {
                it.name
            },
            onDismissRequest = {
                showVerticalArrangement = false
            },
            onUpdateClick = { verticalArrangement ->
                onUpdateGridItemSettings(gridItemSettings.copy(verticalArrangement = verticalArrangement))

                showVerticalArrangement = false
            },
        )
    }
}

@Composable
private fun TextColorSettingsRow(
    modifier: Modifier = Modifier,
    gridItemSettings: GridItemSettings,
    onClick: () -> Unit,
) {
    when (gridItemSettings.textColor) {
        TextColor.System,
        TextColor.Light,
        TextColor.Dark,
        -> {
            SettingsColumn(
                modifier = modifier,
                title = "Text Color",
                subtitle = gridItemSettings.textColor.name,
                onClick = onClick,
            )
        }

        TextColor.Custom -> {
            CustomColorSettingsRow(
                modifier = modifier,
                title = "Text Color",
                customColor = gridItemSettings.customTextColor,
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun CustomColorSettingsRow(
    modifier: Modifier = Modifier,
    title: String,
    customColor: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(customColor),
                    shape = CircleShape,
                ),
        )
    }
}
