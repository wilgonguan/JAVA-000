package com.wilgonguan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyClassLoader extends ClassLoader{
	
	public static void main(String[] args) {
		try {
			Class<?> hello = new MyClassLoader().findClass("com.wilgonguan.Hello");// load class
			Method helloMethod = hello.getDeclaredMethod("hello");//反射
			helloMethod.setAccessible(true);
			helloMethod.invoke(hello.newInstance());
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | 
				IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		FileInputStream inputStream = null;
		byte[] content = null;
		try {
			File file = new File("D:\\Hello.class");
			content = new byte[(int) file.length()];
			inputStream = new FileInputStream(file);
			inputStream.read(content);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return defineClass(name, content, 0, content.length);
	}
}
