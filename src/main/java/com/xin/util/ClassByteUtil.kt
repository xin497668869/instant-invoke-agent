package com.xin.util

import com.xin.base.GeneralClassAdapter
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import vo.BaseException
import java.io.IOException
import java.io.InputStream

/**
 *
 * @author linxixin@cvte.com
 */

fun getNewClassBytes(className: String, classLoader: ClassLoader, method: GeneralClassAdapter): ByteArray {
    val classReader = ClassReader(getClassByte(classLoader, className))
    val classWriter = ClassWriter(classReader, COMPUTE_MAXS or COMPUTE_MAXS)
    method.setCp(classWriter)
    classReader.accept(method, ClassReader.EXPAND_FRAMES)
    return classWriter.toByteArray()
}

fun getClassByte(classLoader: ClassLoader, name: String): ByteArray {
    try {
        val resourceAsStream = classLoader.getResourceAsStream(name.replace('.', '/') + ".class")
                ?: throw BaseException(name + " 类不存在无法进行注入")
        return readClass(
                resourceAsStream, true)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return ByteArray(0)
}

fun readClass(inputStream: InputStream, close: Boolean): ByteArray {
    try {
        var b = ByteArray(inputStream.available())
        var len = 0
        while (true) {
            val n = inputStream.read(b, len, b.size - len)
            if (n == -1) {
                if (len < b.size) {
                    val c = ByteArray(len)
                    System.arraycopy(b, 0, c, 0, len)
                    b = c
                }
                return b
            }
            len += n
            if (len == b.size) {
                val last = inputStream.read()
                if (last < 0) {
                    return b
                }
                val c = ByteArray(b.size + 1000)
                System.arraycopy(b, 0, c, 0, len)
                c[len++] = last.toByte()
                b = c
            }
        }
    } finally {
        if (close) {
            inputStream.close()
        }
    }
}
