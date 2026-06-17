package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.common.UnitTest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class InterceptorsUT {

    @Mock
    private WebSecurityUtil webSecurityUtil;

    @Mock
    private HttpServletResponse httpServletResponse;

    @AfterEach
    public void tearDown() {
        ThreadContext.clearMap();
    }

    @Test
    public void changePasswordFirstLoginInterceptorRedirectsWhenRequired() throws Exception {
        when(webSecurityUtil.isChangePasswordOnFirstLogin()).thenReturn(true);
        when(webSecurityUtil.isUserLoggedIn()).thenReturn(true);
        when(webSecurityUtil.isUsersFirstLogin()).thenReturn(true);

        ChangePasswordFirstLoginInterceptor interceptor = new ChangePasswordFirstLoginInterceptor(webSecurityUtil);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/matches");

        interceptor.postHandle(request, httpServletResponse, new Object(), null);

        verify(httpServletResponse).sendRedirect("/profile/changePassword");
    }

    @Test
    public void changePasswordFirstLoginInterceptorSkipsRedirectOnPasswordPage() throws Exception {
        when(webSecurityUtil.isChangePasswordOnFirstLogin()).thenReturn(true);

        ChangePasswordFirstLoginInterceptor interceptor = new ChangePasswordFirstLoginInterceptor(webSecurityUtil);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/profile/changePassword");

        interceptor.postHandle(request, httpServletResponse, new Object(), null);

        verify(httpServletResponse, never()).sendRedirect(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void executionTimeInterceptorStoresRequestUriInThreadContext() throws Exception {
        ExecutionTimeInterceptor interceptor = new ExecutionTimeInterceptor();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/ranking");

        assertThat(interceptor.preHandle(request, httpServletResponse, new Object())).isTrue();
        assertThat(ThreadContext.get("mdc.requestURI")).isEqualTo("/ranking");

        interceptor.postHandle(request, httpServletResponse, new Object(), null);

        assertThat(ThreadContext.getContext()).isEmpty();
    }
}

