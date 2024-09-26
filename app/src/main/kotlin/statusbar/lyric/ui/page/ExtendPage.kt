package statusbar.lyric.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun ExtendPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val hideTime = remember { mutableStateOf(config.hideTime) }
    val hideNotificationIcon = remember { mutableStateOf(config.hideNotificationIcon) }
    val hideLyricWhenLockScreen = remember { mutableStateOf(config.hideLyricWhenLockScreen) }
    val lyricColorSchemeOptions = listOf(
        stringResource(R.string.color_scheme1),
        stringResource(R.string.color_scheme2)
    )
    val lyricColorSchemeSelectedOption = remember { mutableIntStateOf(config.lyricColorScheme) }
    val longClickStatusBarStop = remember { mutableStateOf(config.longClickStatusBarStop) }
    val clickStatusBarToHideLyric = remember { mutableStateOf(config.clickStatusBarToHideLyric) }
    val slideStatusBarCutSongs = remember { mutableStateOf(config.slideStatusBarCutSongs) }
    val limitVisibilityChange = remember { mutableStateOf(config.limitVisibilityChange) }
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
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.extend_page),
                color = Color.Transparent,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 18.dp),
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = MiuixIcons.ArrowBack,
                            contentDescription = "Back",
                            tint = MiuixTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        }
    ) {
        Box {
            LazyColumn(
                modifier = Modifier
                    .height(getWindowSize().height.dp)
                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)),
                contentPadding = it,
                enableOverScroll = true,
                topAppBarScrollBehavior = scrollBehavior
            ) {
                item {
                    Column {
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
                                .padding(bottom = 6.dp)
                        ) {
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
                            SuperDropdown(
                                title = stringResource(R.string.lyric_color_scheme),
                                items = lyricColorSchemeOptions,
                                selectedIndex = lyricColorSchemeSelectedOption.intValue,
                                onSelectedIndexChange = { newOption ->
                                    lyricColorSchemeSelectedOption.intValue = newOption
                                    config.lyricColorScheme = newOption
                                },
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
                                        }
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.slide_status_bar_cut_songs_y_radius),
                                        onClick = {
                                            showCutSongsYRadiusDialog.value = true
                                        }
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
                                title = stringResource(R.string.dynamic_lyric_speed),
                                checked = dynamicLyricSpeed.value,
                                onCheckedChange = {
                                    dynamicLyricSpeed.value = it
                                    config.dynamicLyricSpeed = it
                                }
                            )
                        }
                        SmallTitle(
                            text = stringResource(R.string.module_fifth)
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
                                        }
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.title_color_and_transparency),
                                        onClick = {
                                            showTitleBgColorDialog.value = true
                                        }
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.title_background_radius),
                                        onClick = {
                                            showTitleRadiusDialog.value = true
                                        }
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.title_background_stroke_width),
                                        onClick = {
                                            showTitleStrokeWidthDialog.value = true
                                        }
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.title_background_stroke_color),
                                        onClick = {
                                            showTitleStrokeColorDialog.value = true
                                        }
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
                        // TODO
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            BasicComponent(
                                leftAction = {
                                    Text(
                                        text = stringResource(R.string.reset_system_ui),
                                        color = Color.Red
                                    )
                                },
                                onClick = {
                                    showDialog.value = true
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
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
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.slide_status_bar_cut_songs_x_radius),
                summary = stringResource(R.string.slide_status_bar_cut_songs_x_radius_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..2000)) {
                            value.value = it
                        }
                    }
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.cancel),
                        onClick = {
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                    Spacer(Modifier.width(20.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.ok),
                        submit = true,
                        onClick = {
                            config.slideStatusBarCutSongsXRadius = if (value.value.isEmpty() || value.value.toInt() < 50) 50 else value.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun CutSongsYRadiusDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.slideStatusBarCutSongsYRadius.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.slide_status_bar_cut_songs_y_radius),
                summary = stringResource(R.string.slide_status_bar_cut_songs_y_radius_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..100)) {
                            value.value = it
                        }
                    }
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.cancel),
                        onClick = {
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                    Spacer(Modifier.width(20.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.ok),
                        submit = true,
                        onClick = {
                            config.slideStatusBarCutSongsYRadius = if (value.value.isEmpty() || value.value.toInt() < 10) 10 else value.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun TitleDelayDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.titleDelayDuration.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.title_delay_duration),
                summary = stringResource(R.string.title_delay_duration_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..10000)) {
                            value.value = it
                        }
                    }
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.cancel),
                        onClick = {
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                    Spacer(Modifier.width(20.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.ok),
                        submit = true,
                        onClick = {
                            config.titleDelayDuration = if (value.value.isEmpty()) 3000 else value.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun TitleBgColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.titleColorAndTransparency) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.title_color_and_transparency),
                summary = stringResource(R.string.lyric_color_and_transparency_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        if (it.isEmpty()) {
                            value.value = it
                        }
                    }
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.cancel),
                        onClick = {
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                    Spacer(Modifier.width(20.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.ok),
                        submit = true,
                        onClick = {
                            ActivityTools.colorCheck(
                                value.value,
                                unit = {
                                    config.lyricColor = it
                                }, "#000000"
                            )
                            config.titleColorAndTransparency = value.value.ifEmpty { "#000000" }
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun TitleRadiusDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.titleBackgroundRadius.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.title_background_radius),
                summary = stringResource(R.string.lyric_background_radius_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..100)) {
                            value.value = it
                        }
                    }
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.cancel),
                        onClick = {
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                    Spacer(Modifier.width(20.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.ok),
                        submit = true,
                        onClick = {
                            config.titleBackgroundRadius = if (value.value.isEmpty()) 50 else value.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun TitleStrokeWidthDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.titleBackgroundStrokeWidth.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.title_background_stroke_width),
                summary = stringResource(R.string.title_background_stroke_width_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..30)) {
                            value.value = it
                        }
                    }
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.cancel),
                        onClick = {
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                    Spacer(Modifier.width(20.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.ok),
                        submit = true,
                        onClick = {
                            config.titleBackgroundStrokeWidth = if (value.value.isEmpty()) 10 else value.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun TitleStrokeColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.titleBackgroundStrokeColorAndTransparency) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.title_background_stroke_color),
                summary = stringResource(R.string.lyric_color_and_transparency_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        if (it.isEmpty()) {
                            value.value = it
                        }
                    }
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.cancel),
                        onClick = {
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                    Spacer(Modifier.width(20.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.ok),
                        submit = true,
                        onClick = {
                            ActivityTools.colorCheck(
                                value.value,
                                unit = {
                                    config.titleBackgroundStrokeColorAndTransparency = it
                                }, "#FFFFFF"
                            )
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                }
            }
        }
    )
}