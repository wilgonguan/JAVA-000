package com.wilgonguan.gateway.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wilgonguan.gateway.filter.BaseHttpFilter;
import com.wilgonguan.gateway.filter.HttpRequestFilter;
import com.wilgonguan.gateway.outbound.HttpOutboundHandler;
import com.wilgonguan.gateway.route.Router;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private HttpOutboundHandler handler;
    private Router router;
    private HttpRequestFilter filter;
    
    public HttpInboundHandler(Router route) {
    	this.router = route;
    	this.filter = new BaseHttpFilter();
        handler = new HttpOutboundHandler();
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            logger.info("channelRead流量接口请求开始");
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            filter.filter(fullRequest, ctx);//过滤
            String uri = fullRequest.uri();
            logger.info("接收到的请求url为{}", uri);
            String destUri = router.route(uri); //路由策略
            handler.handle(destUri, fullRequest, ctx);//处理
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


}