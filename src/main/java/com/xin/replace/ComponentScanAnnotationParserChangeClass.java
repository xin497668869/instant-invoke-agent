package com.xin.replace;

import com.xin.base.GeneralClassAdapter;
import com.xin.replace.base.BaseChangeClass;
import org.objectweb.asm.MethodVisitor;

import java.lang.instrument.Instrumentation;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.POP;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class ComponentScanAnnotationParserChangeClass extends BaseChangeClass {

    public ComponentScanAnnotationParserChangeClass(Instrumentation instrumentation, ClassLoader classLoader) {
        super(instrumentation, classLoader);
    }

    @Override
    protected String getClassName() {
        return "org.springframework.context.annotation.ComponentScanAnnotationParser";
    }

    @Override
    protected GeneralClassAdapter getGeneralClassAdapter() {
        return new GeneralClassAdapter() {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                // 当是sayName方法是做对应的修改
                if ("parse".equals(name)
                        && "(Lorg/springframework/core/annotation/AnnotationAttributes;Ljava/lang/String;)Ljava/util/Set;".equals(
                        descriptor)) {
                    return new ChangeMethodAdapter(mv);
                } else {
                    return mv;
                }
            }

        };
    }

    static class ChangeMethodAdapter extends MethodVisitor {

        int time = 0;

        public ChangeMethodAdapter(MethodVisitor methodVisitor) {
            super(ASM7, methodVisitor);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            super.visitVarInsn(opcode, var);
            if (time > 1 && opcode == ALOAD && var == 3) {
                super.visitVarInsn(ALOAD, 8);
                super.visitLdcInsn("com.xin.base.controller");
                super.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "add", "(Ljava/lang/Object;)Z", true);
                super.visitInsn(POP);
            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            if ("org/springframework/context/annotation/ClassPathBeanDefinitionScanner".equals(owner)
                    && "addExcludeFilter".equals(name)) {
                time++;
            }
        }
    }
}
