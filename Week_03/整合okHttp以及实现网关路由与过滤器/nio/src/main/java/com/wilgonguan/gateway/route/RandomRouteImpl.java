package com.wilgonguan.gateway.route;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 随机选取
 * @author wilgonguan
 *
 */
public class RandomRouteImpl implements Router{
	
	public static final List<?> searhEngines = Arrays.asList("http://www.badu.com","http://www.google.com");
	public static final Random random = new Random();
	@Override
	public String route(String uri) {
		if(uri.contains("search")){
			return (String) searhEngines.get(random.nextInt(2));
		}
		return "http://localhst:8080/";
	}
	

}
