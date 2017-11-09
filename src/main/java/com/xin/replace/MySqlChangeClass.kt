package com.xin.replace

import com.xin.base.GeneralClassAdapter
import java.lang.instrument.Instrumentation
import kotlin.jvm.internal.FunctionReference


/**
 * 修改mysql代码, 用于显示具体sql
 */
class MySqlChangeClass(instrumentation: Instrumentation, classLoader: ClassLoader) : BaseChangeClass(instrumentation, classLoader) {

  companion object Handle {
        fun logSql(buffer: Object?, preparedStatement: Object?) {
            val bufferClass = Thread.currentThread().contextClassLoader.loadClass("com.mysql.jdbc.Buffer")
            val getByteBuffer = bufferClass.getMethod("getByteBuffer")
            val getPosition = bufferClass.getMethod("getPosition")
            val bufferContent = getByteBuffer.invoke(buffer) as ByteArray
            val bufferPosition = getPosition.invoke(buffer) as Int
            val sql = String(bufferContent, 5, bufferPosition - 5)
            println("sql: $sql")
        }
    }

    override fun getClassName(): String {
        return "com.mysql.jdbc.PreparedStatement"
    }


    override fun getGeneralClassAdapter(): GeneralClassAdapter {

        return object : GeneralClassAdapter() {

            override fun visitMethod(access: Int, name: String?, desc: String?,
                                     signature: String?, exceptions: Array<String>?)
                    : org.objectweb.asm.MethodVisitor? {
                val mv = cv!!.visitMethod(access, name, desc, signature, exceptions)

                // 当是sayName方法是做对应的修改
                return if (name == "fillSendPacket" && desc == "([[B[Ljava/io/InputStream;[Z[I)Lcom/mysql/jdbc/Buffer;") {
                    ChangeMethodAdapter(mv)
                } else {
                    mv
                }
            }

            // 定义一个自己的方法访问类
            internal inner class ChangeMethodAdapter(mv: org.objectweb.asm.MethodVisitor) : org.objectweb.asm.MethodVisitor(org.objectweb.asm.Opcodes.ASM4, mv) {
                var arraylength = false
                var aaload = false

                override fun visitMethodInsn(opcode: Int, owner: String, name: String, desc: String, itf: Boolean) {

                    super.visitMethodInsn(opcode, owner, name, desc, itf)
                    if (name.contains("writeBytesNoNull") && aaload && arraylength) {

                        invokeHandle(mv, Handle::logSql as FunctionReference) { mv ->
                            mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 6)
                            mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 0)
                        }
                    }
                }

                override fun visitInsn(opcode: Int) {

                    if (opcode == org.objectweb.asm.Opcodes.AALOAD) {
                        aaload = true
                    } else if (aaload && opcode == org.objectweb.asm.Opcodes.ARRAYLENGTH) {
                        arraylength = true
                    } else {
                        arraylength = false
                        aaload = arraylength
                    }
                    super.visitInsn(opcode)

                }
            }
        }
    }

}
