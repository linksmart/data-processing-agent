package it.ismb.pertlab.pwal.connectors.rest.interceptor;

import it.ismb.pertlab.pwal.connectors.rest.responsewrapper.LinkSmartResponseWrapper;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseFilter implements Filter {

	@Override
	public void destroy() {	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		System.out.println("doFilter");
		
		LinkSmartResponseWrapper lsResponse = new LinkSmartResponseWrapper((HttpServletResponse)response);
		lsResponse.forceContentType("text/plain");
//		lsResponse.forceBufferSize(16384);
//		response.setContentType("text/plain");
		chain.doFilter(request, lsResponse);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {	}

}
