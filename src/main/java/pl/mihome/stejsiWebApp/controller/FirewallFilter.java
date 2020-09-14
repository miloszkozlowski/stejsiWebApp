package pl.mihome.stejsiWebApp.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component; 

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FirewallFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		try {
            chain.doFilter(request, response);
        } catch (RequestRejectedException e) {
            HttpServletResponse responseGot = (HttpServletResponse) response;
            responseGot.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
		
	}


}
