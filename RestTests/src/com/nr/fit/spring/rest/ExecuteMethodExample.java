package com.nr.fit.spring.rest;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRequestCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.ResponseExtractor;

import com.newrelic.api.agent.Trace;

@Controller
public class ExecuteMethodExample {
	
	private Random random = new Random();
	String url ="http://localhost:9090/RandomlySlow/";
	
//	String url ="http://localhost:8080";
	
	public static void main(String[] args) {
		ExecuteMethodExample example = new ExecuteMethodExample();
		example.iterations(20);
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
		//create request entity using HttpHeaders
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		AsyncRequestCallback requestCallback = new AsyncRequestCallback (){
					@Override
					public void doWithRequest(AsyncClientHttpRequest arg0)
							throws IOException {
						System.out.println(arg0.getURI());
					}
				};
				
		ResponseExtractor<String> responseExtractor = new ResponseExtractor<String>(){
			@Override
			public String extractData(ClientHttpResponse arg0)
					throws IOException {
				return arg0.getStatusText();
			}
		};
		Map<String,String> urlVariable = new HashMap<String, String>();
		urlVariable.put("q", "Concretepage");
		ListenableFuture<String> future = asycTemp.execute(url, method, requestCallback, responseExtractor, urlVariable);
		try {
			//waits for the result
			String result = future.get();
			System.out.println(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
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

} 