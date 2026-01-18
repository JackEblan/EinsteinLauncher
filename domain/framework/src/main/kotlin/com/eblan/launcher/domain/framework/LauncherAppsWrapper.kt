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
package com.eblan.launcher.domain.framework

import com.eblan.launcher.domain.model.EblanUserType
import com.eblan.launcher.domain.model.FastLauncherAppsActivityInfo
import com.eblan.launcher.domain.model.FastLauncherAppsShortcutInfo
import com.eblan.launcher.domain.model.LauncherAppsActivityInfo
import com.eblan.launcher.domain.model.LauncherAppsEvent
import com.eblan.launcher.domain.model.LauncherAppsShortcutInfo
import com.eblan.launcher.domain.model.ShortcutConfigActivityInfo
import kotlinx.coroutines.flow.Flow

interface LauncherAppsWrapper {
    val launcherAppsEvent: Flow<LauncherAppsEvent>

    val hasShortcutHostPermission: Boolean

    suspend fun getActivityList(): List<LauncherAppsActivityInfo>

    suspend fun getFastActivityList(): List<FastLauncherAppsActivityInfo>

    suspend fun getActivityList(
        serialNumber: Long,
        packageName: String,
    ): List<LauncherAppsActivityInfo>

    suspend fun getShortcuts(): List<LauncherAppsShortcutInfo>?

    suspend fun getFastShortcuts(): List<FastLauncherAppsShortcutInfo>?

    suspend fun getShortcutsByPackageName(
        serialNumber: Long,
        packageName: String,
    ): List<LauncherAppsShortcutInfo>?

    suspend fun getShortcutConfigActivityList(
        serialNumber: Long,
        packageName: String,
    ): List<ShortcutConfigActivityInfo>

    fun getUserType(serialNumber: Long): EblanUserType
}
