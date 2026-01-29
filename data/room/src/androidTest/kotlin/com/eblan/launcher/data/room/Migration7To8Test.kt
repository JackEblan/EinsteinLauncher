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
package com.eblan.launcher.data.room

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eblan.launcher.data.room.migration.Migration7To8
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class Migration7To8Test {
    private val testDatabase = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        EblanDatabase::class.java,
    )

    @Test
    @Throws(IOException::class)
    fun migrate7To8() {
        // Create database at version 7
        helper.createDatabase(
            testDatabase,
            7,
        ).apply {
            // ApplicationInfoGridItemEntity
            execSQL(
                """
    INSERT INTO ApplicationInfoGridItemEntity (
        id,
        folderId,
        page,
        startColumn,
        startRow,
        columnSpan,
        rowSpan,
        associate,
        componentName,
        packageName,
        icon,
        label,
        override,
        serialNumber,
        customIcon,
        customLabel,
        -- gridItemSettings (no prefix in version 7)
        iconSize,
        textColor,
        textSize,
        showLabel,
        singleLineLabel,
        horizontalAlignment,
        verticalArrangement
    ) VALUES (
        'app_com.example.app_1',
        NULL,
        0,
        0,
        0,
        1,
        1,
        'APP',
        'com.example.app/.MainActivity',
        'com.example.app',
        'ic_launcher_com_example_app',
        'Example App',
        0,
        1002,
        NULL,
        NULL,
        -- gridItemSettings defaults
        0,
        '#FF000000',
        14,
        1,
        1,
        'CENTER',
        'CENTER'
    )
                """.trimIndent(),
            )

            // ShortcutInfoGridItemEntity
            execSQL(
                """
    INSERT INTO ShortcutInfoGridItemEntity (
        id,
        folderId,
        page,
        startColumn,
        startRow,
        columnSpan,
        rowSpan,
        associate,
        shortcutId,
        packageName,
        shortLabel,
        longLabel,
        icon,
        override,
        serialNumber,
        isEnabled,
        eblanApplicationInfoIcon,
        customIcon,
        customShortLabel,
        -- gridItemSettings (no prefix assumed)
        iconSize,
        textColor,
        textSize,
        showLabel,
        singleLineLabel,
        horizontalAlignment,
        verticalArrangement
    ) VALUES (
        'shortcut_1',
        NULL,
        0,
        1,
        0,
        1,
        1,
        'UNSPECIFIED',
        'shortcut_id_1',
        'com.example',
        'Example',
        'Example Shortcut',
        NULL,
        0,
        1,
        1,
        NULL,
        NULL,
        NULL,
        0,
        '#FF000000',
        14,
        1,
        1,
        'CENTER',
        'CENTER'
    )
                """.trimIndent(),
            )

            // FolderGridItemEntity
            execSQL(
                """
    INSERT INTO FolderGridItemEntity (
        id,
        folderId,
        page,
        startColumn,
        startRow,
        columnSpan,
        rowSpan,
        associate,
        label,
        override,
        pageCount,
        icon,
        -- gridItemSettings (no prefix)
        iconSize,
        textColor,
        textSize,
        showLabel,
        singleLineLabel,
        horizontalAlignment,
        verticalArrangement
    ) VALUES (
        'folder_abc123',
        NULL,
        0,
        0,
        0,
        2,
        2,
        'FOLDER',
        'My Folder',
        0,
        4,
        NULL,
        -- gridItemSettings defaults
        0,
        '#FF000000',
        14,
        1,
        1,
        'CENTER',
        'CENTER'
    )
                """.trimIndent(),
            )

            // ShortcutConfigGridItemEntity
            execSQL(
                """
    INSERT INTO ShortcutConfigGridItemEntity (
        id,
        folderId,
        page,
        startColumn,
        startRow,
        columnSpan,
        rowSpan,
        associate,
        componentName,
        packageName,
        activityIcon,
        activityLabel,
        applicationIcon,
        applicationLabel,
        override,
        serialNumber,
        shortcutIntentName,
        shortcutIntentIcon,
        shortcutIntentUri,
        customIcon,
        customLabel,
        -- gridItemSettings (no prefix in v7)
        iconSize,
        textColor,
        textSize,
        showLabel,
        singleLineLabel,
        horizontalAlignment,
        verticalArrangement
    ) VALUES (
        'config_shortcut_001',
        NULL,
        0,
        2,
        1,
        1,
        1,
        'APP',
        'com.whatsapp/.MainActivity',
        'com.whatsapp',
        NULL,
        NULL,
        'ic_whatsapp_default',
        'WhatsApp',
        0,
        1001,
        NULL,
        NULL,
        NULL,
        NULL,
        NULL,
        -- gridItemSettings defaults
        0,
        '#FF000000',
        14,
        1,
        1,
        'CENTER',
        'CENTER'
    )
                """.trimIndent(),
            )

            // WidgetGridItemEntity
            execSQL(
                """
            INSERT INTO WidgetGridItemEntity (
                id,
                folderId,
                page,
                startColumn,
                startRow,
                columnSpan,
                rowSpan,
                associate,
                appWidgetId,
                packageName,
                componentName,
                configure,
                minWidth,
                minHeight,
                resizeMode,
                minResizeWidth,
                minResizeHeight,
                maxResizeWidth,
                maxResizeHeight,
                targetCellHeight,
                targetCellWidth,
                preview,
                label,
                icon,
                override,
                serialNumber,
                -- gridItemSettings fields (no prefix in v7)
                iconSize,
                textColor,
                textSize,
                showLabel,
                singleLineLabel,
                horizontalAlignment,
                verticalArrangement
            ) VALUES (
                'widget_456',
                NULL,
                1,
                0,
                1,
                2,
                2,
                'WIDGET',
                1234,
                'com.google.android.deskclock',
                'com.google.android.deskclock.widget.AnalogClockWidgetProvider',
                NULL,
                110,
                110,
                15,
                80,
                80,
                400,
                400,
                2,
                2,
                NULL,
                'Clock',
                'ic_clock_widget',
                0,
                2001,
                -- gridItemSettings defaults / example values
                0,
                '#FF000000',
                12,
                1,
                0,
                'CENTER',
                'CENTER'
            )
                """.trimIndent(),
            )

            close()
        }

        // Run migration to version 8
        val dbV8 = helper.runMigrationsAndValidate(
            testDatabase,
            8,
            true,
            Migration7To8(),
        )

        // Verify ApplicationInfoGridItemEntity
        dbV8.query(
            """
    SELECT 
        id,
        folderId,
        page,
        startColumn,
        startRow,
        columnSpan,
        rowSpan,
        associate,
        componentName,
        packageName,
        icon,
        label,
        override,
        serialNumber,
        customIcon,
        customLabel,
        -- gridItemSettings
        iconSize,
        textColor,
        textSize,
        showLabel,
        singleLineLabel,
        horizontalAlignment,
        verticalArrangement,
        customTextColor,
        customBackgroundColor,
        padding,
        cornerRadius,
        -- doubleTap
        doubleTap_eblanActionType,
        doubleTap_serialNumber,
        doubleTap_componentName,
        -- swipeUp
        swipeUp_eblanActionType,
        swipeUp_serialNumber,
        swipeUp_componentName,
        -- swipeDown
        swipeDown_eblanActionType,
        swipeDown_serialNumber,
        swipeDown_componentName
    FROM ApplicationInfoGridItemEntity
    WHERE id = 'app_com.example.app_1'
            """.trimIndent(),
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())

            // Core identity & position fields – should be unchanged
            assertEquals(
                "app_com.example.app_1",
                cursor.getString(cursor.getColumnIndexOrThrow("id")),
            )
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("folderId")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("page")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("startColumn")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("startRow")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("columnSpan")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("rowSpan")))
            assertEquals("APP", cursor.getString(cursor.getColumnIndexOrThrow("associate")))

            // Application identification
            assertEquals(
                "com.example.app/.MainActivity",
                cursor.getString(cursor.getColumnIndexOrThrow("componentName")),
            )
            assertEquals(
                "com.example.app",
                cursor.getString(cursor.getColumnIndexOrThrow("packageName")),
            )

            // Visual & label fields
            assertEquals(
                "ic_launcher_com_example_app",
                cursor.getString(cursor.getColumnIndexOrThrow("icon")),
            )
            assertEquals("Example App", cursor.getString(cursor.getColumnIndexOrThrow("label")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("override")))
            assertEquals(1002L, cursor.getLong(cursor.getColumnIndexOrThrow("serialNumber")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("customIcon")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("customLabel")))

            // gridItemSettings – should be preserved
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("iconSize")))
            assertEquals("#FF000000", cursor.getString(cursor.getColumnIndexOrThrow("textColor")))
            assertEquals(14f, cursor.getFloat(cursor.getColumnIndexOrThrow("textSize")), 0.001f)
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("showLabel")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("singleLineLabel")))
            assertEquals(
                "CENTER",
                cursor.getString(cursor.getColumnIndexOrThrow("horizontalAlignment")),
            )
            assertEquals(
                "CENTER",
                cursor.getString(cursor.getColumnIndexOrThrow("verticalArrangement")),
            )

            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("customTextColor")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("customBackgroundColor")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("padding")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("cornerRadius")),
            )

            // New gesture fields — should have default values after migration
            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("doubleTap_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("doubleTap_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("doubleTap_componentName")),
            )

            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeUp_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("swipeUp_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeUp_componentName")),
            )

            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeDown_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("swipeDown_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeDown_componentName")),
            )
        }

        // Verify ShortcutInfoGridItemEntity
        dbV8.query(
            """
    SELECT 
        id,
        folderId,
        page,
        startColumn,
        startRow,
        columnSpan,
        rowSpan,
        associate,
        shortcutId,
        packageName,
        shortLabel,
        longLabel,
        icon,
        override,
        serialNumber,
        isEnabled,
        eblanApplicationInfoIcon,
        customIcon,
        customShortLabel,
        iconSize,
        textColor,
        textSize,
        showLabel,
        singleLineLabel,
        horizontalAlignment,
        verticalArrangement,
        customTextColor,
        customBackgroundColor,
        padding,
        cornerRadius,
        -- doubleTap
        doubleTap_eblanActionType,
        doubleTap_serialNumber,
        doubleTap_componentName,
        -- swipeUp
        swipeUp_eblanActionType,
        swipeUp_serialNumber,
        swipeUp_componentName,
        -- swipeDown
        swipeDown_eblanActionType,
        swipeDown_serialNumber,
        swipeDown_componentName
    FROM ShortcutInfoGridItemEntity
            """.trimIndent(),
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())

            assertEquals("shortcut_1", cursor.getString(cursor.getColumnIndexOrThrow("id")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("folderId")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("page")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("startColumn")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("startRow")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("columnSpan")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("rowSpan")))
            assertEquals("UNSPECIFIED", cursor.getString(cursor.getColumnIndexOrThrow("associate")))

            assertEquals(
                "com.example",
                cursor.getString(cursor.getColumnIndexOrThrow("packageName")),
            )
            assertEquals(
                "shortcut_id_1",
                cursor.getString(cursor.getColumnIndexOrThrow("shortcutId")),
            )

            assertEquals(
                "Example",
                cursor.getString(cursor.getColumnIndexOrThrow("shortLabel")),
            )

            assertEquals(
                "Example Shortcut",
                cursor.getString(cursor.getColumnIndexOrThrow("longLabel")),
            )

            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("icon")))
            assertEquals(false, cursor.getInt(cursor.getColumnIndexOrThrow("override")) != 0)
            assertEquals(1L, cursor.getLong(cursor.getColumnIndexOrThrow("serialNumber")))
            assertEquals(true, cursor.getInt(cursor.getColumnIndexOrThrow("isEnabled")) != 0)

            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("eblanApplicationInfoIcon")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("customIcon")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("customShortLabel")))

            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("iconSize")))
            assertEquals("#FF000000", cursor.getString(cursor.getColumnIndexOrThrow("textColor")))
            assertEquals(14f, cursor.getFloat(cursor.getColumnIndexOrThrow("textSize")), 0.001f)
            assertEquals(true, cursor.getInt(cursor.getColumnIndexOrThrow("showLabel")) != 0)
            assertEquals(true, cursor.getInt(cursor.getColumnIndexOrThrow("singleLineLabel")) != 0)
            assertEquals(
                "CENTER",
                cursor.getString(cursor.getColumnIndexOrThrow("horizontalAlignment")),
            )
            assertEquals(
                "CENTER",
                cursor.getString(cursor.getColumnIndexOrThrow("verticalArrangement")),
            )

            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("customTextColor")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("customBackgroundColor")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("padding")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("cornerRadius")),
            )

            // New gesture fields — should have default values after migration
            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("doubleTap_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("doubleTap_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("doubleTap_componentName")),
            )

            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeUp_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("swipeUp_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeUp_componentName")),
            )

            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeDown_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("swipeDown_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeDown_componentName")),
            )
        }

        // Verify FolderGridItemEntity
        dbV8.query(
            """
    SELECT 
        id,
        folderId,
        page,
        startColumn,
        startRow,
        columnSpan,
        rowSpan,
        associate,
        label,
        override,
        pageCount,
        icon,
        -- gridItemSettings (no prefix)
        iconSize,
        textColor,
        textSize,
        showLabel,
        singleLineLabel,
        horizontalAlignment,
        verticalArrangement,
        customTextColor,
        customBackgroundColor,
        padding,
        cornerRadius,
        -- doubleTap
        doubleTap_eblanActionType,
        doubleTap_serialNumber,
        doubleTap_componentName,
        -- swipeUp
        swipeUp_eblanActionType,
        swipeUp_serialNumber,
        swipeUp_componentName,
        -- swipeDown
        swipeDown_eblanActionType,
        swipeDown_serialNumber,
        swipeDown_componentName
    FROM FolderGridItemEntity
    WHERE id = 'folder_abc123'
            """.trimIndent(),
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())

            // Core fields — should be unchanged after migration
            assertEquals("folder_abc123", cursor.getString(cursor.getColumnIndexOrThrow("id")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("folderId")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("page")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("startColumn")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("startRow")))
            assertEquals(2, cursor.getInt(cursor.getColumnIndexOrThrow("columnSpan")))
            assertEquals(2, cursor.getInt(cursor.getColumnIndexOrThrow("rowSpan")))
            assertEquals("FOLDER", cursor.getString(cursor.getColumnIndexOrThrow("associate")))
            assertEquals("My Folder", cursor.getString(cursor.getColumnIndexOrThrow("label")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("override")))
            assertEquals(4, cursor.getInt(cursor.getColumnIndexOrThrow("pageCount")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("icon")))

            // gridItemSettings — should be preserved
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("iconSize")))
            assertEquals("#FF000000", cursor.getString(cursor.getColumnIndexOrThrow("textColor")))
            assertEquals(14f, cursor.getFloat(cursor.getColumnIndexOrThrow("textSize")), 0.001f)
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("showLabel")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("singleLineLabel")))
            assertEquals(
                "CENTER",
                cursor.getString(cursor.getColumnIndexOrThrow("horizontalAlignment")),
            )
            assertEquals(
                "CENTER",
                cursor.getString(cursor.getColumnIndexOrThrow("verticalArrangement")),
            )

            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("customTextColor")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("customBackgroundColor")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("padding")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("cornerRadius")),
            )

            // New gesture fields — should have default values after migration
            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("doubleTap_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("doubleTap_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("doubleTap_componentName")),
            )

            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeUp_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("swipeUp_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeUp_componentName")),
            )

            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeDown_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("swipeDown_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeDown_componentName")),
            )
        }

        // Verify ShortcutConfigGridItemEntity
        dbV8.query(
            """
    SELECT 
        id,
        folderId,
        page,
        startColumn,
        startRow,
        columnSpan,
        rowSpan,
        associate,
        componentName,
        packageName,
        activityIcon,
        activityLabel,
        applicationIcon,
        applicationLabel,
        override,
        serialNumber,
        shortcutIntentName,
        shortcutIntentIcon,
        shortcutIntentUri,
        customIcon,
        customLabel,
        -- gridItemSettings
        iconSize,
        textColor,
        textSize,
        showLabel,
        singleLineLabel,
        horizontalAlignment,
        verticalArrangement,
        customTextColor,
        customBackgroundColor,
        padding,
        cornerRadius,
        -- doubleTap
        doubleTap_eblanActionType,
        doubleTap_serialNumber,
        doubleTap_componentName,
        -- swipeUp
        swipeUp_eblanActionType,
        swipeUp_serialNumber,
        swipeUp_componentName,
        -- swipeDown
        swipeDown_eblanActionType,
        swipeDown_serialNumber,
        swipeDown_componentName
    FROM ShortcutConfigGridItemEntity
    WHERE id = 'config_shortcut_001'
            """.trimIndent(),
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())

            // Core identity & position fields — should remain unchanged
            assertEquals(
                "config_shortcut_001",
                cursor.getString(cursor.getColumnIndexOrThrow("id")),
            )
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("folderId")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("page")))
            assertEquals(2, cursor.getInt(cursor.getColumnIndexOrThrow("startColumn")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("startRow")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("columnSpan")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("rowSpan")))
            assertEquals("APP", cursor.getString(cursor.getColumnIndexOrThrow("associate")))

            // Shortcut identification
            assertEquals(
                "com.whatsapp/.MainActivity",
                cursor.getString(cursor.getColumnIndexOrThrow("componentName")),
            )
            assertEquals(
                "com.whatsapp",
                cursor.getString(cursor.getColumnIndexOrThrow("packageName")),
            )

            // Labels & icons — should be preserved
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("activityIcon")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("activityLabel")))
            assertEquals(
                "ic_whatsapp_default",
                cursor.getString(cursor.getColumnIndexOrThrow("applicationIcon")),
            )
            assertEquals(
                "WhatsApp",
                cursor.getString(cursor.getColumnIndexOrThrow("applicationLabel")),
            )

            // Other fields
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("override")))
            assertEquals(1001L, cursor.getLong(cursor.getColumnIndexOrThrow("serialNumber")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("shortcutIntentName")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("shortcutIntentIcon")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("shortcutIntentUri")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("customIcon")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("customLabel")))

            // gridItemSettings — should be unchanged
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("iconSize")))
            assertEquals("#FF000000", cursor.getString(cursor.getColumnIndexOrThrow("textColor")))
            assertEquals(14f, cursor.getFloat(cursor.getColumnIndexOrThrow("textSize")), 0.001f)
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("showLabel")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("singleLineLabel")))
            assertEquals(
                "CENTER",
                cursor.getString(cursor.getColumnIndexOrThrow("horizontalAlignment")),
            )
            assertEquals(
                "CENTER",
                cursor.getString(cursor.getColumnIndexOrThrow("verticalArrangement")),
            )

            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("customTextColor")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("customBackgroundColor")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("padding")),
            )
            assertEquals(
                0,
                cursor.getInt(cursor.getColumnIndexOrThrow("cornerRadius")),
            )

            // New gesture fields — should have default values after migration
            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("doubleTap_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("doubleTap_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("doubleTap_componentName")),
            )

            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeUp_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("swipeUp_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeUp_componentName")),
            )

            assertEquals(
                "None",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeDown_eblanActionType")),
            )
            assertEquals(
                0L,
                cursor.getLong(cursor.getColumnIndexOrThrow("swipeDown_serialNumber")),
            )
            assertEquals(
                "",
                cursor.getString(cursor.getColumnIndexOrThrow("swipeDown_componentName")),
            )
        }

        // Verify WidgetGridItemEntity
        dbV8.query(
            """
        SELECT 
            id,
            folderId,
            page,
            startColumn,
            startRow,
            columnSpan,
            rowSpan,
            associate,
            appWidgetId,
            packageName,
            componentName,
            label,
            icon,
            override,
            serialNumber,
            -- gridItemSettings
            iconSize,
            textColor,
            textSize,
            showLabel,
            singleLineLabel,
            horizontalAlignment,
            verticalArrangement,
            customTextColor,
            customBackgroundColor,
            padding,
            cornerRadius
        FROM WidgetGridItemEntity
        WHERE id = 'widget_456'
            """.trimIndent(),
        ).use { cursor ->
            assertTrue(cursor.moveToFirst())

            // Core identity & position fields – should remain unchanged
            assertEquals("widget_456", cursor.getString(cursor.getColumnIndexOrThrow("id")))
            assertNull(cursor.getString(cursor.getColumnIndexOrThrow("folderId")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("page")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("startColumn")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("startRow")))
            assertEquals(2, cursor.getInt(cursor.getColumnIndexOrThrow("columnSpan")))
            assertEquals(2, cursor.getInt(cursor.getColumnIndexOrThrow("rowSpan")))
            assertEquals("WIDGET", cursor.getString(cursor.getColumnIndexOrThrow("associate")))

            // Widget-specific fields
            assertEquals(1234, cursor.getInt(cursor.getColumnIndexOrThrow("appWidgetId")))
            assertEquals(
                "com.google.android.deskclock",
                cursor.getString(cursor.getColumnIndexOrThrow("packageName")),
            )
            assertEquals(
                "com.google.android.deskclock.widget.AnalogClockWidgetProvider",
                cursor.getString(cursor.getColumnIndexOrThrow("componentName")),
            )
            assertEquals("Clock", cursor.getString(cursor.getColumnIndexOrThrow("label")))
            assertEquals("ic_clock_widget", cursor.getString(cursor.getColumnIndexOrThrow("icon")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("override")))
            assertEquals(2001L, cursor.getLong(cursor.getColumnIndexOrThrow("serialNumber")))

            // Existing gridItemSettings – should be preserved
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("iconSize")))
            assertEquals("#FF000000", cursor.getString(cursor.getColumnIndexOrThrow("textColor")))
            assertEquals(12, cursor.getInt(cursor.getColumnIndexOrThrow("textSize")))
            assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("showLabel")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("singleLineLabel")))
            assertEquals(
                "CENTER",
                cursor.getString(cursor.getColumnIndexOrThrow("horizontalAlignment")),
            )
            assertEquals(
                "CENTER",
                cursor.getString(cursor.getColumnIndexOrThrow("verticalArrangement")),
            )

            // New columns added in migration 7→8 – must exist and have default value 0
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("customTextColor")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("customBackgroundColor")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("padding")))
            assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("cornerRadius")))
        }
    }
}
