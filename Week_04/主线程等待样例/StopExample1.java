package com.wilgonguan.java2;

/**
 * Value
 * @author wilgonguan
 *
 */
public class StopExample1 {
	public static void main(String[] args) throws InterruptedException {
        Entity entity = new Entity();
        Thread thread = new Thread(new MyRunnable(entity));
        thread.start();
        // 获取子线程的返回值：主线程等待法
        while (entity.getName() == null){
            Thread.sleep(1000);
        }
        System.out.println(entity.getName());
    }

}
