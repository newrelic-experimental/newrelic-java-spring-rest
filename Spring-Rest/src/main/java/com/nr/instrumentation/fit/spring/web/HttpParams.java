package com.nr.instrumentation.fit.spring.web;

import java.net.URI;

public class HttpParams {

	protected URI uri;
	protected String procedure;
	protected String library;
	public HttpParams(URI uri, String procedure, String library) {
		super();
		this.uri = uri;
		this.procedure = procedure;
		this.library = library;
	}
	
	
	
}
