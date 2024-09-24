package statusbar.lyric.ui.page

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun IconPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val iconSwitch = remember { mutableStateOf(config.iconSwitch) }
    val showDialog = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.icon_page),
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
                                title = stringResource(R.string.icon_switch),
                                checked = iconSwitch.value,
                                onCheckedChange = {
                                    iconSwitch.value = it
                                    config.iconSwitch = it
                                }
                            )
                            // TODO
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
                    }
                    RestartDialog(showDialog)
                }
            }
        }
    }
}