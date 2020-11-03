package com.wilgonguan.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpVersion;

/**
 * 基础过滤
 * @author wilgonguan
 *
 */
public class BaseHttpFilter implements HttpRequestFilter{

	@Override
	public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
		fullRequest.setProtocolVersion(HttpVersion.HTTP_1_0);
	}

}
