package kr.co.kkensu.integrationmap.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import kr.co.kkensu.integrationmap.BuildConfig;

/**
 * 현재 빌드 설정이 무엇인지 알아내기 위한 util
 */
public class BuildConfigUtil {

    @SuppressWarnings({"ConstantConditions", "PointlessBooleanExpression"})
    public static boolean isDebuggable(Context context) {
        return 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
    }

    public static boolean isRelease() {
        return BuildConfig.BUILD_TYPE.equals("release") || BuildConfig.BUILD_TYPE.equals("releaseDebug");
    }

    public static boolean isGAAvailable() {
        return isRelease() || isBeta();
    }

    public static boolean isBeta() {
        return BuildConfig.BUILD_TYPE.equals("beta");
    }

    public static boolean isEncrypt() {
        return isRelease() || isBeta();
    }
}
