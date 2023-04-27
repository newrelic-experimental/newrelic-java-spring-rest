package com.nr.fit.spring.rest;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import com.newrelic.api.agent.Trace;

public class ExchangeMethodExample {
	
	private Random random = new Random();
//	String url ="http://localhost:9090/RandomlySlow/";
	String url ="http://localhost:8080";

	public static void main(String[] args) {
		ExchangeMethodExample example = new ExchangeMethodExample();
		example.iterations(20);
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
			pauseSeconds(s);
		}
	}
	
	@Trace(dispatcher=true)
	public void iteration() {
		get();
		int n = random.nextInt(25);
		pauseUnits(n);
	}
	
	@Trace
	public void get() {
		AsyncRestTemplate asycTemp = new AsyncRestTemplate();
		HttpMethod method = HttpMethod.GET;
		Class<String> responseType = String.class;
		//create request entity using HttpHeaders
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);
		ListenableFuture<ResponseEntity<String>> future = asycTemp.exchange(url, method, requestEntity, responseType);
		try {
			//waits for the result
			ResponseEntity<String> entity = future.get();
			HttpHeaders respHeaders = entity.getHeaders();
			Set<String> keys = respHeaders.keySet();
			System.out.println("Response Headers");
			for(String key : keys) {
				System.out.print("\tHeader"+key+": ");
			List<String> values = respHeaders.get(key);
				for(String value : values) {
					System.out.print(value+", ");
				}
				System.out.println();
			}
			//prints body source code for the given URL
			System.out.println(entity.getBody());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
