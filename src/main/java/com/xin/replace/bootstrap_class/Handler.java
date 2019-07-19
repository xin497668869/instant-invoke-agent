package com.xin.replace.bootstrap_class;

import com.xin.Boot;
import com.xin.replace.WebappLoaderChangeClass;
import com.xin.replace.base.HandleMethod;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class Handler implements HandleMethod {

    public void handle2(ClassLoader classLoader, ClassLoader parentClassLoader) {
        Boot.commonClassLoader = classLoader;
        Thread.currentThread()
              .setContextClassLoader(classLoader);
        new WebappLoaderChangeClass(Boot.instrumentation, classLoader).redefineClass();
    }
}
