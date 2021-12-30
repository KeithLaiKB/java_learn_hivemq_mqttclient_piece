package com.learn.hivemq_mqttclient.sender.mytestlearn;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 
 * @author laipl
 *
 * 1. 给予一个 num1,
 * 2. 把他传入CompletableFuture 进行 +1+1的处理
 * 3. 给一定的时间, 让CompletableFuture的那个过程完成
 * 4. 再来查看 num1的值
 * 
 * 发现: 
 * 1. num1 是没有被改变的
 * 2. CompletableFuture的 result确实是改变的
 * 
 * 思考:
 * CompletableFuture 应该 是有自己的线程池的, 并进行存放自己的局部变量
 *
 */
public class TestReactiveProgram {
	public static void main(String[] args) {
		// 给一个要观测的变量
		int num1=0;
		
		// 这个"a" 只是一个名字而已
		// 在这个过程中 
		// num1 = 0 	-> num1 +1 -> numTmp1
		// -> numTmp1+1 -> numTmp2 -> result = 2
		CompletableFuture<Integer> future1  = CompletableFuture.completedFuture("a").thenApply((s) -> { 
			System.out.println("kk:"+s);
			Integer numTmp1 = num1+1;
			System.out.println("hi:"+numTmp1);
			return numTmp1;
			
			}).thenApply((rs) -> { 
				
				Integer numTmp2 = rs+1;
				System.out.println("hi:"+numTmp2);
				return numTmp2;
				
			});
		
		
		// 等待五秒 给足够的时间 给 completefuture完成 
    	try {
    		Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	// 此时探讨  completefuture 的过程 完成后, 看一下 num1 是否 有被改变
    	// 发现 其实num1 没被改变
		System.out.println(num1);
		
		 
		Integer result1 = null; 
		try {
			// 然而 completefuture 的result 是被改变的
			// 所以我感觉completefuture 有自己的线程池, 和 用线程内的局部变量 
			result1 = future1.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result1);
		
		
		//可以看出 就算是sleep 了5000ms, num1还是不会有改变
	}
}
