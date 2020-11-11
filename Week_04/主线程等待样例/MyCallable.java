package com.wilgonguan.java2;

import java.util.concurrent.Callable;

public class MyCallable implements Callable<String>{

	@Override
	public String call() throws Exception {
		Thread.sleep(1000);
		return "Hello Kitty";
	}

}
