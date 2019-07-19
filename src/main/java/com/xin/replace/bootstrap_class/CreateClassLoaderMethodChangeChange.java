package com.xin.replace.bootstrap_class;

import com.xin.replace.base.BaseMethodChange;
import com.xin.replace.base.SettingInstance;
import com.xin.replace.webappLoader_class.WebappLoaderClassChange;
import com.xin.util.ClassRedefineUtil;
import com.xin.util.InvokeUtil;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class CreateClassLoaderMethodChangeChange extends BaseMethodChange {

    public CreateClassLoaderMethodChangeChange(MethodVisitor methodVisitor) {
        super(methodVisitor);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == ARETURN) {
            mv.visitVarInsn(ASTORE, 6);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitVarInsn(ALOAD, 2);
            InvokeUtil.invokeStaticMethod(mv, this, "handle");

            mv.visitVarInsn(ALOAD, 6);
        }
        mv.visitInsn(opcode);
    }

    public void handle(ClassLoader classLoader, ClassLoader parentClassLoader) {
        SettingInstance.setClassLoader(classLoader);

        Thread.currentThread()
              .setContextClassLoader(classLoader);
        ClassRedefineUtil.initClass(new WebappLoaderClassChange());
    }
}
