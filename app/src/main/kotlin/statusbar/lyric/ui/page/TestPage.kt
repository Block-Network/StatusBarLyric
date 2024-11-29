package statusbar.lyric.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import statusbar.lyric.MainActivity.Companion.context
import statusbar.lyric.MainActivity.Companion.testReceiver
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTestTools.getClass
import statusbar.lyric.tools.ActivityTools.showToastOnLooper
import statusbar.lyric.tools.Tools.goMainThread
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun TestPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val showDialog = remember { mutableStateOf(false) }
    val testMode = remember { mutableStateOf(config.testMode) }
    val relaxConditions = remember { mutableStateOf(config.relaxConditions) }

    Column {
        TopAppBar(
            title = stringResource(R.string.hook_page),
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                IconButton(
                    modifier = Modifier.padding(start = 18.dp),
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(34.dp),
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
                Column(Modifier.padding(top = 18.dp)) {
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
                                    titleColor = BasicComponentColors(
                                        color = MiuixTheme.colorScheme.primary,
                                        disabledColor = MiuixTheme.colorScheme.disabledOnSecondaryVariant
                                    ),
                                    rightText = stringResource(R.string.tips1),
                                    onClick = {
                                        context.getClass()
                                        when (testReceiver) {
                                            true -> navController.navigate("ChoosePage")
                                            else -> {
                                                Thread {
                                                    Thread.sleep(500)
                                                    goMainThread {
                                                        if (testReceiver) navController.navigate("ChoosePage") else {
                                                            showToastOnLooper(context.getString(R.string.broadcast_receive_timeout))
                                                        }
                                                    }
                                                }.start()
                                            }
                                        }
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
                            titleColor = BasicComponentColors(
                                color = Color.Red,
                                disabledColor = MiuixTheme.colorScheme.disabledOnSecondaryVariant
                            ),
                            onClick = {
                                showDialog.value = true
                            }
                        )
                    }
                    SmallTitle(
                        modifier = Modifier.padding(bottom = 12.dp),
                        text = stringResource(R.string.test_mode_tips).split("\n")[1]
                    )
                }
                Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
            }
        }
    }
    RestartDialog(showDialog)
}