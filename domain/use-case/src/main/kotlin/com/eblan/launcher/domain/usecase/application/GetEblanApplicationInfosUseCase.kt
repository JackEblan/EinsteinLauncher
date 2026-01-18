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
package com.eblan.launcher.domain.usecase.application

import com.eblan.launcher.domain.common.dispatcher.Dispatcher
import com.eblan.launcher.domain.common.dispatcher.EblanDispatchers
import com.eblan.launcher.domain.framework.LauncherAppsWrapper
import com.eblan.launcher.domain.model.EblanApplicationInfo
import com.eblan.launcher.domain.model.EblanUserType
import com.eblan.launcher.domain.repository.EblanApplicationInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetEblanApplicationInfosUseCase @Inject constructor(
    private val eblanApplicationInfoRepository: EblanApplicationInfoRepository,
    private val launcherAppsWrapper: LauncherAppsWrapper,
    @param:Dispatcher(EblanDispatchers.Default) private val defaultDispatcher: CoroutineDispatcher,
) {
    operator fun invoke(labelFlow: Flow<String>): Flow<Map<EblanUserType, List<EblanApplicationInfo>>> = combine(
        eblanApplicationInfoRepository.eblanApplicationInfos,
        labelFlow,
    ) { eblanApplicationInfos, label ->
        eblanApplicationInfos.filter { eblanApplicationInfo ->
            eblanApplicationInfo.label.contains(
                other = label,
                ignoreCase = true,
            )
        }.sortedBy { eblanApplicationInfo ->
            eblanApplicationInfo.label.lowercase()
        }.groupBy { eblanApplicationInfo ->
            launcherAppsWrapper.getUserType(serialNumber = eblanApplicationInfo.serialNumber)
        }
    }.flowOn(defaultDispatcher)
}
