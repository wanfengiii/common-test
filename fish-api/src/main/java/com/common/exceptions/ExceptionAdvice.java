package com.common.exceptions;

import com.common.api.response.ApiError;
import com.common.api.response.ErrorResponse;
import com.common.api.response.Message;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.base.Joiner;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.common.api.response.ApiError.ACCESS_DENIED;
import static com.common.api.response.ApiError.METHOD_ARGUMENT_NOT_VALID;

@ControllerAdvice
@Log4j2
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler({ 
		RestApiException.class, 
		AuthenticationException.class, 
		AccessDeniedException.class,
		Exception.class 
	})
	public final ResponseEntity<Object> handlePlatformException(Exception ex, WebRequest request) {
		HttpHeaders headers = new HttpHeaders();

		if (ex instanceof RestApiException) {
			RestApiException e = (RestApiException) ex;
			return new ResponseEntity<>(errorBody(e), headers, e.getApiError().getHttpStatus());
		} else if (ex instanceof AuthenticationException) {
			ApiError err = ApiError.getApiError((AuthenticationException)ex);
			return response(headers, err);
		} else if (ex instanceof AccessDeniedException) {
			return response(headers, ACCESS_DENIED);
		} else {
			ApiError apiError = ApiError.INTERNAL_SERVER_ERROR;
			log.error(apiError.getMessage(), ex);
			return new ResponseEntity<>(ErrorResponse.of(apiError, ex.getMessage()), headers, apiError.getHttpStatus());
		}
	}

	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		return new ResponseEntity<>(ErrorResponse.of(getMsg(ex)), headers, METHOD_ARGUMENT_NOT_VALID.getHttpStatus());
	}

	// try to retrieve clear error message
	private Message getMsg(HttpMessageNotReadableException ex) {
		Throwable e = ex;
		Throwable t = ex.getCause();
		String pathname = null;
		if (t instanceof InvalidFormatException) {
			pathname = getPathName((InvalidFormatException) t);
			e = t;
			t = e.getCause();
			if (t instanceof DateTimeParseException) {
				e = t;
			}
		}

		String code = METHOD_ARGUMENT_NOT_VALID.getCode() + (pathname == null ? "" : "." + pathname);
		String msg = (pathname == null ? "" : pathname + " is invalid, ") + e.getMessage();
		return Message.of(code, msg);
	}

	private String getPathName(InvalidFormatException e) {
		List<Reference> paths = e.getPath();
		if (CollectionUtils.isEmpty(paths)) {
			return null;
		}

		List<String> list = new ArrayList<>();
		for (int i = 0; i < paths.size(); i++) {
			if (i == 0) {
				list.add(paths.get(0).getFrom().getClass().getSimpleName());
			}
			list.add(paths.get(i).getFieldName());
		}

		return Joiner.on(".").join(list);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ApiError apiError = METHOD_ARGUMENT_NOT_VALID;
		return new ResponseEntity<>(errorBody(ex.getBindingResult()), headers, apiError.getHttpStatus());
	}
	
	@Override
	protected ResponseEntity<Object> handleBindException(
			BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		ApiError apiError = METHOD_ARGUMENT_NOT_VALID;
		return new ResponseEntity<>(errorBody(ex.getBindingResult()), headers, apiError.getHttpStatus());
	}

	private ErrorResponse errorBody(BindingResult br) {
		List<Message> errors = 
				br
				.getFieldErrors()
				.stream()
				.map(this::fieldError)
				.collect(Collectors.toList());
		return ErrorResponse.of(errors);
	}

	private Message fieldError(FieldError fe) {
		ApiError err = METHOD_ARGUMENT_NOT_VALID;
		String code = err.getCode() + "." + fe.getObjectName() + "." + fe.getField();
		return Message.of(code, err.getMessage(), fe.getField() + " " + fe.getDefaultMessage());
	}

	private ErrorResponse errorBody(RestApiException e) {
		return ErrorResponse.of(e.getApiError(), e.getArgs());
	}

	private ResponseEntity<Object> response(HttpHeaders headers, ApiError apiError, Object... args) {
		ErrorResponse res = ErrorResponse.of(apiError, args);
		return new ResponseEntity<>(res, headers, apiError.getHttpStatus());
	}

}