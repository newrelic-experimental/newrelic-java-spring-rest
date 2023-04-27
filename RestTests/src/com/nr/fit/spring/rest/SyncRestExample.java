package com.nr.fit.spring.rest;

import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.newrelic.api.agent.Trace;

public class SyncRestExample {

	//String url ="http://localhost:9090/RandomlySlow/";
	String url ="http://localhost:8080";
	private Random random = new Random();
	
	public static void main(String[] args) {
		SyncRestExample example = new SyncRestExample();
		example.iterations(40);
	}
	
	public void doGet() {
		RestTemplate template = new RestTemplate();
		
		ResponseEntity<String> response = template.getForEntity(url, String.class);
		
		System.out.println("====================================");
		System.out.println("            Response is");
		System.out.println(response.getBody());
		System.out.println("====================================");
	}
	
	private void pauseSeconds(int n) {
		if(n > 0) {
			pause(n*1000L);
		}
	}
	
	private void pauseUnits(int n) {
		if(n > 0) {
			pause(n*100L);
		}
	}
	
	private void pause(long ms) {
		System.out.println("Pausing for "+ms+" ms");
		if(ms > 0) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void iterations(int n) {
		for(int i=1;i<=n;i++) {
			System.out.println("Performing iteration "+i+" of "+n);
			iteration();
			int s = random.nextInt(45);
			System.out.println("Completed iteration "+i+" of "+n);
			pauseSeconds(s);
		}
	}
	
	@Trace(dispatcher=true)
	public void iteration() {
		doGet();
		int n = random.nextInt(25);
		pauseUnits(n);
	}

}
