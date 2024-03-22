package org.springframework.http.client;

import org.springframework.http.HttpHeaders;
import org.springframework.util.concurrent.ListenableFuture;

import com.newrelic.api.agent.Headers;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.fit.spring.web.HttpParams;
import com.newrelic.instrumentation.fit.spring.web.NRListener;
import com.newrelic.instrumentation.fit.spring.web.SpringRestHeaders;

@SuppressWarnings("deprecation")
@Weave
abstract class AbstractAsyncClientHttpRequest implements AsyncClientHttpRequest {

	private final HttpHeaders headers = Weaver.callOriginal();

	public ListenableFuture<ClientHttpResponse> executeAsync() {
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if (this.headers != null) {
			SpringRestHeaders nrHeaders = new SpringRestHeaders(this.headers);
			transaction.insertDistributedTraceHeaders((Headers)nrHeaders);
		} 
		ListenableFuture<ClientHttpResponse> f = Weaver.callOriginal();
		String procedure = getMethod().name();
		HttpParams httpParams = new HttpParams(getURI(), procedure, "Spring-Rest");
		NRListener<ClientHttpResponse> callback = new NRListener<ClientHttpResponse>(transaction.getToken(), transaction.startSegment("RestRequest"), httpParams);
		f.addCallback(callback,callback);
		return f;	
	}
}
