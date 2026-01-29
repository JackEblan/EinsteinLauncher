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
package com.eblan.launcher.feature.home.screen.application

import android.graphics.Rect
import android.os.Build
import android.os.UserHandle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.addLastModifiedToFileCacheKey
import com.eblan.launcher.designsystem.icon.EblanLauncherIcons
import com.eblan.launcher.domain.model.AppDrawerSettings
import com.eblan.launcher.domain.model.Associate
import com.eblan.launcher.domain.model.EblanAction
import com.eblan.launcher.domain.model.EblanActionType
import com.eblan.launcher.domain.model.EblanAppWidgetProviderInfo
import com.eblan.launcher.domain.model.EblanApplicationInfo
import com.eblan.launcher.domain.model.EblanApplicationInfoGroup
import com.eblan.launcher.domain.model.EblanShortcutInfo
import com.eblan.launcher.domain.model.EblanShortcutInfoByGroup
import com.eblan.launcher.domain.model.EblanUser
import com.eblan.launcher.domain.model.EblanUserType
import com.eblan.launcher.domain.model.GetEblanApplicationInfosByLabel
import com.eblan.launcher.domain.model.GridItem
import com.eblan.launcher.domain.model.GridItemData
import com.eblan.launcher.domain.model.GridItemSettings
import com.eblan.launcher.domain.model.ManagedProfileResult
import com.eblan.launcher.domain.model.TextColor
import com.eblan.launcher.feature.home.component.scroll.OffsetNestedScrollConnection
import com.eblan.launcher.feature.home.component.scroll.OffsetOverscrollEffect
import com.eblan.launcher.feature.home.model.Drag
import com.eblan.launcher.feature.home.model.GridItemSource
import com.eblan.launcher.feature.home.model.Screen
import com.eblan.launcher.feature.home.model.SharedElementKey
import com.eblan.launcher.feature.home.screen.widget.AppWidgetScreen
import com.eblan.launcher.feature.home.util.getGridItemTextColor
import com.eblan.launcher.feature.home.util.getHorizontalAlignment
import com.eblan.launcher.feature.home.util.getSystemTextColor
import com.eblan.launcher.feature.home.util.getVerticalArrangement
import com.eblan.launcher.framework.packagemanager.AndroidPackageManagerWrapper
import com.eblan.launcher.framework.usermanager.AndroidUserManagerWrapper
import com.eblan.launcher.ui.SearchBar
import com.eblan.launcher.ui.local.LocalLauncherApps
import com.eblan.launcher.ui.local.LocalPackageManager
import com.eblan.launcher.ui.local.LocalUserManager
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun SharedTransitionScope.ApplicationScreen(
    modifier: Modifier = Modifier,
    currentPage: Int,
    swipeY: Float,
    getEblanApplicationInfosByLabel: GetEblanApplicationInfosByLabel,
    paddingValues: PaddingValues,
    drag: Drag,
    appDrawerSettings: AppDrawerSettings,
    gridItemSource: GridItemSource?,
    screenHeight: Int,
    eblanShortcutInfosGroup: Map<EblanShortcutInfoByGroup, List<EblanShortcutInfo>>,
    hasShortcutHostPermission: Boolean,
    eblanAppWidgetProviderInfosGroup: Map<String, List<EblanAppWidgetProviderInfo>>,
    iconPackFilePaths: Map<String, String>,
    isPressHome: Boolean,
    managedProfileResult: ManagedProfileResult?,
    screen: Screen,
    klwpIntegration: Boolean,
    alpha: Float,
    cornerSize: Dp,
    onLongPressGridItem: (
        gridItemSource: GridItemSource,
        imageBitmap: ImageBitmap?,
    ) -> Unit,
    onUpdateGridItemOffset: (
        intOffset: IntOffset,
        intSize: IntSize,
    ) -> Unit,
    onGetEblanApplicationInfosByLabel: (String) -> Unit,
    onDismiss: () -> Unit,
    onDraggingGridItem: () -> Unit,
    onVerticalDrag: (Float) -> Unit,
    onDragEnd: (Float) -> Unit,
    onEditApplicationInfo: (
        serialNumber: Long,
        packageName: String,
    ) -> Unit,
    onUpdateSharedElementKey: (SharedElementKey?) -> Unit,
) {
    Surface(
        modifier = modifier
            .offset {
                IntOffset(x = 0, y = swipeY.roundToInt())
            }
            .fillMaxSize()
            .clip(RoundedCornerShape(cornerSize))
            .alpha(alpha),
        color = if (klwpIntegration) {
            Color.Transparent
        } else {
            MaterialTheme.colorScheme.surface
        },
    ) {
        Success(
            currentPage = currentPage,
            paddingValues = paddingValues,
            drag = drag,
            appDrawerSettings = appDrawerSettings,
            gridItemSource = gridItemSource,
            getEblanApplicationInfosByLabel = getEblanApplicationInfosByLabel,
            eblanShortcutInfosGroup = eblanShortcutInfosGroup,
            hasShortcutHostPermission = hasShortcutHostPermission,
            screenHeight = screenHeight,
            eblanAppWidgetProviderInfosGroup = eblanAppWidgetProviderInfosGroup,
            iconPackFilePaths = iconPackFilePaths,
            isPressHome = isPressHome,
            managedProfileResult = managedProfileResult,
            screen = screen,
            klwpIntegration = klwpIntegration,
            onLongPressGridItem = onLongPressGridItem,
            onUpdateGridItemOffset = onUpdateGridItemOffset,
            onGetEblanApplicationInfosByLabel = onGetEblanApplicationInfosByLabel,
            onDismiss = onDismiss,
            onDraggingGridItem = onDraggingGridItem,
            onVerticalDrag = onVerticalDrag,
            onDragEnd = onDragEnd,
            onEditApplicationInfo = onEditApplicationInfo,
            onUpdateSharedElementKey = onUpdateSharedElementKey,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.Success(
    modifier: Modifier = Modifier,
    currentPage: Int,
    paddingValues: PaddingValues,
    drag: Drag,
    appDrawerSettings: AppDrawerSettings,
    gridItemSource: GridItemSource?,
    getEblanApplicationInfosByLabel: GetEblanApplicationInfosByLabel,
    eblanShortcutInfosGroup: Map<EblanShortcutInfoByGroup, List<EblanShortcutInfo>>,
    hasShortcutHostPermission: Boolean,
    screenHeight: Int,
    eblanAppWidgetProviderInfosGroup: Map<String, List<EblanAppWidgetProviderInfo>>,
    iconPackFilePaths: Map<String, String>,
    isPressHome: Boolean,
    managedProfileResult: ManagedProfileResult?,
    screen: Screen,
    klwpIntegration: Boolean,
    onLongPressGridItem: (
        gridItemSource: GridItemSource,
        imageBitmap: ImageBitmap?,
    ) -> Unit,
    onUpdateGridItemOffset: (
        intOffset: IntOffset,
        intSize: IntSize,
    ) -> Unit,
    onGetEblanApplicationInfosByLabel: (String) -> Unit,
    onDismiss: () -> Unit,
    onDraggingGridItem: () -> Unit,
    onVerticalDrag: (Float) -> Unit,
    onDragEnd: (Float) -> Unit,
    onEditApplicationInfo: (
        serialNumber: Long,
        packageName: String,
    ) -> Unit,
    onUpdateSharedElementKey: (SharedElementKey?) -> Unit,
) {
    val density = LocalDensity.current

    var showPopupApplicationMenu by remember { mutableStateOf(false) }

    var popupIntOffset by remember { mutableStateOf(IntOffset.Zero) }

    var popupIntSize by remember { mutableStateOf(IntSize.Zero) }

    val launcherApps = LocalLauncherApps.current

    val leftPadding = with(density) {
        paddingValues.calculateStartPadding(LayoutDirection.Ltr).roundToPx()
    }

    val topPadding = with(density) {
        paddingValues.calculateTopPadding().roundToPx()
    }

    val horizontalPagerState = rememberPagerState(
        pageCount = {
            getEblanApplicationInfosByLabel.eblanApplicationInfos.keys.size
        },
    )

    val appDrawerRowsHeight = with(density) {
        appDrawerSettings.appDrawerRowsHeight.dp.roundToPx()
    }

    var eblanApplicationInfoGroup by remember { mutableStateOf<EblanApplicationInfoGroup?>(null) }

    val searchBarState = rememberSearchBarState()

    LaunchedEffect(key1 = isPressHome) {
        if (isPressHome) {
            showPopupApplicationMenu = false

            onDismiss()
        }

        if (isPressHome && searchBarState.currentValue == SearchBarValue.Expanded) {
            searchBarState.animateToCollapsed()
        }
    }

    LaunchedEffect(key1 = drag) {
        if (drag == Drag.Start && searchBarState.currentValue == SearchBarValue.Expanded) {
            searchBarState.animateToCollapsed()
        }
    }

    BackHandler {
        showPopupApplicationMenu = false

        onDismiss()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
            ),
    ) {
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            searchBarState = searchBarState,
            title = "Search Applications",
            onChangeLabel = onGetEblanApplicationInfosByLabel,
        )

        if (getEblanApplicationInfosByLabel.eblanApplicationInfos.keys.size > 1) {
            EblanApplicationInfoTabRow(
                currentPage = horizontalPagerState.currentPage,
                eblanApplicationInfos = getEblanApplicationInfosByLabel.eblanApplicationInfos,
                klwpIntegration = klwpIntegration,
                onAnimateScrollToPage = horizontalPagerState::animateScrollToPage,
            )

            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = horizontalPagerState,
            ) { index ->
                EblanApplicationInfosPage(
                    index = index,
                    currentPage = currentPage,
                    paddingValues = paddingValues,
                    drag = drag,
                    appDrawerSettings = appDrawerSettings,
                    getEblanApplicationInfosByLabel = getEblanApplicationInfosByLabel,
                    iconPackFilePaths = iconPackFilePaths,
                    managedProfileResult = managedProfileResult,
                    screen = screen,
                    onLongPressGridItem = onLongPressGridItem,
                    onUpdateGridItemOffset = { intOffset, intSize ->
                        onUpdateGridItemOffset(intOffset, intSize)

                        popupIntOffset = intOffset

                        popupIntSize = intSize
                    },
                    onUpdatePopupMenu = { newShowPopupApplicationMenu ->
                        showPopupApplicationMenu = newShowPopupApplicationMenu
                    },
                    onVerticalDrag = onVerticalDrag,
                    onDragEnd = onDragEnd,
                    onDraggingGridItem = onDraggingGridItem,
                    onUpdateSharedElementKey = onUpdateSharedElementKey,
                )
            }
        } else {
            EblanApplicationInfosPage(
                index = 0,
                currentPage = currentPage,
                paddingValues = paddingValues,
                drag = drag,
                appDrawerSettings = appDrawerSettings,
                getEblanApplicationInfosByLabel = getEblanApplicationInfosByLabel,
                iconPackFilePaths = iconPackFilePaths,
                managedProfileResult = managedProfileResult,
                screen = screen,
                onLongPressGridItem = onLongPressGridItem,
                onUpdateGridItemOffset = { intOffset, intSize ->
                    onUpdateGridItemOffset(intOffset, intSize)

                    popupIntOffset = intOffset

                    popupIntSize = IntSize(
                        width = intSize.width,
                        height = appDrawerRowsHeight,
                    )
                },
                onUpdatePopupMenu = { newShowPopupApplicationMenu ->
                    showPopupApplicationMenu = newShowPopupApplicationMenu
                },
                onVerticalDrag = onVerticalDrag,
                onDragEnd = onDragEnd,
                onDraggingGridItem = onDraggingGridItem,
                onUpdateSharedElementKey = onUpdateSharedElementKey,
            )
        }
    }

    if (showPopupApplicationMenu && gridItemSource?.gridItem != null) {
        PopupApplicationInfoMenu(
            paddingValues = paddingValues,
            popupIntOffset = popupIntOffset,
            gridItem = gridItemSource.gridItem,
            popupIntSize = popupIntSize,
            eblanShortcutInfosGroup = eblanShortcutInfosGroup,
            hasShortcutHostPermission = hasShortcutHostPermission,
            currentPage = currentPage,
            drag = drag,
            gridItemSettings = appDrawerSettings.gridItemSettings,
            eblanAppWidgetProviderInfos = eblanAppWidgetProviderInfosGroup,
            onDismissRequest = {
                showPopupApplicationMenu = false
            },
            onEditApplicationInfo = onEditApplicationInfo,
            onTapShortcutInfo = { serialNumber, packageName, shortcutId ->
                val sourceBoundsX = popupIntOffset.x + leftPadding

                val sourceBoundsY = popupIntOffset.y + topPadding

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    launcherApps.startShortcut(
                        serialNumber = serialNumber,
                        packageName = packageName,
                        id = shortcutId,
                        sourceBounds = Rect(
                            sourceBoundsX,
                            sourceBoundsY,
                            sourceBoundsX + popupIntSize.width,
                            sourceBoundsY + popupIntSize.height,
                        ),
                    )
                }
            },
            onLongPressGridItem = onLongPressGridItem,
            onUpdateGridItemOffset = onUpdateGridItemOffset,
            onDraggingGridItem = onDraggingGridItem,
            onWidgets = { newEblanApplicationInfoGroup ->
                eblanApplicationInfoGroup = newEblanApplicationInfoGroup
            },
            onUpdateSharedElementKey = onUpdateSharedElementKey,
        )
    }

    if (eblanApplicationInfoGroup != null) {
        AppWidgetScreen(
            currentPage = currentPage,
            eblanApplicationInfoGroup = eblanApplicationInfoGroup,
            eblanAppWidgetProviderInfosGroup = eblanAppWidgetProviderInfosGroup,
            gridItemSettings = appDrawerSettings.gridItemSettings,
            paddingValues = paddingValues,
            drag = drag,
            screenHeight = screenHeight,
            isPressHome = isPressHome,
            screen = screen,
            onLongPressGridItem = onLongPressGridItem,
            onUpdateGridItemOffset = onUpdateGridItemOffset,
            onDismiss = {
                eblanApplicationInfoGroup = null
            },
            onDraggingGridItem = onDraggingGridItem,
            onUpdateSharedElementKey = onUpdateSharedElementKey,
        )
    }
}

@OptIn(ExperimentalUuidApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.EblanApplicationInfoItem(
    modifier: Modifier = Modifier,
    currentPage: Int,
    drag: Drag,
    eblanApplicationInfo: EblanApplicationInfo,
    appDrawerSettings: AppDrawerSettings,
    paddingValues: PaddingValues,
    iconPackFilePaths: Map<String, String>,
    screen: Screen,
    onUpdateGridItemOffset: (
        intOffset: IntOffset,
        intSize: IntSize,
    ) -> Unit,
    onLongPressGridItem: (
        gridItemSource: GridItemSource,
        imageBitmap: ImageBitmap?,
    ) -> Unit,
    onUpdatePopupMenu: (Boolean) -> Unit,
    onDraggingGridItem: () -> Unit,
    onUpdateSharedElementKey: (SharedElementKey?) -> Unit,
) {
    var intOffset by remember { mutableStateOf(IntOffset.Zero) }

    var intSize by remember { mutableStateOf(IntSize.Zero) }

    val graphicsLayer = rememberGraphicsLayer()

    val scale = remember { Animatable(1f) }

    val scope = rememberCoroutineScope()

    val density = LocalDensity.current

    val launcherApps = LocalLauncherApps.current

    val textColor = getSystemTextColor(
        systemTextColor = appDrawerSettings.gridItemSettings.textColor,
        systemCustomTextColor = appDrawerSettings.gridItemSettings.customTextColor,
    )

    val appDrawerRowsHeight = appDrawerSettings.appDrawerRowsHeight.dp

    val maxLines = if (appDrawerSettings.gridItemSettings.singleLineLabel) 1 else Int.MAX_VALUE

    val icon = iconPackFilePaths[eblanApplicationInfo.componentName] ?: eblanApplicationInfo.icon

    val horizontalAlignment =
        getHorizontalAlignment(horizontalAlignment = appDrawerSettings.gridItemSettings.horizontalAlignment)

    val verticalArrangement =
        getVerticalArrangement(verticalArrangement = appDrawerSettings.gridItemSettings.verticalArrangement)

    val leftPadding = with(density) {
        paddingValues.calculateStartPadding(LayoutDirection.Ltr).roundToPx()
    }

    val topPadding = with(density) {
        paddingValues.calculateTopPadding().roundToPx()
    }

    var isLongPress by remember { mutableStateOf(false) }

    val isDragging by remember(key1 = drag) {
        derivedStateOf {
            isLongPress && (drag == Drag.Start || drag == Drag.Dragging)
        }
    }

    val id = remember { Uuid.random().toHexString() }

    LaunchedEffect(key1 = drag) {
        when (drag) {
            Drag.Dragging if isLongPress -> {
                onDraggingGridItem()

                onUpdatePopupMenu(false)
            }

            Drag.End, Drag.Cancel -> {
                isLongPress = false

                scale.stop()

                if (scale.value < 1f) {
                    scale.animateTo(1f)
                }
            }

            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .pointerInput(key1 = drag) {
                detectTapGestures(
                    onTap = {
                        scope.launch {
                            scale.animateTo(0.5f)

                            scale.animateTo(1f)

                            val sourceBoundsX = intOffset.x + leftPadding

                            val sourceBoundsY = intOffset.y + topPadding

                            launcherApps.startMainActivity(
                                serialNumber = eblanApplicationInfo.serialNumber,
                                componentName = eblanApplicationInfo.componentName,
                                sourceBounds = Rect(
                                    sourceBoundsX,
                                    sourceBoundsY,
                                    sourceBoundsX + intSize.width,
                                    sourceBoundsY + intSize.height,
                                ),
                            )
                        }
                    },
                    onLongPress = {
                        scope.launch {
                            scale.animateTo(0.5f)

                            scale.animateTo(1f)

                            val data = GridItemData.ApplicationInfo(
                                serialNumber = eblanApplicationInfo.serialNumber,
                                componentName = eblanApplicationInfo.componentName,
                                packageName = eblanApplicationInfo.packageName,
                                icon = eblanApplicationInfo.icon,
                                label = eblanApplicationInfo.label,
                                customIcon = eblanApplicationInfo.customIcon,
                                customLabel = eblanApplicationInfo.customLabel,
                            )

                            onLongPressGridItem(
                                GridItemSource.New(
                                    gridItem = GridItem(
                                        id = id,
                                        folderId = null,
                                        page = currentPage,
                                        startColumn = -1,
                                        startRow = -1,
                                        columnSpan = 1,
                                        rowSpan = 1,
                                        data = data,
                                        associate = Associate.Grid,
                                        override = false,
                                        gridItemSettings = appDrawerSettings.gridItemSettings,
                                        doubleTap = EblanAction(
                                            eblanActionType = EblanActionType.None,
                                            serialNumber = 0L,
                                            componentName = "",
                                        ),
                                        swipeUp = EblanAction(
                                            eblanActionType = EblanActionType.None,
                                            serialNumber = 0L,
                                            componentName = "",
                                        ),
                                        swipeDown = EblanAction(
                                            eblanActionType = EblanActionType.None,
                                            serialNumber = 0L,
                                            componentName = "",
                                        ),
                                    ),
                                ),
                                graphicsLayer.toImageBitmap(),
                            )

                            onUpdateGridItemOffset(
                                intOffset,
                                intSize,
                            )

                            onUpdateSharedElementKey(
                                SharedElementKey(
                                    id = id,
                                    screen = screen,
                                ),
                            )

                            onUpdatePopupMenu(true)

                            isLongPress = true
                        }
                    },
                    onPress = {
                        awaitRelease()

                        scale.stop()

                        isLongPress = false

                        if (scale.value < 1f) {
                            scale.animateTo(1f)
                        }
                    },
                )
            }
            .height(appDrawerRowsHeight)
            .scale(
                scaleX = scale.value,
                scaleY = scale.value,
            )
            .padding(appDrawerSettings.gridItemSettings.padding.dp)
            .background(
                color = Color(appDrawerSettings.gridItemSettings.customBackgroundColor),
                shape = RoundedCornerShape(size = appDrawerSettings.gridItemSettings.cornerRadius.dp),
            ),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
    ) {
        if (!isDragging) {
            Box(
                modifier = Modifier.size(appDrawerSettings.gridItemSettings.iconSize.dp),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(eblanApplicationInfo.customIcon ?: icon)
                        .addLastModifiedToFileCacheKey(true).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .sharedElementWithCallerManagedVisibility(
                            rememberSharedContentState(
                                key = SharedElementKey(
                                    id = id,
                                    screen = screen,
                                ),
                            ),
                            visible = drag == Drag.Cancel || drag == Drag.End,
                        )
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }

                            drawLayer(graphicsLayer)
                        }
                        .onGloballyPositioned { layoutCoordinates ->
                            intOffset = layoutCoordinates.positionInRoot().round()

                            intSize = layoutCoordinates.size
                        }
                        .matchParentSize(),
                )

                if (eblanApplicationInfo.serialNumber != 0L) {
                    ElevatedCard(
                        modifier = Modifier
                            .size((appDrawerSettings.gridItemSettings.iconSize * 0.40).dp)
                            .align(Alignment.BottomEnd),
                    ) {
                        Icon(
                            imageVector = EblanLauncherIcons.Work,
                            contentDescription = null,
                            modifier = Modifier.padding(2.dp),
                        )
                    }
                }
            }

            if (appDrawerSettings.gridItemSettings.showLabel) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = eblanApplicationInfo.customLabel ?: eblanApplicationInfo.label,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    maxLines = maxLines,
                    fontSize = appDrawerSettings.gridItemSettings.textSize.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.EblanApplicationInfosPage(
    modifier: Modifier = Modifier,
    index: Int,
    currentPage: Int,
    paddingValues: PaddingValues,
    drag: Drag,
    appDrawerSettings: AppDrawerSettings,
    getEblanApplicationInfosByLabel: GetEblanApplicationInfosByLabel,
    iconPackFilePaths: Map<String, String>,
    managedProfileResult: ManagedProfileResult?,
    screen: Screen,
    onLongPressGridItem: (
        gridItemSource: GridItemSource,
        imageBitmap: ImageBitmap?,
    ) -> Unit,
    onUpdateGridItemOffset: (
        intOffset: IntOffset,
        intSize: IntSize,
    ) -> Unit,
    onUpdatePopupMenu: (Boolean) -> Unit,
    onVerticalDrag: (Float) -> Unit,
    onDragEnd: (Float) -> Unit,
    onDraggingGridItem: () -> Unit,
    onUpdateSharedElementKey: (SharedElementKey?) -> Unit,
) {
    val userManager = LocalUserManager.current

    val packageManager = LocalPackageManager.current

    val eblanUser = getEblanApplicationInfosByLabel.eblanApplicationInfos.keys.toList().getOrElse(
        index = index,
        defaultValue = {
            EblanUser(
                serialNumber = 0L,
                eblanUserType = EblanUserType.Personal,
                isPrivateSpaceEntryPointHidden = false,
            )
        },
    )

    val userHandle = userManager.getUserForSerialNumber(serialNumber = eblanUser.serialNumber)

    var isQuietModeEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = userHandle) {
        if (userHandle != null) {
            isQuietModeEnabled = userManager.isQuietModeEnabled(userHandle = userHandle)
        }
    }

    LaunchedEffect(key1 = managedProfileResult) {
        if (managedProfileResult != null && managedProfileResult.serialNumber == eblanUser.serialNumber) {
            isQuietModeEnabled = managedProfileResult.isQuiteModeEnabled
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isQuietModeEnabled) {
            QuiteModeScreen(
                packageManager = packageManager,
                userManager = userManager,
                userHandle = userHandle,
                onUpdateRequestQuietModeEnabled = { newIsQuietModeEnabled ->
                    isQuietModeEnabled = newIsQuietModeEnabled
                },
                onVerticalDrag = onVerticalDrag,
                onDragEnd = onDragEnd,
            )
        } else {
            EblanApplicationInfos(
                eblanUser = eblanUser,
                currentPage = currentPage,
                paddingValues = paddingValues,
                drag = drag,
                appDrawerSettings = appDrawerSettings,
                getEblanApplicationInfosByLabel = getEblanApplicationInfosByLabel,
                iconPackFilePaths = iconPackFilePaths,
                screen = screen,
                managedProfileResult = managedProfileResult,
                onLongPressGridItem = onLongPressGridItem,
                onUpdateGridItemOffset = onUpdateGridItemOffset,
                onUpdatePopupMenu = onUpdatePopupMenu,
                onVerticalDrag = onVerticalDrag,
                onDragEnd = onDragEnd,
                onDraggingGridItem = onDraggingGridItem,
                onUpdateSharedElementKey = onUpdateSharedElementKey,
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && packageManager.isDefaultLauncher() && eblanUser.serialNumber > 0 && userHandle != null) {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            end = 10.dp,
                            bottom = paddingValues.calculateBottomPadding() + 10.dp,
                        ),
                    onClick = {
                        userManager.requestQuietModeEnabled(
                            enableQuiteMode = true,
                            userHandle = userHandle,
                        )

                        isQuietModeEnabled = userManager.isQuietModeEnabled(userHandle)
                    },
                ) {
                    Icon(
                        imageVector = EblanLauncherIcons.WorkOff,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuiteModeScreen(
    modifier: Modifier = Modifier,
    packageManager: AndroidPackageManagerWrapper,
    userManager: AndroidUserManagerWrapper,
    userHandle: UserHandle?,
    onUpdateRequestQuietModeEnabled: (Boolean) -> Unit,
    onVerticalDrag: (Float) -> Unit,
    onDragEnd: (Float) -> Unit,
) {
    Column(
        modifier = modifier
            .pointerInput(key1 = Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        onVerticalDrag(dragAmount)
                    },
                    onDragEnd = {
                        onDragEnd(0f)
                    },
                )
            }
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Work apps are paused", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "You won't receive notifications from your work apps",
            textAlign = TextAlign.Center,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && packageManager.isDefaultLauncher() && userHandle != null) {
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    userManager.requestQuietModeEnabled(
                        enableQuiteMode = false,
                        userHandle = userHandle,
                    )

                    onUpdateRequestQuietModeEnabled(userManager.isQuietModeEnabled(userHandle = userHandle))
                },
            ) {
                Text(text = "Unpause")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.EblanApplicationInfos(
    modifier: Modifier = Modifier,
    eblanUser: EblanUser,
    currentPage: Int,
    paddingValues: PaddingValues,
    drag: Drag,
    appDrawerSettings: AppDrawerSettings,
    getEblanApplicationInfosByLabel: GetEblanApplicationInfosByLabel,
    iconPackFilePaths: Map<String, String>,
    screen: Screen,
    managedProfileResult: ManagedProfileResult?,
    onLongPressGridItem: (
        gridItemSource: GridItemSource,
        imageBitmap: ImageBitmap?,
    ) -> Unit,
    onUpdateGridItemOffset: (
        intOffset: IntOffset,
        intSize: IntSize,
    ) -> Unit,
    onUpdatePopupMenu: (Boolean) -> Unit,
    onVerticalDrag: (Float) -> Unit,
    onDragEnd: (Float) -> Unit,
    onDraggingGridItem: () -> Unit,
    onUpdateSharedElementKey: (SharedElementKey?) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val overscrollEffect = remember(key1 = scope) {
        OffsetOverscrollEffect(
            scope = scope,
            onVerticalDrag = onVerticalDrag,
            onDragEnd = onDragEnd,
        )
    }

    val lazyGridState = rememberLazyGridState()

    val canOverscroll by remember(key1 = lazyGridState) {
        derivedStateOf {
            val lastVisibleIndex =
                lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            lastVisibleIndex < lazyGridState.layoutInfo.totalItemsCount - 1
        }
    }

    val nestedScrollConnection = remember {
        OffsetNestedScrollConnection(
            onVerticalDrag = onVerticalDrag,
            onDragEnd = onDragEnd,
        )
    }

    var isQuietModeEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .run {
                if (!canOverscroll) {
                    nestedScroll(nestedScrollConnection)
                } else {
                    this
                }
            }
            .fillMaxSize(),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(count = appDrawerSettings.appDrawerColumns),
            state = lazyGridState,
            modifier = Modifier.matchParentSize(),
            contentPadding = PaddingValues(
                bottom = paddingValues.calculateBottomPadding(),
            ),
            overscrollEffect = if (canOverscroll) {
                overscrollEffect
            } else {
                rememberOverscrollEffect()
            },
        ) {
            when (eblanUser.eblanUserType) {
                EblanUserType.Personal -> {
                    items(getEblanApplicationInfosByLabel.eblanApplicationInfos[eblanUser].orEmpty()) { eblanApplicationInfo ->
                        key(eblanApplicationInfo.serialNumber, eblanApplicationInfo.componentName) {
                            EblanApplicationInfoItem(
                                currentPage = currentPage,
                                drag = drag,
                                eblanApplicationInfo = eblanApplicationInfo,
                                appDrawerSettings = appDrawerSettings,
                                paddingValues = paddingValues,
                                iconPackFilePaths = iconPackFilePaths,
                                screen = screen,
                                onUpdateGridItemOffset = onUpdateGridItemOffset,
                                onLongPressGridItem = onLongPressGridItem,
                                onUpdatePopupMenu = onUpdatePopupMenu,
                                onDraggingGridItem = onDraggingGridItem,
                                onUpdateSharedElementKey = onUpdateSharedElementKey,
                            )
                        }
                    }

                    privateSpace(
                        privateEblanUser = getEblanApplicationInfosByLabel.privateEblanUser,
                        privateEblanApplicationInfos = getEblanApplicationInfosByLabel.privateEblanApplicationInfos,
                        managedProfileResult = managedProfileResult,
                        isQuietModeEnabled = isQuietModeEnabled,
                        drag = drag,
                        appDrawerSettings = appDrawerSettings,
                        paddingValues = paddingValues,
                        iconPackFilePaths = iconPackFilePaths,
                        onUpdateGridItemOffset = onUpdateGridItemOffset,
                        onLongPressGridItem = onLongPressGridItem,
                        onUpdatePopupMenu = onUpdatePopupMenu,
                        onUpdateIsQuietModeEnabled = { newIsQuiteModeEnabled ->
                            isQuietModeEnabled = newIsQuiteModeEnabled
                        },
                    )
                }

                else -> {
                    items(getEblanApplicationInfosByLabel.eblanApplicationInfos[eblanUser].orEmpty()) { eblanApplicationInfo ->
                        key(eblanApplicationInfo.serialNumber, eblanApplicationInfo.componentName) {
                            EblanApplicationInfoItem(
                                currentPage = currentPage,
                                drag = drag,
                                eblanApplicationInfo = eblanApplicationInfo,
                                appDrawerSettings = appDrawerSettings,
                                paddingValues = paddingValues,
                                iconPackFilePaths = iconPackFilePaths,
                                screen = screen,
                                onUpdateGridItemOffset = onUpdateGridItemOffset,
                                onLongPressGridItem = onLongPressGridItem,
                                onUpdatePopupMenu = onUpdatePopupMenu,
                                onDraggingGridItem = onDraggingGridItem,
                                onUpdateSharedElementKey = onUpdateSharedElementKey,
                            )
                        }
                    }
                }
            }
        }

        if (!WindowInsets.isImeVisible) {
            ScrollBarThumb(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxHeight(),
                lazyGridState = lazyGridState,
                appDrawerSettings = appDrawerSettings,
                paddingValues = paddingValues,
                onScrollToItem = lazyGridState::scrollToItem,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EblanApplicationInfoTabRow(
    currentPage: Int,
    eblanApplicationInfos: Map<EblanUser, List<EblanApplicationInfo>>,
    klwpIntegration: Boolean,
    onAnimateScrollToPage: suspend (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()

    SecondaryTabRow(
        selectedTabIndex = currentPage,
        containerColor = if (klwpIntegration) {
            Color.Transparent
        } else {
            TabRowDefaults.secondaryContainerColor
        },
    ) {
        eblanApplicationInfos.keys.forEachIndexed { index, eblanUser ->
            Tab(
                selected = currentPage == index,
                onClick = {
                    scope.launch {
                        onAnimateScrollToPage(index)
                    }
                },
                text = {
                    Text(
                        text = eblanUser.eblanUserType.name,
                        maxLines = 1,
                    )
                },
            )
        }
    }
}

@Composable
private fun ScrollBarThumb(
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState,
    appDrawerSettings: AppDrawerSettings,
    paddingValues: PaddingValues,
    onScrollToItem: suspend (Int) -> Unit,
) {
    val density = LocalDensity.current

    val scope = rememberCoroutineScope()

    val appDrawerRowsHeightPx = with(density) {
        appDrawerSettings.appDrawerRowsHeight.dp.roundToPx()
    }

    val bottomPadding = with(density) {
        paddingValues.calculateBottomPadding().roundToPx()
    }

    val thumbHeight by remember(lazyGridState) {
        derivedStateOf {
            with(density) {
                (lazyGridState.layoutInfo.viewportSize.height / 4).toDp()
            }
        }
    }

    val viewPortThumbY by remember(key1 = lazyGridState) {
        derivedStateOf {
            val totalRows =
                (lazyGridState.layoutInfo.totalItemsCount + appDrawerSettings.appDrawerColumns - 1) / appDrawerSettings.appDrawerColumns

            val visibleRows =
                ceil(lazyGridState.layoutInfo.viewportSize.height / appDrawerRowsHeightPx.toFloat()).toInt()

            val scrollableRows = (totalRows - visibleRows).coerceAtLeast(0)

            val availableScroll = scrollableRows * appDrawerRowsHeightPx

            val row = lazyGridState.firstVisibleItemIndex / appDrawerSettings.appDrawerColumns

            val totalScrollY =
                (row * appDrawerRowsHeightPx) + lazyGridState.firstVisibleItemScrollOffset

            val thumbHeightPx = with(density) {
                thumbHeight.toPx()
            }

            val availableHeight =
                (lazyGridState.layoutInfo.viewportSize.height - thumbHeightPx - bottomPadding).coerceAtLeast(
                    0f,
                )

            if (availableScroll <= 0) {
                0f
            } else {
                (totalScrollY.toFloat() / availableScroll.toFloat() * availableHeight).coerceIn(
                    0f,
                    availableHeight,
                )
            }
        }
    }

    var isDraggingThumb by remember { mutableStateOf(false) }

    var thumbY by remember { mutableFloatStateOf(0f) }

    val thumbAlpha by animateFloatAsState(
        targetValue = if (lazyGridState.isScrollInProgress || isDraggingThumb) 1f else 0.2f,
    )

    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .offset {
                    val y = if (isDraggingThumb) {
                        thumbY
                    } else {
                        viewPortThumbY
                    }

                    IntOffset(0, y.roundToInt())
                }
                .pointerInput(key1 = lazyGridState) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            thumbY = viewPortThumbY

                            isDraggingThumb = true
                        },
                        onVerticalDrag = { _, deltaY ->
                            val totalRows =
                                (lazyGridState.layoutInfo.totalItemsCount + appDrawerSettings.appDrawerColumns - 1) / appDrawerSettings.appDrawerColumns

                            val visibleRows =
                                ceil(lazyGridState.layoutInfo.viewportSize.height / appDrawerRowsHeightPx.toFloat()).toInt()

                            val scrollableRows = (totalRows - visibleRows).coerceAtLeast(0)

                            val availableScroll = scrollableRows * appDrawerRowsHeightPx

                            val thumbHeightPx = with(density) { thumbHeight.toPx() }

                            val availableHeight =
                                lazyGridState.layoutInfo.viewportSize.height - thumbHeightPx - bottomPadding

                            thumbY = (thumbY + deltaY).coerceIn(0f, availableHeight)

                            val progress = thumbY / availableHeight

                            val targetScrollY = progress * availableScroll

                            val targetRow = targetScrollY / appDrawerRowsHeightPx

                            val targetIndex =
                                (targetRow * appDrawerSettings.appDrawerColumns).roundToInt()
                                    .coerceIn(0, lazyGridState.layoutInfo.totalItemsCount)

                            scope.launch {
                                onScrollToItem(targetIndex)
                            }
                        },
                        onDragEnd = {
                            isDraggingThumb = false
                        },
                        onDragCancel = {
                            isDraggingThumb = false
                        },
                    )
                }
                .alpha(thumbAlpha)
                .size(width = 8.dp, height = thumbHeight)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp),
                ),
        )
    }
}
