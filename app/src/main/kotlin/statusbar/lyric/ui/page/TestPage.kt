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
import androidx.compose.foundation.layout.imePadding
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
import statusbar.lyric.MainActivity.Companion.context
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTestTools.getClass
import statusbar.lyric.tools.ActivityTestTools.waitResponse
import statusbar.lyric.tools.Tools
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
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
fun TestPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val showDialog = remember { mutableStateOf(false) }
    val testMode = remember { mutableStateOf(config.testMode) }
    val relaxConditions = remember { mutableStateOf(config.relaxConditions) }
    Scaffold(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.hook_page),
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
                                        titleColor = MiuixTheme.colorScheme.primary,
                                        summary = stringResource(R.string.tips1),
                                        onClick = {
                                            waitResponse()
                                            context.getClass()
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
                        SmallTitle(
                            text = stringResource(R.string.test_mode_tips).split("\n")[1]
                        )
                    }
                    RestartDialog(showDialog)
                }
            }
        }
    }
}

@Composable
fun RestartDialog(showDialog: MutableState<Boolean>) {
    showDialog(
        show = showDialog.value,
        content = {
            SuperDialog(
                title = stringResource(R.string.reset_system_ui),
                summary = stringResource(R.string.restart_systemui_tips),
                show = showDialog,
                onDismissRequest = {
                    showDialog.value = false
                },
            ) {
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
                            Tools.shell("killall com.android.systemui", true)
                            dismissDialog()
                            showDialog.value = false
                        }
                    )
                }
            }
        }
    )
}