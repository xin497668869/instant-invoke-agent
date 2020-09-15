package com.xin.replace.webappLoader_class;

import com.xin.base.GeneralClassAdapter;
import com.xin.replace.base.BaseClassChange;
import org.objectweb.asm.MethodVisitor;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class WebappLoaderClassChange extends BaseClassChange {

    @Override
    protected void logWhenRedefine(boolean success) {
        if (success) {
            System.out.println("注入 WebappLocader 成功");
        }
    }

    @Override
    protected String getClassName() {
        return "org.apache.catalina.loader.WebappLoader";
    }

    @Override
    protected GeneralClassAdapter getGeneralClassAdapter() {
        return new GeneralClassAdapter() {

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                // 当是sayName方法是做对应的修改
                if ("startInternal".equals(name) && "()V".equals(descriptor)) {
                    return new StartMethodChange(mv);
                } else {
                    return mv;
                }
            }
        };
    }


}

