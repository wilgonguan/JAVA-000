package com.wilgonguan.java2;

public class MyRunnable implements Runnable{
	private Entity entity;
	public MyRunnable(Entity entity) {
		this.entity = entity;
	}
	@Override
	public void run() {
		 try {
			Thread.sleep(1000);
			entity.setName("Hello Kitty");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
