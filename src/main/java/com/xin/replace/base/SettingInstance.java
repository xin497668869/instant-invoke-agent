package com.xin.replace.base;

import java.lang.instrument.Instrumentation;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class SettingInstance {
    private static Instrumentation instrumentation;
    private static Integer port;
    private static ClassLoader classLoader;
    private static boolean debuging;

    public static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public static void setInstrumentation(Instrumentation instrumentation) {
        SettingInstance.instrumentation = instrumentation;
    }

    public static Integer getPort() {
        return port;
    }

    public static void setPort(Integer port) {
        SettingInstance.port = port;
    }

    public static ClassLoader getClassLoader() {
        return classLoader;
    }

    public static void setClassLoader(ClassLoader classLoader) {
        SettingInstance.classLoader = classLoader;
    }

    public static boolean isDebuging() {
        return debuging;
    }

    public static void setDebuging(boolean debuging) {
        SettingInstance.debuging = debuging;
    }
}
