package com.xin.replace;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.PreparedStatement;
import com.sql.SQLUtils;
import com.xin.Boot;
import com.xin.base.GeneralClassAdapter;
import com.xin.replace.base.BaseChangeClass;
import com.xin.util.InvokeUtil;
import dnl.utils.text.table.TextTreeTable;
import org.objectweb.asm.MethodVisitor;

import java.lang.instrument.Instrumentation;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASM7;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class MysqlChangeClass extends BaseChangeClass {

    public MysqlChangeClass(Instrumentation instrumentation, ClassLoader classLoader) {
        super(instrumentation, classLoader);
    }

    public static void logSql(Buffer buffer, PreparedStatement preparedStatement) {
        if (!Boot.debuging) {
            return;
        }
        String sql = new String(buffer.getByteBuffer(), 5, buffer.getPosition() - 5);
        System.out.println("sql:[ " + SQLUtils.format(sql, "mysql") + " ]");
        try (Statement statement = preparedStatement.getConnection()
                                                    .createStatement()) {
            ResultSet resultSet = statement.executeQuery("EXPLAIN " + sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            String[] columnNames = new String[metaData.getColumnCount()];
            String[] columnDatas = new String[metaData.getColumnCount()];
            while (resultSet.next()) {
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    columnNames[i - 1] = metaData.getColumnName(i);
                    columnDatas[i - 1] = resultSet.getString(i);
                }
            }
            resultSet.close();
            TextTreeTable textTreeTable = new TextTreeTable(columnNames,
                                                            new String[][]{columnDatas});
            textTreeTable.printTable(System.out, 0);
            System.out.println();

        } catch (SQLException e) {
            e.printStackTrace();
        }
//        try {
//            Class bufferClass = Thread.currentThread()
//                                      .getContextClassLoader()
//                                      .loadClass("com.mysql.jdbc.Buffer");
//            Method getByteBuffer = bufferClass.getMethod("getByteBuffer");
//            Method getPosition = bufferClass.getMethod("getPosition");
//            byte[] bufferContent = (byte[]) getByteBuffer.invoke(buffer);
//            int bufferPosition = (int) getPosition.invoke(buffer);
//            String sql = new String(bufferContent, 5, bufferPosition - 5);
//            System.out.println("sql:[ " + SQLUtils.format(sql, "mysql") + " ]");
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
    }

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

                    return new ChangeMethodAdapter(mv);
                } else {
                    return mv;
                }
            }
        };
    }

    public class ChangeMethodAdapter extends MethodVisitor {
        int i = 0;

        ChangeMethodAdapter(MethodVisitor mv) {
            super(ASM7, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            if (name.contains("writeBytesNoNull")) {
                i++;
                if (i == 6) {
                    System.out.println("sql prin");
                    mv.visitVarInsn(ALOAD, 6);
                    mv.visitVarInsn(ALOAD, 0);
                    InvokeUtil.invokeStaticMethod(mv, MysqlChangeClass.class, "logSql");
                }
            }
        }
    }
}

