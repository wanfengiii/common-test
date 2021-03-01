package com.common.util;

import com.common.ModelMapperFactory;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class PageUtils {

	private static final ModelMapper modelMapper = ModelMapperFactory.getObject();

	public static final <T> Page<T> getPageResult(List<T> allData, Pageable pageable) {
		if (CollectionUtils.isEmpty(allData)) {
			return Page.empty(pageable);
		}

		int fromIndex = (int) pageable.getOffset();
		int toIndex = (pageable.getPageSize() == Integer.MAX_VALUE ? Integer.MAX_VALUE : (fromIndex + pageable.getPageSize()));
		List<T> content = ListUtils.safeSubList(allData, fromIndex, toIndex);
		return PageableExecutionUtils.getPage(content, pageable, () -> allData.size());
	}
	
	public static final <T, R> Page<R> transform(Page<T> source, Function<? super T, ? extends R> mapper) {
		List<T> data = source.getContent();
		if (CollectionUtils.isEmpty(data)) {
			return PageableExecutionUtils.getPage(Collections.emptyList(), source.getPageable(), () -> source.getTotalElements());
		}

		List<R> newData = data.stream()
			.map(mapper)
			.collect(Collectors.toList());
		
		return PageableExecutionUtils.getPage(newData, source.getPageable(), () -> source.getTotalElements());
	}

	public static final <T, R> Page<T> transform(Page<R> source, Class<T> clazz) {
		return transform(source,  r -> modelMapper.map(r, clazz));
	}

}