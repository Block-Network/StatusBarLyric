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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.ActivityTools.changeConfig
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
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
fun LyricPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val lyricWidth = remember { mutableStateOf(config.lyricWidth.toString()) }
    val fixedLyricWidth = remember { mutableStateOf(config.fixedLyricWidth) }
    val lyricAnimOptions = listOf(
        stringResource(R.string.lyrics_animation_none),
        stringResource(R.string.lyrics_animation_top),
        stringResource(R.string.lyrics_animation_bottom),
        stringResource(R.string.lyrics_animation_start),
        stringResource(R.string.lyrics_animation_end),
        stringResource(R.string.lyrics_animation_fade),
        stringResource(R.string.lyrics_animation_scale_x_y),
        stringResource(R.string.lyrics_animation_scale_x),
        stringResource(R.string.lyrics_animation_scale_y),
        stringResource(R.string.lyrics_animation_random),
    )
    val lyricAnimSelectedOption = remember { mutableIntStateOf(config.lyricAnimation) }
    val lyricInterpolatorOptions = listOf(
        stringResource(R.string.lyrics_interpolator_linear),
        stringResource(R.string.lyrics_interpolator_accelerate),
        stringResource(R.string.lyrics_interpolator_decelerate),
        stringResource(R.string.lyrics_interpolator_accelerate_decelerate),
        stringResource(R.string.lyrics_interpolator_overshoot),
        stringResource(R.string.lyrics_interpolator_bounce),
    )
    val lyricInterpolatorSelectedOption = remember { mutableIntStateOf(config.lyricInterpolator) }
    val showDialog = remember { mutableStateOf(false) }
    val showLyricWidthDialog = remember { mutableStateOf(false) }
    val showLyricSizeDialog = remember { mutableStateOf(false) }
    val showLyricColorDialog = remember { mutableStateOf(false) }
    val showLyricGradientDialog = remember { mutableStateOf(false) }
    val showLyricGradientBgColorDialog = remember { mutableStateOf(false) }
    val showLyricBgRadiusDialog = remember { mutableStateOf(false) }
    val showLyricLetterSpacingDialog = remember { mutableStateOf(false) }
    val showLyricStrokeWidthDialog = remember { mutableStateOf(false) }
    val showLyricSpeedDialog = remember { mutableStateOf(false) }
    val showLyricTopMarginsDialog = remember { mutableStateOf(false) }
    val showLyricBottomMarginsDialog = remember { mutableStateOf(false) }
    val showLyricStartMarginsDialog = remember { mutableStateOf(false) }
    val showLyricEndMarginsDialog = remember { mutableStateOf(false) }
    val showLyricAnimDurationDialog = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.lyric_page),
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
        LyricAnimDurationDialog(showLyricAnimDurationDialog)
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
                        SmallTitle(
                            text = stringResource(R.string.module_second)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 6.dp)
                        ) {
                            SuperArrow(
                                title = stringResource(R.string.lyric_width),
                                onClick = {
                                    showLyricWidthDialog.value = true
                                }
                            )
                            AnimatedVisibility(
                                visible = lyricWidth.value != "0",
                            ) {
                                SuperSwitch(
                                    title = stringResource(R.string.fixed_lyric_width),
                                    summary = stringResource(R.string.fixed_lyric_width_tips),
                                    checked = fixedLyricWidth.value,
                                    onCheckedChange = {
                                        fixedLyricWidth.value = it
                                        config.fixedLyricWidth = it
                                        changeConfig()
                                    }
                                )
                            }
                            SuperArrow(
                                title = stringResource(R.string.lyric_size),
                                onClick = {
                                    showLyricSizeDialog.value = true
                                }
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyric_color_and_transparency),
                                onClick = {
                                    showLyricColorDialog.value = true
                                }
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyrics_are_gradient_and_transparent),
                                titleColor = MiuixTheme.colorScheme.primary,
                                rightText = stringResource(R.string.tips1),
                                onClick = {
                                    showLyricGradientDialog.value = true
                                }
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyrics_gradient_background_color_and_transparency),
                                onClick = {
                                    showLyricGradientBgColorDialog.value = true
                                }
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyric_background_radius),
                                onClick = {
                                    showLyricBgRadiusDialog.value = true
                                }
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyric_letter_spacing),
                                onClick = {
                                    showLyricLetterSpacingDialog.value = true
                                }
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyric_stroke_width),
                                onClick = {
                                    showLyricStrokeWidthDialog.value = true
                                }
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyric_speed),
                                onClick = {
                                    showLyricSpeedDialog.value = true
                                }
                            )
                        }
                        SmallTitle(
                            text = stringResource(R.string.module_fourth)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 6.dp)
                        ) {
                            SuperArrow(
                                title = stringResource(R.string.lyric_top_margins),
                                onClick = {
                                    showLyricTopMarginsDialog.value = true
                                }
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyric_bottom_margins),
                                onClick = {
                                    showLyricBottomMarginsDialog.value = true
                                }
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyric_start_margins),
                                titleColor = MiuixTheme.colorScheme.primary,
                                rightText = stringResource(R.string.tips1),
                                onClick = {
                                    showLyricStartMarginsDialog.value = true
                                }
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyric_end_margins),
                                titleColor = MiuixTheme.colorScheme.primary,
                                rightText = stringResource(R.string.tips1),
                                onClick = {
                                    showLyricEndMarginsDialog.value = true
                                }
                            )
                        }
                        SmallTitle(
                            text = stringResource(R.string.module_sixth)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 6.dp)
                        ) {
                            SuperDropdown(
                                title = stringResource(R.string.lyrics_animation),
                                items = lyricAnimOptions,
                                selectedIndex = lyricAnimSelectedOption.intValue,
                                onSelectedIndexChange = { newOption ->
                                    lyricAnimSelectedOption.intValue = newOption
                                    config.lyricAnimation = newOption
                                    changeConfig()
                                },
                            )
                            SuperDropdown(
                                title = stringResource(R.string.lyrics_animation_interpolator),
                                items = lyricInterpolatorOptions,
                                selectedIndex = lyricInterpolatorSelectedOption.intValue,
                                onSelectedIndexChange = { newOption ->
                                    lyricInterpolatorSelectedOption.intValue = newOption
                                    config.lyricInterpolator = newOption
                                    changeConfig()
                                },
                            )
                            SuperArrow(
                                title = stringResource(R.string.lyrics_animation_duration),
                                onClick = {
                                    showLyricAnimDurationDialog.value = true
                                }
                            )
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            BasicComponent(
                                title = stringResource(R.string.reset_system_ui),
                                titleColor = Color.Red,
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
    LyricWidthDialog(showLyricWidthDialog, lyricWidth)
    LyricSizeDialog(showLyricSizeDialog)
    LyricColorDialog(showLyricColorDialog)
    LyricGradientDialog(showLyricGradientDialog)
    LyricGradientBgColorDialog(showLyricGradientBgColorDialog)
    LyricBgRadiusDialog(showLyricBgRadiusDialog)
    LyricLetterSpacingDialog(showLyricLetterSpacingDialog)
    LyricStrokeWidthDialog(showLyricStrokeWidthDialog)
    LyricSpeedDialog(showLyricSpeedDialog)
    LyricTopMarginsDialog(showLyricTopMarginsDialog)
    LyricBottomMarginsDialog(showLyricBottomMarginsDialog)
    LyricStartMarginsDialog(showLyricStartMarginsDialog)
    LyricEndMarginsDialog(showLyricEndMarginsDialog)
}

@Composable
fun LyricWidthDialog(showDialog: MutableState<Boolean>, lyricWidth: MutableState<String>) {
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_width),
                summary = stringResource(R.string.lyric_width_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = lyricWidth.value,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..100)) {
                            lyricWidth.value = it
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
                            config.lyricWidth = if (lyricWidth.value.isEmpty()) 0 else lyricWidth.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricSizeDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricSize.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_size),
                summary = stringResource(R.string.lyric_size_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            config.lyricSize = if (value.value.isEmpty()) 0 else value.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricColor) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_color_and_transparency),
                summary = stringResource(R.string.lyric_color_and_transparency_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    label = "#FFFFFF",
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        value.value = it
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
                                })
                            dismissDialog()
                            showDialog.value = false
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricGradientDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricGradientColor) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyrics_are_gradient_and_transparent),
                summary = stringResource(R.string.lyrics_are_gradient_and_transparent_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    label = "#ff0099,#d508a8,#aa10b8",
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        value.value = it
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
                                    config.lyricGradientColor = it
                                })
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
fun LyricGradientBgColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricBackgroundColor) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyrics_gradient_background_color_and_transparency),
                summary = stringResource(R.string.lyrics_gradient_background_color_and_transparency_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
                TextField(
                    label = "#00000000",
                    modifier = Modifier.padding(bottom = 16.dp),
                    value = value.value,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    backgroundColor = MiuixTheme.colorScheme.secondary,
                    onValueChange = {
                        value.value = it
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
                                    config.lyricBackgroundColor = it
                                })
                            dismissDialog()
                            showDialog.value = false
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricBgRadiusDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricBackgroundRadius.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_background_radius),
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
                            config.lyricBackgroundRadius = if (value.value.isEmpty()) 0 else value.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricLetterSpacingDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricLetterSpacing.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_letter_spacing),
                summary = stringResource(R.string.lyric_letter_spacing_tips),
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
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..50)) {
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
                            config.lyricLetterSpacing = if (value.value.isEmpty()) 0 else value.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricStrokeWidthDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricStrokeWidth.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_stroke_width),
                summary = stringResource(R.string.lyric_stroke_width_tips),
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
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..400)) {
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
                            config.lyricStrokeWidth = value.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricSpeedDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricSpeed.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_speed),
                summary = stringResource(R.string.lyric_speed_tips),
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
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..20)) {
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
                            config.lyricSpeed = if (value.value.isEmpty()) 1 else value.value.toInt()
                            dismissDialog()
                            showDialog.value = false
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricTopMarginsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricTopMargins.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_top_margins),
                summary = stringResource(R.string.lyric_top_margins_tips),
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
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in -100..100)) {
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
                            dismissDialog()
                            showDialog.value = false
                            config.lyricTopMargins = if (value.value.isNotEmpty()) value.value.toInt() else return@Button
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricBottomMarginsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricBottomMargins.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_bottom_margins),
                summary = stringResource(R.string.lyric_bottom_margins_tips),
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
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in -100..100)) {
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
                            dismissDialog()
                            showDialog.value = false
                            config.lyricBottomMargins = if (value.value.isNotEmpty()) value.value.toInt() else return@Button
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricStartMarginsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricStartMargins.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_start_margins),
                summary = stringResource(R.string.lyric_start_margins_tips),
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
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in -2000..2000)) {
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
                            dismissDialog()
                            showDialog.value = false
                            config.lyricStartMargins = if (value.value.isNotEmpty()) value.value.toInt() else return@Button
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricEndMarginsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.lyricEndMargins.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyric_end_margins),
                summary = stringResource(R.string.lyric_end_margins_tips),
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
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in -2000..2000)) {
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
                            dismissDialog()
                            showDialog.value = false
                            config.lyricEndMargins = if (value.value.isNotEmpty()) value.value.toInt() else return@Button
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun LyricAnimDurationDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.animationDuration.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.lyrics_animation_duration),
                summary = stringResource(R.string.lyric_animation_duration_tips),
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
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..1000)) {
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
                            dismissDialog()
                            showDialog.value = false
                            config.animationDuration = if (value.value.isEmpty() || value.value.toInt() < 300) 300 else value.value.toInt()
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}