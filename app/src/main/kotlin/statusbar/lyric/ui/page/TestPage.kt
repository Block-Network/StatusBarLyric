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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import statusbar.lyric.MainActivity.Companion.testReceiver
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTestTools.dataList
import statusbar.lyric.tools.ActivityTestTools.getClass
import statusbar.lyric.tools.ActivityTools.showToastOnLooper
import statusbar.lyric.tools.Tools.goMainThread
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
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize
import top.yukonga.miuix.kmp.utils.overScrollVertical

@Composable
fun TestPage(
    navController: NavController,
    currentRoute: String
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val showDialog = remember { mutableStateOf(false) }
    val testMode = remember { mutableStateOf(config.testMode) }
    val relaxConditions = remember { mutableStateOf(config.relaxConditions) }

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
                title = stringResource(R.string.hook_page),
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
            overscrollEffect = null
        ) {
            item {
                Column(Modifier.padding(top = 6.dp)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        SuperSwitch(
                            title = stringResource(R.string.test_mode),
                            checked = testMode.value,
                            onCheckedChange = {
                                testMode.value = it
                                config.testMode = it
                            }
                        )
                        AnimatedVisibility(testMode.value) {
                            Column {
                                SuperSwitch(
                                    title = stringResource(R.string.relax_conditions),
                                    summary = stringResource(R.string.relax_conditions_tips),
                                    checked = relaxConditions.value,
                                    onCheckedChange = {
                                        relaxConditions.value = it
                                        config.relaxConditions = it
                                    }
                                )
                                SuperArrow(
                                    title = stringResource(R.string.get_hook),
                                    titleColor = BasicComponentDefaults.titleColor(
                                        color = MiuixTheme.colorScheme.primary
                                    ),
                                    rightText = stringResource(R.string.tips1),
                                    onClick = {
                                        dataList = arrayListOf()
                                        MainActivity.appContext.getClass()
                                        when (testReceiver && dataList.isNotEmpty()) {
                                            true -> if (currentRoute != "ChoosePage") {
                                                navController.navigate("ChoosePage") {
                                                    popUpTo("TestPage") {
                                                        inclusive = false
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }

                                            else -> {
                                                Thread {
                                                    Thread.sleep(500)
                                                    goMainThread {
                                                        if (testReceiver && dataList.isNotEmpty()) {
                                                            if (currentRoute != "ChoosePage") {
                                                                navController.navigate("ChoosePage") {
                                                                    popUpTo("TestPage") {
                                                                        inclusive = false
                                                                    }
                                                                    launchSingleTop = true
                                                                    restoreState = true
                                                                }
                                                            }
                                                        } else {
                                                            showToastOnLooper(MainActivity.appContext.getString(R.string.broadcast_receive_timeout))
                                                        }
                                                    }
                                                }.start()
                                            }
                                        }
                                    },
                                    holdDownState = currentRoute == "ChoosePage"
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
                            titleColor = BasicComponentColors(
                                color = Color.Red,
                                disabledColor = MiuixTheme.colorScheme.disabledOnSecondaryVariant
                            ),
                            onClick = {
                                showDialog.value = true
                            },
                            holdDownState = showDialog.value
                        )
                    }
                    SmallTitle(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = stringResource(R.string.test_mode_tips).split("\n")[1]
                    )
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
}