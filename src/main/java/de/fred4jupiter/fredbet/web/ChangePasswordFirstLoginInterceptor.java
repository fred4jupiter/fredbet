package de.fred4jupiter.fredbet.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * This handler will redirect the user to the password change page if its the
 * first login.
 * 
 * @author michael
 *
 */
public class ChangePasswordFirstLoginInterceptor implements HandlerInterceptor {

	private static final String CHANGE_PASSWORD_ENDPOINT = "/profile/changePassword";

	private final WebSecurityUtil webSecurityUtil;

	public ChangePasswordFirstLoginInterceptor(WebSecurityUtil webSecurityUtil) {
		this.webSecurityUtil = webSecurityUtil;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {

		if (!webSecurityUtil.isChangePasswordOnFirstLogin()) {
			return;
		}

		final String requestURI = request.getRequestURI();
		if (requestURI.contains(CHANGE_PASSWORD_ENDPOINT)) {
			return;
		}

		if (webSecurityUtil.isUserLoggedIn() && webSecurityUtil.isUsersFirstLogin()) {
			// Redirect View verwenden. Message wird nicht angezeigt
			response.sendRedirect(CHANGE_PASSWORD_ENDPOINT);
		}
	}

}
