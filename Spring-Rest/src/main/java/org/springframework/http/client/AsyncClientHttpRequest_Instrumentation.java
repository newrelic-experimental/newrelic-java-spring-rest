package org.springframework.http.client;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.util.concurrent.ListenableFuture;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.fit.spring.web.HttpParams;
import com.nr.instrumentation.fit.spring.web.NRCallback;

@Weave(type=MatchType.Interface,originalName="org.springframework.http.client.AsyncClientHttpRequest")
public abstract class AsyncClientHttpRequest_Instrumentation {
	
	public abstract HttpMethod getMethod();
	public abstract URI getURI();

	@Trace
	public ListenableFuture<ClientHttpResponse> executeAsync() {
		ListenableFuture<ClientHttpResponse> f = Weaver.callOriginal();
//		Transaction transaction = NewRelic.getAgent().getTransaction();
//		String procedure = getMethod().name();
//		HttpParams httpParams = new HttpParams(getURI(), procedure, "Spring-Rest");
		
//		NRCallback callback = new NRCallback(transaction.getToken(), transaction.startSegment("RestRequest"), httpParams);
//		f.addCallback(callback, callback);
		return f;
	}
}
