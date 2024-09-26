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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools.changeConfig
import statusbar.lyric.tools.AnimTools
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
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun IconPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val iconSwitch = remember { mutableStateOf(config.iconSwitch) }
    val forceTheIconToBeDisplayed = remember { mutableStateOf(config.forceTheIconToBeDisplayed) }
    val showDialog = remember { mutableStateOf(false) }
    val showIconSizeDialog = remember { mutableStateOf(false) }
    val showIconColorDialog = remember { mutableStateOf(false) }
    val showIconBgColorDialog = remember { mutableStateOf(false) }
    val showIconTopMarginsDialog = remember { mutableStateOf(false) }
    val showIconBottomMarginsDialog = remember { mutableStateOf(false) }
    val showIconStartMarginsDialog = remember { mutableStateOf(false) }
    val showIconChangeAllIconsDialog = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.icon_page),
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
                                title = stringResource(R.string.icon_switch),
                                checked = iconSwitch.value,
                                onCheckedChange = {
                                    iconSwitch.value = it
                                    config.iconSwitch = it
                                    changeConfig()
                                }
                            )
                        }
                        AnimatedVisibility(
                            visible = iconSwitch.value,
                            enter = AnimTools().enterTransition(0),
                            exit = AnimTools().exitTransition(40)
                        ) {
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
                                        title = stringResource(R.string.icon_size),
                                        onClick = {
                                            showIconSizeDialog.value = true
                                        }
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.icon_color_and_transparency),
                                        onClick = {
                                            showIconColorDialog.value = true
                                        }
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.icon_background_color_and_transparency),
                                        onClick = {
                                            showIconBgColorDialog.value = true
                                        }
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = iconSwitch.value,
                            enter = AnimTools().enterTransition(20),
                            exit = AnimTools().exitTransition(20)
                        ) {
                            Column {
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
                                        title = stringResource(R.string.icon_top_margins),
                                        onClick = {
                                            showIconTopMarginsDialog.value = true
                                        }
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.icon_bottom_margins),
                                        onClick = {
                                            showIconBottomMarginsDialog.value = true
                                        }
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.icon_start_margins),
                                        titleColor = MiuixTheme.colorScheme.primary,
                                        rightText = stringResource(R.string.tips1),
                                        onClick = {
                                            showIconStartMarginsDialog.value = true
                                        }
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = iconSwitch.value,
                            enter = AnimTools().enterTransition(40),
                            exit = AnimTools().exitTransition(0)
                        ) {
                            Column {
                                SmallTitle(
                                    text = stringResource(R.string.module_third)
                                )
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                        .padding(bottom = 6.dp)
                                ) {
                                    SuperSwitch(
                                        title = stringResource(R.string.force_the_icon_to_be_displayed),
                                        checked = forceTheIconToBeDisplayed.value,
                                        onCheckedChange = {
                                            forceTheIconToBeDisplayed.value = it
                                            config.forceTheIconToBeDisplayed = it
                                            changeConfig()
                                        }
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.change_all_icons),
                                        onClick = {
                                            showIconChangeAllIconsDialog.value = true
                                        }
                                    )
                                }
                            }
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
    IconSizeDialog(showIconSizeDialog)
    IconColorDialog(showIconColorDialog)
    IconBgColorDialog(showIconBgColorDialog)
    IconTopMarginsDialog(showIconTopMarginsDialog)
    IconBottomMarginsDialog(showIconBottomMarginsDialog)
    IconStartMarginsDialog(showIconStartMarginsDialog)
    IconChangeAllIconsDialog(showIconChangeAllIconsDialog)
}

@Composable
fun IconSizeDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconSize.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.icon_size),
                summary = stringResource(R.string.icon_size_tips),
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
                            config.iconSize = if (value.value.isEmpty()) 0 else value.value.toInt()
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
fun IconColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconColor) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.icon_color_and_transparency),
                summary = stringResource(R.string.icon_color_and_transparency_tips),
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
                            config.iconColor = value.value.ifEmpty { "" }
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
fun IconBgColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconBgColor) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.icon_background_color_and_transparency),
                summary = stringResource(R.string.icon_background_color_and_transparency_tips),
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
                            config.iconBgColor = value.value.ifEmpty { "" }
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
fun IconTopMarginsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconTopMargins.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.icon_top_margins),
                summary = stringResource(R.string.icon_top_margins_tips),
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
                            config.iconBottomMargins = if (value.value.isNotEmpty()) value.value.toInt() else return@Button
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun IconBottomMarginsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconBottomMargins.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.icon_bottom_margins),
                summary = stringResource(R.string.icon_bottom_margins_tips),
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
                            config.iconBottomMargins = if (value.value.isNotEmpty()) value.value.toInt() else return@Button
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun IconStartMarginsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconStartMargins.toString()) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.icon_start_margins),
                summary = stringResource(R.string.icon_start_margins_tips),
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
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in -500..500)) {
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
                            config.iconStartMargins = if (value.value.isNotEmpty()) value.value.toInt() else return@Button
                            changeConfig()
                        }
                    )
                }
            }
        }
    )
}


@Composable
fun IconChangeAllIconsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.changeAllIcons) }
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.change_all_icons),
                summary = stringResource(R.string.change_all_icons_tips),
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
                            config.changeAllIcons = value.value.ifEmpty { "" }
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
