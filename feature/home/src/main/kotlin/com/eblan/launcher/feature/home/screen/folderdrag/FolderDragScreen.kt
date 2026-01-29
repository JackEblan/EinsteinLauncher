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
package com.eblan.launcher.feature.home.screen.folderdrag

import android.widget.Toast
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.eblan.launcher.domain.model.FolderDataById
import com.eblan.launcher.domain.model.GridItem
import com.eblan.launcher.domain.model.GridItemCache
import com.eblan.launcher.domain.model.HomeSettings
import com.eblan.launcher.domain.model.MoveGridItemResult
import com.eblan.launcher.domain.model.TextColor
import com.eblan.launcher.feature.home.component.grid.GridItemContent
import com.eblan.launcher.feature.home.component.grid.GridLayout
import com.eblan.launcher.feature.home.component.indicator.PageIndicator
import com.eblan.launcher.feature.home.model.Drag
import com.eblan.launcher.feature.home.model.GridItemSource
import com.eblan.launcher.feature.home.model.PageDirection
import com.eblan.launcher.feature.home.model.Screen
import com.eblan.launcher.feature.home.model.SharedElementKey
import com.eblan.launcher.feature.home.screen.drag.handlePageDirection
import com.eblan.launcher.feature.home.util.PAGE_INDICATOR_HEIGHT
import com.eblan.launcher.feature.home.util.getSystemTextColor

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun SharedTransitionScope.FolderDragScreen(
    modifier: Modifier = Modifier,
    folderGridHorizontalPagerState: PagerState,
    folderDataById: FolderDataById,
    gridItemCache: GridItemCache,
    gridItemSource: GridItemSource?,
    textColor: TextColor,
    drag: Drag,
    dragIntOffset: IntOffset,
    screenWidth: Int,
    screenHeight: Int,
    paddingValues: PaddingValues,
    homeSettings: HomeSettings,
    moveGridItemResult: MoveGridItemResult?,
    statusBarNotifications: Map<String, Int>,
    hasShortcutHostPermission: Boolean,
    iconPackFilePaths: Map<String, String>,
    lockMovement: Boolean,
    screen: Screen,
    onMoveFolderGridItem: (
        movingGridItem: GridItem,
        x: Int,
        y: Int,
        columns: Int,
        rows: Int,
        gridWidth: Int,
        gridHeight: Int,
        lockMovement: Boolean,
    ) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    onMoveGridItemOutsideFolder: (
        gridItemSource: GridItemSource,
        folderId: String,
        movingGridItem: GridItem,
    ) -> Unit,
    onUpdateSharedElementKey: (SharedElementKey?) -> Unit,
) {
    requireNotNull(gridItemSource)

    val context = LocalContext.current

    val density = LocalDensity.current

    var pageDirection by remember { mutableStateOf<PageDirection?>(null) }

    val pageIndicatorHeightPx = with(density) {
        PAGE_INDICATOR_HEIGHT.dp.roundToPx()
    }

    var titleHeight by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = drag, key2 = dragIntOffset) {
        handleDragFolderGridItem(
            density = density,
            currentPage = folderGridHorizontalPagerState.currentPage,
            drag = drag,
            gridItem = gridItemSource.gridItem,
            dragIntOffset = dragIntOffset,
            screenHeight = screenHeight,
            screenWidth = screenWidth,
            pageIndicatorHeight = pageIndicatorHeightPx,
            columns = homeSettings.folderColumns,
            rows = homeSettings.folderRows,
            isScrollInProgress = folderGridHorizontalPagerState.isScrollInProgress,
            paddingValues = paddingValues,
            titleHeight = titleHeight,
            lockMovement = lockMovement,
            folderId = folderDataById.folderId,
            screen = screen,
            onMoveFolderGridItem = onMoveFolderGridItem,
            onMoveGridItemOutsideFolder = onMoveGridItemOutsideFolder,
            onUpdatePageDirection = { newPageDirection ->
                pageDirection = newPageDirection
            },
            onUpdateSharedElementKey = onUpdateSharedElementKey,
        )
    }

    LaunchedEffect(key1 = pageDirection) {
        handlePageDirection(
            currentPage = folderGridHorizontalPagerState.currentPage,
            pageDirection = pageDirection,
            onAnimateScrollToPage = { page ->
                folderGridHorizontalPagerState.animateScrollToPage(page = page)

                pageDirection = null
            },
        )
    }

    LaunchedEffect(key1 = drag) {
        when (drag) {
            Drag.End, Drag.Cancel -> {
                handleDropFolderGridItem(
                    moveGridItemResult = moveGridItemResult,
                    density = density,
                    dragIntOffset = dragIntOffset,
                    screenHeight = screenHeight,
                    pageIndicatorHeight = pageIndicatorHeightPx,
                    paddingValues = paddingValues,
                    onDragEnd = onDragEnd,
                    onDragCancel = {
                        Toast.makeText(
                            context,
                            "Layout was canceled due to an invalid position",
                            Toast.LENGTH_LONG,
                        ).show()

                        onDragCancel()
                    },
                )
            }

            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
            ),
    ) {
        Column(
            modifier = Modifier
                .onSizeChanged {
                    titleHeight = it.height
                }
                .fillMaxWidth()
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = folderDataById.label,
                color = getSystemTextColor(
                    systemTextColor = textColor,
                    systemCustomTextColor = homeSettings.gridItemSettings.customTextColor,
                ),
                style = MaterialTheme.typography.headlineLarge,
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        HorizontalPager(
            state = folderGridHorizontalPagerState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
            ),
            userScrollEnabled = false,
        ) { index ->
            GridLayout(
                modifier = Modifier.fillMaxSize(),
                gridItems = gridItemCache.folderGridItemsCacheByPage[index],
                columns = homeSettings.folderColumns,
                rows = homeSettings.folderRows,
                content = { gridItem ->
                    val isDragging = (
                        drag == Drag.Start ||
                            drag == Drag.Dragging
                        ) &&
                        gridItem.id == gridItemSource.gridItem.id

                    GridItemContent(
                        gridItem = gridItem,
                        textColor = textColor,
                        gridItemSettings = homeSettings.gridItemSettings,
                        isDragging = isDragging,
                        statusBarNotifications = statusBarNotifications,
                        hasShortcutHostPermission = hasShortcutHostPermission,
                        drag = drag,
                        iconPackFilePaths = iconPackFilePaths,
                        screen = screen,
                        isScrollInProgress = folderGridHorizontalPagerState.isScrollInProgress,
                    )
                },
            )
        }

        PageIndicator(
            modifier = Modifier
                .height(PAGE_INDICATOR_HEIGHT.dp)
                .fillMaxWidth(),
            gridHorizontalPagerState = folderGridHorizontalPagerState,
            infiniteScroll = false,
            pageCount = folderDataById.pageCount,
            color = getSystemTextColor(
                systemTextColor = textColor,
                systemCustomTextColor = homeSettings.gridItemSettings.customTextColor,
            ),
        )
    }
}
