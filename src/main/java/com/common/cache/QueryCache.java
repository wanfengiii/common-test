package com.common.cache;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Cacheable(cacheNames = "CACHE_COMMON_QUERY", keyGenerator="methodAwareCacheKeyGenerator")
public @interface QueryCache {
	
}