package com.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public abstract class CopyBeanUtils {

	public static void copyPropertiesNotNull(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source, null));
    }

    public static void copyPropertiesNotNullIgnore(Object source, Object target, List<String> ignore) {
    	List<String> ignoreProperties = new ArrayList<String>();
        ignoreProperties.addAll(Arrays.asList(getNullPropertyNames(source,ignore)));
        BeanUtils.copyProperties(source, target, ignoreProperties.toArray(new String[ignoreProperties.size()]));
    }

    private static String[] getNullPropertyNames(Object source,List<String> ignore) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
				.filter(propertyName -> (wrappedSource.getPropertyValue(propertyName) == null
						&& !ignore.contains(propertyName)))
                .toArray(String[]::new);
    }
}
