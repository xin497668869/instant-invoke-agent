package com.xin;

import com.xin.replace.ComponentScanAnnotationParserChangeClass;
import com.xin.replace.MysqlChangeClass;
import com.xin.replace.bootstrap_class.BootstrapChangeClass;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

import static com.xin.util.ClassByteUtil.getClassByte;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class Boot {

    public static Instrumentation instrumentation;
    public static Integer port;
    public static ClassLoader commonClassLoader;
    public static boolean debuging;

    /**
     * 主要用途替换新增用途的
     */
    public static void initClass(ClassLoader classLoader) {

//        System.out.println("准备替换功能模块asdfasdf");
//        try {
//            new MySqlConfigChangeClass(instrumentation, classLoader).redefineClass();
//
//        } catch (Exception e) {
//            System.err.println("MySqlConfigChangeClass替换失败 ");
//            e.printStackTrace();
//        }

        try {
            new MysqlChangeClass(instrumentation, classLoader).redefineClass();

        } catch (Exception e) {
            System.err.println("MySqlChangeClass替换失败 ");
            e.printStackTrace();
        }

        try {
            forceToloadClass(classLoader);
            new ComponentScanAnnotationParserChangeClass(instrumentation, classLoader).redefineClass();

        } catch (Exception e) {
            System.err.println("ComponentScanAnnotationParserChangeClass替换失败 ");
            e.printStackTrace();
        }
    }

    /**
     * 强制用tomcat项目里面的classLoader去加载TestController,
     * 否则TestController会被appClassloader加载,会出现TestController的父类无法找到的情况
     */
    private static void forceToloadClass(ClassLoader classLoader) {
        try {
            Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            method.setAccessible(true);
            String testControllerName = "com.xin.base.controller.TestController$$";
            byte[] testControllerBytes = getClassByte(ClassLoader.getSystemClassLoader(), testControllerName);
            method.invoke(classLoader, testControllerName, testControllerBytes, 0, testControllerBytes.length);

            String httpServerName = "com.xin.base.controller.HttpServer$$";
            byte[] httpServerBytes = getClassByte(ClassLoader.getSystemClassLoader(), httpServerName);
            method.invoke(classLoader, httpServerName, httpServerBytes, 0, httpServerBytes.length);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author linxixin@cvte.com
     */

    public static void premain(String agentArg, Instrumentation instrumentation) {
        System.out.println("!!!插件参数是: $agentArg");
        Integer port = Integer.valueOf(agentArg);
        Boot.instrumentation = instrumentation;
        Boot.port = port;

        /**
         * 判断是否用tomcat启动
         */
        try {
            ClassLoader.getSystemClassLoader()
                       .loadClass("org.springframework.core.env.Environment");
            initClass(ClassLoader.getSystemClassLoader());
            System.out.println("非tomcat启动");

        } catch (Exception e) {
            System.out.println("用tomcat启动");
            new BootstrapChangeClass(instrumentation, ClassLoader.getSystemClassLoader()).redefineClass();
        }
    }

}
