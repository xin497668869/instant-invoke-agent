package com.xin.base.controller;

import com.alibaba.fastjson.JSON;
import com.xin.replace.base.SettingInstance;
import com.xin.vo.ResponseData;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author linxixin@cvte.com
 */
@Component
public class TTTestController$$T implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int order = 0;
    private ConcurrentMap<Class, Object> beanMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void startServer() throws IOException {
        int port = SettingInstance.getPort();

        System.out.println("服务器准备启动 端口号:" + port);
        HttpServer$$ postData = new HttpServer$$(port, this);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Object getBean(Class cls, String methodName) {
        Class orgCls = cls;
        List<Object> beans = new ArrayList<>();
        if (!applicationContext.getBeansOfType(cls)
                               .isEmpty()) {
            return applicationContext.getBean(cls);
        }

        while (true) {
            beans.addAll(applicationContext.getBeansOfType(cls)
                                           .values());
            for (Class anInterface : cls.getInterfaces()) {
                beans.addAll(applicationContext.getBeansOfType(anInterface)
                                               .values());
                addBeanByInterface(anInterface, beans);
            }

            cls = cls.getSuperclass();
            if (cls == null || Object.class.equals(cls)) {
                break;
            }
        }
        for (Object bean : beans) {
            if (bean instanceof Advised) {
                try {
                    if (orgCls.isInstance(((Advised) bean).getTargetSource()
                                                          .getTarget())) {
                        return ((Advised) bean).getTargetSource()
                                               .getTarget();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void addBeanByInterface(Class cls, List<Object> beans) {
        for (Class anInterface : cls.getInterfaces()) {
            beans.addAll(applicationContext.getBeansOfType(anInterface)
                                           .values());
            addBeanByInterface(anInterface, beans);
        }
    }

    ResponseData handleRequest(MethodVo methodVo) {
        try {
            Class<?> aClass = Class.forName(methodVo.getClassName());
            Object bean = getBean(aClass, methodVo.getMethodName());
            if (bean == null) {
                System.err.println("===== 这个类没找到bean或者还没被注入 可以使用@Component 注解类: " + aClass);
            }
            int uuid = ++order;
            System.out.println("===== invoke [" + dateFormat.format(
                    new Date()) + "] [" + uuid + "] 方法:" + methodVo.getMethodName() + " 类: " + methodVo.getClassName() + " =====");

//            final List<Class> paramClasss = Objects.requireNonNull(methodVo.getParamNames()).stream().map(s -> {
//                try {
//                    return Class.forName(s);
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }).collect(Collectors.toList());
            Method declaredMethod = aClass.getDeclaredMethod(methodVo.getMethodName());
            declaredMethod.setAccessible(true);
            long startTime = System.currentTimeMillis();
            Object result = null;
            try {
                SettingInstance.setDebuging(true);
                result = declaredMethod.invoke(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SettingInstance.setDebuging(false);

            long interval = System.currentTimeMillis() - startTime;
            if (!declaredMethod.getReturnType()
                               .equals(Void.class)
                    && !declaredMethod.getReturnType()
                                      .equals(Void.TYPE)) {
                if (result == null) {
                    System.out.println("===== return: \nnull");
                } else {
                    System.out.println("===== return: \n" + JSON.toJSONString(result, true));
                }
            }

            System.out.println("===== end [" + dateFormat.format(new Date()) + "] [" + uuid + "] 时间为:" + getFormatTime(
                    interval) + "ms  方法:" + methodVo.getMethodName() + " 类: " + methodVo.getClassName() + " =====");

            if (order > 10000000) {
                order = 1;
            }
        } catch (NoSuchMethodException e) {
            System.err.println("没找到方法, 请compile之后在运行");
        } catch (ClassNotFoundException e) {
            System.err.println("没找到对应的类, 请compile之后在运行");
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println("执行异常 ");
        }
        return null;
    }

    private String getFormatTime(long millis) {
        if (millis < 1000) {
            return String.valueOf(millis);
        } else {
            String misecondStr = String.valueOf(millis);
            return misecondStr.substring(0, misecondStr.length() - 3) + "," + misecondStr.substring(misecondStr.length() - 3);
        }
    }

    public static class MethodVo {
        private String className;
        private String methodName;

        public MethodVo() {
        }

        public MethodVo(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }
    }
}
