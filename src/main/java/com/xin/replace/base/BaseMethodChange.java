package com.xin.replace.base;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class BaseMethodChange extends MethodVisitor {
    public BaseMethodChange(MethodVisitor methodVisitor) {
        super(ASM5, methodVisitor);
    }
}
