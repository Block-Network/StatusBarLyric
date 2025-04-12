/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/Block-Network/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as
 * published by Block-Network contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/Block-Network/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.Tools.isNotNull
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.getWindowSize
import top.yukonga.miuix.kmp.utils.overScrollVertical

@Composable
fun ExtendPage(
    navController: NavController
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val viewLocationOptions = listOf(
        stringResource(R.string.title_gravity_start),
        stringResource(R.string.title_gravity_end)
    )

    val viewLocationSelectedOption = remember { mutableIntStateOf(config.viewLocation) }
    val hideTime = remember { mutableStateOf(config.hideTime) }
    val hideNotificationIcon = remember { mutableStateOf(config.hideNotificationIcon) }
    val hideLyricWhenLockScreen = remember { mutableStateOf(config.hideLyricWhenLockScreen) }
    val longClickStatusBarStop = remember { mutableStateOf(config.longClickStatusBarStop) }
    val clickStatusBarToHideLyric = remember { mutableStateOf(config.clickStatusBarToHideLyric) }
    val slideStatusBarCutSongs = remember { mutableStateOf(config.slideStatusBarCutSongs) }
    val limitVisibilityChange = remember { mutableStateOf(config.limitVisibilityChange) }
    val timeoutRestore = remember { mutableStateOf(config.timeoutRestore) }
    val dynamicLyricSpeed = remember { mutableStateOf(config.dynamicLyricSpeed) }
    val titleSwitch = remember { mutableStateOf(config.titleSwitch) }
    val titleShowWithSameLyric = remember { mutableStateOf(config.titleShowWithSameLyric) }
    val titleGravityOptions = listOf(
        stringResource(R.string.title_gravity_start),
        stringResource(R.string.title_gravity_center),
        stringResource(R.string.title_gravity_end)
    )
    val titleGravitySelectedOption = remember { mutableIntStateOf(config.titleGravity) }
    val showDialog = remember { mutableStateOf(false) }
    val showCutSongsXRadiusDialog = remember { mutableStateOf(false) }
    val showCutSongsYRadiusDialog = remember { mutableStateOf(false) }
    val showTitleDelayDialog = remember { mutableStateOf(false) }
    val showTitleBgColorDialog = remember { mutableStateOf(false) }
    val showTitleRadiusDialog = remember { mutableStateOf(false) }
    val showTitleStrokeWidthDialog = remember { mutableStateOf(false) }
    val showTitleStrokeColorDialog = remember { mutableStateOf(false) }

    val hazeState = remember { HazeState() }
    val hazeStyle = HazeStyle(
        backgroundColor = MiuixTheme.colorScheme.background,
        tint = HazeTint(
            MiuixTheme.colorScheme.background.copy(
                if (scrollBehavior.state.collapsedFraction <= 0f) 1f
                else lerp(1f, 0.67f, (scrollBehavior.state.collapsedFraction))
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                color = Color.Transparent,
                modifier = Modifier
                    .hazeEffect(hazeState) {
                        style = hazeStyle
                        blurRadius = 25.dp
                        noiseFactor = 0f
                    }
                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Right))
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Right))
                    .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                    .windowInsetsPadding(WindowInsets.captionBar.only(WindowInsetsSides.Top)),
                title = stringResource(R.string.extend_page),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 20.dp),
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.Back,
                            contentDescription = "Back",
                            tint = MiuixTheme.colorScheme.onBackground
                        )
                    }
                },
                defaultWindowInsetsPadding = false
            )
        },
        popupHost = { null }
    ) {
        LazyColumn(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .height(getWindowSize().height.dp)
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Right))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Right)),
            contentPadding = it,
        ) {
            item {
                Column(Modifier.padding(top = 6.dp)) {
                    Text(
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
                        text = stringResource(R.string.module_extend),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Red
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        SuperDropdown(
                            title = stringResource(R.string.view_location),
                            items = viewLocationOptions,
                            selectedIndex = viewLocationSelectedOption.intValue,
                            onSelectedIndexChange = { newOption ->
                                viewLocationSelectedOption.intValue = newOption
                                config.viewLocation = newOption
                            },
                        )
                        SuperSwitch(
                            title = stringResource(R.string.hide_time),
                            checked = hideTime.value,
                            onCheckedChange = {
                                hideTime.value = it
                                config.hideTime = it
                            }
                        )
                        SuperSwitch(
                            title = stringResource(R.string.hide_notification_icon),
                            checked = hideNotificationIcon.value,
                            onCheckedChange = {
                                hideNotificationIcon.value = it
                                config.hideNotificationIcon = it
                            }
                        )
                        SuperSwitch(
                            title = stringResource(R.string.hide_lyric_when_lock_screen),
                            checked = hideLyricWhenLockScreen.value,
                            onCheckedChange = {
                                hideLyricWhenLockScreen.value = it
                                config.hideLyricWhenLockScreen = it
                            }
                        )
                        SuperSwitch(
                            title = stringResource(R.string.long_click_status_bar_stop),
                            checked = longClickStatusBarStop.value,
                            onCheckedChange = {
                                longClickStatusBarStop.value = it
                                config.longClickStatusBarStop = it
                            }
                        )
                        SuperSwitch(
                            title = stringResource(R.string.click_status_bar_to_hide_lyric),
                            checked = clickStatusBarToHideLyric.value,
                            onCheckedChange = {
                                clickStatusBarToHideLyric.value = it
                                config.clickStatusBarToHideLyric = it
                            }
                        )
                        SuperSwitch(
                            title = stringResource(R.string.slide_status_bar_cut_songs),
                            checked = slideStatusBarCutSongs.value,
                            onCheckedChange = {
                                slideStatusBarCutSongs.value = it
                                config.slideStatusBarCutSongs = it
                            }
                        )
                        AnimatedVisibility(
                            visible = slideStatusBarCutSongs.value,
                        ) {
                            Column {
                                SuperArrow(
                                    title = stringResource(R.string.slide_status_bar_cut_songs_x_radius),
                                    onClick = {
                                        showCutSongsXRadiusDialog.value = true
                                    },
                                    holdDownState = showCutSongsXRadiusDialog.value
                                )
                                SuperArrow(
                                    title = stringResource(R.string.slide_status_bar_cut_songs_y_radius),
                                    onClick = {
                                        showCutSongsYRadiusDialog.value = true
                                    },
                                    holdDownState = showCutSongsYRadiusDialog.value
                                )
                            }
                        }
                        SuperSwitch(
                            title = stringResource(R.string.limit_visibility_change),
                            summary = stringResource(R.string.limit_visibility_change_tips),
                            checked = limitVisibilityChange.value,
                            onCheckedChange = {
                                limitVisibilityChange.value = it
                                config.limitVisibilityChange = it
                            }
                        )
                        SuperSwitch(
                            title = stringResource(R.string.timeout_restore),
                            checked = timeoutRestore.value,
                            onCheckedChange = {
                                timeoutRestore.value = it
                                config.timeoutRestore = it
                            }
                        )
                        SuperSwitch(
                            title = stringResource(R.string.dynamic_lyric_speed),
                            checked = dynamicLyricSpeed.value,
                            onCheckedChange = {
                                dynamicLyricSpeed.value = it
                                config.dynamicLyricSpeed = it
                            }
                        )
                    }
                    SmallTitle(
                        text = stringResource(R.string.module_fifth),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.title_switch),
                            checked = titleSwitch.value,
                            onCheckedChange = {
                                titleSwitch.value = it
                                config.titleSwitch = it
                            }
                        )
                        AnimatedVisibility(titleSwitch.value) {
                            Column {
                                SuperSwitch(
                                    title = stringResource(R.string.title_show_with_same_lyric),
                                    checked = titleShowWithSameLyric.value,
                                    onCheckedChange = {
                                        titleShowWithSameLyric.value = it
                                        config.titleShowWithSameLyric = it
                                    }
                                )
                                SuperArrow(
                                    title = stringResource(R.string.title_delay_duration),
                                    onClick = {
                                        showTitleDelayDialog.value = true
                                    },
                                    holdDownState = showTitleDelayDialog.value
                                )
                                SuperArrow(
                                    title = stringResource(R.string.title_color_and_transparency),
                                    onClick = {
                                        showTitleBgColorDialog.value = true
                                    },
                                    holdDownState = showTitleBgColorDialog.value
                                )
                                SuperArrow(
                                    title = stringResource(R.string.title_background_radius),
                                    onClick = {
                                        showTitleRadiusDialog.value = true
                                    },
                                    holdDownState = showTitleRadiusDialog.value
                                )
                                SuperArrow(
                                    title = stringResource(R.string.title_background_stroke_width),
                                    onClick = {
                                        showTitleStrokeWidthDialog.value = true
                                    },
                                    holdDownState = showTitleStrokeWidthDialog.value
                                )
                                SuperArrow(
                                    title = stringResource(R.string.title_background_stroke_color),
                                    onClick = {
                                        showTitleStrokeColorDialog.value = true
                                    },
                                    holdDownState = showTitleStrokeColorDialog.value
                                )
                                SuperDropdown(
                                    title = stringResource(R.string.title_gravity),
                                    items = titleGravityOptions,
                                    selectedIndex = titleGravitySelectedOption.intValue,
                                    onSelectedIndexChange = { newOption ->
                                        titleGravitySelectedOption.intValue = newOption
                                        config.titleGravity = newOption
                                    },
                                )
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(top = 6.dp, bottom = 12.dp)
                    ) {
                        BasicComponent(
                            title = stringResource(R.string.reset_system_ui),
                            titleColor = BasicComponentDefaults.titleColor(
                                color = Color.Red
                            ),
                            onClick = {
                                showDialog.value = true
                            }
                        )
                    }
                }
                Spacer(
                    Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
                                WindowInsets.captionBar.asPaddingValues().calculateBottomPadding()
                    )
                )
            }
        }
    }
    RestartDialog(showDialog)
    CutSongsXRadiusDialog(showCutSongsXRadiusDialog)
    CutSongsYRadiusDialog(showCutSongsYRadiusDialog)
    TitleDelayDialog(showTitleDelayDialog)
    TitleBgColorDialog(showTitleBgColorDialog)
    TitleRadiusDialog(showTitleRadiusDialog)
    TitleStrokeWidthDialog(showTitleStrokeWidthDialog)
    TitleStrokeColorDialog(showTitleStrokeColorDialog)
}

@Composable
fun CutSongsXRadiusDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.slideStatusBarCutSongsXRadius.toString()) }
    SuperDialog(
        title = stringResource(R.string.slide_status_bar_cut_songs_x_radius),
        summary = stringResource(R.string.slide_status_bar_cut_songs_x_radius_tips),
        show = showDialog,
        onDismissRequest = { dismissDialog(showDialog) },
    ) {
        TextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = value.value,
            maxLines = 1,
            onValueChange = { value.value = it }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = { dismissDialog(showDialog) }
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    if (value.value.toIntOrNull().isNotNull() && value.value.toInt() in 0..2000) {
                        config.slideStatusBarCutSongsXRadius = value.value.toInt()
                    } else {
                        config.slideStatusBarCutSongsXRadius = 50
                        value.value = "50"
                    }
                    dismissDialog(showDialog)
                }
            )
        }
    }
}

@Composable
fun CutSongsYRadiusDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.slideStatusBarCutSongsYRadius.toString()) }
    SuperDialog(
        title = stringResource(R.string.slide_status_bar_cut_songs_y_radius),
        summary = stringResource(R.string.slide_status_bar_cut_songs_y_radius_tips),
        show = showDialog,
        onDismissRequest = { dismissDialog(showDialog) },
    ) {
        TextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = value.value,
            maxLines = 1,
            onValueChange = { value.value = it }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = { dismissDialog(showDialog) }
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    if (value.value.toIntOrNull().isNotNull() && value.value.toInt() in 0..100) {
                        config.slideStatusBarCutSongsYRadius = value.value.toInt()
                    } else {
                        config.slideStatusBarCutSongsYRadius = 10
                        value.value = "10"
                    }
                    dismissDialog(showDialog)
                }
            )
        }
    }
}

@Composable
fun TitleDelayDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.titleDelayDuration.toString()) }
    SuperDialog(
        title = stringResource(R.string.title_delay_duration),
        summary = stringResource(R.string.title_delay_duration_tips),
        show = showDialog,
        onDismissRequest = { dismissDialog(showDialog) },
    ) {
        TextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = value.value,
            maxLines = 1,
            onValueChange = { value.value = it }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = { dismissDialog(showDialog) }
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    if (value.value.toIntOrNull().isNotNull() && value.value.toInt() in 0..10000) {
                        config.titleDelayDuration = value.value.toInt()
                    } else {
                        config.titleDelayDuration = 3000
                        value.value = "3000"
                    }
                    dismissDialog(showDialog)
                }
            )
        }
    }
}

@Composable
fun TitleBgColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.titleColorAndTransparency) }
    SuperDialog(
        title = stringResource(R.string.title_color_and_transparency),
        summary = stringResource(R.string.lyric_color_and_transparency_tips),
        show = showDialog,
        onDismissRequest = { dismissDialog(showDialog) },
    ) {
        TextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = value.value,
            maxLines = 1,
            onValueChange = { value.value = it }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = {
                    dismissDialog(showDialog)
                }
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    ActivityTools.colorCheck(value.value, unit = { config.titleColorAndTransparency = it }, "#000000")
                    dismissDialog(showDialog)
                }
            )
        }
    }
}

@Composable
fun TitleRadiusDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.titleBackgroundRadius.toString()) }
    SuperDialog(
        title = stringResource(R.string.title_background_radius),
        summary = stringResource(R.string.lyric_background_radius_tips),
        show = showDialog,
        onDismissRequest = { dismissDialog(showDialog) },
    ) {
        TextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = value.value,
            maxLines = 1,
            onValueChange = { value.value = it }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = { dismissDialog(showDialog) }
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    if (value.value.toIntOrNull().isNotNull() && value.value.toInt() in 0..100) {
                        config.titleBackgroundRadius = value.value.toInt()
                    } else {
                        config.titleBackgroundRadius = 50
                        value.value = "50"
                    }
                    dismissDialog(showDialog)
                }
            )
        }
    }
}

@Composable
fun TitleStrokeWidthDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.titleBackgroundStrokeWidth.toString()) }
    SuperDialog(
        title = stringResource(R.string.title_background_stroke_width),
        summary = stringResource(R.string.title_background_stroke_width_tips),
        show = showDialog,
        onDismissRequest = { dismissDialog(showDialog) },
    ) {
        TextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = value.value,
            maxLines = 1,
            onValueChange = { value.value = it }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = { dismissDialog(showDialog) }
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    if (value.value.toIntOrNull().isNotNull() && value.value.toInt() in 0..30) {
                        config.titleBackgroundStrokeWidth = value.value.toInt()
                    } else {
                        config.titleBackgroundStrokeWidth = 10
                        value.value = "10"
                    }
                    dismissDialog(showDialog)
                }
            )
        }
    }
}

@Composable
fun TitleStrokeColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.titleBackgroundStrokeColorAndTransparency) }
    SuperDialog(
        title = stringResource(R.string.title_background_stroke_color),
        summary = stringResource(R.string.lyric_color_and_transparency_tips),
        show = showDialog,
        onDismissRequest = { dismissDialog(showDialog) },
    ) {
        TextField(
            modifier = Modifier.padding(bottom = 16.dp),
            value = value.value,
            maxLines = 1,
            onValueChange = {
                value.value = it
            }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                onClick = { dismissDialog(showDialog) }
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    ActivityTools.colorCheck(value.value, unit = { config.titleBackgroundStrokeColorAndTransparency = it }, "#FFFFFF")
                    dismissDialog(showDialog)
                }
            )
        }
    }
}