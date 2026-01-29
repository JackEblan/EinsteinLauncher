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
package com.eblan.launcher.data.room.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration7To8 : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        updateApplicationInfoGridItemEntities(db)

        updateShortcutInfoGridItemEntities(db)

        updateFolderGridItemEntities(db)

        updateShortcutConfigGridItemEntities(db)

        updateWidgetGridItemEntities(db)
    }

    private fun updateApplicationInfoGridItemEntities(db: SupportSQLiteDatabase) {
        // Add action columns to ApplicationInfoGridItemEntity
        db.execSQL(
            """
                ALTER TABLE ApplicationInfoGridItemEntity 
                ADD COLUMN doubleTap_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ApplicationInfoGridItemEntity 
                ADD COLUMN doubleTap_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ApplicationInfoGridItemEntity 
                ADD COLUMN doubleTap_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ApplicationInfoGridItemEntity 
                ADD COLUMN swipeUp_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ApplicationInfoGridItemEntity 
                ADD COLUMN swipeUp_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ApplicationInfoGridItemEntity 
                ADD COLUMN swipeUp_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ApplicationInfoGridItemEntity 
                ADD COLUMN swipeDown_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ApplicationInfoGridItemEntity 
                ADD COLUMN swipeDown_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ApplicationInfoGridItemEntity 
                ADD COLUMN swipeDown_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ApplicationInfoGridItemEntity 
            ADD COLUMN customTextColor INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ApplicationInfoGridItemEntity 
            ADD COLUMN customBackgroundColor INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ApplicationInfoGridItemEntity 
            ADD COLUMN padding INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ApplicationInfoGridItemEntity 
            ADD COLUMN cornerRadius INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
    }

    private fun updateShortcutInfoGridItemEntities(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                ALTER TABLE ShortcutInfoGridItemEntity 
                ADD COLUMN doubleTap_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutInfoGridItemEntity 
                ADD COLUMN doubleTap_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutInfoGridItemEntity 
                ADD COLUMN doubleTap_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutInfoGridItemEntity 
                ADD COLUMN swipeUp_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutInfoGridItemEntity 
                ADD COLUMN swipeUp_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutInfoGridItemEntity 
                ADD COLUMN swipeUp_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutInfoGridItemEntity 
                ADD COLUMN swipeDown_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutInfoGridItemEntity 
                ADD COLUMN swipeDown_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutInfoGridItemEntity 
                ADD COLUMN swipeDown_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ShortcutInfoGridItemEntity 
            ADD COLUMN customTextColor INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ShortcutInfoGridItemEntity 
            ADD COLUMN customBackgroundColor INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ShortcutInfoGridItemEntity 
            ADD COLUMN padding INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ShortcutInfoGridItemEntity 
            ADD COLUMN cornerRadius INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
    }

    private fun updateFolderGridItemEntities(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                ALTER TABLE FolderGridItemEntity 
                ADD COLUMN doubleTap_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE FolderGridItemEntity 
                ADD COLUMN doubleTap_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE FolderGridItemEntity 
                ADD COLUMN doubleTap_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE FolderGridItemEntity 
                ADD COLUMN swipeUp_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE FolderGridItemEntity 
                ADD COLUMN swipeUp_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE FolderGridItemEntity 
                ADD COLUMN swipeUp_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE FolderGridItemEntity 
                ADD COLUMN swipeDown_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE FolderGridItemEntity 
                ADD COLUMN swipeDown_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE FolderGridItemEntity 
                ADD COLUMN swipeDown_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE FolderGridItemEntity 
            ADD COLUMN customTextColor INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE FolderGridItemEntity 
            ADD COLUMN customBackgroundColor INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE FolderGridItemEntity 
            ADD COLUMN padding INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE FolderGridItemEntity 
            ADD COLUMN cornerRadius INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
    }

    private fun updateShortcutConfigGridItemEntities(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                ALTER TABLE ShortcutConfigGridItemEntity 
                ADD COLUMN doubleTap_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutConfigGridItemEntity 
                ADD COLUMN doubleTap_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutConfigGridItemEntity 
                ADD COLUMN doubleTap_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )

        db.execSQL(
            """
                ALTER TABLE ShortcutConfigGridItemEntity 
                ADD COLUMN swipeUp_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutConfigGridItemEntity 
                ADD COLUMN swipeUp_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutConfigGridItemEntity 
                ADD COLUMN swipeUp_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )

        db.execSQL(
            """
                ALTER TABLE ShortcutConfigGridItemEntity 
                ADD COLUMN swipeDown_eblanActionType TEXT NOT NULL DEFAULT 'None'
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutConfigGridItemEntity 
                ADD COLUMN swipeDown_serialNumber INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE ShortcutConfigGridItemEntity 
                ADD COLUMN swipeDown_componentName TEXT NOT NULL DEFAULT ''
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ShortcutConfigGridItemEntity 
            ADD COLUMN customTextColor INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ShortcutConfigGridItemEntity 
            ADD COLUMN customBackgroundColor INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ShortcutConfigGridItemEntity 
            ADD COLUMN padding INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
            ALTER TABLE ShortcutConfigGridItemEntity 
            ADD COLUMN cornerRadius INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
    }

    private fun updateWidgetGridItemEntities(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
                ALTER TABLE WidgetGridItemEntity 
                ADD COLUMN customTextColor INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE WidgetGridItemEntity 
                ADD COLUMN customBackgroundColor INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE WidgetGridItemEntity 
                ADD COLUMN padding INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
        db.execSQL(
            """
                ALTER TABLE WidgetGridItemEntity 
                ADD COLUMN cornerRadius INTEGER NOT NULL DEFAULT 0
            """.trimIndent(),
        )
    }
}
