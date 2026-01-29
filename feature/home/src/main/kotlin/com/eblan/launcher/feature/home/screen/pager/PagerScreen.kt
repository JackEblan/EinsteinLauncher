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
package com.eblan.launcher.feature.home.screen.pager

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.util.Consumer
import com.eblan.launcher.domain.model.AppDrawerSettings
import com.eblan.launcher.domain.model.EblanActionType
import com.eblan.launcher.domain.model.EblanAppWidgetProviderInfo
import com.eblan.launcher.domain.model.EblanApplicationInfoGroup
import com.eblan.launcher.domain.model.EblanShortcutConfig
import com.eblan.launcher.domain.model.EblanShortcutInfo
import com.eblan.launcher.domain.model.EblanShortcutInfoByGroup
import com.eblan.launcher.domain.model.EblanUser
import com.eblan.launcher.domain.model.ExperimentalSettings
import com.eblan.launcher.domain.model.GestureSettings
import com.eblan.launcher.domain.model.GetEblanApplicationInfosByLabel
import com.eblan.launcher.domain.model.GridItem
import com.eblan.launcher.domain.model.HomeSettings
import com.eblan.launcher.domain.model.ManagedProfileResult
import com.eblan.launcher.domain.model.TextColor
import com.eblan.launcher.feature.home.model.Drag
import com.eblan.launcher.feature.home.model.GridItemSource
import com.eblan.launcher.feature.home.model.Screen
import com.eblan.launcher.feature.home.model.SharedElementKey
import com.eblan.launcher.feature.home.screen.application.ApplicationScreen
import com.eblan.launcher.feature.home.screen.shortcutconfig.ShortcutConfigScreen
import com.eblan.launcher.feature.home.screen.widget.AppWidgetScreen
import com.eblan.launcher.feature.home.screen.widget.WidgetScreen
import com.eblan.launcher.ui.local.LocalLauncherApps
import com.eblan.launcher.ui.local.LocalWallpaperManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalLayoutApi::class)
@Composable
internal fun SharedTransitionScope.PagerScreen(
    modifier: Modifier = Modifier,
    gridItems: List<GridItem>,
    gridItemsByPage: Map<Int, List<GridItem>>,
    drag: Drag,
    dockGridItems: List<GridItem>,
    textColor: TextColor,
    screenWidth: Int,
    screenHeight: Int,
    paddingValues: PaddingValues,
    hasShortcutHostPermission: Boolean,
    hasSystemFeatureAppWidgets: Boolean,
    gestureSettings: GestureSettings,
    appDrawerSettings: AppDrawerSettings,
    gridItemSource: GridItemSource?,
    homeSettings: HomeSettings,
    gridHorizontalPagerState: PagerState,
    currentPage: Int,
    statusBarNotifications: Map<String, Int>,
    eblanShortcutInfosGroup: Map<EblanShortcutInfoByGroup, List<EblanShortcutInfo>>,
    eblanAppWidgetProviderInfosGroup: Map<String, List<EblanAppWidgetProviderInfo>>,
    iconPackFilePaths: Map<String, String>,
    managedProfileResult: ManagedProfileResult?,
    screen: Screen,
    experimentalSettings: ExperimentalSettings,
    getEblanApplicationInfosByLabel: GetEblanApplicationInfosByLabel,
    eblanAppWidgetProviderInfos: Map<EblanApplicationInfoGroup, List<EblanAppWidgetProviderInfo>>,
    eblanShortcutConfigs: Map<EblanUser, Map<EblanApplicationInfoGroup, List<EblanShortcutConfig>>>,
    onTapFolderGridItem: (String) -> Unit,
    onDraggingGridItem: () -> Unit,
    onEditGridItem: (String) -> Unit,
    onResize: () -> Unit,
    onSettings: () -> Unit,
    onEditPage: (List<GridItem>) -> Unit,
    onLongPressGridItem: (
        gridItemSource: GridItemSource,
        imageBitmap: ImageBitmap?,
    ) -> Unit,
    onUpdateGridItemOffset: (
        intOffset: IntOffset,
        intSize: IntSize,
    ) -> Unit,
    onGetEblanApplicationInfosByLabel: (String) -> Unit,
    onGetEblanShortcutConfigsByLabel: (String) -> Unit,
    onGetEblanAppWidgetProviderInfosByLabel: (String) -> Unit,
    onDeleteGridItem: (GridItem) -> Unit,
    onEditApplicationInfo: (
        serialNumber: Long,
        packageName: String,
    ) -> Unit,
    onUpdateSharedElementKey: (SharedElementKey?) -> Unit,
    onResetOverlay: () -> Unit,
) {
    val context = LocalContext.current

    val density = LocalDensity.current

    val launcherApps = LocalLauncherApps.current

    var hasDoubleTap by remember { mutableStateOf(false) }

    var showAppDrawer by remember { mutableStateOf(false) }

    var showWidgets by remember { mutableStateOf(false) }

    var showShortcutConfigActivities by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

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

    var lastSwipeUpY by rememberSaveable { mutableFloatStateOf(screenHeight.toFloat()) }

    var lastSwipeDownY by rememberSaveable { mutableFloatStateOf(screenHeight.toFloat()) }

    val swipeUpY = remember { Animatable(lastSwipeUpY) }

    val swipeDownY = remember { Animatable(lastSwipeDownY) }

    val wallpaperManagerWrapper = LocalWallpaperManager.current

    val view = LocalView.current

    val activity = LocalActivity.current as ComponentActivity

    val swipeY by remember {
        derivedStateOf {
            if (swipeUpY.value < screenHeight.toFloat() && gestureSettings.swipeUp.eblanActionType == EblanActionType.OpenAppDrawer) {
                swipeUpY
            } else if (swipeDownY.value < screenHeight.toFloat() && gestureSettings.swipeDown.eblanActionType == EblanActionType.OpenAppDrawer) {
                swipeDownY
            } else {
                Animatable(screenHeight.toFloat())
            }
        }
    }

    val pagerAlpha by remember {
        derivedStateOf {
            val threshold = screenHeight / 2

            ((swipeY.value - threshold) / threshold).coerceIn(0f, 1f)
        }
    }

    var isPressHome by remember { mutableStateOf(false) }

    var eblanApplicationInfoGroup by remember { mutableStateOf<EblanApplicationInfoGroup?>(null) }

    val isApplicationScreenVisible by remember {
        derivedStateOf {
            swipeY.value < screenHeight.toFloat()
        }
    }

    val alpha by remember {
        derivedStateOf {
            if (experimentalSettings.klwpIntegration) {
                1f
            } else {
                ((screenHeight - swipeY.value) / (screenHeight / 2)).coerceIn(0f, 1f)
            }
        }
    }

    val cornerSize by remember {
        derivedStateOf {
            val progress = swipeY.value.coerceAtLeast(0f) / screenHeight

            (20 * progress).dp
        }
    }

    LaunchedEffect(key1 = hasDoubleTap) {
        handleHasDoubleTap(
            hasDoubleTap = hasDoubleTap,
            gestureSettings = gestureSettings,
            launcherApps = launcherApps,
            context = context,
            onOpenAppDrawer = {
                showAppDrawer = true
            },
        )

        hasDoubleTap = false
    }

    DisposableEffect(key1 = activity) {
        val listener = Consumer<Intent> { intent ->
            scope.launch {
                handleActionMainIntent(
                    gridHorizontalPagerState = gridHorizontalPagerState,
                    intent = intent,
                    initialPage = homeSettings.initialPage,
                    wallpaperScroll = homeSettings.wallpaperScroll,
                    wallpaperManagerWrapper = wallpaperManagerWrapper,
                    pageCount = homeSettings.pageCount,
                    infiniteScroll = homeSettings.infiniteScroll,
                    windowToken = view.windowToken,
                    swipeY = swipeY,
                    screenHeight = screenHeight,
                    showWidgets = showWidgets,
                    showShortcutConfigActivities = showShortcutConfigActivities,
                    eblanApplicationInfoGroup = eblanApplicationInfoGroup,
                    onHome = {
                        isPressHome = true
                    },
                )

                handleEblanActionIntent(
                    intent = intent,
                    launcherApps = launcherApps,
                    context = context,
                    onOpenAppDrawer = {
                        showAppDrawer = true
                    },
                )
            }
        }

        activity.addOnNewIntentListener(listener)

        onDispose {
            activity.removeOnNewIntentListener(listener)
        }
    }

    LaunchedEffect(key1 = drag) {
        if (drag == Drag.End || drag == Drag.Cancel) {
            onResetOverlay()
        }
    }

    LaunchedEffect(key1 = isApplicationScreenVisible) {
        handleKlwpBroadcasts(
            klwpIntegration = experimentalSettings.klwpIntegration,
            isApplicationScreenVisible = isApplicationScreenVisible,
            context = context,
        )
    }

    LaunchedEffect(key1 = swipeY) {
        snapshotFlow { swipeY.value }.onEach { swipeY ->
            if (swipeY == screenHeight.toFloat()) {
                lastSwipeUpY = screenHeight.toFloat()
                lastSwipeDownY = screenHeight.toFloat()
            } else {
                lastSwipeUpY = 0f
                lastSwipeDownY = 0f
            }
        }.collect()
    }

    HorizontalPagerScreen(
        modifier = modifier
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        scope.launch {
                            swipeUpY.snapTo(swipeUpY.value + dragAmount)

                            swipeDownY.snapTo(swipeDownY.value - dragAmount)
                        }
                    },
                    onDragEnd = {
                        swipeEblanAction(
                            gestureSettings = gestureSettings,
                            swipeUpY = swipeUpY.value,
                            swipeDownY = swipeDownY.value,
                            screenHeight = screenHeight,
                            launcherApps = launcherApps,
                            context = context,
                        )

                        scope.launch {
                            resetSwipeOffset(
                                gestureSettings = gestureSettings,
                                swipeDownY = swipeDownY,
                                screenHeight = screenHeight,
                                swipeUpY = swipeUpY,
                            )
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            swipeUpY.animateTo(screenHeight.toFloat())

                            swipeDownY.animateTo(screenHeight.toFloat())
                        }
                    },
                )
            }
            .alpha(pagerAlpha),
        gridHorizontalPagerState = gridHorizontalPagerState,
        currentPage = currentPage,
        gridItems = gridItems,
        gridItemsByPage = gridItemsByPage,
        gridWidth = gridWidth,
        gridHeight = gridHeight,
        paddingValues = paddingValues,
        dockGridItems = dockGridItems,
        textColor = textColor,
        drag = drag,
        hasShortcutHostPermission = hasShortcutHostPermission,
        hasSystemFeatureAppWidgets = hasSystemFeatureAppWidgets,
        gridItemSource = gridItemSource,
        homeSettings = homeSettings,
        statusBarNotifications = statusBarNotifications,
        eblanShortcutInfos = eblanShortcutInfosGroup,
        eblanAppWidgetProviderInfos = eblanAppWidgetProviderInfosGroup,
        iconPackFilePaths = iconPackFilePaths,
        isPressHome = isPressHome,
        screen = screen,
        onTapFolderGridItem = onTapFolderGridItem,
        onEditGridItem = onEditGridItem,
        onResize = onResize,
        onSettings = onSettings,
        onEditPage = onEditPage,
        onWidgets = {
            showWidgets = true
        },
        onShortcutConfigActivities = {
            showShortcutConfigActivities = true
        },
        onDoubleTap = {
            hasDoubleTap = true
        },
        onLongPressGridItem = onLongPressGridItem,
        onUpdateGridItemOffset = onUpdateGridItemOffset,
        onDraggingGridItem = onDraggingGridItem,
        onDeleteGridItem = onDeleteGridItem,
        onUpdateSharedElementKey = onUpdateSharedElementKey,
        onUpdateEblanApplicationInfoGroup = { newEblanApplicationInfoGroup ->
            eblanApplicationInfoGroup = newEblanApplicationInfoGroup
        },
        onOpenAppDrawer = {
            showAppDrawer = true
        },
    )

    if (gestureSettings.swipeUp.eblanActionType == EblanActionType.OpenAppDrawer || gestureSettings.swipeDown.eblanActionType == EblanActionType.OpenAppDrawer) {
        ApplicationScreen(
            currentPage = currentPage,
            swipeY = swipeY.value,
            getEblanApplicationInfosByLabel = getEblanApplicationInfosByLabel,
            paddingValues = paddingValues,
            drag = drag,
            appDrawerSettings = appDrawerSettings,
            screenHeight = screenHeight,
            eblanShortcutInfosGroup = eblanShortcutInfosGroup,
            hasShortcutHostPermission = hasShortcutHostPermission,
            eblanAppWidgetProviderInfosGroup = eblanAppWidgetProviderInfosGroup,
            iconPackFilePaths = iconPackFilePaths,
            onLongPressGridItem = onLongPressGridItem,
            onUpdateGridItemOffset = onUpdateGridItemOffset,
            onGetEblanApplicationInfosByLabel = onGetEblanApplicationInfosByLabel,
            gridItemSource = gridItemSource,
            isPressHome = isPressHome,
            managedProfileResult = managedProfileResult,
            screen = screen,
            klwpIntegration = experimentalSettings.klwpIntegration,
            alpha = alpha,
            cornerSize = cornerSize,
            onDismiss = {
                scope.launch {
                    swipeY.animateTo(
                        targetValue = screenHeight.toFloat(),
                        animationSpec = tween(
                            easing = FastOutSlowInEasing,
                        ),
                    )

                    isPressHome = false
                }
            },
            onDraggingGridItem = onDraggingGridItem,
            onVerticalDrag = { dragAmount ->
                scope.launch {
                    swipeY.snapTo(swipeY.value + dragAmount)
                }
            },
            onDragEnd = { remaining ->
                scope.launch {
                    handleApplyFling(
                        offsetY = swipeY,
                        remaining = remaining,
                        screenHeight = screenHeight,
                    )
                }
            },
            onEditApplicationInfo = onEditApplicationInfo,
            onUpdateSharedElementKey = onUpdateSharedElementKey,
        )
    }

    if (showAppDrawer) {
        LaunchedEffect(key1 = Unit) {
            swipeY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            )
        }

        ApplicationScreen(
            currentPage = currentPage,
            swipeY = swipeY.value,
            getEblanApplicationInfosByLabel = getEblanApplicationInfosByLabel,
            paddingValues = paddingValues,
            drag = drag,
            appDrawerSettings = appDrawerSettings,
            screenHeight = screenHeight,
            eblanShortcutInfosGroup = eblanShortcutInfosGroup,
            hasShortcutHostPermission = hasShortcutHostPermission,
            eblanAppWidgetProviderInfosGroup = eblanAppWidgetProviderInfosGroup,
            iconPackFilePaths = iconPackFilePaths,
            managedProfileResult = managedProfileResult,
            screen = screen,
            onLongPressGridItem = onLongPressGridItem,
            onUpdateGridItemOffset = onUpdateGridItemOffset,
            onGetEblanApplicationInfosByLabel = onGetEblanApplicationInfosByLabel,
            gridItemSource = gridItemSource,
            isPressHome = isPressHome,
            klwpIntegration = experimentalSettings.klwpIntegration,
            alpha = alpha,
            cornerSize = cornerSize,
            onDismiss = {
                scope.launch {
                    swipeY.animateTo(
                        targetValue = screenHeight.toFloat(),
                        animationSpec = tween(
                            easing = FastOutSlowInEasing,
                        ),
                    )

                    showAppDrawer = false

                    isPressHome = false
                }
            },
            onDraggingGridItem = onDraggingGridItem,
            onVerticalDrag = { dragAmount ->
                scope.launch {
                    swipeY.snapTo(swipeY.value + dragAmount)
                }
            },
            onDragEnd = { remaining ->
                scope.launch {
                    handleApplyFling(
                        offsetY = swipeY,
                        remaining = remaining,
                        screenHeight = screenHeight,
                        onDismiss = {
                            showAppDrawer = false
                        },
                    )
                }
            },
            onEditApplicationInfo = onEditApplicationInfo,
            onUpdateSharedElementKey = onUpdateSharedElementKey,
        )
    }

    if (showWidgets) {
        WidgetScreen(
            currentPage = currentPage,
            eblanAppWidgetProviderInfos = eblanAppWidgetProviderInfos,
            gridItemSettings = homeSettings.gridItemSettings,
            paddingValues = paddingValues,
            drag = drag,
            screenHeight = screenHeight,
            isPressHome = isPressHome,
            screen = screen,
            onLongPressGridItem = onLongPressGridItem,
            onUpdateGridItemOffset = onUpdateGridItemOffset,
            onGetEblanAppWidgetProviderInfosByLabel = onGetEblanAppWidgetProviderInfosByLabel,
            onDismiss = {
                showWidgets = false

                isPressHome = false
            },
            onDraggingGridItem = onDraggingGridItem,
            onUpdateSharedElementKey = onUpdateSharedElementKey,
        )
    }

    if (showShortcutConfigActivities) {
        ShortcutConfigScreen(
            currentPage = currentPage,
            eblanShortcutConfigs = eblanShortcutConfigs,
            paddingValues = paddingValues,
            drag = drag,
            gridItemSettings = homeSettings.gridItemSettings,
            screenHeight = screenHeight,
            isPressHome = isPressHome,
            screen = screen,
            onLongPressGridItem = onLongPressGridItem,
            onUpdateGridItemOffset = onUpdateGridItemOffset,
            onGetEblanShortcutConfigsByLabel = onGetEblanShortcutConfigsByLabel,
            onDismiss = {
                showShortcutConfigActivities = false

                isPressHome = false
            },
            onDraggingGridItem = onDraggingGridItem,
            onUpdateSharedElementKey = onUpdateSharedElementKey,
        )
    }

    if (eblanApplicationInfoGroup != null) {
        AppWidgetScreen(
            currentPage = currentPage,
            eblanApplicationInfoGroup = eblanApplicationInfoGroup,
            eblanAppWidgetProviderInfosGroup = eblanAppWidgetProviderInfosGroup,
            gridItemSettings = homeSettings.gridItemSettings,
            paddingValues = paddingValues,
            drag = drag,
            screenHeight = screenHeight,
            isPressHome = isPressHome,
            screen = screen,
            onLongPressGridItem = onLongPressGridItem,
            onUpdateGridItemOffset = onUpdateGridItemOffset,
            onDismiss = {
                eblanApplicationInfoGroup = null

                isPressHome = false
            },
            onDraggingGridItem = onDraggingGridItem,
            onUpdateSharedElementKey = onUpdateSharedElementKey,
        )
    }
}
