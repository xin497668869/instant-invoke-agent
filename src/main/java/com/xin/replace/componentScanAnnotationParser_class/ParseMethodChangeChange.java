package com.xin.replace.componentScanAnnotationParser_class;

import com.xin.replace.base.BaseMethodChange;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.POP;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class ParseMethodChangeChange extends BaseMethodChange {

    int time = 0;

    public ParseMethodChangeChange(MethodVisitor methodVisitor) {
        super(methodVisitor);
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
