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
package com.eblan.launcher.feature.home.screen.resize

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.eblan.launcher.domain.model.Associate
import com.eblan.launcher.domain.model.GridItem
import com.eblan.launcher.domain.model.GridItemCache
import com.eblan.launcher.domain.model.GridItemData
import com.eblan.launcher.domain.model.GridItemSettings
import com.eblan.launcher.domain.model.HomeSettings
import com.eblan.launcher.domain.model.MoveGridItemResult
import com.eblan.launcher.domain.model.TextColor
import com.eblan.launcher.feature.home.component.grid.GridItemContent
import com.eblan.launcher.feature.home.component.grid.GridLayout
import com.eblan.launcher.feature.home.component.indicator.PageIndicator
import com.eblan.launcher.feature.home.model.Drag
import com.eblan.launcher.feature.home.model.Screen
import com.eblan.launcher.feature.home.util.PAGE_INDICATOR_HEIGHT
import com.eblan.launcher.feature.home.util.getGridItemTextColor
import com.eblan.launcher.feature.home.util.getSystemTextColor

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun SharedTransitionScope.ResizeScreen(
    modifier: Modifier = Modifier,
    currentPage: Int,
    gridItemCache: GridItemCache,
    gridItem: GridItem?,
    screenWidth: Int,
    screenHeight: Int,
    dockGridItemsCache: List<GridItem>,
    textColor: TextColor,
    paddingValues: PaddingValues,
    homeSettings: HomeSettings,
    statusBarNotifications: Map<String, Int>,
    hasShortcutHostPermission: Boolean,
    iconPackFilePaths: Map<String, String>,
    lockMovement: Boolean,
    moveGridItemResult: MoveGridItemResult?,
    screen: Screen,
    gridHorizontalPagerState: PagerState,
    onResizeGridItem: (
        gridItem: GridItem,
        columns: Int,
        rows: Int,
        lockMovement: Boolean,
    ) -> Unit,
    onResizeEnd: (GridItem) -> Unit,
    onResizeCancel: () -> Unit,
) {
    requireNotNull(gridItem)

    val density = LocalDensity.current

    val leftPadding = with(density) {
        paddingValues.calculateStartPadding(LayoutDirection.Ltr).roundToPx()
    }

    val rightPadding = with(density) {
        paddingValues.calculateEndPadding(LayoutDirection.Ltr).roundToPx()
    }

    val topPadding = with(density) {
        paddingValues.calculateTopPadding().roundToPx()
    }

    val bottomPadding = with(density) {
        paddingValues.calculateBottomPadding().roundToPx()
    }

    val horizontalPadding = leftPadding + rightPadding

    val verticalPadding = topPadding + bottomPadding

    val gridWidth = screenWidth - horizontalPadding

    val gridHeight = screenHeight - verticalPadding

    val dockHeight = homeSettings.dockHeight.dp

    val dockHeightPx = with(density) {
        dockHeight.roundToPx()
    }

    val pageIndicatorHeightPx = with(density) {
        PAGE_INDICATOR_HEIGHT.dp.roundToPx()
    }

    var currentGridItem by remember {
        mutableStateOf(gridItem)
    }

    LaunchedEffect(key1 = moveGridItemResult) {
        moveGridItemResult?.movingGridItem?.let { movingGridItem ->
            currentGridItem = movingGridItem
        }
    }

    BackHandler {
        onResizeCancel()
    }

    Column(
        modifier = modifier
            .pointerInput(key1 = Unit) {
                detectTapGestures(
                    onTap = {
                        onResizeEnd(gridItem)
                    },
                )
            }
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        GridLayout(
            modifier = Modifier.weight(1f),
            gridItems = gridItemCache.gridItemsCacheByPage[currentPage].orEmpty(),
            columns = homeSettings.columns,
            rows = homeSettings.rows,
            { gridItem ->
                GridItemContent(
                    gridItem = gridItem,
                    textColor = textColor,
                    gridItemSettings = homeSettings.gridItemSettings,
                    isDragging = false,
                    statusBarNotifications = statusBarNotifications,
                    hasShortcutHostPermission = hasShortcutHostPermission,
                    drag = Drag.End,
                    iconPackFilePaths = iconPackFilePaths,
                    screen = screen,
                    isScrollInProgress = gridHorizontalPagerState.isScrollInProgress,
                )
            },
        )

        PageIndicator(
            modifier = Modifier
                .height(PAGE_INDICATOR_HEIGHT.dp)
                .fillMaxWidth(),
            gridHorizontalPagerState = gridHorizontalPagerState,
            infiniteScroll = homeSettings.infiniteScroll,
            pageCount = homeSettings.pageCount,
            color = getSystemTextColor(
                systemTextColor = textColor,
                systemCustomTextColor = homeSettings.gridItemSettings.customTextColor,
            ),
        )

        GridLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(dockHeight),
            gridItems = dockGridItemsCache,
            columns = homeSettings.dockColumns,
            rows = homeSettings.dockRows,
            { gridItem ->
                GridItemContent(
                    gridItem = gridItem,
                    textColor = textColor,
                    gridItemSettings = homeSettings.gridItemSettings,
                    isDragging = false,
                    statusBarNotifications = statusBarNotifications,
                    hasShortcutHostPermission = hasShortcutHostPermission,
                    drag = Drag.End,
                    iconPackFilePaths = iconPackFilePaths,
                    screen = screen,
                    isScrollInProgress = gridHorizontalPagerState.isScrollInProgress,
                )
            },
        )
    }

    when (currentGridItem.associate) {
        Associate.Grid -> {
            val cellWidth = gridWidth / homeSettings.columns

            val cellHeight = (gridHeight - pageIndicatorHeightPx - dockHeightPx) / homeSettings.rows

            val x = currentGridItem.startColumn * cellWidth

            val y = currentGridItem.startRow * cellHeight

            val width = currentGridItem.columnSpan * cellWidth

            val height = currentGridItem.rowSpan * cellHeight

            val gridX = x + leftPadding

            val gridY = y + topPadding

            ResizeOverlay(
                gridItem = currentGridItem,
                gridWidth = gridWidth,
                gridHeight = gridHeight - dockHeightPx,
                cellWidth = cellWidth,
                cellHeight = cellHeight,
                columns = homeSettings.columns,
                rows = homeSettings.rows,
                x = gridX,
                y = gridY,
                width = width,
                height = height,
                textColor = textColor,
                lockMovement = lockMovement,
                gridItemSettings = homeSettings.gridItemSettings,
                onResizeGridItem = onResizeGridItem,
            )
        }

        Associate.Dock -> {
            val cellWidth = gridWidth / homeSettings.dockColumns

            val cellHeight = dockHeightPx / homeSettings.dockRows

            val x = currentGridItem.startColumn * cellWidth

            val y = currentGridItem.startRow * cellHeight

            val dockX = x + leftPadding

            val dockY = (y + topPadding) + (gridHeight - dockHeightPx)

            val width = currentGridItem.columnSpan * cellWidth

            val height = currentGridItem.rowSpan * cellHeight

            ResizeOverlay(
                gridItem = currentGridItem,
                gridWidth = gridWidth,
                gridHeight = dockHeightPx,
                cellWidth = cellWidth,
                cellHeight = cellHeight,
                columns = homeSettings.dockColumns,
                rows = homeSettings.dockRows,
                x = dockX,
                y = dockY,
                width = width,
                height = height,
                textColor = textColor,
                lockMovement = lockMovement,
                gridItemSettings = homeSettings.gridItemSettings,
                onResizeGridItem = onResizeGridItem,
            )
        }
    }
}

@Composable
private fun ResizeOverlay(
    gridItem: GridItem,
    gridWidth: Int,
    gridHeight: Int,
    cellWidth: Int,
    cellHeight: Int,
    columns: Int,
    rows: Int,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    textColor: TextColor,
    lockMovement: Boolean,
    gridItemSettings: GridItemSettings,
    onResizeGridItem: (
        gridItem: GridItem,
        columns: Int,
        rows: Int,
        lockMovement: Boolean,
    ) -> Unit,
) {
    val currentTextColor = if (gridItem.override) {
        getGridItemTextColor(
            systemTextColor = textColor,
            systemCustomTextColor = gridItemSettings.customTextColor,
            gridItemTextColor = gridItem.gridItemSettings.textColor,
            gridItemCustomTextColor = gridItem.gridItemSettings.customTextColor,
        )
    } else {
        getSystemTextColor(
            systemTextColor = textColor,
            systemCustomTextColor = gridItemSettings.customTextColor,
        )
    }

    when (val data = gridItem.data) {
        is GridItemData.ApplicationInfo,
        is GridItemData.ShortcutInfo,
        is GridItemData.Folder,
        is GridItemData.ShortcutConfig,
        -> {
            GridItemResizeOverlay(
                gridItem = gridItem,
                gridWidth = gridWidth,
                gridHeight = gridHeight,
                cellWidth = cellWidth,
                cellHeight = cellHeight,
                columns = columns,
                rows = rows,
                x = x,
                y = y,
                width = width,
                height = height,
                color = currentTextColor,
                lockMovement = lockMovement,
                onResizeGridItem = onResizeGridItem,
            )
        }

        is GridItemData.Widget -> {
            WidgetGridItemResizeOverlay(
                gridItem = gridItem,
                gridWidth = gridWidth,
                gridHeight = gridHeight,
                rows = rows,
                columns = columns,
                data = data,
                x = x,
                y = y,
                width = width,
                height = height,
                color = currentTextColor,
                lockMovement = lockMovement,
                onResizeWidgetGridItem = onResizeGridItem,
            )
        }
    }
}
