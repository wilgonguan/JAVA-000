package com.wilgonguan.gateway.route;

/**
 * 请求路由处理
 * @author wilgonguan
 *
 */
public interface Router {
	/**
	 * 根据请求uri，随机得到目标uri
	 * @param uri
	 * @return
	 */
	String route(String uri);
}
