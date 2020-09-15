package com.xin.base;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class GeneralClassAdapter extends ClassVisitor {

    public GeneralClassAdapter() {
        super(ASM5);
    }

    public void setCp(ClassVisitor cv) {
        this.cv = cv;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
