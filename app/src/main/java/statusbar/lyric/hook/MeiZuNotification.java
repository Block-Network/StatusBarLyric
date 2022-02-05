/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/577fkj/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric.hook;

import android.app.Notification;

public class MeiZuNotification extends Notification {
    public static final int FLAG_ALWAYS_SHOW_TICKER_HOOK = 0x01000000;
    public static final int FLAG_ONLY_UPDATE_TICKER_HOOK = 0x02000000;
    public static final int FLAG_ALWAYS_SHOW_TICKER = 0x01000000;
    public static final int FLAG_ONLY_UPDATE_TICKER = 0x02000000;
}