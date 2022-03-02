package android.content.pm;

public class PackageInfo {

    public String overlayTarget;

    public int versionCode;

    public String versionName;

    public ApplicationInfo applicationInfo;

    public String packageName;

    public long getLongVersionCode() {
        throw new RuntimeException("Stub!");
    }
}
