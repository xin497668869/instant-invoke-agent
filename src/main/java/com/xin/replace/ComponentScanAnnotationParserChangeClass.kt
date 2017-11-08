package com.xin.replace

/**
 *
 * @author linxixin@cvte.com
 */

import com.xin.base.GeneralClassAdapter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import java.lang.instrument.Instrumentation

/**
 * @author linxixin@cvte.com
 * @version 1.0
 * @description
 */

class ComponentScanAnnotationParserChangeClass(instrumentation: Instrumentation, classLoader: ClassLoader) : BaseChangeClass(instrumentation, classLoader) {

    override fun getClassName(): String {
        return "org.springframework.context.annotation.ComponentScanAnnotationParser"
    }

    override fun getGeneralClassAdapter(): GeneralClassAdapter {
        return object : GeneralClassAdapter() {

            override fun visitMethod(access: Int, name: String?, desc: String?,
                                     signature: String?, exceptions: Array<String>?): MethodVisitor {
                val mv = cv.visitMethod(access, name, desc, signature, exceptions)

                // 当是sayName方法是做对应的修改
                return if (name == "parse" && desc == "(Lorg/springframework/core/annotation/AnnotationAttributes;Ljava/lang/String;)Ljava/util/Set;") {
                    ChangeMethodAdapter(mv)
                } else {
                    mv
                }
            }

            // 定义一个自己的方法访问类
            inner class ChangeMethodAdapter(mv: MethodVisitor) : MethodVisitor(ASM6, mv) {
                var time = 0
                override fun visitMethodInsn(opcode: Int, owner: String, name: String, desc: String, itf: Boolean) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf)
                    if (owner == "org/springframework/context/annotation/ClassPathBeanDefinitionScanner" && name == "addExcludeFilter") {
                        time++
                    }
                }

                override fun visitVarInsn(opcode: Int, `var`: Int) {
                    super.visitVarInsn(opcode, `var`)
                    if (time > 1 && opcode == ALOAD && `var` == 3) {
                        super.visitVarInsn(ALOAD, 8)
                        super.visitLdcInsn("com.xin.base")
                        super.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z", true)
                        super.visitInsn(POP)
                    }

                }

            }
        }
    }
}

