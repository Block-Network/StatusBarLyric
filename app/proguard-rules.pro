# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-optimizationpasses 5


-keep class * implements de.robv.android.xposed.IXposedHookLoadPackage {
    public void *(de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam);
}

-keep class * implements de.robv.android.xposed.IXposedHookInitPackageResources {
    public void *(de.robv.android.xposed.callbacks.XC_InitPackageResources$InitPackageResourcesParam);
}
-keep class * implements de.robv.android.xposed.IXposedHookZygoteInit {
    public void *(de.robv.android.xposed.IXposedHookZygoteInit$StartupParam);
}

-keep class * extends java.io.Serializable{*;}

-keep class * extends android.app.Notification{*;}
# keep rx
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}

-keep class com.microsoft.appcenter.utils.DeviceInfoHelper {
    public static synchronized com.microsoft.appcenter.ingestion.models.Device getDeviceInfo(android.content.Context);
}

-repackageclasses "截图看NM日志"
-obfuscationdictionary dict.txt
-classobfuscationdictionary dict.txt
-packageobfuscationdictionary dict.txt
