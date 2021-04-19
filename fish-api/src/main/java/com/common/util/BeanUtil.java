package com.common.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


public abstract class BeanUtil {

	private static Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);
	
	/**
	 * Note: no exception thrown even failed.
	 */
	public static void blankAsNull(Object bean, String[] propNames) {
		if (bean == null || propNames == null || propNames.length == 0) {
			return;
		}
		for (String propName : propNames) {
			blankAsNull(bean, propName);
		}
	}
	
	/**
	 * Note: no exception thrown even failed.
	 */
	public static void blankAsNull(Object bean, String propName) {
		Object value = getProperty(bean, propName);
		if (value != null && StringUtils.isBlank(value.toString())) {
			setPropertyQuietly(bean, propName, null);
		}
	}
	
	/**
	 * Note: no exception thrown even failed.
	 */
	public static void nullAsFalse(Object bean, String[] propNames) {
		if (bean == null || propNames == null || propNames.length == 0) {
			return;
		}
		for (String propName : propNames) {
			nullAsFalse(bean, propName);
		}
	}
	
	/**
	 * Note: no exception thrown even failed.
	 */
	public static void nullAsFalse(Object bean, String propName) {
		Object value = getProperty(bean, propName);
		if (value == null) {
			setPropertyQuietly(bean, propName, Boolean.FALSE);
		}
	}

	/**
	 * Note: no exception thrown even failed.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> Collection<T> getProperty(Collection c, String propName) {
		if (CollectionUtils.isEmpty(c)) {
			return Collections.emptyList();
		}
		
		List<T> result = new ArrayList<>(c.size());
		for (Object bean : c) {
			result.add((T) getProperty(bean, propName));
		}
		return result;
	}
	
	/**
	 * Note: no exception thrown even failed.
	 */
	public static Map<String, Object> toMap(Object bean, String[] propNames) {
		Map<String, Object> result = new HashMap<>();
		for (String propName : propNames) {
			result.put(propName, getProperty(bean, propName));
		}
		return result;
	}
	
	/**
	 * Note: no exception thrown even failed.
	 */
	public static Object getProperty(Object bean, String name) {
		try {
			return PropertyUtils.getProperty(bean, name);
		} catch (Exception e) {
			// ignore
			return null;
		}
	}
	
	/**
	 * Note: no exception thrown even failed.
	 */
	public static void setPropertyQuietly(Object bean, String name, Object value) {
		try {
			PropertyUtils.setProperty(bean, name, value);
		} catch (Exception e) {
			// ignore
		}	
	}		
		
	public static void copyPropertiesQuietly(Object src, Object dest, String[] propNames) {
		try {
			copyProperties(src, dest, propNames);
		} catch (Exception e) {
			// ignore
			LOGGER.error("failed to copy properties", e);
		}
	}
	
	public static void copyProperties(Object src, Object dest, String[] propNames) 
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		if (propNames != null && propNames.length > 0 && src != null && dest != null) {
			PropertyUtilsBean util = BeanUtilsBean.getInstance().getPropertyUtils();
			for (String propName : propNames) {
				Object value = util.getProperty(src, propName);
				util.setProperty(dest, propName, value);
			}
		}
	}

	public static void copyPropertiesQuietly(Object src, Object dest) {
		try {
			PropertyUtils.copyProperties(dest, src);
		} catch (Exception e) {
			// ignore
			LOGGER.error("failed to copy properties", e);
		}
	}

	/**
	 * <p>根据指定的字段集合，对比两个bean的字段值，如果不一致返回#{@obj1中的值}</p>
	 * @param obj1 需要比对的bean1(如果两个值比对不一致，返回此bean的值)
	 * @param obj2 需要比对的bean2
	 * @param compareFields 指定比对的字段
	 * */
	public static List<String> getDiffValueByComparStringsInSpecifiedFields(Object obj1, Object obj2, List<String> compareFields) {
		List<String> diffValues = new ArrayList<>();
		for (String fieldName : compareFields) {
			try {
				Object value1 = BeanUtil.getProperty(obj1, fieldName);
				Object value2 = BeanUtil.getProperty(obj2, fieldName);
				//如果两个参数不一致，则放入obj1的的值value1
				if (!Objects.equals(value1, value2)) {
					diffValues.add((String) value1);
				}
			} catch (Exception ignore){
				continue;
			}
		}
		return diffValues;
	}


}
