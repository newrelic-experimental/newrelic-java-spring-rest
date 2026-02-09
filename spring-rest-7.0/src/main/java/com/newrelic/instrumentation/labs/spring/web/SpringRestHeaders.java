package com.newrelic.instrumentation.labs.spring.web;

import java.util.Collection;
import java.util.Collections;

import org.springframework.http.HttpHeaders;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

public class SpringRestHeaders implements Headers {
	
	HttpHeaders headers = null;
	
	public SpringRestHeaders(HttpHeaders h) {
		headers = h;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		if(headers != null) {
			return headers.getFirst(name);
		}
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		if(headers != null) {
			return headers.get(name);
		}
		return null;
	}

	@Override
	public void setHeader(String name, String value) {
		headers.add(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		headers.add(name, value);
	}

	@Override
	public Collection<String> getHeaderNames() {
		if(headers != null) {
			return headers.headerNames();
		}
		return Collections.emptyList();
	}

	@Override
	public boolean containsHeader(String name) {
		return getHeaderNames().contains(name);
	}

}
