package com.xin.util;

import com.xin.base.GeneralClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class ClassByteUtil {

    public static byte[] getNewClassBytes(String className, ClassLoader classLoader, GeneralClassAdapter method) {

        ClassReader classReader = new ClassReader(getClassByte(classLoader, className));
        ClassWriter classWriter = new ClassWriter(classReader, COMPUTE_MAXS);
        method.setCp(classWriter);
        classReader.accept(method, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    public static byte[] getClassByte(ClassLoader classLoader, String name) {
        try (InputStream resourceAsStream = classLoader.getResourceAsStream(name.replace('.', '/') + ".class")) {

            if (resourceAsStream == null) {
                throw new RuntimeException(name + " 类不存在无法进行注入");
            }
            return readClass(resourceAsStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] readClass(InputStream resourceAsStream) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream)) {
            int n;
            byte[] buffer = new byte[1024 * 2];
            while (true) {
                if (-1 == (n = bufferedInputStream.read(buffer))) {
                    break;
                }
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("没读取到文件");
        }
    }

}
