package com.wilgonguan.java;

/**
 * 测试类
 * @author wilgonguan
 */
public class App {
	public static final String URL = "http://www.baidu.com";
	public static final OkHttpService http = new OkHttpService();
	
    public static void main( String[] args ){
        //同步
        System.out.println(http.getRequest(URL));
        //异步
        http.asyncGetRequest(URL);
        
        
    }
}
