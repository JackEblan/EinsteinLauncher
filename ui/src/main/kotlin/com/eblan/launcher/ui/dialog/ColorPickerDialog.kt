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
package com.eblan.launcher.ui.dialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.eblan.launcher.designsystem.component.EblanDialogContainer

@Composable
fun ColorPickerDialog(
    modifier: Modifier = Modifier,
    customColor: Int,
    onDismissRequest: () -> Unit,
    onColorSelected: (Int) -> Unit,
) {
    val hsv = FloatArray(3).apply {
        android.graphics.Color.RGBToHSV(
            android.graphics.Color.red(customColor),
            android.graphics.Color.green(customColor),
            android.graphics.Color.blue(customColor),
            this,
        )
    }

    var hue by remember { mutableFloatStateOf(hsv[0]) }

    var saturation by remember { mutableFloatStateOf(hsv[1]) }

    var value by remember { mutableFloatStateOf(hsv[2]) }

    var alpha by remember { mutableFloatStateOf(Color(customColor).alpha) }

    EblanDialogContainer(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
        ) {
            ColorPicker(
                modifier = Modifier.padding(10.dp),
                hue = hue,
                saturation = saturation,
                value = value,
                alpha = alpha,
                onSaturationSelected = { newSaturation ->
                    saturation = newSaturation
                },
                onValueSelected = { newValue ->
                    value = newValue
                },
                onHueSelected = { newHue ->
                    hue = newHue
                },
                onAlphaSelected = { newAlpha ->
                    alpha = newAlpha
                },
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        end = 10.dp,
                        bottom = 10.dp,
                    ),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = onDismissRequest,
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(5.dp))

                TextButton(
                    onClick = {
                        onColorSelected(
                            Color.hsv(hue, saturation, value).copy(alpha = alpha).toArgb(),
                        )

                        onDismissRequest()
                    },
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
private fun ColorPicker(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float,
    value: Float,
    alpha: Float,
    onSaturationSelected: (Float) -> Unit,
    onValueSelected: (Float) -> Unit,
    onHueSelected: (Float) -> Unit,
    onAlphaSelected: (Float) -> Unit,
) {
    Column(modifier = modifier) {
        SaturationValueCanvas(
            hue = hue,
            saturation = saturation,
            value = value,
            onSaturationSelected = onSaturationSelected,
            onValueSelected = onValueSelected,
        )

        Spacer(modifier = Modifier.height(24.dp))

        HueCanvas(
            hue = hue,
            onHueSelected = onHueSelected,
        )

        Spacer(modifier = Modifier.height(24.dp))

        AlphaCanvas(
            alpha = alpha,
            onAlphaSelected = onAlphaSelected,
        )
    }
}

@Composable
private fun SaturationValueCanvas(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float,
    value: Float,
    onSaturationSelected: (Float) -> Unit,
    onValueSelected: (Float) -> Unit,
) {
    Canvas(
        modifier = modifier
            .pointerInput(key1 = Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        onSaturationSelected((offset.x / size.width).coerceIn(0f, 1f))

                        onValueSelected(1f - (offset.y / size.height).coerceIn(0f, 1f))
                    },
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        onSaturationSelected((change.position.x / size.width).coerceIn(0f, 1f))

                        onValueSelected(1f - (change.position.y / size.height).coerceIn(0f, 1f))
                    },
                )
            }
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        drawRect(color = Color.hsv(hue, 1f, 1f))

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.White, Color.Transparent),
            ),
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black),
            ),
        )

        val indicatorX = saturation * size.width
        val indicatorY = (1f - value) * size.height

        drawCircle(
            color = Color.Black,
            radius = 12f,
            center = Offset(indicatorX, indicatorY),
            style = Stroke(width = 4f),
        )

        drawCircle(
            color = Color.White,
            radius = 12f,
            center = Offset(indicatorX, indicatorY),
            style = Stroke(width = 2f),
        )
    }
}

@Composable
private fun HueCanvas(
    modifier: Modifier = Modifier,
    hue: Float,
    onHueSelected: (Float) -> Unit,
) {
    Canvas(
        modifier = modifier
            .pointerInput(key1 = Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        onHueSelected((offset.x / size.width).coerceIn(0f, 1f) * 360f)
                    },
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, _ ->
                        onHueSelected((change.position.x / size.width).coerceIn(0f, 1f) * 360f)
                    },
                )
            }
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp)),
    ) {
        val hueColors = listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color.Magenta,
            Color.Red,
        )
        drawRect(
            brush = Brush.horizontalGradient(hueColors),
            size = size,
        )

        val selectorX = (hue / 360f) * size.width

        drawRect(
            color = Color.White,
            topLeft = Offset(selectorX - 4f, 0f),
            size = Size(8f, size.height),
        )

        drawRect(
            color = Color.Black.copy(alpha = 0.4f),
            topLeft = Offset(selectorX - 4f, 0f),
            size = Size(8f, size.height),
            style = Stroke(width = 1f),
        )
    }
}

@Composable
private fun AlphaCanvas(
    alpha: Float,
    onAlphaSelected: (Float) -> Unit,
) {
    Canvas(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onAlphaSelected((offset.x / size.width).coerceIn(0f, 1f))
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    onAlphaSelected((change.position.x / size.width).coerceIn(0f, 1f))
                    change.consume()
                }
            }
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp)),
    ) {
        val squareSize = 20.dp.toPx()

        val columns = kotlin.math.ceil(size.width / squareSize).toInt()
        val rows = kotlin.math.ceil(size.height / squareSize).toInt()

        for (column in 0 until columns) {
            val columnAlpha =
                ((column * squareSize) / size.width).coerceIn(0f, 1f)

            val black = Color.Black.copy(alpha = 0.4f * columnAlpha)
            val white = Color.White.copy(alpha = 0.4f * columnAlpha)

            for (row in 0 until rows) {
                val isBlack = (row + column) % 2 == 0

                drawRect(
                    color = if (isBlack) black else white,
                    topLeft = Offset(
                        x = column * squareSize,
                        y = row * squareSize,
                    ),
                    size = Size(squareSize, squareSize),
                )
            }
        }

        val selectorX = alpha.coerceIn(0f, 1f) * size.width

        drawLine(
            color = Color.White,
            start = Offset(selectorX, 0f),
            end = Offset(selectorX, size.height),
            strokeWidth = 8f,
        )

        drawLine(
            color = Color.Black.copy(alpha = 0.4f),
            start = Offset(selectorX, 0f),
            end = Offset(selectorX, size.height),
            strokeWidth = 1f,
        )
    }
}
