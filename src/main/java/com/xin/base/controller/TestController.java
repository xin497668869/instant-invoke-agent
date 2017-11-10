package com.xin.base.controller;

import com.alibaba.fastjson.JSON;
import com.xin.StoreInfo;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import vo.MethodVo;
import vo.ResponseData;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linxixin@cvte.com
 */
@Component
public class TestController implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int              order      = 0;

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try {
                System.out.println("服务器准备启动 端口号:" + StoreInfo.INSTANCE.getPort());
                ServerSocket serverSocket = new ServerSocket(StoreInfo.INSTANCE.getPort());
                while (true) {
                    Socket socket = serverSocket.accept();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String content = br.readLine();
                    MethodVo methodVo = JSON.parseObject(content, MethodVo.class);
                    ResponseData responseData = handle(methodVo);
                    if (responseData != null) {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        bw.write(JSON.toJSONString(responseData) + "\r\n");
                        bw.flush();
                    } else {
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        bw.write("  \r\n");
                        bw.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private ResponseData handle(MethodVo methodVo) {
        try {
            Class<?> aClass = Class.forName(methodVo.getClassName());
            Object bean = applicationContext.getBean(aClass);
            if (bean == null) {
                System.err.println("===== 这个类还没被注入 可以使用@Component 注解类: $aClass");
            }
            int uuid = ++order;
            System.out.println("===== invoke ["+dateFormat.format(new Date())+"] ["+uuid+"] 方法:"+methodVo.getMethodName()+" 类: "+methodVo.getClassName()+" =====");

            final List<Class> paramClasss = methodVo.getParamNames().stream().map(s -> {
                try {
                    return Class.forName(s);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            Method declaredMethod = aClass.getDeclaredMethod(methodVo.getMethodName(), paramClasss.toArray(new Class[paramClasss.size()]));

            long startTime = System.currentTimeMillis();
            Object result = null;
            try {
                result = declaredMethod.invoke(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long interval = System.currentTimeMillis() - startTime;
            if (result == null) {
                System.out.println("===== return: \nnull");
            } else {
                System.out.println("===== return: \n" + JSON.toJSONString(result, true));
            }

            System.out.println("===== end [" + dateFormat.format(new Date()) + "] [" + uuid + "] 时间为:" + getFormatTime(interval) + "ms  方法:${methodVo.methodName} 类: ${methodVo.className} =====");

            if (order > 10000000) {
                order = 1;
            }
        } catch (NoSuchMethodException e) {
            System.err.println("没找到方法, 请compile之后在运行");
        } catch (ClassNotFoundException e) {
            System.err.println("没找到对应的类, 请compile之后在运行");
        } catch (Throwable e) {
            System.err.println("执行异常");
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


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
