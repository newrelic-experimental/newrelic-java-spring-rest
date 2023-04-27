package org.springframework.http.client.support;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.fit.spring.web.AsyncOutboundWrapper;

@Weave(type=MatchType.BaseClass)
public abstract class AsyncHttpAccessor {

	@Trace(excludeFromTransactionTrace=true)
	protected AsyncClientHttpRequest createAsyncRequest(URI url, HttpMethod method) {
		AsyncClientHttpRequest req = Weaver.callOriginal();
		AsyncOutboundWrapper wrapper = new AsyncOutboundWrapper(req);
		AgentBridge.getAgent().getTransaction().getCrossProcessState().processOutboundRequestHeaders(wrapper, NewRelic.getAgent().getTracedMethod());
		//NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		return req;
	}
}
