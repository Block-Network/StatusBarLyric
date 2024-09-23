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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import getWindowSize
import statusbar.lyric.R
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixTopAppBar
import top.yukonga.miuix.kmp.basic.MiuixBox
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixLazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScaffold
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.rememberMiuixTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun IconPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberMiuixTopAppBarState())
    MiuixScaffold(
        modifier = Modifier
            .imePadding()
            .fillMaxSize(),
        topBar = {
            MiuixTopAppBar(
                title = stringResource(R.string.icon_page),
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
                        MiuixCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            insideMargin = DpSize(0.dp, 0.dp)
                        ) {

                        }
                    }
                }
            }
        }
    }
}