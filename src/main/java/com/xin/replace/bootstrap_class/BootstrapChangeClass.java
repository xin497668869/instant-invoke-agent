package com.xin.replace.bootstrap_class;

import com.xin.base.GeneralClassAdapter;
import com.xin.replace.base.BaseChangeClass;
import com.xin.replace.base.BasicChangeMethod;
import com.xin.util.InvokeUtil;
import org.objectweb.asm.MethodVisitor;

import java.lang.instrument.Instrumentation;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class BootstrapChangeClass extends BaseChangeClass {
    public BootstrapChangeClass(Instrumentation instrumentation, ClassLoader classLoader) {
        super(instrumentation, classLoader);
    }

    @Override
    protected String getClassName() {
        return "org.apache.catalina.startup.Bootstrap";
    }

    @Override
    protected GeneralClassAdapter getGeneralClassAdapter() {
        return new GeneralClassAdapter() {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

                // 当是sayName方法是做对应的修改
                if ("createClassLoader".equals(name)
                        && "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/ClassLoader;".equals(
                        descriptor)) {
                    System.out.println("!!@");

                    return new ChangeMethodAdapter(mv);
                } else {
                    return mv;
                }
            }
        };

    }

    public static class ChangeMethodAdapter extends BasicChangeMethod {

        public ChangeMethodAdapter(MethodVisitor methodVisitor) {
            super(methodVisitor);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == ARETURN) {
                mv.visitVarInsn(ASTORE, 6);
                mv.visitVarInsn(ALOAD, 6);
                mv.visitVarInsn(ALOAD, 2);
                InvokeUtil.invokeStaticMethod22(mv, BootstrapChangeClass.class, Handler::handle2);

                mv.visitVarInsn(ALOAD, 6);
            }
            mv.visitInsn(opcode);
        }

    }
}
