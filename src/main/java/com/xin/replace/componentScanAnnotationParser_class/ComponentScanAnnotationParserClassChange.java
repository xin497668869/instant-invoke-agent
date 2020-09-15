package com.xin.replace.componentScanAnnotationParser_class;

import com.xin.base.GeneralClassAdapter;
import com.xin.replace.base.BaseClassChange;
import org.objectweb.asm.MethodVisitor;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class ComponentScanAnnotationParserClassChange extends BaseClassChange {

    @Override
    protected void logWhenRedefine(boolean success) {
        if (success) {
            System.out.println("快速调试 controller 注入成功, 可启动快速调试功能");
        }
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
                    return new ParseMethodChangeChange(mv);
                } else {
                    return mv;
                }
            }

        };
    }

}
