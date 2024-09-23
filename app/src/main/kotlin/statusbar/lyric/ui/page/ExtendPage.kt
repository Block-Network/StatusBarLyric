package statusbar.lyric.ui.page

import android.widget.Toast
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import getWindowSize
import statusbar.lyric.MainActivity.Companion.context
import statusbar.lyric.MainActivity.Companion.isLoad
import statusbar.lyric.MainActivity.Companion.safeSP
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.ui.theme.AppTheme
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixSuperArrow
import top.yukonga.miuix.kmp.MiuixSuperSwitch
import top.yukonga.miuix.kmp.MiuixTopAppBar
import top.yukonga.miuix.kmp.basic.MiuixBox
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixLazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScaffold
import top.yukonga.miuix.kmp.basic.MiuixSmallTitle
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.rememberMiuixTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ExtendPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberMiuixTopAppBarState())
    val mMIUIHideNetworkSpeed = remember { mutableStateOf(config.mMIUIHideNetworkSpeed) }
    val mMiuiPadOptimize = remember { mutableStateOf(config.mMiuiPadOptimize) }
    val mHyperOSTexture = remember { mutableStateOf(config.mHyperOSTexture) }
    MiuixScaffold(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
        topBar = {
            MiuixTopAppBar(
                title = stringResource(R.string.extend_page),
                color = Color.Transparent,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(start = 12.dp),
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
                        MiuixSmallTitle(
                            text = stringResource(R.string.miui_or_hyper),
                        )
                        MiuixCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            insideMargin = DpSize(0.dp, 0.dp)
                        ) {
                            MiuixSuperSwitch(
                                title = stringResource(R.string.miui_hide_network_speed),

                                checked = mMIUIHideNetworkSpeed.value,
                                onCheckedChange = {
                                    if (isLoad) {
                                        mMIUIHideNetworkSpeed.value = it
                                        safeSP.putAny("mMIUIHideNetworkSpeed", it)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            R.string.module_inactivated,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                insideMargin = DpSize(16.dp, 16.dp)
                            )
                            MiuixSuperSwitch(
                                title = stringResource(R.string.miui_pad_optimize),

                                checked = mMiuiPadOptimize.value,
                                onCheckedChange = {
                                    if (isLoad) {
                                        mMiuiPadOptimize.value = it
                                        safeSP.putAny("mMiuiPadOptimize", it)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            R.string.module_inactivated,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                insideMargin = DpSize(16.dp, 16.dp)
                            )
                        }

                        MiuixSmallTitle(
                            text = stringResource(R.string.hyperos),
                        )
                        MiuixCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            insideMargin = DpSize(0.dp, 0.dp)
                        ) {
                            MiuixSuperSwitch(
                                title = stringResource(R.string.hyperos_texture),

                                checked = mHyperOSTexture.value,
                                onCheckedChange = {
                                    if (isLoad) {
                                        mHyperOSTexture.value = it
                                    } else {
                                        Toast.makeText(
                                            context,
                                            R.string.module_inactivated,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                insideMargin = DpSize(16.dp, 16.dp)
                            )
                            MiuixSuperArrow(
                                title = stringResource(R.string.hyperos_texture_radio),

                                onClick = {
                                    navController.navigate("HomePage")
                                },
                                insideMargin = DpSize(16.dp, 16.dp)
                            )
                            MiuixSuperArrow(
                                title = stringResource(R.string.hyperos_texture_corner),

                                onClick = {
                                    navController.navigate("HomePage")
                                },
                                insideMargin = DpSize(16.dp, 16.dp)
                            )
                            MiuixSuperArrow(
                                title = stringResource(R.string.hyperos_texture_color),

                                onClick = {
                                    navController.navigate("HomePage")
                                },
                                insideMargin = DpSize(16.dp, 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}