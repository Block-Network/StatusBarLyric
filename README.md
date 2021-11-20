<h1 align="center">MIUI 状态栏歌词</h1>

<div align="center">

![Release Download](https://img.shields.io/github/downloads/577fkj/MIUIStatusBarLyricw/total?style=flat-square)
[![Release Version](https://img.shields.io/github/v/release/577fkj/MIUIStatusBarLyricw?style=flat-square)](https://github.com/577fkj/MIUIStatusBarLyricw/releases/latest)
[![GitHub license](https://img.shields.io/github/license/577fkj/MIUIStatusBarLyricw?style=flat-square)](LICENSE)
[![GitHub Star](https://img.shields.io/github/stars/577fkj/MIUIStatusBarLyricw?style=flat-square)](https://github.com/577fkj/MIUIStatusBarLyricw/stargazers)
[![GitHub Fork](https://img.shields.io/github/forks/577fkj/MIUIStatusBarLyricw?style=flat-square)](https://github.com/577fkj/MIUIStatusBarLyricw/network/members)
![GitHub Repo size](https://img.shields.io/github/repo-size/577fkj/MIUIStatusBarLyricw?style=flat-square&color=3cb371)
[![GitHub Repo Languages](https://img.shields.io/github/languages/top/577fkj/MIUIStatusBarLyricw?style=flat-square)](https://github.com/577fkj/MIUIStatusBarLyricw/search?l=java)

[![Build Status](https://img.shields.io/endpoint.svg?url=https://actions-badge.atrox.dev/577fkj/MIUIStatusBarLyricw/badge&style=flat-square)](https://actions-badge.atrox.dev/577fkj/MIUIStatusBarLyricw/goto?ref=develop)
[![GitHub Star](https://img.shields.io/github/stars/577fkj/MIUIStatusBarLyricw.svg?style=social)](https://github.com/577fkj/MIUIStatusBarLyricw)
[![电报群](https://img.shields.io/badge/电报群-MIUIStatusBatLyric-blue.svg?style=flat-square&color=12b7f5)](https://t.me/MIUIStatusBatLyric)
[![QQ群](https://img.shields.io/badge/QQ群-884185860-blue.svg?style=flat-square&color=12b7f5)](https://qm.qq.com/cgi-bin/qm/qr?k=ea_MP7zFoZJEdpxDFQcadBdbZmwYXZHh&jump_from=webapi)
[![爱发电](https://img.shields.io/badge/爱发电-@xiao_wine-blue.svg?style=flat-square&color=12b7f5)](https://afdian.net/@xiao_wine)

</div>

### 语言 / Language

简体中文 / [English](README_EN.md)

### 支持的系统版本

#### 支持 MIUI 10/12/12.5 (MIUI 11 大概率也是支持的[未测试])
#### 理论支持部分类原生

## 支持的音乐软件版本
### 完美适配：（标记最高适配的版本）
- [APlayer](https://github.com/rRemix/APlayer) : v1.5.7.9
- 网易云音乐 : v8.6.13

### 应用主动适配：（标记开始适配的版本）
- [椒盐音乐](https://github.com/Moriafly/SaltPlayerSource) ：v4.5+

### 应用内打开蓝牙歌词：（标记最高适配的版本）
- QQ音乐 : v10.17.0.11 （另需额外连接蓝牙）
- 酷狗音乐 : v10.9.0
- 酷我音乐 : v10.0.1.0
- 咪咕音乐 : v7.6.1 (另需额外蓝牙)
- 网易云音乐 : v7.2.22

## 感谢名单（不分先后）
- @潇风残月 (感谢提供的库以及对代码的极限精简)
- @柒猫Sebun_Neko (感谢提供的图标)
- @Yife Playte (感谢帮忙优化矢量图)

### 下载

- [Releases](https://github.com/577fkj/MIUIStatusBarLyricw/releases)  
- [酷安](https://www.coolapk.com/apk/miui.statusbar.lyric)  
- [测试版](https://github.com/577fkj/MIUIStatusBarLyricw/actions/workflows/Android.yml)  

## 音乐主动适配
### API 发送歌词示例

Kotlin

```kotlin
fun sendLyric(context: Context, lyric: String, icon: String, packName: String) {
    val intent = Intent().apply {
        action = "Lyric_Server"
        putExtra("Lyric_Data", lyric)
        putExtra("Lyric_Type", "app")
        putExtra("Lyric_PackName", packName)
        putExtra("Lyric_Icon", icon)
    }
    context.sendBroadcast(intent)
}
```

Java

```java
public void sendLyric(Context context, String lyric, String icon, String packName) {
    context.sendBroadcast(new Intent()
            .setAction("Lyric_Server")
            .putExtra("Lyric_Data", lyric)
            .putExtra("Lyric_Type", "app")
            .putExtra("Lyric_PackName", packName)
            .putExtra("Lyric_Icon", icon)
    );
}
```

| 参数 | 解释 |
| :-- | :-- |
| context  | Context |
| lyric    | 歌词文本 |
| icon     | 无前缀的 base64 编码（推荐32x32） |
| packName | 你的软件包名 |

#### 暂停/停止播放 (不提供会导致无法正常取消显示歌词)

Kotlin

```kotlin
fun stopLyric(context: Context) {
    val intent = Intent().apply {
        action = "Lyric_Server"
        putExtra("Lyric_Type", "app_stop")
    }
    context.sendBroadcast(intent)
}
```

Java

```java
public void stopLyric(Context context) {
    context.sendBroadcast(new Intent()
            .setAction("Lyric_Server")
            .putExtra("Lyric_Type", "app_stop")
    );
}
```

| 参数 | 解释 |
| :-- | :-- |
| context  | Context |