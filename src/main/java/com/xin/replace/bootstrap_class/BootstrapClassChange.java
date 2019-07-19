package com.xin.replace.bootstrap_class;

import com.xin.base.GeneralClassAdapter;
import com.xin.replace.base.BaseClassChange;
import org.objectweb.asm.MethodVisitor;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class BootstrapClassChange extends BaseClassChange {

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
                    return new CreateClassLoaderMethodChangeChange(mv);
                } else {
                    return mv;
                }
            }
        };

    }

}
