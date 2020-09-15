package com.xin;

import com.xin.replace.base.SettingInstance;
import com.xin.replace.bootstrap_class.BootstrapClassChange;
import com.xin.replace.componentScanAnnotationParser_class.ComponentScanAnnotationParserClassChange;
import com.xin.replace.mysql_preparedStatement_class.MysqlClassChange;
import com.xin.replace.pgsql_preparedStatement_class.PgsqlClassChange;
import com.xin.util.ClassRedefineUtil;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

import static com.xin.util.ClassByteUtil.getClassByte;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class Boot {


    /**
     * 主要用途替换新增用途的
     */
    public static void initClass(ClassLoader classLoader) {
        ClassRedefineUtil.initClass(new MysqlClassChange());
        ClassRedefineUtil.initClass(new PgsqlClassChange());

        forceToloadClass(classLoader);

        ClassRedefineUtil.initClass(new ComponentScanAnnotationParserClassChange());
    }

    /**
     * 强制用tomcat项目里面的classLoader去加载TestController,
     * 否则TestController会被appClassloader加载,会出现TestController的父类无法找到的情况
     */
    private static void forceToloadClass(ClassLoader classLoader) {
        try {
            Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            method.setAccessible(true);
            String testControllerName = "com.xin.base.controller.TTTestController$$T";
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
        System.out.println("插件参数是: " + agentArg);
        Integer port = Integer.valueOf(agentArg);
        SettingInstance.setInstrumentation(instrumentation);
        SettingInstance.setPort(port);
        SettingInstance.setDebuging(false);
        SettingInstance.setClassLoader(ClassLoader.getSystemClassLoader());

        /**
         * 判断是否用tomcat启动
         */
        try {
            ClassLoader.getSystemClassLoader()
                       .loadClass("org.springframework.core.env.Environment");
            initClass(ClassLoader.getSystemClassLoader());

        } catch (Exception e) {
            System.out.println("用tomcat启动");
            ClassRedefineUtil.initClass(new BootstrapClassChange());
        }
    }

}
