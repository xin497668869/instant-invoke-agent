package com.xin.replace.pgsql_preparedStatement_class;

import com.xin.base.GeneralClassAdapter;
import com.xin.replace.base.BaseClassChange;
import org.objectweb.asm.MethodVisitor;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class PgsqlClassChange extends BaseClassChange {

    @Override
    protected void logWhenRedefine(boolean success) {
        if (success) {
            System.out.println("注入 pgsql 成功, 可打印 pgsql 的完整 sql ");
        }
    }

    @Override
    protected String getClassName() {
        return "org.postgresql.core.v3.QueryExecutorImpl";
    }

    @Override
    protected GeneralClassAdapter getGeneralClassAdapter() {
        return new GeneralClassAdapter() {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                // 当是sayName方法是做对应的修改
                if (name.equals("sendOneQuery")
                        && desc.equals("(Lorg/postgresql/core/v3/SimpleQuery;Lorg/postgresql/core/v3/SimpleParameterList;III)V")) {

                    return new SendOneQueryMethodChange(mv);
                } else {
                    return mv;
                }
            }
        };
    }

}

