package com.xin.replace.base;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class BaseMethodChange extends MethodVisitor {
    public BaseMethodChange(MethodVisitor methodVisitor) {
        super(ASM7, methodVisitor);
    }
}
