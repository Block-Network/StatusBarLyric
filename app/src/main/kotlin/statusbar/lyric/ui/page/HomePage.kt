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

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import statusbar.lyric.MainActivity
import statusbar.lyric.MainActivity.Companion.isLoad
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.AnimTools
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Settings
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize
import top.yukonga.miuix.kmp.utils.overScrollVertical

@Composable
fun HomePage(
    navController: NavController,
    currentRoute: String
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val masterSwitchState = remember { mutableStateOf(if (isLoad) config.masterSwitch else false) }

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

    LaunchedEffect(Unit) {
        if (!isLoad) {
            masterSwitchState.value = false
            config.masterSwitch = false
        }
    }

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
                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Left))
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Left))
                    .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                    .windowInsetsPadding(WindowInsets.captionBar.only(WindowInsetsSides.Top)),
                title = stringResource(R.string.app_name),
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        modifier = Modifier.padding(end = 20.dp),
                        onClick = {
                            if (currentRoute != "MenuPage") {
                                navController.navigate("MenuPage") {
                                    popUpTo("HomePage") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        holdDownState = currentRoute == "MenuPage"
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.Settings,
                            contentDescription = "Menu",
                            tint = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.8f)
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
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Left))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Left)),
            contentPadding = it,
            overscrollEffect = null
        ) {
            item {
                Column(Modifier.padding(top = 6.dp)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Image(
                            modifier = Modifier.fillMaxWidth(),
                            painter = painterResource(id = R.drawable.ic_home_background),
                            contentDescription = "Logo",
                        )
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.master_switch),
                            checked = masterSwitchState.value,
                            onCheckedChange = {
                                if (isLoad) {
                                    masterSwitchState.value = it
                                    config.masterSwitch = it
                                } else {
                                    Toast.makeText(MainActivity.appContext, R.string.module_inactivated, Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    AnimatedVisibility(
                        visible = !masterSwitchState.value
                    ) {
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                ShowSuperLyric()
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                BasicComponent(
                                    title = stringResource(R.string.restart_app),
                                    titleColor = BasicComponentDefaults.titleColor(
                                        color = Color.Red
                                    ),
                                    onClick = {
                                        Thread {
                                            Thread.sleep(500)
                                            ActivityTools.restartApp()
                                        }.start()
                                    }
                                )
                            }
                        }
                    }
                    Column {
                        AnimatedVisibility(
                            visible = masterSwitchState.value,
                            enter = AnimTools().enterTransition(0),
                            exit = AnimTools().exitTransition(100)
                        ) {
                            Column {
                                SmallTitle(
                                    text = stringResource(R.string.module_first),
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                        .padding(bottom = 6.dp)
                                ) {
                                    SuperArrow(
                                        title = stringResource(R.string.hook_page),
                                        summary = if (config.textViewId == 0) {
                                            stringResource(R.string.test_mode_tips).split("\n")[0]
                                        } else null,
                                        onClick = {
                                            if (currentRoute != "TestPage") {
                                                navController.navigate("TestPage") {
                                                    popUpTo("HomePage") {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        holdDownState = currentRoute == "TestPage"
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = masterSwitchState.value,
                            enter = AnimTools().enterTransition(40),
                            exit = AnimTools().exitTransition(60)
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
                                        title = stringResource(R.string.lyric_page),
                                        onClick = {
                                            if (currentRoute != "LyricPage") {
                                                navController.navigate("LyricPage") {
                                                    popUpTo("HomePage") {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        holdDownState = currentRoute == "LyricPage"
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.icon_page),
                                        onClick = {
                                            if (currentRoute != "IconPage") {
                                                navController.navigate("IconPage") {
                                                    popUpTo("HomePage") {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        holdDownState = currentRoute == "IconPage"
                                    )
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = masterSwitchState.value,
                            enter = AnimTools().enterTransition(80),
                            exit = AnimTools().exitTransition(20)
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
                                        .padding(bottom = 12.dp)
                                ) {
                                    SuperArrow(
                                        title = stringResource(R.string.extend_page),
                                        onClick = {
                                            if (currentRoute != "ExtendPage") {
                                                navController.navigate("ExtendPage") {
                                                    popUpTo("HomePage") {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        holdDownState = currentRoute == "ExtendPage"
                                    )

                                    SuperArrow(
                                        title = stringResource(R.string.system_special_page),
                                        onClick = {
                                            if (currentRoute != "SystemSpecialPage") {
                                                navController.navigate("SystemSpecialPage") {
                                                    popUpTo("HomePage") {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        holdDownState = currentRoute == "SystemSpecialPage"
                                    )
                                }
                            }
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
}

@Composable
fun ShowSuperLyric() {
    val openSuperLyricUrl = { ActivityTools.openUrl("https://github.com/HChenX/SuperLyric/releases") }
    SuperArrow(
        title = stringResource(R.string.super_lyric_tip),
        titleColor = BasicComponentColors(
            color = Color.Red,
            disabledColor = MiuixTheme.colorScheme.disabledOnSecondaryVariant
        ),
        summary = stringResource(R.string.click_to_install),
        onClick = {
            openSuperLyricUrl()
        }
    )
}
