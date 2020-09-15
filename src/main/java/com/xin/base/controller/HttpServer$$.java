package com.xin.base.controller;

import com.alibaba.fastjson.JSON;
import com.xin.base.controller.TTTestController$$T.MethodVo;
import com.xin.vo.ResponseData;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
public class HttpServer$$ extends NanoHTTPD {

    private final TTTestController$$T TTTestController$$T;

    public HttpServer$$(int port, TTTestController$$T TTTestController$$T) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        this.TTTestController$$T = TTTestController$$T;
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            Map<String, String> bodyMap = new HashMap<>();
            session.parseBody(bodyMap);
            MethodVo methodVo = JSON.parseObject(bodyMap.get("postData"), MethodVo.class);
            ResponseData responseData = TTTestController$$T.handleRequest(methodVo);
            return newFixedLengthResponse("{\"code\":\"success\"}\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse("{\"code\":\"fail\"}\n");
    }
}
