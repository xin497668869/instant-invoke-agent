package com.xin.base;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class GeneralClassAdapter extends ClassVisitor {

    public GeneralClassAdapter() {
        super(ASM7);
    }

    public void setCp(ClassVisitor cv) {
        this.cv = cv;
    }

    public void changeMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
