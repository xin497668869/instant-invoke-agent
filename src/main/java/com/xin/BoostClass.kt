package com.xin

import com.xin.StoreInfo.instrumentation
import com.xin.StoreInfo.port
import com.xin.replace.BootstrapChangeClass
import com.xin.replace.ComponentScanAnnotationParserChangeClass
import com.xin.replace.MySqlChangeClass
import com.xin.replace.MySqlConfigChangeClass
import com.xin.util.getClassByte
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
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

    } catch (e: Exception) {
        System.err.println("MySqlConfigChangeClass替换失败 ")
        e.printStackTrace()
    }

    try {
        MySqlChangeClass(instrumentation, classLoader).redefineClass()


    } catch (e: Exception) {
        System.err.println("MySqlChangeClass替换失败 ")
        e.printStackTrace()
    }

    try {
        println("准备尝试")
//        val classReader = ClassReader(getClassByte(ClassLoader.getSystemClassLoader(), "com.xin.base.controller.TestController"))
//        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_MAXS)
//        classWriter.visit(52,0,"com/xin/base/controller/TestController",null,"java/lang/Object", arrayOf("org/springframework/context/ApplicationContextAware"))
//        val toByteArray = classWriter.toByteArray()
//
//
//        val method = ClassLoader::class.java.getDeclaredMethod("defineClass", String::class.java, ByteArray::class.java, Int::class.java, Int::class.java)
//
//        method.isAccessible=true
//        val invoke = method.invoke(classLoader, "com.xin.base.controller.TestController", toByteArray, 0, toByteArray.size)
//        println("哈哈, 强制执行")
        ComponentScanAnnotationParserChangeClass(instrumentation, classLoader).redefineClass()

    } catch (e: Exception) {
        System.err.println("ComponentScanAnnotationParserChangeClass替换失败 ")
        e.printStackTrace()
    }


}

fun main(args: Array<String>) {
    val classReader = ClassReader(getClassByte(ClassLoader.getSystemClassLoader(), "com.xin.base.controller.TestController"))
    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_MAXS)

    println()
}

