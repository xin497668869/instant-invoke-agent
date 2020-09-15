package com.xin.replace.base;

import com.xin.base.GeneralClassAdapter;

import java.lang.instrument.ClassDefinition;

import static com.xin.util.ClassByteUtil.getNewClassBytes;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public abstract class BaseClassChange {

    public BaseClassChange() {
    }

    final public void redefineClass() {
        String className = getClassName();
        ClassLoader classLoader = SettingInstance.getClassLoader();
        try {
            classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            logWhenRedefine(false);
            return;
        }

        try {
            byte[] newClassBytes = getNewClassBytes(className, classLoader, getGeneralClassAdapter());

            ClassDefinition classDefinition = new ClassDefinition(getLoadedClass(),
                                                                  newClassBytes);
            SettingInstance.getInstrumentation()
                           .redefineClasses(classDefinition);

            logWhenRedefine(true);
        } catch (Exception e) {
            System.out.println("热部署类注入失败, 无法进行热部署:" + className + " err: " + e.getMessage());
            if (e.getMessage() == null || e.getMessage()
                                           .isEmpty()) {
                e.printStackTrace();
            }

        }
    }

    protected abstract void logWhenRedefine(boolean success);

    protected abstract String getClassName();

    protected abstract GeneralClassAdapter getGeneralClassAdapter();

    private Class getLoadedClass() throws ClassNotFoundException {
        try {
            return SettingInstance.getClassLoader()
                                  .loadClass(getClassName());
        } catch (ClassNotFoundException e) {
            throw e;
        }
    }

}
