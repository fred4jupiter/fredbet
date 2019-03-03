package de.fred4jupiter.fredbet.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

public class HeaderLogHandlerInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(HeaderLogHandlerInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        LOG.debug("----- Request ---------");
        Collections.list(request.getHeaderNames()).forEach(n -> LOG.debug(n + ": " + request.getHeader(n)));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        LOG.debug("----- response ---------");
        response.getHeaderNames().forEach(n -> LOG.debug(n + ": " + response.getHeader(n)));

        LOG.debug("response status: " + response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}
