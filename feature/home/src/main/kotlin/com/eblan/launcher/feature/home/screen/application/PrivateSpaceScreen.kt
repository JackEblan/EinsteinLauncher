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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.eblan.launcher.domain.model.EblanApplicationInfo
import com.eblan.launcher.domain.model.EblanUser
import com.eblan.launcher.domain.model.GridItem
import com.eblan.launcher.domain.model.GridItemData
import com.eblan.launcher.domain.model.ManagedProfileResult
import com.eblan.launcher.feature.home.model.Drag
import com.eblan.launcher.feature.home.model.GridItemSource
import com.eblan.launcher.feature.home.util.getHorizontalAlignment
import com.eblan.launcher.feature.home.util.getSystemTextColor
import com.eblan.launcher.feature.home.util.getVerticalArrangement
import com.eblan.launcher.ui.local.LocalLauncherApps
import com.eblan.launcher.ui.local.LocalPackageManager
import com.eblan.launcher.ui.local.LocalUserManager
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal fun LazyGridScope.privateSpace(
    privateEblanUser: EblanUser?,
    privateEblanApplicationInfos: List<EblanApplicationInfo>,
    managedProfileResult: ManagedProfileResult?,
    isQuietModeEnabled: Boolean,
    drag: Drag,
    appDrawerSettings: AppDrawerSettings,
    paddingValues: PaddingValues,
    iconPackFilePaths: Map<String, String>,
    onUpdateGridItemOffset: (
        intOffset: IntOffset,
        intSize: IntSize,
    ) -> Unit,
    onLongPressGridItem: (
        gridItemSource: GridItemSource,
        imageBitmap: ImageBitmap?,
    ) -> Unit,
    onUpdatePopupMenu: (Boolean) -> Unit,
    onUpdateIsQuietModeEnabled: (Boolean) -> Unit,
) {
    if (privateEblanUser == null) return

    if (privateEblanUser.isPrivateSpaceEntryPointHidden) return

    stickyHeader {
        PrivateSpaceStickyHeader(
            privateEblanUser = privateEblanUser,
            managedProfileResult = managedProfileResult,
            isQuietModeEnabled = isQuietModeEnabled,
            onUpdateIsQuietModeEnabled = onUpdateIsQuietModeEnabled,
        )
    }

    if (!isQuietModeEnabled) {
        items(privateEblanApplicationInfos) { eblanApplicationInfo ->
            PrivateSpaceEblanApplicationInfoItem(
                drag = drag,
                eblanApplicationInfo = eblanApplicationInfo,
                appDrawerSettings = appDrawerSettings,
                paddingValues = paddingValues,
                iconPackFilePaths = iconPackFilePaths,
                onUpdateGridItemOffset = onUpdateGridItemOffset,
                onLongPressGridItem = onLongPressGridItem,
                onUpdatePopupMenu = onUpdatePopupMenu,
            )
        }
    }
}

@Composable
private fun PrivateSpaceStickyHeader(
    privateEblanUser: EblanUser?,
    managedProfileResult: ManagedProfileResult?,
    isQuietModeEnabled: Boolean,
    onUpdateIsQuietModeEnabled: (Boolean) -> Unit,
) {
    if (privateEblanUser == null) return

    val userManager = LocalUserManager.current

    val packageManager = LocalPackageManager.current

    val launcherApps = LocalLauncherApps.current

    val userHandle =
        userManager.getUserForSerialNumber(serialNumber = privateEblanUser.serialNumber)

    val privateSpaceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) {}

    LaunchedEffect(key1 = userHandle) {
        if (userHandle != null) {
            onUpdateIsQuietModeEnabled(userManager.isQuietModeEnabled(userHandle = userHandle))
        }
    }

    LaunchedEffect(key1 = managedProfileResult) {
        if (managedProfileResult != null && managedProfileResult.serialNumber == privateEblanUser.serialNumber) {
            onUpdateIsQuietModeEnabled(managedProfileResult.isQuiteModeEnabled)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Private",
        )

        Row {
            launcherApps.getPrivateSpaceSettingsIntent()?.let { intentSender ->
                IconButton(
                    onClick = {
                        privateSpaceLauncher.launch(
                            IntentSenderRequest.Builder(intentSender).build(),
                        )
                    },
                ) {
                    Icon(
                        imageVector = EblanLauncherIcons.Settings,
                        contentDescription = null,
                    )
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && packageManager.isDefaultLauncher() && userHandle != null) {
                IconButton(
                    onClick = {
                        userManager.requestQuietModeEnabled(
                            enableQuiteMode = !isQuietModeEnabled,
                            userHandle = userHandle,
                        )

                        onUpdateIsQuietModeEnabled(userManager.isQuietModeEnabled(userHandle))
                    },
                ) {
                    Icon(
                        imageVector = if (isQuietModeEnabled) {
                            EblanLauncherIcons.Lock
                        } else {
                            EblanLauncherIcons.LockOpen
                        },
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun PrivateSpaceEblanApplicationInfoItem(
    modifier: Modifier = Modifier,
    drag: Drag,
    eblanApplicationInfo: EblanApplicationInfo,
    appDrawerSettings: AppDrawerSettings,
    paddingValues: PaddingValues,
    iconPackFilePaths: Map<String, String>,
    onUpdateGridItemOffset: (
        intOffset: IntOffset,
        intSize: IntSize,
    ) -> Unit,
    onLongPressGridItem: (
        gridItemSource: GridItemSource,
        imageBitmap: ImageBitmap?,
    ) -> Unit,
    onUpdatePopupMenu: (Boolean) -> Unit,
) {
    var intOffset by remember { mutableStateOf(IntOffset.Zero) }

    var intSize by remember { mutableStateOf(IntSize.Zero) }

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

    val id = remember { Uuid.random().toHexString() }

    LaunchedEffect(key1 = drag) {
        if (drag == Drag.End || drag == Drag.Cancel) {
            scale.stop()

            if (scale.value < 1f) {
                scale.animateTo(1f)
            }
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
                                        page = -1,
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
                                null,
                            )

                            onUpdateGridItemOffset(
                                intOffset,
                                intSize,
                            )

                            onUpdatePopupMenu(true)
                        }
                    },
                    onPress = {
                        awaitRelease()

                        scale.stop()

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
        Box(
            modifier = Modifier.size(appDrawerSettings.gridItemSettings.iconSize.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(eblanApplicationInfo.customIcon ?: icon)
                    .addLastModifiedToFileCacheKey(true).build(),
                contentDescription = null,
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        intOffset = layoutCoordinates.positionInRoot().round()

                        intSize = layoutCoordinates.size
                    }
                    .matchParentSize(),
            )

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
