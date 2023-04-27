package org.springframework.http.client.support;

import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.fit.spring.web.OutboundWrapper;

@Weave(type=MatchType.BaseClass)
public abstract class HttpAccessor {

	@Trace(excludeFromTransactionTrace=true)
	protected ClientHttpRequest createRequest(URI url, HttpMethod method) {
		ClientHttpRequest req = Weaver.callOriginal();
		OutboundWrapper wrapper = new OutboundWrapper(req);
		AgentBridge.getAgent().getTransaction().getCrossProcessState().processOutboundRequestHeaders(wrapper, NewRelic.getAgent().getTracedMethod());
//		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		wrapper.dumpHeaders();
		return req;
	}
}
