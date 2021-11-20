<h1 align="center">MIUI Status bar lyric</h1>

<div align="center">

![Release Download](https://img.shields.io/github/downloads/577fkj/MIUIStatusBarLyric/total?style=flat-square)
[![Release Version](https://img.shields.io/github/v/release/577fkj/MIUIStatusBarLyric?style=flat-square)](https://github.com/577fkj/MIUIStatusBarLyric/releases/latest)
[![GitHub license](https://img.shields.io/github/license/577fkj/MIUIStatusBarLyric?style=flat-square)](LICENSE)
[![GitHub Star](https://img.shields.io/github/stars/577fkj/MIUIStatusBarLyric?style=flat-square)](https://github.com/577fkj/MIUIStatusBarLyric/stargazers)
[![GitHub Fork](https://img.shields.io/github/forks/577fkj/MIUIStatusBarLyric?style=flat-square)](https://github.com/577fkj/MIUIStatusBarLyric/network/members)
![GitHub Repo size](https://img.shields.io/github/repo-size/577fkj/MIUIStatusBarLyric?style=flat-square&color=3cb371)
[![GitHub Repo Languages](https://img.shields.io/github/languages/top/577fkj/MIUIStatusBarLyric?style=flat-square)](https://github.com/577fkj/MIUIStatusBarLyric/search?l=java)

[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2FSteamTools-Team%2FSteamTools%2Fbadge%3Fref%3Ddevelop&style=flat-square)](https://actions-badge.atrox.dev/577fkj/MIUIStatusBarLyric/goto?ref=develop)
[![GitHub Star](https://img.shields.io/github/stars/577fkj/MIUIStatusBarLyric.svg?style=social)](https://github.com/577fkj/MIUIStatusBarLyric)
[![Telegram](https://img.shields.io/badge/Telegram-MIUIStatusBatLyric-blue.svg?style=flat-square&color=12b7f5)](https://t.me/MIUIStatusBatLyric)
[![QQ group](https://img.shields.io/badge/QQ group-884185860-blue.svg?style=flat-square&color=12b7f5)](https://qm.qq.com/cgi-bin/qm/qr?k=ea_MP7zFoZJEdpxDFQcadBdbZmwYXZHh&jump_from=webapi)

</div>

### 语言 / Language

[简体中文](README.md) / English

### Supported system versions

#### Support MIUI 10/12/12.5 (MIUI 11[Not tested])
#### Theoretical support other system

## Supported Music app versions
### Perfect fit：（Mark the most suitable version）
- [APlayer](https://github.com/rRemix/APlayer) : v1.5.7.9
- NetEase Cloud Music : v8.6.13

### App active adaptation：（Mark the start adapting version）
- [SaltPlayer](https://github.com/Moriafly/SaltPlayerSource) ：v4.5+

### Open Bluetooth lyrics in the app：（Mark the most suitable version）
- QQ Music : v10.17.0.11 （Need to connect to Bluetooth）
- kugou Music : v10.9.0
- kuwo Music : v10.0.1.0
- Migu Music : v7.6.1 (Need to connect to Bluetooth)
- NetEase Cloud Music : v7.2.22

## Thanks list（In no particular order）
- @潇风残月 (Thanks for the provided library and the extreme streamlining of the code)
- @柒猫Sebun_Neko (Thanks for the provided icon)
- @Yife Playte (Thanks for helping optimize vector)

### Downloads

- [Releases](https://github.com/577fkj/MIUIStatusBarLyric/releases)
- [CoolAPK](https://www.coolapk.com/apk/miui.statusbar.lyric)
- [Beta](https://github.com/577fkj/MIUIStatusBarLyric/actions/workflows/Android.yml)

### contact us

- [QQ Group](https://jq.qq.com/?_wv=1027&amp;k=KQeQjgsv)
- [Telegram](https://t.me/MIUIStatusBatLyric)
- [CoolAPK](https://www.coolapk.com/apk/miui.statusbar.lyric)
- [BUG feedback (Github)](https://github.com/577fkj/MIUIStatusBarLyric/issues/new)

## App active adaptation
### API Send lyrics example

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

| Parameter | Explain |
| :-- | :-- |
| context  | Context |
| lyric    | Lyric text |
| icon     | Base64 encoding without prefix (32x32 recommended) |
| packName | Your package name |

#### Pause/stop playback (Failure to provide will cause the lyrics to be not cancelled normally)

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

| Parameter | Explain |
| :-- | :-- |
| context  | Context |