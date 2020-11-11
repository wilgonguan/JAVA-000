package com.wilgonguan.java2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/**
 * ExecutorService
 * @author wilgonguan
 *
 */
public class StopExample4 {
	@SuppressWarnings("all")
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future future = executorService.submit(new MyCallable());
        if(!future.isDone()){
        	System.out.println("task has not finished!");
        }
        System.out.println(future.get());
    }
}
