# StatusBarAPi

[APIDemo](https://github.com/577fkj/StatusBarApiExample)

[Jitpack](https://www.jitpack.io/#577fkj/MIUIStatusBarLyric/3.34.12v61)

You need to add in build.gradle
```
allprojects {
    repositories {
        ....
        maven { url 'https://www.jitpack.io' }
    }
}


dependencies {
    implementation 'com.github.577fkj:MIUIStatusBarLyric:v1.1'
}
```

```java
import StatusbarLyric.API.StatusBarLyric;
```

Add under application in AndroidManifest.xml
```xml
<meta-data
android:name="XStatusBarLyric"
android:value="true" />
```

# StatusBarAPi

[APIDemo](https://github.com/577fkj/StatusBarApiExample)

[Jitpack](https://www.jitpack.io/#577fkj/MIUIStatusBarLyric/3.34.12v61)

需要在 build.gradle 添加
```
allprojects {
    repositories {
        ....
        maven { url 'https://www.jitpack.io' }
    }
}


dependencies {
    implementation 'com.github.577fkj:MIUIStatusBarLyric:v1.1'
}
```

```java
import StatusbarLyric.API.StatusBarLyric;
```

在AndroidManifest.xml 中的 application 下添加
```xml
<meta-data
android:name="XStatusBarLyric"
android:value="true" />
```