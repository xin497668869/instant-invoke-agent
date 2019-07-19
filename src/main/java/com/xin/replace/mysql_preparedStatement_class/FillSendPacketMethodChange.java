package com.xin.replace.mysql_preparedStatement_class;

import com.mysql.jdbc.Buffer;
import com.mysql.jdbc.PreparedStatement;
import com.sql.SQLUtils;
import com.xin.replace.base.BaseMethodChange;
import com.xin.replace.base.SettingInstance;
import com.xin.util.InvokeUtil;
import com.xin.util.table.TextTable;
import org.objectweb.asm.MethodVisitor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import static org.objectweb.asm.Opcodes.ALOAD;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class FillSendPacketMethodChange extends BaseMethodChange {
    private int i = 0;

    FillSendPacketMethodChange(MethodVisitor mv) {
        super(mv);
    }

    public static void logSql(Buffer buffer, PreparedStatement preparedStatement) {
        if (!SettingInstance.isDebuging()) {
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
            TextTable textTreeTable = new TextTable(columnNames,
                                                    new String[][]{columnDatas});
            textTreeTable.printTable(System.out, 0);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if (name.contains("writeBytesNoNull")) {
            i++;
            if (i == 6) {
                mv.visitVarInsn(ALOAD, 6);
                mv.visitVarInsn(ALOAD, 0);
                InvokeUtil.invokeStaticMethod(mv, this, "logSql");
            }
        }
    }
}
