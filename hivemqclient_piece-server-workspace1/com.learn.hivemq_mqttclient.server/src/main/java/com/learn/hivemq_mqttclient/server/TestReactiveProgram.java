package com.learn.hivemq_mqttclient.server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TestReactiveProgram {
	public static void main(String[] args) {
		int num1=0;
		
		CompletableFuture<Integer> future1  = CompletableFuture.completedFuture("a").thenApply((s) -> { 
			
			Integer numTmp1 = num1+1;
			System.out.println("hi:"+numTmp1);
			return numTmp1;
			
			}).thenApply((rs) -> { 
				
				Integer numTmp2 = rs+1;
				System.out.println("hi:"+numTmp2);
				return numTmp2;
				
			});
		
		/*
		Integer result1 = null;
		try {
			result1 = CompletableFuture.completedFuture("a").thenApply((s) -> { 
				
				Integer numTmp1 = num1+1;
				System.out.println("hi:"+numTmp1);
				return numTmp1;
				}).thenApply((rs) -> { 
					Integer numTmp2 = rs+1;
					System.out.println("hi:"+numTmp2);
					return numTmp2;
				}).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		
    	try {
    		Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(num1);
		
		
		Integer result1 = null; 
		try {
			result1 = future1.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result1);
	}
}
