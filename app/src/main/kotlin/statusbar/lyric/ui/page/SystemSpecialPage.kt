package statusbar.lyric.ui.page

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
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
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextButton
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
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun SystemSpecialPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val mMiuiHideNetworkSpeed = remember { mutableStateOf(config.mMiuiHideNetworkSpeed) }
    val mMiuiPadOptimize = remember { mutableStateOf(config.mMiuiPadOptimize) }
    val hideCarrier = remember { mutableStateOf(config.hideCarrier) }
    val mHyperOSTexture = remember { mutableStateOf(config.mHyperOSTexture) }
    val mHideFocusedNotice = remember { mutableStateOf(config.hideFocusedNotice) }
    val showDialog = remember { mutableStateOf(false) }
    val showRadioDialog = remember { mutableStateOf(false) }
    val showCornerDialog = remember { mutableStateOf(false) }
    val showBgColorDialog = remember { mutableStateOf(false) }

    val hazeState = remember { HazeState() }
    val hazeStyle = HazeStyle(
        backgroundColor = MiuixTheme.colorScheme.background,
        tint = HazeTint(MiuixTheme.colorScheme.background.copy(0.67f))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                color = Color.Transparent,
                modifier = Modifier
                    .hazeChild(hazeState) {
                        style = hazeStyle
                        blurRadius = 25.dp
                        noiseFactor = 0f
                    }
                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Right))
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Right)),
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
                            modifier = Modifier.size(40.dp),
                            imageVector = MiuixIcons.ArrowBack,
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
                .haze(state = hazeState)
                .height(getWindowSize().height.dp)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Right))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Right)),
            topAppBarScrollBehavior = scrollBehavior,
            contentPadding = it,
        ) {
            item {
                Column(Modifier.padding(top = 16.dp)) {
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
                        text = stringResource(R.string.hyperos),
                        modifier = Modifier.padding(top = 6.dp)
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
                        SuperSwitch(
                            title = stringResource(R.string.hide_focused_notice),
                            checked = mHideFocusedNotice.value,
                            onCheckedChange = {
                                mHideFocusedNotice.value = it
                                config.hideFocusedNotice = it
                            }
                        )
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
                Spacer(
                    Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                )
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
    SuperDialog(
        title = stringResource(R.string.hyperos_texture_radio),
        summary = stringResource(R.string.lyric_stroke_width_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
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
                    config.mHyperOSTextureRadio =
                        if (value.value.isEmpty()) 25 else value.value.toInt()
                    dismissDialog(showDialog)
                }
            )
        }
    }
}

@Composable
fun CornerDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.mHyperOSTextureCorner.toString()) }
    SuperDialog(
        title = stringResource(R.string.hyperos_texture_corner),
        summary = stringResource(R.string.lyric_letter_spacing_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
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
                    config.mHyperOSTextureCorner =
                        if (value.value.isEmpty()) 25 else value.value.toInt()
                    dismissDialog(showDialog)
                }
            )
        }
    }
}

@Composable
fun BgColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.mHyperOSTextureBgColor) }
    SuperDialog(
        title = stringResource(R.string.hyperos_texture_color),
        summary = stringResource(R.string.lyric_color_and_transparency_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
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
                    ActivityTools.colorCheck(
                        value.value,
                        unit = { config.mHyperOSTextureBgColor = it },
                        "#15818181"
                    )
                    dismissDialog(showDialog)
                }
            )
        }
    }
}