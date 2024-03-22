package org.springframework.web.client;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class RestTemplate {
	
	@Trace
	protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
		return Weaver.callOriginal();
	}

	@Trace
	protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) {
		Weaver.callOriginal();
	}
	
	
}
