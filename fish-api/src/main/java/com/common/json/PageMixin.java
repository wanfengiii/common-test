package com.common.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class PageMixin {
	
	@JsonIgnore abstract Pageable getPageable();
	
	@JsonIgnore abstract Sort getSort();
    
    @JsonIgnore abstract int getTotalPages();

    @JsonIgnore abstract boolean isLast();

    @JsonProperty("total")
    abstract long getTotalElements();

    @JsonIgnore abstract int getNumber();

    @JsonIgnore abstract int getNumberOfElements();

    @JsonIgnore abstract boolean isFirst();

    @JsonIgnore abstract boolean isEmpty();

    @JsonIgnore abstract int getSize();

}