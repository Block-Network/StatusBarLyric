package statusbar.lyric.utils

import java.util.Locale

object LogMultiLang {
    private val isChinese: Boolean by lazy { Locale.getDefault().language == "zh" }

    val noSupportSystem = if (isChinese) "不支持的系统请打包日志和系统界面发给作者" else "For unsupported systems, please package the log and systemUI and send it to the author."
    val fieldFail = if (isChinese) "反射失败" else "Field fail"
    val fieldSuccess = if (isChinese) "反射成功" else "Field success"
    val normalMode = if (isChinese) "正常模式" else "Normal mode"
    val checkSystem = if (isChinese) "野人 官改" else "野人 official system modification"
    val tries = if (isChinese) "尝试" else "Try"
    val customHook = if (isChinese) "自定义Hook" else "Custom hook"
    val pausePlay = if (isChinese) "暂停播放" else "Pause playback"
    val playerOff = if (isChinese) "播放器关闭" else "Player off"
    val switchOff = if (isChinese) "开关关闭" else "Switch off"
    val stateCheck = if (isChinese) "状态检测" else "State detection"
    val lyricColor = if (isChinese) "歌词颜色" else "Lyrics color"
    val fontLoad = if (isChinese) "加载个性化字体" else "Load personalized fonts"
    val updateLyric = if (isChinese) "更新歌词" else "Update lyric"
    val offLyric = if (isChinese) "关闭歌词" else "Off lyric"
    val initOk = if (isChinese) "初始化完成" else "Initialization complete"
    val emptyLyric = if (isChinese) "收到歌词空" else "Received lyrics empty"
    val unlockDisplayOnly = if (isChinese) "仅解锁显示" else "Unlock display only"
    val hideIcon = if (isChinese) "隐藏图标" else "Hide icon"
    val showIcon = if (isChinese) "显示图标" else "Show icon"
    val findAntiMethodFail = if (isChinese) "查找反色方法失败" else "Failed to find anti color method"
    val findAntiMethodSuccess = if (isChinese) "查找反色方法成功" else "The reverse color method is found successfully"
    val findSystemAntiClassFail = if (isChinese) "系统方法反色获取失败" else "System method reverse color acquisition failed"
    val systemAntiError = if (isChinese) "系统反色出现错误" else "System color reversal error"
    val lockScreen = if (isChinese) "锁屏" else "Lock screen"
    val lockScreenError = if (isChinese) "锁屏不显示出现错误" else "The lock screen does not display an error"
    val recvData = if (isChinese) "收到广播" else "receive a broadcast"
    val customFont = if (isChinese) "自定义字体" else "Custom font"
    val fontCopyError = if (isChinese) "个性化字体复制失败" else "Personalized font copy failed"
    val lyricServiceError = if (isChinese) "歌词服务出现错误" else "An error occurred in the lyrics service"
    val sendLog = if (isChinese) "反馈日志请不要截图，Lsp右上角保存成文件反馈" else "Please do not take screenshots of the feedback log, save it as a file feedback in the upper right corner of the Lsp"
    val initError = if (isChinese) "初始化失败" else "Init failed"
    val initFontFailed = if (isChinese) "初始化自定义字体失败" else "Init custom font failed"
//    val _ = if (isChinese) "" else ""
}
