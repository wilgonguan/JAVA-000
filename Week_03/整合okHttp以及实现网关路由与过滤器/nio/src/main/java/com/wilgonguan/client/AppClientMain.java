package com.wilgonguan.client;

//客户端测试
public class AppClientMain {
	public static final String URL = "http://127.0.0.1:8888/search";
	public static final OkHttpService http = new OkHttpService();
	
	public static void main(String[] args) {
		System.out.println(http.getRequest(URL));
	}

}
