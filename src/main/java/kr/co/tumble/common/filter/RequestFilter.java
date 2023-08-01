package kr.co.tumble.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * RequestFilter
 */
@Component
@Order(1)
public class RequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        /*do nothing*/
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(servletRequest instanceof HttpServletRequest request){
            MDC.put("requestURL", request.getRequestURI());
        }
    	filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        /*do nothing*/
    }
}
