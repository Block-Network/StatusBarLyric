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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
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
import statusbar.lyric.MainActivity.Companion.context
import statusbar.lyric.MainActivity.Companion.isLoad
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.AnimTools
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
    Scaffold(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.app_name),
                color = Color.Transparent,
                scrollBehavior = scrollBehavior
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
                                        Toast.makeText(
                                            context,
                                            R.string.module_inactivated,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
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
                                            summary = stringResource(R.string.test_mode_tips).split("\n")[0],
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