package com.common.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class MethodAwareCacheKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		String path = generatePath(target, method);
		return SimpleKeyGenerator.generateKey(path, params);
	}

	private String generatePath(Object target, Method method) {
		return target.getClass().getName() + "." + method.getName();
	}
	
}