package statusbar.lyric.ui.page

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
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
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun SystemSpecialPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val mMiuiHideNetworkSpeed = remember { mutableStateOf(config.mMiuiHideNetworkSpeed) }
    val mMiuiPadOptimize = remember { mutableStateOf(config.mMiuiPadOptimize) }
    val hideCarrier = remember { mutableStateOf(config.hideCarrier) }
    val mHyperOSTexture = remember { mutableStateOf(config.mHyperOSTexture) }
    val showDialog = remember { mutableStateOf(false) }
    val showRadioDialog = remember { mutableStateOf(false) }
    val showCornerDialog = remember { mutableStateOf(false) }
    val showBgColorDialog = remember { mutableStateOf(false) }

    Column {
        TopAppBar(
            title = stringResource(R.string.system_special_page),
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
        LazyColumn(
            modifier = Modifier
                .height(getWindowSize().height.dp)
                .background(MiuixTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)),
            enableOverScroll = true,
            topAppBarScrollBehavior = scrollBehavior
        ) {
            item {
                Column {
                    SmallTitle(
                        text = stringResource(R.string.miui_and_hyperos)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.miui_hide_network_speed),
                            checked = mMiuiHideNetworkSpeed.value,
                            onCheckedChange = {
                                mMiuiHideNetworkSpeed.value = it
                                config.mMiuiHideNetworkSpeed = it
                            }
                        )
                        SuperSwitch(
                            title = stringResource(R.string.miui_pad_optimize),

                            checked = mMiuiPadOptimize.value,
                            onCheckedChange = {
                                mMiuiPadOptimize.value = it
                                config.mMiuiPadOptimize = it
                            }
                        )
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                            SuperSwitch(
                                title = stringResource(R.string.hide_carrier),
                                checked = hideCarrier.value,
                                onCheckedChange = {
                                    hideCarrier.value = it
                                    config.hideCarrier = it
                                }
                            )
                        }
                    }
                    SmallTitle(
                        text = stringResource(R.string.hyperos)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 6.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.hyperos_texture),
                            checked = mHyperOSTexture.value,
                            onCheckedChange = {
                                mHyperOSTexture.value = it
                                config.mHyperOSTexture = it
                            }
                        )
                        AnimatedVisibility(mHyperOSTexture.value) {
                            Column {
                                SuperArrow(
                                    title = stringResource(R.string.hyperos_texture_radio),
                                    onClick = {
                                        showRadioDialog.value = true
                                    }
                                )
                                SuperArrow(
                                    title = stringResource(R.string.hyperos_texture_corner),
                                    onClick = {
                                        showCornerDialog.value = true
                                    }
                                )
                                SuperArrow(
                                    title = stringResource(R.string.hyperos_texture_color),
                                    onClick = {
                                        showBgColorDialog.value = true
                                    }
                                )
                            }
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
                        titleColor = Color.Red,
                        onClick = {
                            showDialog.value = true
                        }
                    )
                }
                Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
            }
        }
    }
    RestartDialog(showDialog)
    RadioDialog(showRadioDialog)
    CornerDialog(showCornerDialog)
    BgColorDialog(showBgColorDialog)
}

@Composable
fun RadioDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.mHyperOSTextureRadio.toString()) }
    if (!showDialog.value) return
    showDialog(
        content = {
            SuperDialog(
                title = stringResource(R.string.hyperos_texture_radio),
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            config.mHyperOSTextureRadio = if (value.value.isEmpty()) 25 else value.value.toInt()
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
fun CornerDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.mHyperOSTextureCorner.toString()) }
    if (!showDialog.value) return
    showDialog(
        content = {
            SuperDialog(
                title = stringResource(R.string.hyperos_texture_corner),
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                            config.mHyperOSTextureCorner = if (value.value.isEmpty()) 25 else value.value.toInt()
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
fun BgColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.mHyperOSTextureBgColor) }
    if (!showDialog.value) return
    showDialog(
        content = {
            SuperDialog(
                title = stringResource(R.string.hyperos_texture_color),
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                    config.mHyperOSTextureBgColor = it
                                }, "#15818181"
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