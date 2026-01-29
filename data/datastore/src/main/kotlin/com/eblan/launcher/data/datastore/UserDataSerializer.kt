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
package com.eblan.launcher.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.eblan.launcher.data.datastore.mapper.toEblanActionProto
import com.eblan.launcher.data.datastore.mapper.toThemeProto
import com.eblan.launcher.data.datastore.proto.UserDataProto
import com.eblan.launcher.data.datastore.proto.appdrawer.AppDrawerSettingsProto
import com.eblan.launcher.data.datastore.proto.experimental.ExperimentalSettingsProto
import com.eblan.launcher.data.datastore.proto.general.GeneralSettingsProto
import com.eblan.launcher.data.datastore.proto.gesture.GestureSettingsProto
import com.eblan.launcher.data.datastore.proto.home.GridItemSettingsProto
import com.eblan.launcher.data.datastore.proto.home.HomeSettingsProto
import com.eblan.launcher.data.datastore.proto.home.HorizontalAlignmentProto
import com.eblan.launcher.data.datastore.proto.home.TextColorProto
import com.eblan.launcher.data.datastore.proto.home.VerticalArrangementProto
import com.eblan.launcher.domain.model.EblanAction
import com.eblan.launcher.domain.model.EblanActionType
import com.eblan.launcher.domain.model.Theme
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserDataSerializer @Inject constructor() : Serializer<UserDataProto> {
    private val defaultGeneralSettingsProto = GeneralSettingsProto.newBuilder().apply {
        themeProto = Theme.System.toThemeProto()
        dynamicTheme = false
        iconPackInfoPackageName = ""
    }.build()

    private val defaultGridItemSettingsProto = GridItemSettingsProto.newBuilder().apply {
        iconSize = 50
        textColorProto = TextColorProto.TextColorSystem
        textSize = 10
        showLabel = true
        singleLineLabel = true
        horizontalAlignmentProto = HorizontalAlignmentProto.CenterHorizontally
        verticalArrangementProto = VerticalArrangementProto.Top
        customTextColor = 0x00000000
        customBackgroundColor = 0x00000000
        padding = 0
        cornerRadius = 0
    }.build()

    private val defaultHomeSettingsProto = HomeSettingsProto.newBuilder().apply {
        columns = 5
        rows = 5
        pageCount = 2
        infiniteScroll = false
        dockColumns = 5
        dockRows = 1
        dockHeight = 100
        initialPage = 0
        wallpaperScroll = false
        folderColumns = 5
        folderRows = 5
        gridItemSettingsProto = defaultGridItemSettingsProto
        lockScreenOrientation = false
    }.build()

    private val defaultAppDrawerSettingsProto = AppDrawerSettingsProto.newBuilder().apply {
        appDrawerColumns = 5
        appDrawerRowsHeight = 100
        gridItemSettingsProto = defaultGridItemSettingsProto
    }.build()

    private val defaultGestureSettingsProto = GestureSettingsProto.newBuilder().apply {
        doubleTapProto = EblanAction(
            eblanActionType = EblanActionType.None,
            serialNumber = 0L,
            componentName = "",
        ).toEblanActionProto()

        swipeUpProto = EblanAction(
            eblanActionType = EblanActionType.OpenAppDrawer,
            serialNumber = 0L,
            componentName = "",
        ).toEblanActionProto()

        swipeDownProto = EblanAction(
            eblanActionType = EblanActionType.None,
            serialNumber = 0L,
            componentName = "",
        ).toEblanActionProto()
    }.build()

    private val defaultExperimentalSettings = ExperimentalSettingsProto.newBuilder().apply {
        syncData = true
        firstLaunch = true
        lockMovement = false
        klwpIntegration = false
    }.build()

    override val defaultValue: UserDataProto = UserDataProto.newBuilder().apply {
        homeSettingsProto = defaultHomeSettingsProto
        appDrawerSettingsProto = defaultAppDrawerSettingsProto
        gestureSettingsProto = defaultGestureSettingsProto
        generalSettingsProto = defaultGeneralSettingsProto
        experimentalSettingsProto = defaultExperimentalSettings
    }.build()

    override suspend fun readFrom(input: InputStream): UserDataProto = try {
        UserDataProto.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read proto.", exception)
    }

    override suspend fun writeTo(t: UserDataProto, output: OutputStream) {
        t.writeTo(output)
    }
}
