package com.xin.replace.mysql_preparedStatement_class;

import com.xin.base.GeneralClassAdapter;
import com.xin.replace.base.BaseClassChange;
import org.objectweb.asm.MethodVisitor;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class MysqlClassChange extends BaseClassChange {

    @Override
    protected String getClassName() {
        return "com.mysql.jdbc.PreparedStatement";
    }

    @Override
    protected GeneralClassAdapter getGeneralClassAdapter() {
        return new GeneralClassAdapter() {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                // 当是sayName方法是做对应的修改
                if (name.equals("fillSendPacket")
                        && desc.equals("([[B[Ljava/io/InputStream;[Z[I)Lcom/mysql/jdbc/Buffer;")) {

                    return new FillSendPacketMethodChange(mv);
                } else {
                    return mv;
                }
            }
        };
    }

}

