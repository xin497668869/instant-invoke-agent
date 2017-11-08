package com.xin.base

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ASM6
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 *
 * @author linxixin@cvte.com
 */


open class GeneralClassAdapter : ClassVisitor(ASM6) {
    fun setCp(cv: ClassVisitor) {
        this.cv = cv
    }

    fun getSlashPackage(): String {
        return this.javaClass.`package`.name.replace(".", "/") + "/"
    }
    fun invokeHandle(mv:MethodVisitor,functionReference: FunctionReference, run: (mv:MethodVisitor) -> Unit) {
        val classQualifiedName = (functionReference.owner as KClass<*>).jvmName.replace(".", "/")
        mv.visitFieldInsn(Opcodes.GETSTATIC, classQualifiedName.substringBefore("$"), classQualifiedName.substringAfter("$"), "L$classQualifiedName;")

        run(mv)

        val signature = functionReference.signature
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, classQualifiedName, signature.substringBefore("("), "(" + signature.substringAfter("("), false)

    }


}
