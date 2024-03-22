package com.newrelic.instrumentation.labs.spring.web;

import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRListener<T> implements FailureCallback, SuccessCallback<T> {

	private Token token = null;
	private Segment segment = null;
	private HttpParams httpParams = null;


	public NRListener(Token token, Segment segment, HttpParams httpParams) {
		super();
		this.token = token;
		this.segment = segment;
		this.httpParams = httpParams;
	}

	@Override
	@Trace(async = true)
	public void onSuccess(T result) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		if(segment != null) {
			if(httpParams != null) {
				HttpParameters params = HttpParameters.library(httpParams.library).uri(httpParams.uri).procedure(httpParams.procedure).noInboundHeaders().build();
				segment.reportAsExternal(params);
				segment.end();
			}
		} else if(httpParams != null) {
			HttpParameters params = HttpParameters.library(httpParams.library).uri(httpParams.uri).procedure(httpParams.procedure).noInboundHeaders().build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
	}

	@Override
	@Trace(async = true)
	public void onFailure(Throwable ex) {
		
		if(ex != null) {
			NewRelic.noticeError(ex);
		}
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		if(segment != null) {
			if(httpParams != null) {
				HttpParameters params = HttpParameters.library(httpParams.library).uri(httpParams.uri).procedure(httpParams.procedure).noInboundHeaders().build();
				segment.reportAsExternal(params);
				segment.end();
			}
		} else if(httpParams != null) {
			HttpParameters params = HttpParameters.library(httpParams.library).uri(httpParams.uri).procedure(httpParams.procedure).noInboundHeaders().build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}

	}

}
