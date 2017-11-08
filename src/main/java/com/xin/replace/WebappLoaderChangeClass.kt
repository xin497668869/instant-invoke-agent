package com.xin.replace

import com.xin.base.GeneralClassAdapter
import com.xin.initClass
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.instrument.Instrumentation
import java.net.URLClassLoader
import kotlin.jvm.internal.FunctionReference

/**
 *
 * @author linxixin@cvte.com
 */


class WebappLoaderChangeClass(instrumentation: Instrumentation, classLoader: ClassLoader) : BaseChangeClass(instrumentation, classLoader) {

       companion object Handle {
        fun handleClassLoader(urlClassLoader: URLClassLoader) {
//    val aClass = commonClassLoader.loadClass("org.apache.catalina.loader.WebappClassLoaderBase")
//    val getContextName = aClass.getDeclaredMethod("getContextName")
//    val contextName = getContextName.invoke(urlClassLoader)
            initClass(urlClassLoader)
        }
    }

    override fun getClassName(): String {
        return "org.apache.catalina.loader.WebappLoader"
    }

    override fun getGeneralClassAdapter(): GeneralClassAdapter {
        return object : GeneralClassAdapter() {

            inner class ChangeMethodAdapter(mv: MethodVisitor) : MethodVisitor(Opcodes.ASM6, mv) {
                override fun visitMethodInsn(opcode: Int, owner: String, name: String, desc: String, itf: Boolean) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf)
                    if (opcode == Opcodes.INVOKEINTERFACE && name == "start") {

                        invokeHandle(mv, Handle::handleClassLoader as FunctionReference) { mv ->

                            mv.visitVarInsn(Opcodes.ALOAD, 0)
                            mv.visitFieldInsn(Opcodes.GETFIELD, "org/apache/catalina/loader/WebappLoader", "classLoader", "Lorg/apache/catalina/loader/WebappClassLoaderBase;")
                        }
                    }
                }
            }

            override fun visitMethod(access: Int, name: String?, desc: String?,
                                     signature: String?, exceptions: Array<String>?): MethodVisitor {
                val mv = cv.visitMethod(access, name, desc, signature, exceptions)

                // 当是sayName方法是做对应的修改
                return if (name == "startInternal" && desc == "()V") {
                    ChangeMethodAdapter(mv)
                } else {
                    mv
                }
            }
        }
    }


}
