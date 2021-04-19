package com.common.util;

import java.util.Collections;
import java.util.List;

public abstract class ListUtils {

	public static <T> List<T> safeSubList(List<T> list, int fromIndex, int toIndex) {
		
		if (list == null 
				|| list.size() == 0
				|| fromIndex >= toIndex
				|| toIndex <= 0
				|| fromIndex >= list.size()
				) {
			
			return Collections.emptyList();
		}
		
	    fromIndex = Math.max(0, fromIndex);
	    toIndex = Math.min(list.size(), toIndex);

	    return list.subList(fromIndex, toIndex);
	}
	
}