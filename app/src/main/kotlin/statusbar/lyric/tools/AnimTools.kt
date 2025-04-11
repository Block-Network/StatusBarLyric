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

package statusbar.lyric.tools

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

class AnimTools {
    fun enterTransition(delayMillis: Int = 0): EnterTransition {
        val easing = CubicBezierEasing(0.36f, 1.44f, 0.48f, 1f)
        return fadeIn(animationSpec = tween(400, delayMillis, easing)) + scaleIn(
            initialScale = 0.95f,
            animationSpec = tween(400, delayMillis, easing)
        )
    }

    fun exitTransition(delayMillis: Int = 0): ExitTransition {
        val easing = CubicBezierEasing(0.36f, 1.44f, 0.48f, 1f)
        return fadeOut(animationSpec = tween(300, delayMillis, easing)) + scaleOut(
            targetScale = 0.95f,
            animationSpec = tween(300, delayMillis, easing)
        )
    }
}