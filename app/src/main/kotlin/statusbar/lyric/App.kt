/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/Block-Network/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as
 * published by Block-Network contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/Block-Network/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import statusbar.lyric.ui.page.ChoosePage
import statusbar.lyric.ui.page.ExtendPage
import statusbar.lyric.ui.page.HomePage
import statusbar.lyric.ui.page.IconPage
import statusbar.lyric.ui.page.LyricPage
import statusbar.lyric.ui.page.MenuPage
import statusbar.lyric.ui.page.SystemSpecialPage
import statusbar.lyric.ui.page.TestPage
import statusbar.lyric.ui.theme.AppTheme
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun App() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val navController = rememberNavController()

    val currentRoute = remember {
        mutableStateOf(navController.currentDestination?.route ?: "")
    }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentRoute.value = destination.route ?: ""
        }
    }

    AppTheme {
        Scaffold {
            BoxWithConstraints {
                if (isLandscape || maxWidth > 768.dp) {
                    LandscapeLayout(navController, currentRoute)
                } else {
                    PortraitLayout(navController, currentRoute)
                }
            }
        }
    }
}

@Composable
fun PortraitLayout(
    navController: NavHostController,
    currentRoute: MutableState<String>
) {
    val windowWidth = getWindowSize().width
    val easing = CubicBezierEasing(0.12f, 0.38f, 0.2f, 1f)

    NavHost(
        navController = navController,
        startDestination = "HomePage",
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { windowWidth },
                animationSpec = tween(durationMillis = 500, easing = easing)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -windowWidth / 5 },
                animationSpec = tween(durationMillis = 500, easing = easing)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 500),
                targetAlpha = 0.5f
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -windowWidth / 5 },
                animationSpec = tween(durationMillis = 500, easing = easing)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 500),
                initialAlpha = 0.5f
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { windowWidth },
                animationSpec = tween(durationMillis = 500, easing = easing)
            )
        }
    ) {
        composable("HomePage") { HomePage(navController, currentRoute) }
        pageDestinations(navController, currentRoute)
    }
}

@Composable
fun LandscapeLayout(
    navController: NavHostController,
    currentRoute: MutableState<String>
) {
    val windowWidth = getWindowSize().width
    val easing = CubicBezierEasing(0.12f, 0.88f, 0.2f, 1f)
    val dividerLineColor = MiuixTheme.colorScheme.dividerLine

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier.weight(0.88f)
        ) {
            HomePage(navController, currentRoute)
        }
        Canvas(
            Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp)
                .width(0.75.dp)
        ) {
            drawLine(
                color = dividerLineColor,
                strokeWidth = 0.75.dp.toPx(),
                start = Offset(0.75.dp.toPx() / 2, 0f),
                end = Offset(0.75.dp.toPx() / 2, size.height),
            )
        }
        NavHost(
            navController = navController,
            startDestination = "HomePage",
            modifier = Modifier.weight(1f),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { windowWidth },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 200)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { windowWidth / 5 },
                    animationSpec = tween(durationMillis = 500, easing = easing)
                ) + fadeOut(
                    animationSpec = tween(durationMillis = 500)
                )
            },
        ) {
            composable("HomePage") { EmptyPage() }
            pageDestinations(navController, currentRoute)
        }
    }
}

fun NavGraphBuilder.pageDestinations(
    navController: NavHostController,
    currentRoute: MutableState<String>
) {
    composable("ChoosePage") { ChoosePage(navController) }
    composable("TestPage") { TestPage(navController, currentRoute) }
    composable("MenuPage") { MenuPage(navController) }
    composable("LyricPage") { LyricPage(navController) }
    composable("IconPage") { IconPage(navController) }
    composable("ExtendPage") { ExtendPage(navController) }
    composable("SystemSpecialPage") { SystemSpecialPage(navController) }

}

@Composable
fun EmptyPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            tint = MiuixTheme.colorScheme.secondary,
            modifier = Modifier.size(256.dp)
        )
    }
}
