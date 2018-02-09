package de.fred4jupiter.fredbet.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * The global exception handler for all exceptions.
 * 
 * @author michael
 *
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

	public static final String DEFAULT_ERROR_VIEW = "error";

	@ExceptionHandler(value = Exception.class)
	public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		/*
		 * If the exception is annotated with @ResponseStatus rethrow it and let
		 * the framework handle it. AnnotationUtils is a Spring Framework
		 * utility class.
		 */
		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
			throw e;
		}

		LOG.error(e.getMessage(), e);

		// Otherwise setup and send the user to a default error-view.
		ModelAndView mav = new ModelAndView();
		mav.addObject("exception", e);
		mav.addObject("url", req.getRequestURL());
		mav.setViewName(DEFAULT_ERROR_VIEW);
		return mav;
	}
}
