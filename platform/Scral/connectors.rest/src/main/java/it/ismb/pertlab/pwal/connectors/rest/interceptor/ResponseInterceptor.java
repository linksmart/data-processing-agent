package it.ismb.pertlab.pwal.connectors.rest.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ResponseInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		System.out.println("afterCompletion");
		response.setContentType("text/plain");
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2, ModelAndView arg3) throws Exception {
		System.out.println("postHandle");
		response.setContentType("text/plain");
	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse response,
			Object arg2) throws Exception {
		System.out.println("preHandle");
		if(response!=null)
		{
			System.out.println("response is not null");
			response.setContentType("text/plain");
			response.setBufferSize(16384);	
		}
		else
			System.out.println("afterCompletion - response is null");
		return true;
	}

}
