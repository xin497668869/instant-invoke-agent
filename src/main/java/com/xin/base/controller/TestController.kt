package com.xin.base.controller

import com.alibaba.fastjson.JSON
import com.xin.StoreInfo
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import vo.MethodVo
import vo.ResponseData
import java.io.*
import java.net.ServerSocket
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.PostConstruct

/**
 *
 * @author linxixin@cvte.com
 */
@Component
public class TestController : ApplicationContextAware {


    private lateinit var applicationContext: ApplicationContext


    override fun setApplicationContext(applicationContext: ApplicationContext?) {
        this.applicationContext = applicationContext!!
    }


    private var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private var order = 0

    @PostConstruct
    fun startServer() {
        Thread {

            try {
                val serverSocket = ServerSocket(StoreInfo.port)
                println("服务器启动 端口号:${StoreInfo.port}")
                while (true) {
                    val socket = serverSocket.accept()
                    val br = BufferedReader(InputStreamReader(socket.getInputStream()))
                    val content = br.readLine()
                    val methodVo = JSON.parseObject(content, MethodVo::class.java)
                    val responseData = handle(methodVo)
                    if (responseData != null) {
                        val bw = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                        bw.write(JSON.toJSONString(responseData) + "\r\n")
                        bw.flush()
                    } else {
                        val bw = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                        bw.write("  \r\n")
                        bw.flush()
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun handle(methodVo: MethodVo): ResponseData? {
        try {
            val aClass = Class.forName(methodVo.className)
            val bean = applicationContext.getBean(aClass)
            if (bean == null) {
                System.err.println("===== 这个类还没被注入 可以使用@Component 注解类: $aClass")
            }
            val uuid = ++order
            println("===== invoke [${dateFormat.format(Date())}] [$uuid] 方法:${methodVo.methodName} 类: ${methodVo.className} =====")

            val declaredMethod = aClass.getDeclaredMethod(methodVo.methodName, *methodVo.paramNames!!.map {
                Class.forName(it)
            }.toTypedArray())

            val startTime = System.currentTimeMillis()
            var result: Any? = null
            try {
                result = declaredMethod.invoke(bean)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val interval = System.currentTimeMillis() - startTime
            if (result == null) {
                println("===== return: \nnull")
            } else {
                println("===== return: \n" + JSON.toJSONString(result, true))
            }

            println("===== end [" + dateFormat.format(Date()) + "] [" + uuid + "] 时间为:" + getFormatTime(interval) + "ms  方法:${methodVo.methodName} 类: ${methodVo.className} =====")

            if (order > 10000000) {
                order = 1
            }
        } catch (e: NoSuchMethodException) {
            System.err.println("没找到方法, 请compile之后在运行")
        } catch (e: ClassNotFoundException) {
            System.err.println("没找到对应的类, 请compile之后在运行")
        } catch (e: Throwable) {
            System.err.println("执行异常")
        }
        return null
    }

    private fun getFormatTime(millis: Long): String {
        return if (millis < 1000) {
            millis.toString()
        } else {
            val misecondStr = millis.toString()
            misecondStr.substring(0, misecondStr.length - 3) + "," + misecondStr.substring(misecondStr.length - 3)
        }
    }

}
