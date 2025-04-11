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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
import statusbar.lyric.tools.ActivityTools.changeConfig
import statusbar.lyric.tools.AnimTools
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
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
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.getWindowSize
import top.yukonga.miuix.kmp.utils.overScrollVertical

@Composable
fun IconPage(
    navController: NavController
) {
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
                title = stringResource(R.string.icon_page),
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
                        visible = !iconSwitch.value
                    ) {
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
                    AnimatedVisibility(
                        visible = iconSwitch.value,
                        enter = AnimTools().enterTransition(0),
                        exit = AnimTools().exitTransition(100)
                    ) {
                        Column {
                            SmallTitle(
                                text = stringResource(R.string.module_second),
                                modifier = Modifier.padding(top = 6.dp)
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
                                    },
                                    holdDownState = showIconSizeDialog.value
                                )
                                SuperArrow(
                                    title = stringResource(R.string.icon_color_and_transparency),
                                    onClick = {
                                        showIconColorDialog.value = true
                                    },
                                    holdDownState = showIconColorDialog.value
                                )
                                SuperArrow(
                                    title = stringResource(R.string.icon_background_color_and_transparency),
                                    onClick = {
                                        showIconBgColorDialog.value = true
                                    },
                                    holdDownState = showIconBgColorDialog.value
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = iconSwitch.value,
                        enter = AnimTools().enterTransition(20),
                        exit = AnimTools().exitTransition(80)
                    ) {
                        Column {
                            SmallTitle(
                                text = stringResource(R.string.module_fourth),
                                modifier = Modifier.padding(top = 6.dp)
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
                                    },
                                    holdDownState = showIconTopMarginsDialog.value
                                )
                                SuperArrow(
                                    title = stringResource(R.string.icon_bottom_margins),
                                    onClick = {
                                        showIconBottomMarginsDialog.value = true
                                    },
                                    holdDownState = showIconBottomMarginsDialog.value
                                )
                                SuperArrow(
                                    title = stringResource(R.string.icon_start_margins),
                                    onClick = {
                                        showIconStartMarginsDialog.value = true
                                    },
                                    holdDownState = showIconStartMarginsDialog.value
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = iconSwitch.value,
                        enter = AnimTools().enterTransition(40),
                        exit = AnimTools().exitTransition(60)
                    ) {
                        Column {
                            SmallTitle(
                                text = stringResource(R.string.module_third),
                                modifier = Modifier.padding(top = 6.dp)
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
                                    },
                                    holdDownState = showIconChangeAllIconsDialog.value
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = iconSwitch.value,
                        enter = AnimTools().enterTransition(60),
                        exit = AnimTools().exitTransition(40)
                    ) {
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
    SuperDialog(
        title = stringResource(R.string.icon_size),
        summary = stringResource(R.string.icon_size_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
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
                    config.iconSize = if (value.value.isEmpty()) 0 else value.value.toInt()
                    dismissDialog(showDialog)
                    changeConfig()
                }
            )
        }
    }
}

@Composable
fun IconColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconColor) }
    SuperDialog(
        title = stringResource(R.string.icon_color_and_transparency),
        summary = stringResource(R.string.icon_color_and_transparency_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
        },
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
                    ActivityTools.colorCheck(value.value, unit = { config.iconColor = it })
                    dismissDialog(showDialog)
                    changeConfig()
                }
            )
        }
    }
}

@Composable
fun IconBgColorDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconBgColor) }
    SuperDialog(
        title = stringResource(R.string.icon_background_color_and_transparency),
        summary = stringResource(R.string.icon_background_color_and_transparency_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
        },
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
                    ActivityTools.colorCheck(value.value, unit = { config.iconBgColor = it })
                    dismissDialog(showDialog)
                    changeConfig()
                }
            )
        }
    }
}

@Composable
fun IconTopMarginsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconTopMargins.toString()) }
    SuperDialog(
        title = stringResource(R.string.icon_top_margins),
        summary = stringResource(R.string.icon_top_margins_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
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
                    if (value.value.isEmpty()) value.value = "0"
                    config.iconTopMargins = value.value.toInt()
                    dismissDialog(showDialog)
                    changeConfig()
                }
            )
        }
    }
}

@Composable
fun IconBottomMarginsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconBottomMargins.toString()) }
    SuperDialog(
        title = stringResource(R.string.icon_bottom_margins),
        summary = stringResource(R.string.icon_bottom_margins_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
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
                    if (value.value.isEmpty()) value.value = "0"
                    config.iconBottomMargins = value.value.toInt()
                    dismissDialog(showDialog)
                    changeConfig()
                }
            )
        }
    }
}

@Composable
fun IconStartMarginsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.iconStartMargins.toString()) }
    SuperDialog(
        title = stringResource(R.string.icon_start_margins),
        summary = stringResource(R.string.icon_start_margins_tips),
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
                    if (value.value.toIntOrNull() != null && value.value.toInt() in -2000..2000) {
                        config.iconStartMargins = value.value.toInt()
                    } else {
                        config.iconStartMargins = 0
                        value.value = "0"
                    }
                    dismissDialog(showDialog)
                    changeConfig()
                }
            )
        }
    }
}

@Composable
fun IconChangeAllIconsDialog(showDialog: MutableState<Boolean>) {
    val value = remember { mutableStateOf(config.changeAllIcons) }
    SuperDialog(
        title = stringResource(R.string.change_all_icons),
        summary = stringResource(R.string.change_all_icons_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
        },
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
                    if (value.value.isEmpty()) value.value = ""
                    config.changeAllIcons = value.value
                    dismissDialog(showDialog)
                    changeConfig()
                }
            )
        }
    }
}
