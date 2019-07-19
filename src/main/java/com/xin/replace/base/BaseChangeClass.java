package com.xin.replace.base;

import com.xin.base.GeneralClassAdapter;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

import static com.xin.util.ClassByteUtil.getNewClassBytes;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public abstract class BaseChangeClass {
    private Instrumentation instrumentation;
    private ClassLoader classLoader;

    public BaseChangeClass(Instrumentation instrumentation, ClassLoader classLoader) {
        this.instrumentation = instrumentation;
        this.classLoader = classLoader;
    }

    public void redefineClass() {
        try {
            byte[] newClassBytes = getNewClassBytes(getClassName(), classLoader, getGeneralClassAdapter());

            ClassDefinition classDefinition = new ClassDefinition(getLoadedClass(),
                                                                  newClassBytes);
            instrumentation.redefineClasses(classDefinition);
            System.out.println("可进行热部署测试:" + getClassName() + "替换成功");
        } catch (Exception e) {
            System.out.println("热部署类注入失败, 无法进行热部署: ${getClassName()} err: ${e.message}");
        }
    }

    protected abstract String getClassName();

    protected abstract GeneralClassAdapter getGeneralClassAdapter();

    private Class getLoadedClass() throws ClassNotFoundException {
        try {
            return classLoader.loadClass(getClassName());
        } catch (ClassNotFoundException e) {
            throw e;
        }
    }

}
