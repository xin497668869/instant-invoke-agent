package com.xin.replace

import com.xin.base.GeneralClassAdapter
import com.xin.util.getNewClassBytes
import java.lang.instrument.ClassDefinition
import java.lang.instrument.Instrumentation

/**
 *
 * @author linxixin@cvte.com
 */
abstract class BaseChangeClass(private val instrumentation: Instrumentation, private val classLoader: ClassLoader) {

    protected abstract fun getClassName(): String

    protected abstract fun getGeneralClassAdapter(): GeneralClassAdapter

    fun redefineClass() {
        try {
            val classDefinition = ClassDefinition(getLoadedClass(), getChangedClassByte())
            instrumentation.redefineClasses(classDefinition)
            println("可进行热部署测试:${getClassName()}替换成功")
        } catch (e: ClassNotFoundException) {
            println("热部署类注入失败, 无法进行热部署: ${getClassName()} err: ${e.message}")
        }
    }

   private fun getLoadedClass(): Class<*>? {
        return classLoader.loadClass(getClassName())
    }

    private fun getChangedClassByte(): ByteArray {
        return getNewClassBytes(getClassName(), classLoader, getGeneralClassAdapter())
    }

}