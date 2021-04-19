package com.fish.repository;

import com.fish.api.dto.ProductDto;
import com.fish.api.qo.ProductQO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSupportRepository {

   Page<ProductDto> getProduct(ProductQO qo, Pageable pageable);

}