package de.fred4jupiter.fredbet.web;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExecutionTimeInterceptor implements AsyncHandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionTimeInterceptor.class);

    private static final String START_TIME_ATTRIBUTE_NAME = "startTime";

    // before the actual handler will be executed
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE_NAME, startTime);

        final String requestURI = request.getRequestURI();

        ThreadContext.put("mdc.requestURI", requestURI);
        return true;
    }

    // after the handler is executed
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE_NAME);
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        final String requestURI = request.getRequestURI();

        ThreadContext.put("mdc.executionTime", "" + executeTime);

        LOG.debug("requestURI={}, executionTime={}", requestURI, executeTime);

        ThreadContext.clearMap();
    }
}
