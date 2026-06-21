package de.fred4jupiter.fredbet.web.filter;

import de.fred4jupiter.fredbet.common.UnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class HeaderLogHandlerInterceptorUT {

    private final HeaderLogHandlerInterceptor interceptor = new HeaderLogHandlerInterceptor();

    @Test
    public void preHandleReturnsTrueAndPostHandleAcceptsResponseHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/matches");
        request.addHeader("X-Test", "value");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.addHeader("X-Response", "ok");
        response.setStatus(200);

        boolean result = interceptor.preHandle(request, response, new Object());
        interceptor.postHandle(request, response, new Object(), null);
        interceptor.afterCompletion(request, response, new Object(), null);

        assertThat(result).isTrue();
        assertThat(response.getHeader("X-Response")).isEqualTo("ok");
        assertThat(response.getStatus()).isEqualTo(200);
    }
}

