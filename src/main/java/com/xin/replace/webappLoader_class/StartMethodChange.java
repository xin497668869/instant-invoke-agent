package com.xin.replace.webappLoader_class;

import com.xin.replace.base.BaseMethodChange;
import com.xin.util.InvokeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static com.xin.Boot.initClass;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class StartMethodChange extends BaseMethodChange {

    public StartMethodChange(MethodVisitor methodVisitor) {
        super(methodVisitor);
    }

    public static void handle(ClassLoader urlClassLoader) {
        try {
            urlClassLoader.loadClass("org.springframework.context.annotation.ComponentScanAnnotationParser");
            initClass(urlClassLoader);
        } catch (Exception e) {
            System.out.println("非spring项目, 不注入 $urlClassLoader");
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if ("start".equals(name) && "()V".equals(descriptor)) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, "org/apache/catalina/loader/WebappLoader",
                              "classLoader",
                              "Lorg/apache/catalina/loader/WebappClassLoaderBase;");

            InvokeUtil.invokeStaticMethod(mv, WebappLoaderClassChange.class, "handle");
        }
    }

}
