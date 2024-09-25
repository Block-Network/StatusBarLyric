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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
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
    val titleSwitch = remember { mutableStateOf(config.titleSwitch) }
    val titleShowWithSameLyric = remember { mutableStateOf(config.titleShowWithSameLyric) }
    val dropdownOptions = listOf(
        stringResource(R.string.title_gravity_start),
        stringResource(R.string.title_gravity_center),
        stringResource(R.string.title_gravity_end)
    )
    val dropdownSelectedOption = remember { mutableIntStateOf(config.titleGravity) }
    val showDialog = remember { mutableStateOf(false) }
    val showTitleDelayDialog = remember { mutableStateOf(false) }
    val showTitleBgColorDialog = remember { mutableStateOf(false) }
    val showTitleRadiusDialog = remember { mutableStateOf(false) }
    val showTitleStrokeWidthDialog = remember { mutableStateOf(false) }
    val showTitleStrokeColorDialog = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
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
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
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
                                        items = dropdownOptions,
                                        selectedIndex = dropdownSelectedOption.intValue,
                                        onSelectedIndexChange = { newOption ->
                                            dropdownSelectedOption.intValue = newOption
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
                    RestartDialog(showDialog)
                    TitleDelayDialog(showTitleDelayDialog)
                    TitleBgColorDialog(showTitleBgColorDialog)
                    TitleRadiusDialog(showTitleRadiusDialog)
                    TitleStrokeWidthDialog(showTitleStrokeWidthDialog)
                    TitleStrokeColorDialog(showTitleStrokeColorDialog)
                }
            }
        }
    }
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
                            config.titleBackgroundStrokeColorAndTransparency = value.value.ifEmpty { "#FFFFFF" }
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                }
            }
        }
    )
}