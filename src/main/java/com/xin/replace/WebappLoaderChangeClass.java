package com.xin.replace;

import com.xin.base.GeneralClassAdapter;
import com.xin.replace.base.BaseChangeClass;
import com.xin.util.InvokeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.Instrumentation;

import static com.xin.Boot.initClass;
import static org.objectweb.asm.Opcodes.ASM7;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class WebappLoaderChangeClass extends BaseChangeClass {

    public WebappLoaderChangeClass(Instrumentation instrumentation, ClassLoader classLoader) {
        super(instrumentation, classLoader);
    }

    public static void handle(ClassLoader urlClassLoader) {
        try {
            System.out.println("@@@@@@@@@@ init");
            urlClassLoader.loadClass("org.springframework.context.annotation.ComponentScanAnnotationParser");
            initClass(urlClassLoader);
        } catch (Exception e) {
            System.out.println("非spring项目, 不注入 $urlClassLoader");
        }
    }

    @Override
    protected String getClassName() {
        return "org.apache.catalina.loader.WebappLoader";
    }

    @Override
    protected GeneralClassAdapter getGeneralClassAdapter() {
        System.out.println("webapp 准备构造");
        return new GeneralClassAdapter() {

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                // 当是sayName方法是做对应的修改
                if ("startInternal".equals(name) && "()V".equals(descriptor)) {
                    System.out.println("找到webap方法");
                    return new ChangeMethodAdapter(mv);
                } else {
                    return mv;
                }
            }
        };
    }

    public static class ChangeMethodAdapter extends MethodVisitor {
        public ChangeMethodAdapter(MethodVisitor methodVisitor) {
            super(ASM7, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            if ("start".equals(name) && "()V".equals(descriptor)) {
                System.out.println("找到start方法");
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitFieldInsn(Opcodes.GETFIELD, "org/apache/catalina/loader/WebappLoader",
                                  "classLoader",
                                  "Lorg/apache/catalina/loader/WebappClassLoaderBase;");

                InvokeUtil.invokeStaticMethod(mv, WebappLoaderChangeClass.class, "handle");

//                mv.visitVarInsn(Opcodes.ALOAD, 0);
            }
        }

    }
}

