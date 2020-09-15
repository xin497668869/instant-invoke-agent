package com.xin.replace.pgsql_preparedStatement_class;

import com.sql.SQLUtils;
import com.xin.replace.base.BaseMethodChange;
import com.xin.util.InvokeUtil;
import org.objectweb.asm.MethodVisitor;
import org.postgresql.core.ParameterList;
import org.postgresql.core.Query;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ILOAD;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class SendOneQueryMethodChange extends BaseMethodChange {

    SendOneQueryMethodChange(MethodVisitor mv) {
        super(mv);
    }

    public static void logSql(Query query, ParameterList params, int maxRows,
                              int fetchSize, int flags) {
        try {
//            if (!SettingInstance.isDebuging()) {
//                return;
//            }
            String sql = query.toString(params);
            if (sql.startsWith("SHOW")) {
                System.out.println("sql:[ " + sql + " ]");
            } else {
                String mysql = SQLUtils.format(sql, "postgresql");
                System.out.println("sql:[ " + mysql + " ]");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ILOAD, 5);
        InvokeUtil.invokeStaticMethod(mv, this, "logSql");
    }

}
