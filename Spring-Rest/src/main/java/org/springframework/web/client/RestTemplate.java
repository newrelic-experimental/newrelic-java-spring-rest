package org.springframework.web.client;

import java.net.URI;
import java.util.logging.Level;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.fit.spring.web.InboundWrapper;

@Weave
public abstract class RestTemplate {
	
	@NewField
	private InboundWrapper inboundWrapper = null;
	

	@Trace(leaf=true)
	protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
		inboundWrapper = null;
		T retValue = Weaver.callOriginal();
		String procedure = method.name();
		if(inboundWrapper != null) {
			inboundWrapper.dumpHeaders();
			HttpParameters params = HttpParameters.library("SpringRest").uri(url).procedure(procedure).inboundHeaders(inboundWrapper).build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		} else {
			NewRelic.getAgent().getLogger().log(Level.FINE, "inboundWrapper has not been set");
			HttpParameters params = HttpParameters.library("SpringRest").uri(url).procedure(procedure).noInboundHeaders().build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
		return retValue;
	}
	
	protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) {
		inboundWrapper = new InboundWrapper(response);
		NewRelic.getAgent().getLogger().log(Level.FINE, "inboundWrapper has been set to {0} using response {1}",inboundWrapper,response);
		
		Weaver.callOriginal();
	}
	
	
}
