package statusbar.lyric.hook.module

import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.tools.Tools.isNotNull

class Self : BaseHook() {
    override fun init() {
        loadClassOrNull("statusbar.lyric.tools.ActivityTools").isNotNull {
            it.methodFinder().filterByName("isHook").first().createHook {
                returnConstant(true)
            }
        }
    }
}