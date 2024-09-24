package statusbar.lyric

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import statusbar.lyric.ui.page.ChoosePage
import statusbar.lyric.ui.page.ExtendPage
import statusbar.lyric.ui.page.HomePage
import statusbar.lyric.ui.page.IconPage
import statusbar.lyric.ui.page.LyricPage
import statusbar.lyric.ui.page.SystemSpecialPage
import statusbar.lyric.ui.page.TestPage
import statusbar.lyric.ui.theme.AppTheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun App(
    navController: NavHostController
) {
    val windowWidth = getWindowSize().width

    AppTheme {
        NavHost(
            navController = navController,
            startDestination = "HomePage",
            enterTransition = { slideInHorizontally(initialOffsetX = { windowWidth }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -windowWidth / 5 }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -windowWidth / 5 }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { windowWidth }) }
        ) {
            composable("HomePage") { HomePage(navController) }
            composable("TestPage") { TestPage(navController) }
            composable("LyricPage") { LyricPage(navController) }
            composable("IconPage") { IconPage(navController) }
            composable("ExtendPage") { ExtendPage(navController) }
            composable("SystemSpecialPage") { SystemSpecialPage(navController) }
            composable("ChoosePage") { ChoosePage(navController) }
        }
    }
}