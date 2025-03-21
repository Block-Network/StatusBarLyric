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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import statusbar.lyric.BuildConfig
import statusbar.lyric.MainActivity.Companion.context
import statusbar.lyric.MainActivity.Companion.isLoad
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.AnimTools
import statusbar.lyric.tools.Tools.isNot
import statusbar.lyric.tools.Tools.isNotNull
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LazyColumn
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

@Composable
fun HomePage(
    navController: NavController,
    currentRoute: MutableState<String>
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val masterSwitchState = remember { mutableStateOf(if (isLoad) config.masterSwitch else false) }
    val lyricGetterApi = checkLyricGetterApi()

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
        if (checkLyricGetterApi() != 0 || !isLoad) {
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
                    },
                title = stringResource(R.string.app_name),
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        modifier = Modifier.padding(end = 20.dp),
                        onClick = {
                            if (currentRoute.value != "MenuPage") {
                                navController.navigate("MenuPage") {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        holdDownState = currentRoute.value == "MenuPage"
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.Settings,
                            contentDescription = "Menu",
                            tint = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                },
                horizontalPadding = 26.dp,
            )
        },
        popupHost = { null }
    ) {
        LazyColumn(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .height(getWindowSize().height.dp)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Left))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Left)),
            topAppBarScrollBehavior = scrollBehavior,
            contentPadding = it,
        ) {
            item {
                Column(Modifier.padding(top = 18.dp)) {
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
                    if (lyricGetterApi != 0) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            ShowLyricGetter(lyricGetterApi)
                        }
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
                                if (lyricGetterApi == 1) {
                                    Toast.makeText(
                                        context,
                                        R.string.no_supported_version_lyric_getter,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (lyricGetterApi == 2) {
                                    Toast.makeText(
                                        context,
                                        R.string.no_lyric_getter,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (isLoad) {
                                    masterSwitchState.value = it
                                    config.masterSwitch = it
                                } else {
                                    Toast.makeText(
                                        context,
                                        R.string.module_inactivated,
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                                            if (currentRoute.value != "TestPage") {
                                                navController.navigate("TestPage") {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        holdDownState = currentRoute.value == "TestPage"
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
                                            if (currentRoute.value != "LyricPage") {
                                                navController.navigate("LyricPage") {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        holdDownState = currentRoute.value == "LyricPage"
                                    )
                                    SuperArrow(
                                        title = stringResource(R.string.icon_page),
                                        onClick = {
                                            if (currentRoute.value != "IconPage") {
                                                navController.navigate("IconPage") {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        holdDownState = currentRoute.value == "IconPage"
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
                                            if (currentRoute.value != "ExtendPage") {
                                                navController.navigate("ExtendPage") {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        holdDownState = currentRoute.value == "ExtendPage"
                                    )

                                    SuperArrow(
                                        title = stringResource(R.string.system_special_page),
                                        onClick = {
                                            if (currentRoute.value != "SystemSpecialPage") {
                                                navController.navigate("SystemSpecialPage") {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        holdDownState = currentRoute.value == "SystemSpecialPage"
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
fun ShowLyricGetter(int: Int) {
    val openLyricGetterUrl = { ActivityTools.openUrl("https://github.com/xiaowine/Lyric-Getter/") }
    when (int) {
        0 -> return
        1 -> {
            SuperArrow(
                leftAction = {
                    Image(
                        modifier = Modifier.padding(end = 12.dp),
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = "Warning"
                    )
                },
                title = stringResource(R.string.no_supported_version_lyric_getter),
                titleColor = BasicComponentDefaults.titleColor(
                    color = Color.Red
                ),
                summary = stringResource(R.string.click_to_install),
                onClick = {
                    openLyricGetterUrl()
                }
            )
        }

        2 -> {
            SuperArrow(
                leftAction = {
                    Image(
                        modifier = Modifier.padding(end = 12.dp),
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = "Warning"
                    )
                },
                title = stringResource(R.string.no_lyric_getter),
                titleColor = BasicComponentColors(
                    color = Color.Red,
                    disabledColor = MiuixTheme.colorScheme.disabledOnSecondaryVariant
                ),
                summary = stringResource(R.string.click_to_install),
                onClick = {
                    openLyricGetterUrl()
                }
            )
        }
    }
}

private fun checkLyricGetterApi(): Int {
    ActivityTools.checkInstalled("cn.lyric.getter").isNotNull {
        val getterVersion = it.metaData.getInt("Getter_Version")
        if (getterVersion != BuildConfig.API_VERSION) {
            return 1
        }
    }.isNot {
        return 2
    }
    return 0
}