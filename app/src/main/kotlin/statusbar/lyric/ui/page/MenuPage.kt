package statusbar.lyric.ui.page

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.BackupTools
import statusbar.lyric.tools.Tools
import statusbar.lyric.tools.Tools.bigTextOne
import statusbar.lyric.tools.Tools.buildTime
import statusbar.lyric.tools.Tools.getPhoneName
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
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

@SuppressLint("ContextCastToActivity")
@Composable
fun MenuPage(navController: NavController, currentStartDestination: MutableState<String>) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val ac = LocalContext.current as Activity
    val outLog = remember { mutableStateOf(config.outLog) }
    val showLauncherIcon = remember { mutableStateOf(config.showLauncherIcon) }
    val showDialog = remember { mutableStateOf(false) }
    val showResetDialog = remember { mutableStateOf(false) }

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
                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Right)),
                title = stringResource(R.string.menu_page),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 20.dp),
                        onClick = {
                            navController.navigate(currentStartDestination.value) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            navController.popBackStack(
                                currentStartDestination.value,
                                inclusive = false
                            )
                        }
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.Back,
                            contentDescription = "Back",
                            tint = MiuixTheme.colorScheme.onBackground
                        )
                    }
                },
                horizontalPadding = 26.dp,
                defaultWindowInsetsPadding = false
            )
        },
        popupHost = { null }
    ) {
        BackHandler(true) {
            navController.navigate(currentStartDestination.value) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            navController.popBackStack(currentStartDestination.value, inclusive = false)
        }

        LazyColumn(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .height(getWindowSize().height.dp)
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Right))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Right)),
            topAppBarScrollBehavior = scrollBehavior,
            contentPadding = it
        ) {
            item {
                Column(Modifier.padding(top = 18.dp)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Column {
                            SuperSwitch(
                                title = stringResource(R.string.show_launcher_icon),
                                checked = showLauncherIcon.value,
                                onCheckedChange = {
                                    showLauncherIcon.value = it
                                    config.showLauncherIcon = it
                                    context.packageManager.setComponentEnabledSetting(
                                        ComponentName(context, "statusbar.lyric.AliasActivity"),
                                        if (it) {
                                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                                        } else {
                                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                                        },
                                        PackageManager.DONT_KILL_APP
                                    )
                                }
                            )
                            SuperSwitch(
                                title = stringResource(R.string.show_logcat),
                                checked = outLog.value,
                                onCheckedChange = {
                                    outLog.value = it
                                    config.outLog = it
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
                    Column {
                        SuperArrow(
                            title = stringResource(R.string.backup_config),
                            onClick = {
                                BackupTools.backup(ac, ActivityOwnSP.ownSP)
                            }
                        )
                        SuperArrow(
                            title = stringResource(R.string.recovery_config),
                            onClick = {
                                BackupTools.recovery(ac, ActivityOwnSP.ownSP)
                            }
                        )
                        SuperArrow(
                            title = stringResource(R.string.clear_config),
                            onClick = {
                                showResetDialog.value = true
                            }
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
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
                SmallTitle(
                    text = stringResource(R.string.info),
                    modifier = Modifier.padding(top = 6.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.version_code),
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
                        )
                        Text(
                            text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) ${
                                bigTextOne(
                                    BuildConfig.BUILD_TYPE
                                )
                            }",
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Text(
                            text = stringResource(R.string.buildtime),
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
                        )
                        Text(
                            text = buildTime,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Text(
                            text = stringResource(R.string.currentdevice),
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
                        )
                        Text(
                            text = "$getPhoneName (${Build.DEVICE}) SDK${Build.VERSION.SDK_INT}",
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Text(
                            text = "Lyric Getter",
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp)
                        )
                        Text(
                            text = "API${BuildConfig.API_VERSION} \uD83D\uDE0A",
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                        )
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
    RestartDialog(showDialog = showDialog)
    ResetDialog(showDialog = showResetDialog)
}

@Composable
fun ResetDialog(showDialog: MutableState<Boolean>) {
    SuperDialog(
        title = stringResource(R.string.clear_config),
        summary = stringResource(R.string.clear_config_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
        },
    ) {
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
                    config.clear()
                    dismissDialog(showDialog)
                    Thread {
                        Thread.sleep(500)
                        ActivityTools.restartApp()
                    }.start()
                }
            )
        }
    }
}

@Composable
fun RestartDialog(showDialog: MutableState<Boolean>) {
    SuperDialog(
        title = stringResource(R.string.reset_system_ui),
        summary = stringResource(R.string.restart_systemui_tips),
        show = showDialog,
        onDismissRequest = {
            dismissDialog(showDialog)
        },
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.ok),
                onClick = {
                    Tools.shell("killall com.android.systemui", true)
                    dismissDialog(showDialog)
                }
            )
            Spacer(Modifier.width(20.dp))
            TextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.cancel),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                onClick = {
                    dismissDialog(showDialog)
                }
            )
        }
    }
}