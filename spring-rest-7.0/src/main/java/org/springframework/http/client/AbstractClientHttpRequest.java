package org.springframework.http.client;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.spring.web.SpringRestHeaders;

@Weave
public abstract class AbstractClientHttpRequest implements ClientHttpRequest {

	private final HttpHeaders headers = Weaver.callOriginal();
	
	@Trace(leaf = true)
	public ClientHttpResponse execute() {
		if(headers != null) {
			SpringRestHeaders nrHeaders = new SpringRestHeaders(headers);
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(nrHeaders);
		}
		URI uri = getURI();
		HttpMethod method = getMethod();
		
		HttpParameters params = HttpParameters.library("SpringRest").uri(uri).procedure(method.name()).noInboundHeaders().build();
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.setMetricName("Custom","SpringRest","ClientRequest",getClass().getSimpleName(),"execute");
		traced.reportAsExternal(params);
		
		return Weaver.callOriginal();
	}
}
