package statusbar.lyric.ui.page

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@Preview(locale = "zh")
@Composable
fun HomePagePreview() {
    HomePage(navController = rememberNavController())
}

@Composable
fun HomePage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val masterSwitchState = remember { mutableStateOf(if (isLoad) config.masterSwitch else false) }
    val lyricGetterApi = checkLyricGetterApi()

    LaunchedEffect(Unit) {
        if (checkLyricGetterApi() != 0 || !isLoad) {
            masterSwitchState.value = false
            config.masterSwitch = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.app_name),
                color = Color.Transparent,
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(
                        modifier = Modifier.padding(end = 18.dp),
                        onClick = {
                            navController.navigate("MenuPage")
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(id = R.drawable.ic_menu),
                            contentDescription = "Menu",
                            tint = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
            )
        },
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
                                        titleColor = Color.Red,
                                        onClick = {
                                            ActivityTools.restartApp()
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
                                        text = stringResource(R.string.module_first)
                                    )
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp)
                                            .padding(bottom = 6.dp)
                                    ) {
                                        SuperArrow(
                                            title = stringResource(R.string.hook_page),
                                            summary = if (config.textViewId == 0) stringResource(R.string.test_mode_tips).split("\n")[0] else null,
                                            onClick = {
                                                navController.navigate("TestPage")
                                            }
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
                                        text = stringResource(R.string.module_second)
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
                                                navController.navigate("LyricPage")
                                            }
                                        )
                                        SuperArrow(
                                            title = stringResource(R.string.icon_page),
                                            onClick = {
                                                navController.navigate("IconPage")
                                            }
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
                                        text = stringResource(R.string.module_third)
                                    )
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp)
                                            .padding(bottom = 6.dp)
                                    ) {
                                        SuperArrow(
                                            title = stringResource(R.string.extend_page),
                                            onClick = {
                                                navController.navigate("ExtendPage")
                                            }
                                        )
                                        SuperArrow(
                                            title = stringResource(R.string.system_special_page),
                                            onClick = {
                                                navController.navigate("SystemSpecialPage")
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
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
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = "Warning"
                    )
                },
                title = stringResource(R.string.no_supported_version_lyric_getter),
                titleColor = Color.Red,
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
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = "Warning"
                    )
                },
                title = stringResource(R.string.no_lyric_getter),
                titleColor = Color.Red,
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