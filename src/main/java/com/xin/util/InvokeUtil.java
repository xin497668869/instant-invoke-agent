package com.xin.util;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class InvokeUtil {

    public static Method getMethodByName(Class cls, String methodName) {
        for (Method declaredMethod : cls.getDeclaredMethods()) {
            if (declaredMethod.getName()
                              .equals(methodName)) {
                return declaredMethod;
            }
        }
        return null;
    }

    public static void invokeStaticMethod(MethodVisitor mv, Object clsObject,
                                          String methodName) {
        invokeStaticMethod(mv, clsObject.getClass(), methodName);
    }

    public static void invokeStaticMethod(MethodVisitor mv, Class cls,
                                          String methodName) {
        Method methodByName = getMethodByName(cls, methodName);
        if (methodByName == null) {
            throw new RuntimeException("注入失败, 没找到方法 " + cls + "  " + methodName);
        }

        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                           cls.getName()
                              .replace(".", "/"),
                           methodName,
                           Type.getMethodDescriptor(methodByName),
                           false);
    }
}
