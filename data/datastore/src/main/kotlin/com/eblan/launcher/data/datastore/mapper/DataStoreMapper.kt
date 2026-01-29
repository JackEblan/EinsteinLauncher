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
package com.eblan.launcher.data.datastore.mapper

import com.eblan.launcher.data.datastore.proto.appdrawer.AppDrawerSettingsProto
import com.eblan.launcher.data.datastore.proto.experimental.ExperimentalSettingsProto
import com.eblan.launcher.data.datastore.proto.general.GeneralSettingsProto
import com.eblan.launcher.data.datastore.proto.general.ThemeProto
import com.eblan.launcher.data.datastore.proto.gesture.EblanActionProto
import com.eblan.launcher.data.datastore.proto.gesture.EblanActionTypeProto
import com.eblan.launcher.data.datastore.proto.gesture.GestureSettingsProto
import com.eblan.launcher.data.datastore.proto.home.GridItemSettingsProto
import com.eblan.launcher.data.datastore.proto.home.HomeSettingsProto
import com.eblan.launcher.data.datastore.proto.home.HorizontalAlignmentProto
import com.eblan.launcher.data.datastore.proto.home.TextColorProto
import com.eblan.launcher.data.datastore.proto.home.VerticalArrangementProto
import com.eblan.launcher.domain.model.AppDrawerSettings
import com.eblan.launcher.domain.model.EblanAction
import com.eblan.launcher.domain.model.EblanActionType
import com.eblan.launcher.domain.model.ExperimentalSettings
import com.eblan.launcher.domain.model.GeneralSettings
import com.eblan.launcher.domain.model.GestureSettings
import com.eblan.launcher.domain.model.GridItemSettings
import com.eblan.launcher.domain.model.HomeSettings
import com.eblan.launcher.domain.model.HorizontalAlignment
import com.eblan.launcher.domain.model.TextColor
import com.eblan.launcher.domain.model.Theme
import com.eblan.launcher.domain.model.VerticalArrangement

internal fun HomeSettingsProto.toHomeSettings(): HomeSettings = HomeSettings(
    columns = columns,
    rows = rows,
    pageCount = pageCount,
    infiniteScroll = infiniteScroll,
    dockColumns = dockColumns,
    dockRows = dockRows,
    dockHeight = dockHeight,
    initialPage = initialPage,
    wallpaperScroll = wallpaperScroll,
    folderColumns = folderColumns,
    folderRows = folderRows,
    gridItemSettings = gridItemSettingsProto.toGridItemSettings(),
    lockScreenOrientation = lockScreenOrientation,
)

internal fun AppDrawerSettingsProto.toAppDrawerSettings(): AppDrawerSettings = AppDrawerSettings(
    appDrawerColumns = appDrawerColumns,
    appDrawerRowsHeight = appDrawerRowsHeight,
    gridItemSettings = gridItemSettingsProto.toGridItemSettings(),
)

internal fun GridItemSettingsProto.toGridItemSettings(): GridItemSettings = GridItemSettings(
    iconSize = iconSize,
    textColor = textColorProto.toTextColor(),
    textSize = textSize,
    showLabel = showLabel,
    singleLineLabel = singleLineLabel,
    horizontalAlignment = horizontalAlignmentProto.toHorizontalAlignment(),
    verticalArrangement = verticalArrangementProto.toVerticalArrangement(),
    customTextColor = customTextColor,
    customBackgroundColor = customBackgroundColor,
    padding = padding,
    cornerRadius = cornerRadius,
)

internal fun GeneralSettingsProto.toGeneralSettings(): GeneralSettings = GeneralSettings(
    theme = themeProto.toDarkThemeConfig(),
    dynamicTheme = dynamicTheme,
    iconPackInfoPackageName = iconPackInfoPackageName,
)

internal fun TextColor.toTextColorProto(): TextColorProto = when (this) {
    TextColor.System -> TextColorProto.TextColorSystem
    TextColor.Light -> TextColorProto.TextColorLight
    TextColor.Dark -> TextColorProto.TextColorDark
    TextColor.Custom -> TextColorProto.TextColorCustom
}

internal fun TextColorProto.toTextColor(): TextColor = when (this) {
    TextColorProto.TextColorSystem, TextColorProto.UNRECOGNIZED -> TextColor.System
    TextColorProto.TextColorLight -> TextColor.Light
    TextColorProto.TextColorDark -> TextColor.Dark
    TextColorProto.TextColorCustom -> TextColor.Custom
}

internal fun HorizontalAlignment.toHorizontalAlignmentProto(): HorizontalAlignmentProto = when (this) {
    HorizontalAlignment.Start -> HorizontalAlignmentProto.Start
    HorizontalAlignment.CenterHorizontally -> HorizontalAlignmentProto.CenterHorizontally
    HorizontalAlignment.End -> HorizontalAlignmentProto.End
}

internal fun HorizontalAlignmentProto.toHorizontalAlignment(): HorizontalAlignment = when (this) {
    HorizontalAlignmentProto.Start -> HorizontalAlignment.Start
    HorizontalAlignmentProto.CenterHorizontally, HorizontalAlignmentProto.UNRECOGNIZED -> HorizontalAlignment.CenterHorizontally
    HorizontalAlignmentProto.End -> HorizontalAlignment.End
}

internal fun VerticalArrangement.toVerticalArrangementProto(): VerticalArrangementProto = when (this) {
    VerticalArrangement.Top -> VerticalArrangementProto.Top
    VerticalArrangement.Center -> VerticalArrangementProto.Center
    VerticalArrangement.Bottom -> VerticalArrangementProto.Bottom
}

internal fun VerticalArrangementProto.toVerticalArrangement(): VerticalArrangement = when (this) {
    VerticalArrangementProto.Top -> VerticalArrangement.Top
    VerticalArrangementProto.Center, VerticalArrangementProto.UNRECOGNIZED -> VerticalArrangement.Center
    VerticalArrangementProto.Bottom -> VerticalArrangement.Bottom
}

internal fun EblanAction.toEblanActionProto(): EblanActionProto = EblanActionProto.newBuilder()
    .setEblanActionTypeProto(eblanActionType.toEblanActionTypeProto())
    .setSerialNumber(serialNumber)
    .setComponentName(componentName)
    .build()

internal fun GestureSettingsProto.toGestureSettings(): GestureSettings = GestureSettings(
    doubleTap = doubleTapProto.toEblanAction(),
    swipeUp = swipeUpProto.toEblanAction(),
    swipeDown = swipeDownProto.toEblanAction(),
)

internal fun EblanActionType.toEblanActionTypeProto(): EblanActionTypeProto = when (this) {
    EblanActionType.None -> EblanActionTypeProto.None
    EblanActionType.OpenAppDrawer -> EblanActionTypeProto.OpenAppDrawer
    EblanActionType.OpenNotificationPanel -> EblanActionTypeProto.OpenNotificationPanel
    EblanActionType.OpenApp -> EblanActionTypeProto.OpenApp
    EblanActionType.LockScreen -> EblanActionTypeProto.LockScreen
    EblanActionType.OpenQuickSettings -> EblanActionTypeProto.OpenQuickSettings
    EblanActionType.OpenRecents -> EblanActionTypeProto.OpenRecents
}

internal fun EblanActionTypeProto.toEblanActionType(): EblanActionType = when (this) {
    EblanActionTypeProto.None, EblanActionTypeProto.UNRECOGNIZED -> EblanActionType.None
    EblanActionTypeProto.OpenAppDrawer -> EblanActionType.OpenAppDrawer
    EblanActionTypeProto.OpenNotificationPanel -> EblanActionType.OpenNotificationPanel
    EblanActionTypeProto.OpenApp -> EblanActionType.OpenApp
    EblanActionTypeProto.LockScreen -> EblanActionType.LockScreen
    EblanActionTypeProto.OpenQuickSettings -> EblanActionType.OpenQuickSettings
    EblanActionTypeProto.OpenRecents -> EblanActionType.OpenRecents
}

internal fun EblanActionProto.toEblanAction(): EblanAction = EblanAction(
    eblanActionType = eblanActionTypeProto.toEblanActionType(),
    serialNumber = serialNumber,
    componentName = componentName,
)

internal fun Theme.toThemeProto(): ThemeProto = when (this) {
    Theme.System -> ThemeProto.DarkThemeConfigSystem
    Theme.Light -> ThemeProto.DarkThemeConfigLight
    Theme.Dark -> ThemeProto.DarkThemeConfigDark
}

internal fun ThemeProto.toDarkThemeConfig(): Theme = when (this) {
    ThemeProto.DarkThemeConfigSystem, ThemeProto.UNRECOGNIZED -> Theme.System
    ThemeProto.DarkThemeConfigLight -> Theme.Light
    ThemeProto.DarkThemeConfigDark -> Theme.Dark
}

internal fun GridItemSettings.toGridItemSettingsProto(): GridItemSettingsProto = GridItemSettingsProto.newBuilder()
    .setIconSize(iconSize)
    .setTextColorProto(textColor.toTextColorProto())
    .setTextSize(textSize)
    .setShowLabel(showLabel)
    .setSingleLineLabel(singleLineLabel)
    .setHorizontalAlignmentProto(horizontalAlignment.toHorizontalAlignmentProto())
    .setVerticalArrangementProto(verticalArrangement.toVerticalArrangementProto())
    .setCustomTextColor(customTextColor)
    .setCustomBackgroundColor(customBackgroundColor)
    .setPadding(padding)
    .setCornerRadius(cornerRadius)
    .build()

internal fun HomeSettings.toHomeSettingsProto(): HomeSettingsProto = HomeSettingsProto.newBuilder()
    .setColumns(columns)
    .setRows(rows)
    .setPageCount(pageCount)
    .setInfiniteScroll(infiniteScroll)
    .setDockColumns(dockColumns)
    .setDockRows(dockRows)
    .setDockHeight(dockHeight)
    .setInitialPage(initialPage)
    .setWallpaperScroll(wallpaperScroll)
    .setFolderColumns(folderColumns)
    .setFolderRows(folderRows)
    .setGridItemSettingsProto(gridItemSettings.toGridItemSettingsProto())
    .setLockScreenOrientation(lockScreenOrientation)
    .build()

internal fun AppDrawerSettings.toAppDrawerSettingsProto(): AppDrawerSettingsProto = AppDrawerSettingsProto.newBuilder()
    .setAppDrawerColumns(appDrawerColumns)
    .setAppDrawerRowsHeight(appDrawerRowsHeight)
    .setGridItemSettingsProto(gridItemSettings.toGridItemSettingsProto())
    .build()

internal fun GeneralSettings.toGeneralSettingsProto(): GeneralSettingsProto = GeneralSettingsProto.newBuilder()
    .setThemeProto(theme.toThemeProto())
    .setDynamicTheme(dynamicTheme)
    .setIconPackInfoPackageName(iconPackInfoPackageName)
    .build()

internal fun GestureSettings.toGestureSettingsProto(): GestureSettingsProto = GestureSettingsProto.newBuilder()
    .setDoubleTapProto(doubleTap.toEblanActionProto())
    .setSwipeUpProto(swipeUp.toEblanActionProto())
    .setSwipeDownProto(swipeDown.toEblanActionProto())
    .build()

internal fun ExperimentalSettings.toExperimentalSettingsProto(): ExperimentalSettingsProto = ExperimentalSettingsProto.newBuilder()
    .setSyncData(syncData)
    .setFirstLaunch(firstLaunch)
    .setLockMovement(lockMovement)
    .setKlwpIntegration(klwpIntegration)
    .build()

internal fun ExperimentalSettingsProto.toExperimentalSettings(): ExperimentalSettings = ExperimentalSettings(
    syncData = syncData,
    firstLaunch = firstLaunch,
    lockMovement = lockMovement,
    klwpIntegration = klwpIntegration,
)
