package com.xin.replace

import com.xin.base.GeneralClassAdapter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.instrument.Instrumentation
import java.util.*
import kotlin.jvm.internal.FunctionReference

/**
 *
 * 修改mysql代码, 用于显示具体sql
 * @author linxixin@cvte.com
 */


class MySqlConfigChangeClass(instrumentation: Instrumentation, classLoader: ClassLoader) : BaseChangeClass(instrumentation, classLoader) {

    companion object Handle {
        fun printMysqlInfo(info: Properties?, url: String?) {
//            println("instant-invoke:数据库配置信息 $url $info")
        }
    }

    override fun getClassName(): String {
        return "com.mysql.jdbc.ConnectionImpl"
    }

    override fun getGeneralClassAdapter(): GeneralClassAdapter {
        return object : GeneralClassAdapter() {

            override fun visitMethod(access: Int, name: String?, desc: String?,
                                     signature: String?, exceptions: Array<String>?): MethodVisitor {
                val mv = cv.visitMethod(access, name, desc, signature, exceptions)
//            println(MySqlConfigChangeClass)
                // 当是sayName方法是做对应的修改
                return if (name == "getInstance" && desc == "(Ljava/lang/String;ILjava/util/Properties;Ljava/lang/String;Ljava/lang/String;)Lcom/mysql/jdbc/Connection;") {
                    ChangeMethodAdapter(mv)
                } else {
                    mv
                }
            }

            // 定义一个自己的方法访问类
            internal inner class ChangeMethodAdapter(mv: MethodVisitor) : MethodVisitor(Opcodes.ASM6, mv) {

                override fun visitCode() {
                    super.visitCode()
                    invokeHandle(mv, Handle::printMysqlInfo as FunctionReference) { mv ->
                        mv.visitVarInsn(Opcodes.ALOAD, 2)
                        mv.visitVarInsn(Opcodes.ALOAD, 4)
                    }
                }
            }
        }
    }

}
