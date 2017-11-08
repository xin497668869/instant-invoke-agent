package com.xin

import com.xin.StoreInfo.instrumentation
import com.xin.StoreInfo.port
import com.xin.replace.BootstrapChangeClass
import com.xin.replace.ComponentScanAnnotationParserChangeClass
import com.xin.replace.MySqlChangeClass
import com.xin.replace.MySqlConfigChangeClass
import java.lang.instrument.Instrumentation


object StoreInfo {
    lateinit var commonClassLoader: ClassLoader
    lateinit var instrumentation: Instrumentation
    var port: Int = -1
}

/**
 *
 * @author linxixin@cvte.com
 */

fun premain(agentArg: String, instrumentation: Instrumentation) {
    println("插件参数是: $agentArg")
    StoreInfo.instrumentation = instrumentation

    try {
        port = agentArg.toInt()
    } catch (e: Exception) {
        e.printStackTrace()
        return
    }

    /**
     * 判断是否用tomcat启动
     */
    try {
        ClassLoader.getSystemClassLoader().loadClass("org.springframework.core.env.Environment")
        initClass(ClassLoader.getSystemClassLoader())
        println("非tomcat启动")

    } catch (e: ClassNotFoundException) {
        println("用tomcat启动")
        BootstrapChangeClass(instrumentation, ClassLoader.getSystemClassLoader()).redefineClass()
    }
}

/**
 * 主要用途替换新增用途的
 *
 * */
fun initClass(classLoader: ClassLoader) {

    println("准备替换功能模块")
    try {
        MySqlConfigChangeClass(instrumentation, classLoader).redefineClass()

        MySqlChangeClass(instrumentation, classLoader).redefineClass()

        ComponentScanAnnotationParserChangeClass(instrumentation, classLoader).redefineClass()

    } catch (e: Exception) {
        System.err.println("mysql 的PreparedStatement替换失败 ")
        e.printStackTrace()
    }

//    try {
//        componentScanAnnotationParserChangeClass(classLoader)
//
//    } catch (e: Exception) {
//        System.err.println("componentScanAnnotationParser替换失败")
//    }
//
//    try {
//        mySqlConfigChangeClass(classLoader)
//    } catch (e: Exception) {
//        System.err.println("ConnectionImpl替换失败")
//    }
}

