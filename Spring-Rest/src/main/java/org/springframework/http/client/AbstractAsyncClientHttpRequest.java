package org.springframework.http.client;

import org.springframework.http.HttpHeaders;
import org.springframework.util.concurrent.ListenableFuture;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.spring.web.HttpParams;
import com.newrelic.instrumentation.labs.spring.web.NRListener;
import com.newrelic.instrumentation.labs.spring.web.SpringRestHeaders;

@Weave
abstract class AbstractAsyncClientHttpRequest implements AsyncClientHttpRequest {

	private final HttpHeaders headers = Weaver.callOriginal();

	@Trace(leaf = true)
	public ListenableFuture<ClientHttpResponse> executeAsync()  {
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if(headers != null) {
			SpringRestHeaders nrHeaders = new SpringRestHeaders(headers);
			transaction.insertDistributedTraceHeaders(nrHeaders);
		}
		ListenableFuture<ClientHttpResponse> f = Weaver.callOriginal();
		String procedure = getMethod().name();
		HttpParams httpParams = new HttpParams(getURI(), procedure, "Spring-Rest");
		
		NRListener<ClientHttpResponse> callback = new NRListener<ClientHttpResponse>(transaction.getToken(), transaction.startSegment("RestRequest"), httpParams);
		f.addCallback(callback, callback);
		return f;
	}

}
