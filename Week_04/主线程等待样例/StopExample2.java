package com.wilgonguan.java2;

/**
 * Join
 * @author wilgonguan
 *
 */
public class StopExample2 {
	public static void main(String[] args) throws InterruptedException {
        Entity entity = new Entity();
        Thread thread = new Thread(new MyRunnable(entity));
        thread.start();
        // 获取子线程的返回值：Thread的join方法来阻塞主线程，直到子线程返回
        thread.join();
        System.out.println(entity.getName());
    }

}
