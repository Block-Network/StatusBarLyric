package statusbar.lyric.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import getWindowSize
import statusbar.lyric.MainActivity.Companion.safeSP
import statusbar.lyric.R
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixSuperArrow
import top.yukonga.miuix.kmp.MiuixSuperSwitch
import top.yukonga.miuix.kmp.MiuixTopAppBar
import top.yukonga.miuix.kmp.basic.MiuixBox
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixLazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScaffold
import top.yukonga.miuix.kmp.basic.MiuixSmallTitle
import top.yukonga.miuix.kmp.rememberMiuixTopAppBarState

@Composable
fun HomePage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberMiuixTopAppBarState())
    val masterSwitchState = remember { mutableStateOf(safeSP.getBoolean("masterSwitch", false)) }

    MiuixScaffold(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
        topBar = {
            MiuixTopAppBar(
                title = stringResource(R.string.app_name),
                color = Color.Transparent,
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        MiuixBox {
            MiuixLazyColumn(
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
                        MiuixCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(top = 12.dp, bottom = 6.dp),
                            insideMargin = DpSize(0.dp, 0.dp)
                        ) {
                            MiuixSuperSwitch(
                                title = stringResource(R.string.master_switch),
                                checked = masterSwitchState.value,
                                onCheckedChange = {
                                    masterSwitchState.value = it
                                    safeSP.putAny("masterSwitch", it)
                                },
                                insideMargin = DpSize(15.dp, 15.dp)
                            )
                        }
                        AnimatedVisibility(masterSwitchState.value) {
                            Column {
                                MiuixSmallTitle(
                                    text = stringResource(R.string.test_mode_tips).split("\n")[0],
                                    insideMargin = DpSize(24.dp, 8.dp)
                                )
                                MiuixCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    insideMargin = DpSize(0.dp, 0.dp)
                                ) {
                                    MiuixSuperArrow(
                                        title = stringResource(R.string.hook_page),
                                        onClick = {
                                            navController.navigate("TestPage")
                                        },
                                        insideMargin = DpSize(15.dp, 15.dp)
                                    )
                                }
                                MiuixCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    insideMargin = DpSize(0.dp, 0.dp)
                                ) {
                                    MiuixSuperArrow(
                                        title = stringResource(R.string.lyric_page),
                                        onClick = {
                                            navController.navigate("LyricPage")
                                        },
                                        insideMargin = DpSize(15.dp, 15.dp)
                                    )
                                    MiuixSuperArrow(
                                        title = stringResource(R.string.icon_page),
                                        onClick = {
                                            navController.navigate("IconPage")
                                        },
                                        insideMargin = DpSize(15.dp, 15.dp)
                                    )
                                }
                                MiuixCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    insideMargin = DpSize(0.dp, 0.dp)
                                ) {
                                    MiuixSuperArrow(
                                        title = stringResource(R.string.extend_page),
                                        onClick = {
                                            navController.navigate("ExtendPage")
                                        },
                                        insideMargin = DpSize(15.dp, 15.dp)
                                    )
                                    MiuixSuperArrow(
                                        title = stringResource(R.string.system_special_page),
                                        onClick = {
                                            navController.navigate("SystemSpecialPage")
                                        },
                                        insideMargin = DpSize(15.dp, 15.dp)
                                    )
                                }
                                MiuixSmallTitle(
                                    text = stringResource(R.string.tips1),
                                    insideMargin = DpSize(24.dp, 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}