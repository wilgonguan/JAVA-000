package com.wilgonguan.java2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * FutureTask
 * @author wilgonguan
 *
 */
public class StopExample3 {
	@SuppressWarnings("all")
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask futureTask = new FutureTask(new MyCallable());
        Thread thread = new Thread(futureTask);
        thread.start();
        if(!futureTask.isDone()){
        	System.out.println("task has not finished!");
        }
            
        System.out.println(futureTask.get());
    }
}
