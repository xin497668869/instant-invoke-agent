package com.xin.base.controller;

import com.alibaba.fastjson.JSON;
import com.xin.base.controller.TestController$$.MethodVo;
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

    private final TestController$$ testController$$;

    public HttpServer$$(int port, TestController$$ testController$$) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        this.testController$$ = testController$$;
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            Map<String, String> bodyMap = new HashMap<>();
            session.parseBody(bodyMap);
            MethodVo methodVo = JSON.parseObject(bodyMap.get("postData"), MethodVo.class);
            ResponseData responseData = testController$$.handleRequest(methodVo);
            return newFixedLengthResponse("{\"code\":\"success\"}\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFixedLengthResponse("{\"code\":\"fail\"}\n");
    }
}
