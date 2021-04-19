package com.common.security;

import com.common.api.response.ApiError;
import com.common.api.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.common.api.response.ApiError.ACCESS_DENIED;

public class JwtTokenFilter extends GenericFilterBean {

    private JwtTokenProvider jwtTokenProvider;

    private ObjectMapper om = new ObjectMapper();

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
        if (StringUtils.isBlank(token)) {
            writeError(res, ACCESS_DENIED);
            return;
        }
        try {
            if (jwtTokenProvider.validateToken(token)) {
                Authentication auth = (token != null ? jwtTokenProvider.getAuthentication(token) : null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception ex) {
            ApiError error = getApiError(ex);
            writeError(res, error);
            return;
        }

        filterChain.doFilter(req, res);
    }

    private void writeError(ServletResponse res, ApiError error) throws IOException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(error.getHttpStatus().value());
        om.writeValue(response.getOutputStream(), ErrorResponse.of(error));
    }

    private ApiError getApiError(Exception ex) {
		if (ex instanceof AuthenticationException) {
			return ApiError.getApiError((AuthenticationException)ex);
		} else if (ex instanceof AccessDeniedException) {
			return ACCESS_DENIED;
		} else {
			return ApiError.INTERNAL_SERVER_ERROR;
		}
    }

}
